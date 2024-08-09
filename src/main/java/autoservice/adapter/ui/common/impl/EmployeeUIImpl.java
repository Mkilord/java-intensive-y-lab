package autoservice.adapter.ui.common.impl;

import autoservice.adapter.service.CarService;
import autoservice.adapter.service.SalesOrderService;
import autoservice.adapter.service.ServiceOrderService;
import autoservice.adapter.service.UserService;
import autoservice.adapter.service.impl.AuditService;
import autoservice.adapter.service.impl.UserServiceImpl;
import autoservice.adapter.ui.common.EmployeeUI;
import autoservice.adapter.ui.components.menu.Menu;
import autoservice.adapter.ui.components.menu.SelectAction;
import autoservice.adapter.ui.components.utils.ConsoleUtils;
import autoservice.adapter.ui.components.utils.ModalMenu;
import autoservice.adapter.ui.components.utils.Viewer;
import autoservice.model.*;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_ACTION;
import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_VIEW;
import static autoservice.adapter.ui.components.menu.SelectAction.*;
import static autoservice.adapter.ui.components.utils.ConsoleUtils.*;

public abstract class EmployeeUIImpl extends AppUIImpl implements EmployeeUI {
    protected EmployeeUIImpl(Scanner in, CarService carService,
                             SalesOrderService salesOrderService,
                             ServiceOrderService serviceOrderService,
                             UserService userService, User loggedInUser) {
        super(in, carService, salesOrderService, serviceOrderService, loggedInUser);
        this.userService = userService;
    }

    protected final UserService userService;

    @Override
    public void showCreateCar() {
        System.out.println("Input information about car:");
        System.out.println("Input car's make:");
        var make = readStr(in, NOT_BLANK_STR);
        System.out.println("Input car's model:");
        var model = readStr(in, NOT_BLANK_STR);
        System.out.println("Input car's year:");
        var year = readInt(in);
        System.out.println("Input car's price:");
        var price = readLong(in);
        Car car = new Car(make, model, year, price);
        ModalMenu.getSaveOrNoDialog(in, "Save or no?", () -> {
            carService.add(car);
            System.out.println("Car was add to System!");
            AuditService.logAction(loggedInUser.getUsername(), AuditAction.ADD_CAR, car.getView());
            return EXIT;
        }, () -> {
            System.out.println("Operation canceled!");
            return EXIT;
        });
    }

    @Override
    public void showEditCar(Car car) {
        @AllArgsConstructor
        class TempCar {
            String make, model;
            int year;
            long price;
        }
        TempCar tempCar = new TempCar(car.getMake(), car.getModel(), car.getYear(), car.getPrice());
        var menu = Menu.create(
                "make",
                "model",
                "year",
                "price",
                "-Complete").withHeader("Select option:");
        create(() -> {
                    System.out.println("Your make:");
                    tempCar.make = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    return CONTINUE;
                }, () -> {
                    System.out.println("Your model:");
                    tempCar.model = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    return CONTINUE;
                }, () -> {
                    System.out.println("Your year:");
                    tempCar.year = readInt(in);
                    return CONTINUE;
                }, () -> {
                    System.out.println("Your price:");
                    tempCar.price = (readLong(in));
                    return CONTINUE;
                }, () -> EXIT

        ).readInCycle(in, menu);
        System.out.println("New car field:");
        System.out.printf("Id: %d; make: %s; model: %s; year: %d; price: %d%n",
                car.getId(), tempCar.make, tempCar.model, tempCar.year, tempCar.price);
        ModalMenu.getSaveOrNoDialog(in, "Save or not?", () -> {
            car.setMake(tempCar.make);
            car.setModel(tempCar.model);
            car.setYear(tempCar.year);
            car.setPrice(tempCar.price);
            AuditService.logAction(loggedInUser.getUsername(), AuditAction.EDIT_CAR, "Car id: " + car.getId());
            System.out.println("Car was changed!");
            return EXIT;
        }, GO_BACK_ACTION);
    }

