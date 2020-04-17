package pacApp.pacData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import pacApp.pacModel.Car;

import java.util.List;

@Configuration
@Slf4j
public class InitTestData {
  private static final Logger log = LoggerFactory.getLogger(InitTestData.class);

  @Bean
  CommandLineRunner initTestDatabase(CarRepository repository) {  
	    return (args) -> {

			CarFactory carFactory = new CarFactory();
			List<Car> carList = carFactory.buildCars();

			int index = 0;
			long id = 1L;
			while (index < (carList.size()/4)*3) {
				Car car = carList.get(index);
				car.setId(id);
				repository.save(car);

				index++;
				id++;
			}
	    };
  }
}
