package pacApp.pacController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import pacApp.pacData.CarFactory;
import pacApp.pacData.CarRepository;
import pacApp.pacData.RentalRepository;
import pacApp.pacData.UserRepository;
import pacApp.pacException.CarNotFoundException;
import pacApp.pacLogic.Constants;
import pacApp.pacModel.Car;
import pacApp.pacModel.Currency;
import pacApp.pacModel.Rental;
import pacApp.pacModel.User;
import pacApp.pacModel.request.CarInfo;
import pacApp.pacModel.response.GenericResponse;
import pacApp.pacSecurity.JwtAuthenticatedProfile;
import pacApp.pacSoapConnector.SoapConvertCurrencyConnector;

@RestController
@Api(value = "CarController", description = "Operations pertaining to Cars in Car Management System")
public class CarController extends BaseRestController {

    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    private final CarRepository repository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public CarController(CarRepository repository, RentalRepository rentalRepository, UserRepository userRepository){
        this.repository = repository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/cars")
    public ResponseEntity getAllCars(){
        String userEmail = super.getAuthentication().getName();

        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Invalid user");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        List<Car> carList = this.repository.findAll();

        List<Car> availableCarList = new Vector<Car>();

        for (Car car : carList) {
            boolean isCarAvailable = this.checkForCarBooking(car);

            if (isCarAvailable) {
                availableCarList.add(car);
            }
        }

        List<CarInfo> carInfoList = this.convertCarsToCarInfos(availableCarList);

        Currency userDefaultCurrency = user.getDefaultCurrency();
        log.info(userDefaultCurrency.toString());

        if (userDefaultCurrency == Constants.SERVICE_CURRENCY) {
            return new ResponseEntity<>(carInfoList, HttpStatus.OK);
        }

        carInfoList = this.priceConversionForCars(carInfoList, userDefaultCurrency.name());

        //TODO: fix duplicate car locations

        //CarFactory carFactory = new CarFactory();
        //availableCarList = carFactory.randomUpdateCarLocations(availableCarList);

        //availableCarList = this.repository.saveAll(availableCarList);

        return new ResponseEntity<>(carInfoList, HttpStatus.OK);
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity getCar(@PathVariable Long id){
        String userEmail = super.getAuthentication().getName();

        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(), "Invalid user");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        Optional<Car> optionalCar = this.repository.findById(id);

        if (!optionalCar.isPresent()) {
            throw new CarNotFoundException(id);
        }

        Car car = optionalCar.get();

        boolean isCarAvailable = this.checkForCarBooking(car);

        if (!isCarAvailable) {
            GenericResponse response = new GenericResponse(HttpStatus.FORBIDDEN.value(), "Car is not available");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        CarInfo carInfo = this.convertCarToCarInfo(car);

        Currency userDefaultCurrency = user.getDefaultCurrency();
        log.info(userDefaultCurrency.toString());

        if (userDefaultCurrency == Constants.SERVICE_CURRENCY) {
            return new ResponseEntity<>(carInfo, HttpStatus.OK);
        }

        carInfo = this.priceConversionForCar(carInfo, userDefaultCurrency.name());

        return new ResponseEntity<>(carInfo, HttpStatus.OK);
    }

    @PostMapping("/cars")
    public ResponseEntity saveCar(@RequestBody Car car){
        String userEmail = super.getAuthentication().getName();

        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        //TODO: implement user roles

        long userId = user.getId();

        if (userId != 1L) {
            GenericResponse response = new GenericResponse(HttpStatus.FORBIDDEN.value(),"Request forbidden");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Car newCar = this.repository.save(car);

        return new ResponseEntity<>(newCar, HttpStatus.CREATED);
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity updateCar(@RequestBody Car newCar, @PathVariable Long id){
        String userEmail = super.getAuthentication().getName();

        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        //TODO: implement user roles

        long userId = user.getId();

        if (userId != 1L) {
            GenericResponse response = new GenericResponse(HttpStatus.FORBIDDEN.value(),"Request forbidden");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Optional<Car> optionalCar = this.repository.findById(id);

        if (!optionalCar.isPresent()) {
            newCar.setId(id.longValue());

            newCar = this.repository.save(newCar);

            return new ResponseEntity<>(newCar, HttpStatus.OK);
        }

        Car car = optionalCar.get();
        car.setType(newCar.getType());
        car.setLatitude(newCar.getLatitude());
        car.setLongitude(newCar.getLongitude());

        car = this.repository.save(car);

        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity deleteCar(@PathVariable Long id){
        String userEmail = super.getAuthentication().getName();

        Optional<User> optUser = this.userRepository.findOneByEmail(userEmail);

        if (!optUser.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.BAD_REQUEST.value(),"Invalid user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = optUser.get();

        //TODO: implement user roles

        long userId = user.getId();

        if (userId != 1L) {
            GenericResponse response = new GenericResponse(HttpStatus.FORBIDDEN.value(),"Request forbidden");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Optional<Car> optionalCar = this.repository.findById(id);

        if (!optionalCar.isPresent()) {
            GenericResponse response = new GenericResponse(HttpStatus.NOT_FOUND.value(), "Car " + id.toString() + " not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        this.repository.deleteById(id);

        GenericResponse response = new GenericResponse(HttpStatus.OK.value(), "Car " + id.toString() + " deleted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected boolean checkForCarBooking(Car car) {
        List<Rental> rentalsForCar = this.rentalRepository.findByCar(car);
        boolean isCarAvailable = true;

        for (Rental rental : rentalsForCar) {
            if (rental.getEndDate() == null) {
                isCarAvailable = false;
            }
        }

        return isCarAvailable;
    }

    protected CarInfo convertCarToCarInfo(Car car) {
        CarInfo carInfo = new CarInfo();
        carInfo.setId(Long.valueOf(car.getId()));
        carInfo.setType(car.getType());
        carInfo.setLatitude(car.getLatitude());
        carInfo.setLongitude(car.getLongitude());
        BigDecimal pricePerHourBigDecimal = BigDecimal.valueOf(Constants.PRICE_PER_SECOND * 3600);
        carInfo.setPricePerHour(pricePerHourBigDecimal);

        return carInfo;
    }

    protected List<CarInfo> convertCarsToCarInfos(List<Car> cars) {
        List<CarInfo> carInfoList = new Vector<>();

        for(Car car : cars) {
            CarInfo carInfo = this.convertCarToCarInfo(car);
            carInfoList.add(carInfo);
        }

        return carInfoList;
    }

    protected CarInfo priceConversionForCar(CarInfo carInfo, String outputCurrency) {
        Float value = Float.valueOf(carInfo.getPricePerHour().floatValue());
        Float convertedValue = null;

        SoapConvertCurrencyConnector currencyConnector;

        try {
            currencyConnector = new SoapConvertCurrencyConnector();
            convertedValue = currencyConnector.convertCurrency(value, Constants.SERVICE_CURRENCY.name(), outputCurrency);
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }

        if (convertedValue == null) {
            return carInfo;
        }

        BigDecimal convertedPrice = BigDecimal.valueOf(convertedValue);
        carInfo.setPricePerHour(convertedPrice);

        return carInfo;
    }

    protected List<CarInfo> priceConversionForCars(List<CarInfo> carInfos, String currency) {
        List<CarInfo> carInfoList = new Vector<>();

        for(CarInfo carInfo : carInfos) {
            CarInfo updatedCarInfo = this.priceConversionForCar(carInfo, currency);
            carInfoList.add(updatedCarInfo);
        }

        return carInfoList;
    }
}
