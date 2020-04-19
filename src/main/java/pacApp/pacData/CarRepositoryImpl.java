package pacApp.pacData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pacApp.pacModel.Car;

import java.util.List;
import java.util.Vector;

public class CarRepositoryImpl implements CarRepositoryExtension {

    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
    @Autowired
    CarRepository repository;

    @Override
    public List<Long> getCarIds() {
        List<Car> carList = this.repository.findAll();

        if (carList == null) {
            return new Vector<Long>();
        }

        List<Long> carIdList = new Vector<Long>();

        for(Car car : carList) {
            carIdList.add(Long.valueOf(car.getId()));
        }

        return carIdList;
    }
}
