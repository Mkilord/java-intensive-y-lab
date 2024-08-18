package autoservice.adapter.service.impl;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.OrderException;
import autoservice.adapter.service.RoleException;
import autoservice.model.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class SalesOrderServiceImpl implements MyOrderService<SalesOrder> {
    private final OrderRepository orderRepo;
    private final CarRepository carRepo;


    public SalesOrderServiceImpl(OrderRepository orderRepo, CarRepository carRepo) {
        this.orderRepo = orderRepo;
        this.carRepo = carRepo;
    }


    @Override
    public boolean add(Role role, SalesOrder order) throws RoleException, OrderException {
        if (role != Role.CLIENT) {
            throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        }
        var car = order.getCar();
        if (car.getState() != CarState.FOR_SALE) {
            throw new OrderException(OrderException.INVALID_CAR_ERROR_MSG);
        }
        car.setState(CarState.SOLD);
        carRepo.update(car);
        return orderRepo.create(order);
    }


    @Override
    public boolean delete(Role role, SalesOrder order) throws RoleException {
        if (role != Role.ADMIN) throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        return orderRepo.delete(order);
    }

    @Override
    public void complete(Role role, SalesOrder order) throws RoleException {
        if (role != Role.MANAGER && role != Role.ADMIN) {
            throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        }
        order.setStatus(OrderStatus.COMPLETE);
        orderRepo.update(order);
    }


    @Override
    public void cancel(SalesOrder order) {
        var car = order.getCar();
        car.setState(CarState.FOR_SALE);
        order.setStatus(OrderStatus.CANCEL);
        orderRepo.update(order);
        carRepo.update(car);
    }


    @Override
    public void inProgress(Role role, SalesOrder order) throws RoleException {
        if (role != Role.MANAGER && role != Role.ADMIN) {
            throw new RoleException(RoleException.PERMISSION_ERROR_MSG);
        }
        var car = order.getCar();
        car.setState(CarState.SOLD);
        order.setStatus(OrderStatus.IN_PROGRESS);
        carRepo.update(car);
        orderRepo.update(order);
    }


    @Override
    public List<SalesOrder> getAllOrders(User user) {
        var role = user.getRole();
        if (role == Role.CLIENT) {
            return orderRepo.findByFilter(order -> order.getCustomer().getId() == user.getId())
                    .toList();
        }
        return orderRepo.findAll();
    }

    @Override
    public Optional<SalesOrder> getOrderByFilter(User user, Predicate<SalesOrder> predicate) {
        var role = user.getRole();
        if (role == Role.CLIENT) {
            return orderRepo.findByFilter(((Predicate<SalesOrder>) salesOrder -> salesOrder.getCustomer().getId() == user.getId())
                            .and(predicate))
                    .findFirst();
        }
        return orderRepo.findByFilter(predicate).findFirst();
    }

    @Override
    public List<SalesOrder> getOrdersByFilter(User user, Predicate<SalesOrder> predicate) {
        var role = user.getRole();
        if (role == Role.CLIENT) {
            return orderRepo.findByFilter(((Predicate<SalesOrder>) salesOrder -> salesOrder.getId() == user.getId())
                            .and(predicate))
                    .toList();
        }
        return orderRepo.findByFilter(predicate).toList();
    }
}
