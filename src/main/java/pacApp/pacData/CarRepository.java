package pacApp.pacData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pacApp.pacModel.Car;

public interface CarRepository extends JpaRepository<Car, Long>, CarRepositoryExtension {
    List<Car> findByType(String type);
	Car findById(long id);
}
