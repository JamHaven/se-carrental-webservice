package pacApp.pacController;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.annotations.Api;
import pacApp.pacModel.Car;
import pacApp.pacModel.Rental;

import java.util.List;

@RestController
@Api(value = "RentalController", description = "Operations pertaining to Cars in Car Management System")
public class RentalController {
    private final RentalRepository repository;

    public RentalController(RentalRepository repository){
        this.repository = repository;
    }

    @GetMapping("/rental")
    public List<Rental> getAllRentals(){
        return this.repository.findAll();
    }

    @GetMapping("/rental/{id}")
    public Car getRental(@PathVariable Long id){
        return this.repository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));
    }

    @GetMapping("/rental/user/{id}")
    public List<Rental> getRentalForUser(@PathVariable Long id){
        return this.repository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));
    }

    @PostMapping("/rental")
    public Car saveCar(@RequestBody Rental newRental){
        //TODO: check for user object in rental object
        return this.repository.save(newRental);
    }

    @PutMapping("/rental/{id}")
    public Car updateCar(@RequestBody Rental rental, @PathVariable Long id){
        //TODO: check if rental is linked to user

        /*
        return this.repository.findById(id).map(car -> {
            car.setType(newCar.getType());
            return this.repository.save(car);
        }).orElseGet(() -> {
            newCar.setId(id);
            return repository.save(newCar);
        });
        */

        return null;
    }

    @DeleteMapping("/rental/{id}")
    public void deleteCar(@PathVariable Long id){
        //TODO: check for super user rights
        this.repository.deleteById(id);
    }
}
