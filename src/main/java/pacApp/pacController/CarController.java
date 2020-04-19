package pacApp.pacController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import pacApp.pacException.CarNotFoundException;
import pacApp.pacLogic.Constants;
import pacApp.pacModel.Car;
import pacApp.pacModel.Rental;
import pacApp.pacModel.request.CarInfo;
import pacApp.pacModel.response.GenericResponse;

@RestController
@Api(value = "CarController", description = "Operations pertaining to Cars in Car Management System")
public class CarController {
    private final CarRepository repository;
    private final RentalRepository rentalRepository;

    public CarController(CarRepository repository, RentalRepository rentalRepository){
        this.repository = repository;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping("/cars")
    public List<CarInfo> getAllCars(){
        List<Car> carList = this.repository.findAll();

        List<Car> availableCarList = new Vector<Car>();

        for (Car car : carList) {
            boolean isCarAvailable = this.checkForCarBooking(car);

            if (isCarAvailable) {
                availableCarList.add(car);
            }
        }

        //TODO: fix duplicate car locations

        //CarFactory carFactory = new CarFactory();
        //availableCarList = carFactory.randomUpdateCarLocations(availableCarList);

        //availableCarList = this.repository.saveAll(availableCarList);

        List<CarInfo> carInfoList = this.convertCarsToCarInfos(availableCarList);

        return carInfoList;
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity getCar(@PathVariable Long id){
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

        return new ResponseEntity<>(carInfo, HttpStatus.OK);
    }

    @PostMapping("/cars")
    public Car saveCar(@RequestBody Car car){
        return this.repository.save(car);
    }

    @PutMapping("/cars/{id}")
    public Car updateCar(@RequestBody Car newCar, @PathVariable Long id){
        return this.repository.findById(id).map(car -> {
            car.setType(newCar.getType());
            return this.repository.save(car);
        }).orElseGet(() -> {
            newCar.setId(id);
            return repository.save(newCar);
        });
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity deleteCar(@PathVariable Long id){
        //TODO: check for user role
        //this.repository.deleteById(id);

        GenericResponse response = new GenericResponse(HttpStatus.NOT_FOUND.value(), "Not found");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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
}
