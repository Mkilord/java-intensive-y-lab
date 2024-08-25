package autoservice.adapter.controller;

import autoservice.adapter.service.CarService;
import autoservice.domen.dto.CarRequest;
import autoservice.domen.dto.CarResponse;
import autoservice.domen.dto.SearchRequest;
import autoservice.domen.dto.mapper.CarMapper;
import autoservice.domen.model.enums.CarState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Автомобили")
@Secured({"ADMIN", "MANAGER"})
@RequestMapping("/cars")
public class CarController {

    CarMapper carMapper;
    CarService carService;

    @GetMapping("/getAllCars")
    @Operation(summary = "Получение списка всех автомобилей")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<CarResponse>> getAllCars() {
        var cars = carService.getAll();
        var carResponses = carMapper.toResponseList(cars);
        return ResponseEntity.ok(carResponses);
    }

    @GetMapping("/getCarsForSale")
    @Operation(summary = "Получение списка автомобилей доступных для покупки")
    @Secured({"ADMIN", "MANAGER", "CLIENT"})
    public ResponseEntity<List<CarResponse>> getCarsForSale() {
        var cars = carService.getEntitiesByFilter(car -> car.getState().equals(CarState.FOR_SALE));
        var carResponses = carMapper.toResponseList(cars);
        return ResponseEntity.ok(carResponses);
    }

    @PostMapping("/getSearch")
    @Operation(summary = "Получение списка автомобилей по строке поиска")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<CarResponse>> getCarsByFilter(@RequestBody @Valid SearchRequest searchRequest) {
        var searchSource = searchRequest.getSearchSource();
        var cars = carService.getByString(carService.getAll(), searchSource);
        var carResponses = carMapper.toResponseList(cars);
        return ResponseEntity.ok(carResponses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение автомобиля по ID")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<CarResponse> getCarById(@PathVariable int id) {
        var car = carService.getById(id);
        var carResponse = carMapper.toResponse(car);
        return ResponseEntity.ok(carResponse);
    }

    @GetMapping("/forSale/{id}")
    @Operation(summary = "Получение автомобиля для продажи по ID")
    @Secured({"CLIENT", "ADMIN", "MANAGER"})
    public ResponseEntity<CarResponse> getCarForSaleById(@PathVariable int id) {
        var car = carService.getById(id);
        if (car.getState() != CarState.FOR_SALE) return ResponseEntity.notFound().build();
        var carResponse = carMapper.toResponse(car);
        return ResponseEntity.ok(carResponse);
    }

    @PostMapping
    @Operation(summary = "Создание нового автомобиля")
    @Secured("ADMIN")
    public ResponseEntity<CarResponse> createCar(@RequestBody @Valid CarRequest carRequest) {
        var car = carMapper.toEntity(carRequest);
        var createdCar = carService.create(car);
        var carResponse = carMapper.toResponse(createdCar);
        return ResponseEntity.ok(carResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации об автомобиле")
    @Secured("ADMIN")
    public ResponseEntity<Void> updateCar(@PathVariable int id, @RequestBody @Valid CarRequest carRequest) {
        var car = carMapper.toEntity(carRequest);
        car.setId(id);
        carService.update(car);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление автомобиля по ID")
    @Secured("ADMIN")
    public ResponseEntity<Void> deleteCar(@PathVariable int id) {
        var car = carService.getById(id);
        carService.delete(car);
        return ResponseEntity.noContent().build();
    }
}
