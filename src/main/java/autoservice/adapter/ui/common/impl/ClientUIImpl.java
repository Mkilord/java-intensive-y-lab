package autoservice.adapter.ui.common.impl;

import autoservice.adapter.service.CarService;
import autoservice.adapter.service.SalesOrderService;
import autoservice.adapter.service.ServiceOrderService;
import autoservice.adapter.service.impl.AuditService;
import autoservice.adapter.ui.components.menu.Menu;
import autoservice.adapter.ui.components.menu.SelectAction;
import autoservice.adapter.ui.components.utils.ModalMenu;
import autoservice.adapter.ui.components.utils.Viewer;
import autoservice.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_ACTION;
import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_VIEW;
import static autoservice.adapter.ui.components.menu.SelectAction.*;

public class ClientUIImpl extends AppUIImpl {

    public ClientUIImpl(Scanner in, CarService carService, SalesOrderService salesOrderService, ServiceOrderService serviceOrderService, User loggedInUser) {
        super(in, carService, salesOrderService, serviceOrderService, loggedInUser);
    }

    @Override
    public void showMainMenu() {
        var menu = Menu.create("Cars", "Profile", "Orders", "Logout")
                .withHeader("Select option:");
        create(() -> {
                    super.showCars(carService.getCarsByFilter(car -> car.getState().equals(CarState.FOR_SALE)));
                    showCarsMenu();
                    return CONTINUE;
                }, () -> {
                    super.showProfile();
                    return CONTINUE;
                }, () -> {
                    showOrders(new ArrayList<>(salesOrderService.getOrdersByFilter(order -> order.getCustomer().equals(loggedInUser)
                            && order.getStatus().equals(OrderStatus.IN_PROGRESS))));
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showOrdersMenu(List<Order> orders) {
        var menu = Menu.create("Select", "Sort", "Search", GO_BACK_VIEW)
                .withHeader("Select function: ");
        create(() -> {
                    selectOrder(orders);
                    return EXIT;
                }, () -> {
                    showOrderSortMenu(orders);
                    return CONTINUE;
                }, () -> {
                    showOrderSearch(orders);
                    return CONTINUE;
                },
                () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showOrderOptions(Order order) {
        Menu.create("Cancel", GO_BACK_VIEW).print();
        SelectAction.create(() -> {
            showCancelOrder(order);
            return EXIT;
        }, () -> EXIT).read(in);
    }

    @Override
    public void showOrderSortMenu(List<Order> orders) {
        if (orders.isEmpty()) {
            System.out.println("Nothing was found for this query!");
            return;
        }
        var menu = Menu.create(
                "by id",
                "by date",
                "by car model",
                GO_BACK_VIEW
        ).withHeader("Select filter by:");
        SelectAction.create(() -> {
            orders.sort(Comparator.comparingInt(Order::getId));
            Viewer.viewAll(orders, "");
            return CONTINUE;
        }, () -> {
            orders.sort(Comparator.comparing(Order::getDate));
            Viewer.viewAll(orders, "");
            return CONTINUE;
        }, () -> {
            orders.sort(Comparator.comparing(order -> order.getCar().getModel()));
            Viewer.viewAll(orders, "");
            return CONTINUE;
        }, GO_BACK_ACTION).readInCycle(in, menu);
    }

    @Override
    public void showCarsMenu() {
        var menu = Menu.create("Select", "Sort", "Search", GO_BACK_VIEW)
                .withHeader("Select function: ");
        create(() -> {
                    selectCar(carService.getCarsByFilter(car -> car.getState().equals(CarState.FOR_SALE)));
                    return EXIT;
                }, () -> {
                    showCarSortMenu(new ArrayList<>(carService.getCarsByFilter(car -> car.getState().equals(CarState.FOR_SALE))));
                    return CONTINUE;
                }, () -> {
                    showCarSearch(carService.getCarsByFilter(car -> car.getState().equals(CarState.FOR_SALE)));
                    return CONTINUE;
                }, GO_BACK_ACTION
        ).readInCycle(in, menu);
    }

    @Override
    public void showCarOptions(Car car) {
        Menu.create("Create order", GO_BACK_VIEW).withHeader("Select options:").print();
        SelectAction.create(() -> {
            var salesOrder = new SalesOrder(loggedInUser, car);
            System.out.println("Your order:");
            Viewer.viewOf(car);
            ModalMenu.getYesOrNoDialog(in, "Create order?", () -> {
                salesOrderService.add(salesOrder);
                System.out.println("Your order created!");
                AuditService.logAction(loggedInUser.getUsername(), AuditAction.CREATE_ORDER, salesOrder.getView());
                return EXIT;
            }, () -> EXIT);
            return EXIT;
        }, GO_BACK_ACTION).read(in);
    }

    @Override
    public void showCarSortMenu(List<Car> cars) {
        if (cars.isEmpty()) {
            System.out.println("Nothing was found for this query!");
            return;
        }
        var menu = Menu.create(
                "by id",
                "by make",
                "by model",
                "by year",
                "by price",
                GO_BACK_VIEW
        ).withHeader("Select filter by:");
        SelectAction.create(
                () -> {
                    cars.sort(Comparator.comparingInt(Car::getId));
                    Viewer.viewAll(cars, "");
                    return CONTINUE;
                }, () -> {
                    cars.sort(Comparator.comparing(Car::getMake));
                    Viewer.viewAll(cars, "");
                    return CONTINUE;
                }, () -> {
                    cars.sort(Comparator.comparing(Car::getModel));
                    Viewer.viewAll(cars, "");
                    return CONTINUE;
                }, () -> {
                    cars.sort(Comparator.comparingInt(Car::getYear));
                    Viewer.viewAll(cars, "");
                    return CONTINUE;
                }, () -> {
                    cars.sort(Comparator.comparingLong(Car::getPrice));
                    Viewer.viewAll(cars, "");
                    return CONTINUE;
                }, GO_BACK_ACTION).readInCycle(in, menu);
    }
}
