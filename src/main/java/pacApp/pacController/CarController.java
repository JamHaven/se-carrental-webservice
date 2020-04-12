package pacApp.pacController;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import pacApp.pacData.CarRepository;
import pacApp.pacException.CarNotFoundException;
import pacApp.pacModel.Car;

@RestController
@Api(value = "CarController", description = "Operations pertaining to Cars in Car Management System")
public class CarController {
    private final CarRepository repository;

    public CarController(CarRepository repository){
        this.repository = repository;
    }

    @GetMapping("/cars")
    public List<Car> getAllCars(){
        return this.repository.findAll();
    }

    @GetMapping("/cars/{id}")
    public Car getCar(@PathVariable Long id){
        return this.repository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
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
    public void deleteCar(@PathVariable Long id){
        this.repository.deleteById(id);
    }
}
