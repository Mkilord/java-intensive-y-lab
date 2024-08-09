package autoservice.adapter.ui.common.editors;

import autoservice.model.Order;

public interface OrderEditor {
    void showDelete(Order order);

    void showChangeStatus(Order order);
}
