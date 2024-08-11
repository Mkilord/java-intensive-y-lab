package autoservice.adapter.ui.common.impl;

import autoservice.adapter.service.CarService;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.UserService;
import autoservice.adapter.service.impl.AuditService;
import autoservice.adapter.ui.common.editors.PeopleEditor;
import autoservice.adapter.ui.components.menu.Menu;
import autoservice.adapter.ui.components.menu.SelectAction;
import autoservice.adapter.ui.components.utils.ModalMenu;
import autoservice.adapter.ui.components.utils.Viewer;
import autoservice.model.*;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_ACTION;
import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_VIEW;
import static autoservice.adapter.ui.components.menu.SelectAction.*;
import static autoservice.adapter.ui.components.utils.ConsoleUtils.*;
import static autoservice.model.AuditAction.CHANGE_ROLE;
import static autoservice.model.AuditAction.EDIT_USER;

public class AdminUIImpl extends EmployeeUIImpl implements PeopleEditor {
    public AdminUIImpl(Scanner in, CarService carService,
                       MyOrderService<SalesOrder> salesOrderService,
                       MyOrderService<ServiceOrder> serviceOrderService,
                       UserService userService, User loggedInUser) {
        super(in, carService, salesOrderService, serviceOrderService, userService, loggedInUser);
    }

    @Override
    public void showMainMenu() {
        var menu = Menu.create(
                "Cars",
                "Sale Orders",
                "Service Orders",
                "Peoples",
                "Logs",
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
                    showUsers(userService.getAllUsers());
                    return CONTINUE;
                }, () -> {
                    showLogs();
                    return CONTINUE;
                },
                () -> {
                    showProfile();
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showUserOptions(User user) {
        Viewer.viewOf(user);
        var menu = Menu.create(
                "Edit",
                "Change Role",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    showEdit(user);
                    return CONTINUE;
                }, () -> {
                    showChangeRole(user);
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showEdit(User user) {
        @AllArgsConstructor
        class TempUser {
            String name;
            String surname;
            String password;
            String phone;
        }
        TempUser tempUser = new TempUser(user.getName(), user.getSurname(), user.getPassword(), user.getPhone());

        var menu = Menu.create(
                "Name",
                "Surname",
                "Password",
                "Phone",
                "-Complete").withHeader("Select option:");
        create(() -> {
                    System.out.println("New name:");
                    tempUser.name = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    return CONTINUE;
                }, () -> {
                    System.out.println("New surname:");
                    tempUser.surname = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    return CONTINUE;
                }, () -> {
                    System.out.println("New phone:");
                    tempUser.phone = readStr(in, SIZE_11);
                    return CONTINUE;
                }, () -> {
                    System.out.println("New password:");
                    tempUser.password = readStr(in, NOT_BLANK_STR);
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
        System.out.println("Edited User field:");
        System.out.printf("Username: %s; Name: %s; Surname: %s; Phone: %s; Password: %s%n",
                user.getUsername(), tempUser.name, tempUser.surname, tempUser.phone, tempUser.password);
        ModalMenu.getSaveOrNoDialog(in, "Save or not?", () -> {
            AuditService.logAction(loggedInUser.getUsername(), EDIT_USER, "Old: " + user.getView() + "\nNew: " + tempUser);
            user.setName(tempUser.name);
            user.setSurname(tempUser.surname);
            user.setPhone(tempUser.phone);
            user.setPassword(tempUser.password);
            System.out.println("User was changed!");
            return EXIT;
        }, GO_BACK_ACTION);
    }

    @Override
    public void showChangeRole(User user) {
        System.out.println("User for change role:");
        System.out.println("Actual status: " + user.getRole());
        System.out.println(user.getView());
        var menu = Menu.create(
                "Client",
                "Manager",
                "Admin",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    user.setRole(Role.CLIENT);
                    AuditService.logAction(loggedInUser.getUsername(), CHANGE_ROLE);
                    System.out.println("Role was changed!");
                    return CONTINUE;
                }, () -> {
                    AuditService.logAction(loggedInUser.getUsername(), CHANGE_ROLE);
                    user.setRole(Role.MANAGER);
                    System.out.println("Role was changed!");
                    return CONTINUE;
                }, () -> {
                    AuditService.logAction(loggedInUser.getUsername(), CHANGE_ROLE);
                    user.setRole(Role.ADMIN);
                    System.out.println("Role was changed!");
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showUsersMenu() {
        var users = userService.getAllUsers();
        var menu = Menu.create(
                "Select",
                "Search",
                "Sort",
                GO_BACK_VIEW).withHeader("Select option:");
        create(() -> {
                    selectUser(users);
                    return CONTINUE;
                },
                () -> {
                    showUserSearch(users);
                    return CONTINUE;
                }, () -> {
                    showUserSortMenu(users);
                    return CONTINUE;
                }, () -> EXIT
        ).readInCycle(in, menu);
    }

    @Override
    public void showUserSortMenu(List<User> users) {
        Menu menu = Menu.create(
                "by username",
                "by name",
                "by surname",
                "by phone",
                "by role",
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
                }, () -> {
                    users.sort(Comparator.comparing(User::getRole));
                    Viewer.viewAll(users, "");
                    return CONTINUE;
                }, GO_BACK_ACTION).readInCycle(in, menu);
    }

    private void showLogs() {
        var menu = Menu.create("Show logs",
                        "Export"
                        , GO_BACK_VIEW)
                .withHeader("Select logs function:");
        SelectAction.create(() -> {
            Viewer.viewAllStrings(AuditService.viewLogs(), "There are no logs!");
            showLogMenu();
            return CONTINUE;
        }, () -> {
            try {
                System.out.println("Log was exported in default path. " +
                        "File locate in your user's folder!");
                AuditService.exportLogs("audit_log.txt");
            } catch (IOException e) {
                System.out.println("Failed to export IO Exception");
            }
            return EXIT;
        }, GO_BACK_ACTION).readInCycle(in, menu);
    }

    private void showLogMenu() {
        var menu = Menu.create(
                        "Search",
                        GO_BACK_VIEW)
                .withHeader("Select function:");
        SelectAction.create(() -> {
            searchLog();
            return CONTINUE;
        }, GO_BACK_ACTION).readInCycle(in, menu);
    }

    private void searchLog() {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        var logs = AuditService.filterLogsByString(input);
        Viewer.viewAllStrings(logs, "Not found!");
    }

}
