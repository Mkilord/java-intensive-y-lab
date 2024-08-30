package autoservice.domen.model;

import autoservice.domen.model.enums.OrderStatus;

import java.time.LocalDate;

public class ServiceOrder extends Order {

    public ServiceOrder(int id, LocalDate date, OrderStatus status, User customer, Car car) {
        super(id, date, status, customer, car);
    }
}
