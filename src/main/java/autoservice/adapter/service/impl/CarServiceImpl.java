package autoservice.adapter.service.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.service.CarService;
import autoservice.adapter.service.NotFoundException;
import autoservice.domen.model.Car;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    CarRepository carRepo;

    public List<Car> getByString(List<Car> cars, String searchString) {
        return cars.stream()
                .filter(car -> {
                    boolean matchesId = String.valueOf(car.getId()).equals(searchString);
                    boolean matchesMake = car.getMake().equalsIgnoreCase(searchString);
                    boolean matchesModel = car.getModel().equalsIgnoreCase(searchString);
                    boolean matchesYear = String.valueOf(car.getYear()).equals(searchString);
                    boolean matchesPrice = String.valueOf(car.getPrice()).equals(searchString);
                    boolean matchesState = car.getState().toString().equalsIgnoreCase(searchString);
                    return matchesId || matchesMake || matchesModel || matchesYear || matchesPrice || matchesState;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Car getById(int id) {
        return carRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public Car getEntityByFilter(Predicate<Car> predicate) {
        return carRepo.findByFilter(predicate).findFirst().orElseThrow(
                () -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public List<Car> getAll() {
        var cars = carRepo.findAll().toList();
        if (cars.isEmpty()) throw new NotFoundException(NotFoundException.MSG);
        return cars;
    }

    @Override
    public List<Car> getEntitiesByFilter(Predicate<Car> predicate) {
        return carRepo.findByFilter(predicate).toList();
    }

    @Override
    public Car create(Car car) {
        return carRepo.create(car)
                .orElseThrow(() -> new RuntimeException("Не удалось создать автомобиль!"));
    }

    @Override
    public void delete(Car car) {
        var exist = carRepo.existsById(car.getId());
        if (!exist) {
            throw new NotFoundException("Автомобиль для удаления не найден");
        }
    }

    @Override
    public void update(Car car) {
        var exist = carRepo.existsById(car.getId());
        if (!exist) {
            throw new NotFoundException("Автомобиль для обновления не найден");
        }
        carRepo.update(car);
    }
}
