package pacApp.pacData;

import org.springframework.data.jpa.repository.JpaRepository;
import pacApp.pacModel.Car;
import pacApp.pacModel.Rental;
import pacApp.pacModel.User;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Rental findById(long id);
    List<Rental> findByUser(User user);
    List<Rental> findByCar(Car car);
}
