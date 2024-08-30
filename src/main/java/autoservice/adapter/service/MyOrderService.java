package autoservice.adapter.service;

import autoservice.domen.model.Order;
import autoservice.domen.model.enums.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

public interface MyOrderService<T extends Order> extends EntityService<T> {
    void changeStatus(T order, OrderStatus newStatus);
    @Override
    default List<T> getByString(List<T> orders, String searchString) {
        return orders.stream()
                .filter(order -> {
                    var matchesId = String.valueOf(order.getId()).equals(searchString);
                    var matchesCustomerName = order.getCustomer().getName().equalsIgnoreCase(searchString);
                    var matchesStatus = order.getStatus().toString().equalsIgnoreCase(searchString);
                    var matchesDate = order.getDate().toString().contains(searchString);

                    return matchesId || matchesCustomerName || matchesStatus || matchesDate;
                })
                .collect(Collectors.toList());
    }

}
