package autoservice.adapter.ui.common.editors;

import autoservice.model.Car;

public interface CarsEditor {
    void showCreateCar();

    void showEditCar(Car car);

    void showDeleteCar(Car car);

    void showChangeCarStatus(Car car);
}
