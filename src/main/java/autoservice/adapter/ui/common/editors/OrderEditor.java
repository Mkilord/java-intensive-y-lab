package autoservice.adapter.ui.common.editors;

import autoservice.adapter.service.MyOrderService;
import autoservice.model.Order;

public interface OrderEditor {
    <T extends Order> void  showDelete(T order, MyOrderService<T> orderService);

    <T extends Order> void showChangeStatus(T order, MyOrderService<T> orderService);
}
