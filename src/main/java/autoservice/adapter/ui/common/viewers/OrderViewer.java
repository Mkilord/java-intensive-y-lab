package autoservice.adapter.ui.common.viewers;

import autoservice.model.Order;

import java.util.List;

public interface OrderViewer {
    void showOrders(List<Order> orders);

    void selectOrder(List<Order> orders);

    void showOrdersMenu(List<Order> orders);

    void showCancelOrder(Order order);

    void showOrderOptions(Order order);

    void showOrderSortMenu(List<Order> orders);

    void showOrderSearch(List<Order> orders);
}
