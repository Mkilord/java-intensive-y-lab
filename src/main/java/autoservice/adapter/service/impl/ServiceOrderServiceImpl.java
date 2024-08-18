package autoservice.adapter.service.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.OrderException;
import autoservice.adapter.service.RoleException;
import autoservice.model.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 */
public class ServiceOrderServiceImpl implements MyOrderService<ServiceOrder> {

    private final ServiceOrderRepository serviceOrderRepo;
    private final CarRepository carRepo;

    public ServiceOrderServiceImpl(ServiceOrderRepository serviceOrderRepo, CarRepository carRepo) {
        this.serviceOrderRepo = serviceOrderRepo;
        this.carRepo = carRepo;
    }

    @Override
    public boolean add(Role role, ServiceOrder order) throws RoleException, OrderException {
        if (role != Role.CLIENT) {
            throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        }
        var car = order.getCar();
        if (car.getState() != CarState.FOR_SALE) {
            throw new OrderException(OrderException.INVALID_CAR_ERROR_MSG);
        }
        car.setState(CarState.SOLD);
        carRepo.update(car);
        return serviceOrderRepo.create(order);
    }

    @Override
    public boolean delete(Role role, ServiceOrder order) throws RoleException {
        if (role != Role.ADMIN) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        return serviceOrderRepo.delete(order);
    }

    @Override
    public void complete(Role role, ServiceOrder order) throws RoleException {
        if (role != Role.MANAGER && role != Role.ADMIN) {
            throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        }
        order.setStatus(OrderStatus.COMPLETE);
        serviceOrderRepo.update(order);
    }

    @Override
    public void cancel(ServiceOrder order) {
        var car = order.getCar();
        car.setState(CarState.NOT_SALE);
        order.setStatus(OrderStatus.CANCEL);
        carRepo.update(car);
        serviceOrderRepo.update(order);
    }


    @Override
    public void inProgress(Role role, ServiceOrder order) throws RoleException {
        if (role != Role.MANAGER && role != Role.ADMIN) {
            throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        }
        var car = order.getCar();
        car.setState(CarState.SOLD);
        order.setStatus(OrderStatus.IN_PROGRESS);
        carRepo.update(car);
        serviceOrderRepo.update(order);
    }

    @Override
    public List<ServiceOrder> getAllOrders(User user) {
        var role = user.getRole();
        if (role == Role.CLIENT) {
            return serviceOrderRepo.findByFilter(order -> order.getCustomer().getId() == user.getId())
                    .toList();
        }
        return serviceOrderRepo.findAll();
    }

    @Override
    public List<ServiceOrder> getOrdersByFilter(User user, Predicate<ServiceOrder> predicate) {
        var role = user.getRole();
        if (role == Role.CLIENT) {
            return serviceOrderRepo.findByFilter(((Predicate<ServiceOrder>) salesOrder -> salesOrder.getCustomer().getId() == user.getId())
                            .and(predicate))
                    .toList();
        }
        return serviceOrderRepo.findByFilter(predicate).toList();
    }

    @Override
    public Optional<ServiceOrder> getOrderByFilter(User user, Predicate<ServiceOrder> predicate) {
        var role = user.getRole();
        if (role == Role.CLIENT) {
            return serviceOrderRepo.findByFilter(((Predicate<ServiceOrder>) salesOrder -> salesOrder.getCustomer().getId() == user.getId())
                            .and(predicate))
                    .findFirst();
        }
        return serviceOrderRepo.findByFilter(predicate).findFirst();
    }
}
