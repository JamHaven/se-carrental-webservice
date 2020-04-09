package pacApp.pacData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import pacApp.pacModel.Car;

@Configuration
@Slf4j
public class InitTestData {
  private static final Logger log = LoggerFactory.getLogger(InitTestData.class);

  @Bean
  CommandLineRunner initTestDatabase(CarRepository repository) {  
	    return (args) -> {
	    	Car car1 = new Car(1L,"Car1");
	    	car1.setLatitude(48.208998);
	    	car1.setLongitude(16.373483);
	    	repository.save(car1);

			Car car2 = new Car(2L, "Car2");
			car2.setLatitude(48.217627);
			car2.setLongitude(16.395179);
			repository.save(car2);

			Car car3 = new Car(3L, "Car3");
			car3.setLatitude(48.158457);
			car3.setLongitude(16.382779);
			repository.save(car3);
	    };
  }
}
