package autoservice.adapter.ui.common.viewers;

import autoservice.adapter.service.MyOrderService;
import autoservice.model.Order;

import java.util.List;

public interface OrderViewer {
    <T extends Order> void showOrders(List<T> orders, MyOrderService<T>orderService);

    <T extends Order> void selectOrder(List<T> orders, MyOrderService<T> orderService);

    <T extends Order> void showOrdersMenu(List<T> orders,MyOrderService<T> orderService);

    <T extends Order> void showCancelOrder(T order, MyOrderService<T> orderService);

    <T extends Order> void showOrderOptions(T order, MyOrderService<T> orderService);

    <T extends Order> void showOrderSortMenu(List<T> orders);

    <T extends Order> void showOrderSearch(List<T> orders);
}
