package pacApp.pacData;

import org.springframework.data.jpa.repository.JpaRepository;
import pacApp.pacModel.Rental;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Rental findById(long id);
    //List<Rental> findByUser(User user);
}
