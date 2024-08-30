package autoservice.adapter.service;

import autoservice.domen.model.Car;
import autoservice.domen.model.enums.CarState;
import autoservice.domen.model.enums.Role;
import autoservice.domen.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface CarService extends EntityService<Car>{ }


