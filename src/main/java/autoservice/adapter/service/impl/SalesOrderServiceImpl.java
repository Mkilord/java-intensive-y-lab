package autoservice.adapter.service.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.NotFoundException;
import autoservice.domen.model.SalesOrder;
import autoservice.domen.model.enums.CarState;
import autoservice.domen.model.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesOrderServiceImpl implements MyOrderService<SalesOrder> {
    OrderRepository orderRepo;
    CarRepository carRepo;

    @Override
    public SalesOrder getById(int id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public SalesOrder getEntityByFilter(Predicate<SalesOrder> predicate) {
        return orderRepo.findByFilter(predicate).findFirst()
                .orElseThrow(() -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public List<SalesOrder> getAll() {
        var orders = orderRepo.findAll().toList();
        if (orders.isEmpty()) throw new NotFoundException(NotFoundException.MSG);
        return orders;
    }

    @Override
    public List<SalesOrder> getEntitiesByFilter(Predicate<SalesOrder> predicate) {
        return orderRepo.findByFilter(predicate).toList();
    }

    @Override
    public void changeStatus(SalesOrder order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        orderRepo.update(order);
    }

    @Override
    public SalesOrder create(SalesOrder order) {
        var car = order.getCar();
        car.setState(CarState.FOR_SALE);
        order.setStatus(OrderStatus.CANCEL);
        carRepo.update(car);
        return orderRepo.create(order)
                .orElseThrow(() -> new RuntimeException("Не удалось создать заказ!"));
    }

    @Override
    public void delete(SalesOrder order) {
        var exist = orderRepo.existsById(order.getId());
        if (!exist) {
            throw new NotFoundException("Заказ для удаления не найден");
        }
    }

    @Override
    public void update(SalesOrder order) {
        var exist = orderRepo.existsById(order.getId());
        if (!exist) {
            throw new NotFoundException("Заказ для обновления не найден");
        }
        orderRepo.update(order);
    }
}
