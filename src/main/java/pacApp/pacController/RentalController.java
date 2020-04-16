package pacApp.pacController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import pacApp.pacData.CarRepository;
import pacApp.pacData.RentalRepository;
import pacApp.pacData.UserRepository;
import pacApp.pacException.AuthenticationForbiddenException;
import pacApp.pacException.RentalNotFoundException;
import pacApp.pacModel.Car;
import pacApp.pacModel.Rental;
import pacApp.pacModel.User;
import pacApp.pacModel.request.Booking;
import pacApp.pacModel.response.GenericResponse;
import pacApp.pacSecurity.JwtAuthenticatedProfile;

import java.awt.print.Book;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

@RestController
@Api(value = "RentalController", description = "Operations pertaining to Rentals in Car Rental System")
public class RentalController {

    private final RentalRepository repository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private static final Logger log = LoggerFactory.getLogger(RentalController.class);

    public RentalController(RentalRepository repository, UserRepository userRepository, CarRepository carRepository){
        this.repository = repository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    @GetMapping("/rental")
    public List<Booking> getAllRentals(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            throw new AuthenticationForbiddenException("authentication failure");
        }

        log.info(auth.toString());
        log.info(Boolean.toString(auth.isAuthenticated()));
        log.info(auth.getName());
        log.info(auth.getPrincipal().toString());

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;

        String userEmail = authenticatedProfile.getName();
        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()){
            throw new AuthenticationForbiddenException("authentication failure");
        }

        User user = optUser.get();
        List<Rental> rentals = this.repository.findByUser(user);
        List<Booking> bookings = new Vector<Booking>();

        for(Rental rental : rentals) {
            bookings.add(this.convertRentalToBooking(rental));
        }

        return bookings;
    }

    @GetMapping("/rental/{id}")
    public ResponseEntity getRental(@PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            throw new AuthenticationForbiddenException("authentication failure");
        }

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;

        String userEmail = authenticatedProfile.getName();
        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()){
            throw new AuthenticationForbiddenException("authentication failure");
        }

        User user = optUser.get();
        List<Rental> userRentalList = this.repository.findByUser(user);

        Rental rental = null;

        for (Rental userRental : userRentalList) {
            if (userRental.getId() == id.longValue()) {
                rental = userRental;
            }
        }

        if (rental == null) {
            //throw new RentalNotFoundException(id);
            GenericResponse response = new GenericResponse(HttpStatus.NOT_FOUND.value(), "Rental not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Booking booking = this.convertRentalToBooking(rental);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @RequestMapping(value = "/rental", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveRental(@RequestBody Booking booking){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            //throw new AuthenticationForbiddenException("authentication failure");
            GenericResponse response = new GenericResponse(403,"Authentication failure");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;

        String userEmail = authenticatedProfile.getName();
        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()){
            GenericResponse response = new GenericResponse(400,"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        Long carId = booking.getCarId();

        if (carId == null) {
            GenericResponse response = new GenericResponse(400,"carId is missing in request payload");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<Car> optionalCar = this.carRepository.findById(carId);

        if (!optionalCar.isPresent()) {
            GenericResponse response = new GenericResponse(400,"Car not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Car car = optionalCar.get();

        //safety check for car availability

        List<Rental> rentalsForCar = this.repository.findByCar(car);
        for (Rental rental : rentalsForCar) {
            if (rental.getEndDate() == null) {
                GenericResponse response = new GenericResponse(400,"Car already booked");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        long unixTimestamp = Instant.now().getEpochSecond();

        //validity check for booking.startTime

        if (booking.getStartTime() != null
                && booking.getEndTime() > unixTimestamp - 10
                && booking.getEndTime() < unixTimestamp + 10) {
            unixTimestamp = booking.getEndTime();
        }

        Timestamp startDate = new Timestamp(unixTimestamp * 1000);

        Rental rental = new Rental();
        rental.setStartDate(startDate);
        rental.setUser(user);
        rental.setCar(car);

        Rental savedRental = this.repository.save(rental);

        if (savedRental == null) {
            GenericResponse response = new GenericResponse(500,"Car already booked");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //GenericResponse response = new GenericResponse(200, "Booking successful");
        Booking bookingResponse = this.convertRentalToBooking(savedRental);

        return new ResponseEntity<>(bookingResponse, HttpStatus.OK);
    }

    //@PutMapping("/rental/{id}")
    @RequestMapping(value = "/rental/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRental(@RequestBody Booking booking, @PathVariable Long id){
        if (booking.getId() != null && booking.getId() != id) {
            GenericResponse response = new GenericResponse(400,"Incorrect rental id");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof JwtAuthenticatedProfile)) {
            //throw new AuthenticationForbiddenException("authentication failure");
            GenericResponse response = new GenericResponse(403,"Authentication failure");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        JwtAuthenticatedProfile authenticatedProfile = (JwtAuthenticatedProfile) auth;

        String userEmail = authenticatedProfile.getName();
        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()){
            GenericResponse response = new GenericResponse(400,"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        Optional<Rental> optionalRental = this.repository.findById(id);

        if (!optionalRental.isPresent()) {
            GenericResponse response = new GenericResponse(400,"Rental not found");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Rental rental = optionalRental.get();

        if (rental.getEndDate() != null) {
            GenericResponse response = new GenericResponse(400,"Rental already ended");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User rentalUser = rental.getUser();

        log.info("token user: " + user.getId());
        log.info("rental user: " + rentalUser.getId());

        if (user.getId() != rentalUser.getId()) {
            GenericResponse response = new GenericResponse(401,"Unauthorized");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        long unixTimestamp = Instant.now().getEpochSecond();

        //validity check for booking.endTime

        if (booking.getEndTime() != null
            && booking.getEndTime() > unixTimestamp - 10
            && booking.getEndTime() < unixTimestamp + 10) {
            unixTimestamp = booking.getEndTime();
        }

        Timestamp endDate = new Timestamp(unixTimestamp * 1000);
        rental.setEndDate(endDate);

        rental = this.repository.save(rental);

        //TODO: calculate costs

        //return new ResponseEntity<>(rental, HttpStatus.OK);
        Booking bookingResponse = this.convertRentalToBooking(rental);

        return new ResponseEntity<>(bookingResponse, HttpStatus.OK);
    }

    @DeleteMapping("/rental/{id}")
    public void deleteCar(@PathVariable Long id){
        //TODO: check for super user rights
        //this.repository.deleteById(id);
    }

    protected Booking convertRentalToBooking(Rental rental) {
        Booking booking = new Booking();
        booking.setId(rental.getId());
        booking.setCarId(rental.getCar().getId());

        if (rental.getStartDate() != null) {
            Timestamp startDate = rental.getStartDate();
            booking.setStartTime(startDate.getTime() / 1000);
        }

        if (rental.getEndDate() != null) {
            Timestamp endDate = rental.getEndDate();
            booking.setEndTime(endDate.getTime() / 1000);
        }

        return booking;
    }
}
