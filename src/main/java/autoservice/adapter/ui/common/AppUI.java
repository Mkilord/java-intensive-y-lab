package autoservice.adapter.ui.common;

import autoservice.adapter.ui.common.viewers.CarsViewer;
import autoservice.adapter.ui.common.viewers.OrderViewer;

public interface AppUI extends CarsViewer, OrderViewer {
    void showMainMenu();

    void showProfile();
}
