package autoservice.adapter.service;

import autoservice.model.Order;
import autoservice.model.Role;
import autoservice.model.SalesOrder;
import autoservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface MyOrderService<T extends Order> {
    static <T extends Order> List<T> filterOrdersByString(List<T> orders, String searchString) {
        return orders.stream()
                .filter(order -> {
                    boolean matchesId = String.valueOf(order.getId()).equals(searchString);
                    boolean matchesCustomerName = order.getCustomer().getName().equalsIgnoreCase(searchString);
                    boolean matchesStatus = order.getStatus().toString().equalsIgnoreCase(searchString);
                    boolean matchesDate = order.getDate().toString().contains(searchString);

                    return matchesId || matchesCustomerName || matchesStatus || matchesDate;
                })
                .collect(Collectors.toList());
    }

    boolean add(Role role, T order) throws RoleException, OrderException;
    boolean delete(Role role, T order) throws RoleException;

    void complete(Role role, T order) throws RoleException;

    void cancel(T order);

    void inProgress(Role role, T order) throws RoleException;

    Optional<T> getOrderByFilter(User user, Predicate<T> predicate);
    List<T> getAllOrders(User user);

    List<T> getOrdersByFilter(User user, Predicate<T> predicate);
}
