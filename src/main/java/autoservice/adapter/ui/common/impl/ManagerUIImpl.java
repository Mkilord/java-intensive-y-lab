package autoservice.adapter.ui.common.impl;

import autoservice.adapter.service.CarService;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.UserService;
import autoservice.adapter.ui.components.menu.Menu;
import autoservice.adapter.ui.components.menu.SelectAction;
import autoservice.adapter.ui.components.utils.Viewer;
import autoservice.model.Role;
import autoservice.model.SalesOrder;
import autoservice.model.ServiceOrder;
import autoservice.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_ACTION;
import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_VIEW;
import static autoservice.adapter.ui.components.menu.SelectAction.*;

public class ManagerUIImpl extends EmployeeUIImpl {
    public ManagerUIImpl(Scanner in, CarService carService,
                         MyOrderService<SalesOrder> salesOrderService,
                         MyOrderService<ServiceOrder> serviceOrderService,
                         UserService userService,
                         User loggedInUser) {
        super(in, carService, salesOrderService, serviceOrderService, userService, loggedInUser);
    }

    @Override
    public void showMainMenu() {
        var menu = Menu.create(
                "Cars",
                "Sale Orders",
                "Service Orders",
                "Clients",
                "Profile",
                "Logout").withHeader("Select option:");
        create(() -> {
                    showCars(carService.getAllCar());
                    showCarsMenu();
                    return CONTINUE;
                }, () -> {
                    showOrders(new ArrayList<>(salesOrderService.getAllOrders()), salesOrderService);
                    return CONTINUE;
                }, () -> {
                    showOrders(new ArrayList<>(serviceOrderService.getAllOrders()), serviceOrderService);
                    return CONTINUE;
                }, () -> {
                    showUsers(userService.getUsersByFilter(user -> user.getRole().equals(Role.CLIENT)));
                    return CONTINUE;
                }, () -> {
                    showProfile();
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }


    @Override
    public void showUsersMenu() {
        var clients = userService.getUsersByFilter(user -> user.getRole().equals(Role.CLIENT));
        var menu = Menu.create(
                "Search",
                "Sort",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    showUserSearch(clients);
                    return CONTINUE;
                }, () -> {
                    showUserSortMenu(clients);
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showUserOptions(User user) {
        System.out.println("You haven't permission for this!");
    }

    @Override
    public void showUserSortMenu(List<User> users) {
        Menu menu = Menu.create(
                "by username",
                "by name",
                "by surname",
                "by phone",
                GO_BACK_VIEW
        ).withHeader("Select menu point");
        SelectAction.create(
                () -> {
                    users.sort(Comparator.comparing(User::getUsername));
                    Viewer.viewAll(users, "");
                    return CONTINUE;
                }, () -> {
                    users.sort(Comparator.comparing(User::getName));
                    Viewer.viewAll(users, "");
                    return CONTINUE;
                }, () -> {
                    users.sort(Comparator.comparing(User::getSurname));
                    Viewer.viewAll(users, "");
                    return CONTINUE;
                }, () -> {
                    users.sort(Comparator.comparing(User::getPhone));
                    Viewer.viewAll(users, "");
                    return CONTINUE;
                }, GO_BACK_ACTION).readInCycle(in, menu);
    }
}
