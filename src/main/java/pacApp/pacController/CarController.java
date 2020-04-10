package pacApp.pacController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
import pacApp.pacModel.Session.CurrentAuthUser;

@RestController
@Scope("session")
@Api(value = "CarController", description = "Operations pertaining to Cars in Car Management System")
public class CarController {
    private final CarRepository repository;

    @Autowired
	CurrentAuthUser cau; 
    
    public CarController(CarRepository repository){
        this.repository = repository;
    }

    @GetMapping("/cars")
    public List<Car> getAllCars(){
    	if (!cau.isSessionValid())
    		return null; //TODO: create redireciton to session invalid site 
        return this.repository.findAll();
    }

    @GetMapping("/cars/{id}")
    public Car getCar(@PathVariable Long id){
    	if (!cau.isSessionValid())
    		return null; //TODO: create redireciton to session invalid site 
        return this.repository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
    }

    @PostMapping("/cars")
    public Car saveCar(@RequestBody Car car){
    	if (!cau.isSessionValid())
    		return null; //TODO: create redireciton to session invalid site 
        return this.repository.save(car);
    }

    @PutMapping("/cars/{id}")
    public Car updateCar(@RequestBody Car newCar, @PathVariable Long id){
    	if (!cau.isSessionValid())
    		return null; //TODO: create redireciton to session invalid site 
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
    	if (cau.isSessionValid())
    		this.repository.deleteById(id);
    }
}
