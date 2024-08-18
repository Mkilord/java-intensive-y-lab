package autoservice.adapter.service;

import autoservice.model.Order;

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

    boolean add(T order);

    boolean delete(T order);

    void complete(T order);

    void cancel(T order);

    void inProgress(T order);

    Optional<T> getOrderByFilter(Predicate<T> predicate);

    List<T> getAllOrders();

    List<T> getOrdersByFilter(Predicate<T> predicate);
}