    @Override
    public void showCarsMenu() {
        var menu = Menu.create(
                "Add",
                "Select",
                "Sort",
                "Search",
                GO_BACK_VIEW).withHeader("Select option:");
        SelectAction.create(() -> {
                    showCreateCar();
                    return CONTINUE;
                }, () -> {
                    selectCar(carService.getAllCar());
                    return CONTINUE;
                }, () -> {
                    showCarSortMenu(carService.getAllCar());
                    return CONTINUE;
                }, () -> {
                    showCarSearch(carService.getAllCar());
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showCarOptions(Car car) {
        var menu = Menu.create(
                "Edit",
                "Delete",
                "Change Status",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    showEditCar(car);
                    return EXIT;
                }, () -> {
                    showDeleteCar(car);
                    return EXIT;
                }, () -> {
                    showChangeCarStatus(car);
                    return EXIT;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showChangeCarStatus(Car car) {
        System.out.println("Car for change status:");
        System.out.println("Actual status: " + car.getState());
        System.out.println(car.getView());

        var menu = Menu.create(
                "For Sale",
                "Not Sale",
                "Sold",
                "Service",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    carService.markForSale(car, loggedInUser);
                    System.out.println("Status was changed to For Sale!");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_CAR_STATUS);
                    return EXIT;
                }, () -> {
                    carService.markForNotSale(car, loggedInUser);
                    System.out.println("Status was changed to Not Sale!");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_CAR_STATUS);
                    return EXIT;
                }, () -> {
                    carService.markForSold(car, loggedInUser);
                    System.out.println("Status was changed to Sold");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_CAR_STATUS);
                    return EXIT;
                }, () -> {
                    carService.markForService(car, loggedInUser);
                    System.out.println("Status was changer to for Service");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_CAR_STATUS);
                    return EXIT;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showDeleteCar(Car car) {
        var orderOpt = salesOrderService.getOrderByFilter(order1 -> order1.getCar().equals(car));
        if (orderOpt.isPresent()) {
            System.out.println("Your can't delete car because it in order: \n" + orderOpt.get().getView());
            return;
        }
        System.out.println("Car for delete:");
        Viewer.viewOf(car);
        ModalMenu.getYesOrNoDialog(in, "Do you have delete car?", () -> {
            carService.delete(car);
            AuditService.logAction(loggedInUser.getUsername(), AuditAction.DELETE_CAR, car.getView());
            System.out.println("Car was deleted!");
            return EXIT;
        }, GO_BACK_ACTION);
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

    @Override
    public void showOrdersMenu(List<Order> orders) {
        var menu = Menu.create(
                "Select",
                "Sort",
                "Search",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    selectOrder(orders);
                    return EXIT;
                }, () -> {
                    showOrderSortMenu(orders);
                    return CONTINUE;
                }, () -> {
                    showOrderSearch(orders);
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showDelete(Order order) {
        System.out.println("Order for delete:");
        Viewer.viewOf(order);
        ModalMenu.getYesOrNoDialog(in, "Do you have delete order?", () -> {
            salesOrderService.delete((SalesOrder) order);
            System.out.println("Order was deleted!");
            return EXIT;
        }, GO_BACK_ACTION);
    }

    @Override
    public void showOrderOptions(Order order) {
        var menu = Menu.create(
                "Delete",
                "Change Status",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    showDelete(order);
                    return EXIT;
                }, () -> {
                    showChangeStatus(order);
                    return EXIT;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showChangeStatus(Order order) {
        System.out.println("Order for change status:");
        System.out.println("Actual status: " + order.getStatus());
        System.out.println(order.getView());
        var menu = Menu.create(
                "Complete",
                "In progress",
                "Cancel",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    salesOrderService.complete((SalesOrder) order);
                    System.out.println("Status was changed to Complete!");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_ORDER_STATUS);
                    return EXIT;
                }, () -> {
                    salesOrderService.inProgress((SalesOrder) order);
                    System.out.println("Status was changed to In Progress!");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_ORDER_STATUS);
                    return EXIT;
                }, () -> {
                    salesOrderService.cancel((SalesOrder) order);
                    System.out.println("Status was changed to Cancel");
                    AuditService.logAction(loggedInUser.getUsername(), AuditAction.CHANGE_ORDER_STATUS);
                    return EXIT;
                }, () -> EXIT
        ).readInCycle(in, menu);
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
                "by state",
                "by customer",
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
                        }, () -> {
                            orders.sort(Comparator.comparing(salesOrder -> salesOrder.getCustomer().getName()));
                            Viewer.viewAll(orders, "");
                            return CONTINUE;
                        }, () -> {
                            orders.sort(Comparator.comparing(Order::getStatus));
                            Viewer.viewAll(orders, "");
                            return CONTINUE;
                        }, GO_BACK_ACTION
                )
                .withErrorMsg("Invalid number of menu!")
                .readInCycle(in, menu);
    }

    @Override
    public void showUsers(List<User> users) {
        System.out.println("Users:");
        Viewer.viewAll(users, "Not found!");
    }

    @Override
    public void selectUser(List<User> users) {
        System.out.println("Input username:");
        var username = ConsoleUtils.readStr(in, NOT_BLANK_STR);
        var userOpt = users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
        userOpt.ifPresentOrElse(car -> showUserOptions(userOpt.get()),
                () -> System.out.println("User with this nickname not found!"));
    }

    @Override
    public void showUserSearch(List<User> users) {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        users = UserServiceImpl.filterUsersByString(users, input);
        Viewer.viewAll(users, "Not found!");
    }
}
