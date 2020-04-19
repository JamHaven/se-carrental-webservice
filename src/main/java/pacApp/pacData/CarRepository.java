package pacApp.pacData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pacApp.pacModel.Car;
import pacApp.pacModel.CarType;

public interface CarRepository extends JpaRepository<Car, Long>, CarRepositoryExtension {
    List<Car> findByType(CarType type);
	Car findById(long id);
}
