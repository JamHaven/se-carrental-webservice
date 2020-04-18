package pacApp.pacData;

import pacApp.pacModel.Car;
import pacApp.pacModel.CarType;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class CarFactory {

    public CarFactory() { }

    public List<Car> buildCars() {
        List<Car> carList = new Vector<Car>();

        Car car1 = new Car();
        car1.setType(CarType.SMALL);
        car1.setLatitude(48.208998);
        car1.setLongitude(16.373483);

        carList.add(car1);

        Car car2 = new Car();
        car2.setType(CarType.MEDIUM);
        car2.setLatitude(48.217627);
        car2.setLongitude(16.395179);

        carList.add(car2);

        Car car3 = new Car();
        car3.setType(CarType.LARGE);
        car3.setLatitude(48.158457);
        car3.setLongitude(16.382779);

        carList.add(car3);

        Car car4 = new Car();
        car4.setType(CarType.SMALL);
        car4.setLatitude(48.208998);
        car4.setLongitude(16.373483);

        carList.add(car4);

        Car car5 = new Car();
        car5.setType(CarType.MEDIUM);
        car5.setLatitude(48.217627);
        car5.setLongitude(16.395179);

        carList.add(car5);

        Car car6 = new Car();
        car6.setType(CarType.LARGE);
        car6.setLatitude(48.158457);
        car6.setLongitude(16.382779);

        carList.add(car6);

        Car car7 = new Car();
        car7.setType(CarType.SMALL);
        car7.setLatitude(48.186979);
        car7.setLongitude(16.313139);

        carList.add(car7);

        Car car8 = new Car();
        car8.setType(CarType.MEDIUM);
        car8.setLatitude(48.216768);
        car8.setLongitude(16.312439);

        carList.add(car8);

        Car car9 = new Car();
        car9.setType(CarType.LARGE);
        car9.setLatitude(48.211625);
        car9.setLongitude(16.357607);

        carList.add(car9);

        Car car10 = new Car();
        car10.setType(CarType.SMALL);
        car10.setLatitude(48.231808);
        car10.setLongitude(16.414041);

        carList.add(car10);

        Car car11 = new Car();
        car11.setType(CarType.MEDIUM);
        car11.setLatitude(48.209568);
        car11.setLongitude(16.343960);

        carList.add(car11);

        Car car12 = new Car();
        car12.setType(CarType.LARGE);
        car12.setLatitude(48.202584);
        car12.setLongitude(16.368626);

        carList.add(car12);

        Car car13 = new Car();
        car13.setType(CarType.SMALL);
        car13.setLatitude(48.208173);
        car13.setLongitude(16.366311);

        carList.add(car13);

        Car car14 = new Car();
        car14.setType(CarType.MEDIUM);
        car14.setLatitude(48.256830);
        car14.setLongitude(16.340091);

        carList.add(car14);

        Car car15 = new Car();
        car15.setType(CarType.LARGE);
        car15.setLatitude(48.208045);
        car15.setLongitude(16.386532);

        carList.add(car15);

        Car car16 = new Car();
        car16.setType(CarType.SMALL);
        car16.setLatitude(48.221126);
        car16.setLongitude(16.265881);

        carList.add(car16);

        Car car17 = new Car();
        car17.setType(CarType.MEDIUM);
        car17.setLatitude(48.251458);
        car17.setLongitude(16.299048);

        carList.add(car17);

        Car car18 = new Car();
        car18.setType(CarType.LARGE);
        car18.setLatitude(48.249308);
        car18.setLongitude(16.386448);

        carList.add(car18);

        Car car19 = new Car();
        car19.setType(CarType.SMALL);
        car19.setLatitude(48.122099);
        car19.setLongitude(16.561523);

        carList.add(car19);

        Car car20 = new Car();
        car20.setType(CarType.MEDIUM);
        car20.setLatitude(48.200161);
        car20.setLongitude(16.350621);

        carList.add(car20);

        Car car21 = new Car();
        car21.setType(CarType.LARGE);
        car21.setLatitude(48.201620);
        car21.setLongitude(16.363111);

        carList.add(car21);

        Car car22 = new Car();
        car22.setType(CarType.SMALL);
        car22.setLatitude(48.212784);
        car22.setLongitude(16.368522);

        carList.add(car22);

        Car car23 = new Car();
        car23.setType(CarType.MEDIUM);
        car23.setLatitude(48.202864);
        car23.setLongitude(16.390123);

        carList.add(car23);

        Car car24 = new Car();
        car24.setType(CarType.LARGE);
        car24.setLatitude(48.234886);
        car24.setLongitude(16.327617);

        carList.add(car24);

        Car car25 = new Car();
        car25.setType(CarType.SMALL);
        car25.setLatitude(48.177812);
        car25.setLongitude(16.271076);

        carList.add(car25);

        return carList;
    }

    public List<Car> buildNewCars(List<Car> existingCarList) {
        List<Car> newCarList = new Vector<>();
        List<Car> carList = this.buildCars();

        for (Car car : carList) {
            boolean isNew = true;
            for (Car existingCar : existingCarList) {
                if (existingCar.equals(car)) {
                    isNew = false;
                }
            }

            if (isNew) {
                newCarList.add(car);
            }
        }

        return newCarList;
    }

    public List<Car> buildNewRandomCars(List<Car> existingCarList) {
        List<Car> newCarList = this.buildNewCars(existingCarList);
        List<Car> newRandomCars = new Vector<>();

        int upperBound = newCarList.size();
        int lowerBound = 1;

        Random rn = new Random();
        int count = rn.nextInt(upperBound - lowerBound + 1) + lowerBound;
        int i = 0;

        while (i < count) {
            int index = rn.nextInt(upperBound);
            Car car = newCarList.get(index);
            newRandomCars.add(car);
            i++;
        }

        return newRandomCars;
    }

    public Car buildNewRandomCar(List<Car> existingCarList) {
        List<Car> newCarList = this.buildNewCars(existingCarList);
        //List<Car> newCarList = this.buildNewRandomCars(existingCarList);

        int upperBound = newCarList.size();

        Random rn = new Random();
        int randomIndex = rn.nextInt(upperBound);

        return newCarList.get(randomIndex);
    }

    public List<Car> randomUpdateCarLocations(List<Car> carList) {
        List<Car> newRandomCars = this.buildNewRandomCars(carList);
        newRandomCars = this.filterUniqueCarLocation(carList, newRandomCars);

        int upperBound = carList.size();

        Random rn = new Random();

        for (Car randomCar : newRandomCars) {
            int index = rn.nextInt(upperBound);
            Car car = carList.get(index);

            car.setLatitude(randomCar.getLatitude());
            car.setLongitude(randomCar.getLongitude());
        }

        return carList;
    }

    public List<Car> filterUniqueCarLocation(List<Car> carList, List<Car> newCarList) {
        List<Car> filteredCarList = new Vector<>();

        for (Car newCar : newCarList) {
            boolean isLocationEqual = false;

            for (Car car : carList) {
                if (car.equalLocation(newCar)) {
                    isLocationEqual = true;
                }
            }

            if (!isLocationEqual) {
                filteredCarList.add(newCar);
            }
        }

        return filteredCarList;
    }
}
