package autoservice.adapter.ui.common.viewers;

import autoservice.model.Car;

import java.util.List;

public interface CarsViewer {
    void showCars(List<Car> cars);

    void showCarsMenu();

    void showCarSearch(List<Car> cars);

    void selectCar(List<Car> cars);

    void showCarOptions(Car car);

    void showCarSortMenu(List<Car> cars);
}
