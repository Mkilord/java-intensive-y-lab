package autoservice.adapter.service.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.NotFoundException;
import autoservice.domen.model.ServiceOrder;
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
public class ServiceOrderServiceImpl implements MyOrderService<ServiceOrder> {
    ServiceOrderRepository orderRepo;
    CarRepository carRepo;

    @Override
    public ServiceOrder getById(int id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public ServiceOrder getEntityByFilter(Predicate<ServiceOrder> predicate) {
        return orderRepo.findByFilter(predicate).findFirst()
                .orElseThrow(() -> new NotFoundException(NotFoundException.MSG));
    }

    @Override
    public List<ServiceOrder> getAll() {
        var orders = orderRepo.findAll().toList();
        if (orders.isEmpty()) throw new NotFoundException(NotFoundException.MSG);
        return orders;
    }

    @Override
    public List<ServiceOrder> getEntitiesByFilter(Predicate<ServiceOrder> predicate) {
        return orderRepo.findByFilter(predicate).toList();
    }

    @Override
    public void changeStatus(ServiceOrder order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        orderRepo.update(order);
    }

    @Override
    public ServiceOrder create(ServiceOrder order) {
        var car = order.getCar();
        car.setState(CarState.FOR_SALE);
        order.setStatus(OrderStatus.CANCEL);
        carRepo.update(car);
        return orderRepo.create(order)
                .orElseThrow(() -> new RuntimeException("Не удалось создать заказ на обслуживание!"));
    }

    @Override
    public void delete(ServiceOrder order) {
        var exist = orderRepo.existsById(order.getId());
        if (!exist) {
            throw new NotFoundException("Заказ на обслуживание для удаления не найден");
        }
    }

    @Override
    public void update(ServiceOrder order) {
        var exist = orderRepo.existsById(order.getId());
        if (!exist) {
            throw new NotFoundException("Заказ на обслуживание для обновления не найден");
        }
        orderRepo.update(order);
    }
}
