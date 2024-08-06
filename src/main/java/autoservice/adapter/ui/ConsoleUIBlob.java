package autoservice.adapter.ui;

import autoservice.adapter.config.Loader;
import autoservice.adapter.repository.ServiceOrderRepositoryImpl;
import autoservice.adapter.service.*;
import autoservice.core.model.*;
import autoservice.core.services.CarService;
import autoservice.core.services.OrderService;
import autoservice.core.services.ServiceOrderService;
import autoservice.core.services.UserAuthService;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static autoservice.adapter.ui.ConsoleUtils.*;

public class ConsoleUIBlob {
    private final Scanner in = new Scanner(System.in);
    private User loggedInUser;
    private final UserAuthService userAuthService = new UserAuthServiceImpl(Loader.getUserRepoWithDefaultData());
    private final CarService carService = Loader.getCarServiceWithDefaultData();
    private final OrderService orderService = new OrderServiceImpl(Loader.getOrderRepoWithDefaultData(userAuthService, carService));
    private final ServiceOrderService sOrderService = new ServiceOrderServiceImpl(new ServiceOrderRepositoryImpl());

    public ConsoleUIBlob() {
        System.out.println("Welcome to SuperAuto!");
        start();
    }

    private void start() {
        while (true) {
            showAuthMenu(in);
            switch (loggedInUser.getRole()) {
                case CLIENT -> showMenuForClient(in);
                case ADMIN -> showMenuForAdmin(in);
                case MANAGER -> showManagerMenu(in);
            }
        }
    }

    private void showManagerMenu(Scanner in) {
        do {
            System.out.println("1 Cars");
            System.out.println("2 Sales Orders");
            System.out.println("3 Service Orders");
            System.out.println("4 Clients");
            System.out.println("5 Profile");
            System.out.println("6 Logout");
            var point = readIntOfRange(in, 1, 6);
            switch (point) {
                case 1:
                    showCarsForEmployee(in);
                    break;
                case 2:
                    showOrdersForEmployee(in);
                    break;
                case 3:
                    showServiceOrdersForEmployee(in);
                    break;
                case 4:
                    showUsersForEmployee();
                    showOptionsUsersForEmployee(in);
                    break;
                case 5:
                    System.out.println("Your permission: " + loggedInUser.getRole());
                    Viewer.viewOf(loggedInUser);
                    break;
                case 6:
                    AuditService.logAction(loggedInUser.getUsername(), "Logout!");
                    return;
            }

        } while (true);
    }

    private void showServiceOrdersForEmployee(Scanner in) {
        System.out.println("All service orders: ");
        Viewer.viewAll(sOrderService.getAllSOrders(),
                "No orders!");
        do {
            System.out.println("1 Create");
            System.out.println("2 Select");
            System.out.println("3 Search");
            System.out.println("4 Sort");
            System.out.println("5 -Back");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    createSOrderForEmployee(in);
                    break;
                case 2:
                    selectSOrdersForEmployee(in);
                    return;
                case 3:
                    showSOrderSearchForEmployee(in);
                    return;
                case 4:
                    showSOrdersForManagerByState(in);
                    break;
                case 5:
                    return;
            }
        } while (true);
    }

    private void createSOrderForEmployee(Scanner in) {
        System.out.println("Creating service order:");
        Car car;
        User consumer;
        do {
            System.out.println("Input car id: ");
            var id = readInt(in);
            var carOpt = carService.getById(id);
            if (carOpt.isEmpty()) {
                System.out.println("Invalid car id.");
                System.out.println("Do you want to continue?");
                System.out.println("1 Yes");
                System.out.println("2 No");
                var point = readIntOfRange(in, 1, 2);
                if (point == 2) {
                    return;
                } else {
                    continue;
                }
            }
            car = carOpt.get();
            Optional<User> consumerOpt;
            System.out.println("Input consumer username: ");
            var username = readStr(in, NOT_BLANK_STR);
            consumerOpt = userAuthService.getByUsername(username);
            if (consumerOpt.isEmpty()) {
                System.out.println("Invalid consumer username.");
                System.out.println("Do you want to continue?");
                System.out.println("1 Yes");
                System.out.println("2 No");
                var point = readIntOfRange(in, 1, 2);
                if (point == 2) {
                    return;
                } else {
                    continue;
                }
            }
            consumer = consumerOpt.get();
            System.out.println("Car for service: \n" + car.getView());
            System.out.println("Consumer: \n" + consumer.getView());
            System.out.println("Do you want to create?");
            System.out.println("1 Yes");
            System.out.println("2 No");
            var point = readIntOfRange(in, 1, 2);
            if (point != 2) {
                System.out.println("Service order created!");
                AuditService.logAction(loggedInUser.getUsername(), "Create Service Order");
                sOrderService.add(new ServiceOrder(consumer, car));
            }
            return;
        } while (true);
    }

    private void showSOrdersForManagerByState(Scanner in) {
        do {
            System.out.println("1 In progress");
            System.out.println("2 Completed");
            System.out.println("3 Canceled");
            System.out.println("4 -Back");
            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    showSOrdersSortMenuForEmployee(in, order -> order.getStatus().equals(OrderStatus.IN_PROCESS));
                    return;
                case 2:
                    showSOrdersSortMenuForEmployee(in, order -> order.getStatus().equals(OrderStatus.COMPLETE));
                    return;
                case 3:
                    showSOrdersSortMenuForEmployee(in, order -> order.getStatus().equals(OrderStatus.CANCEL));
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showSOrderSearchForEmployee(Scanner in) {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        List<ServiceOrder> orders;
        orders = sOrderService.getAllSOrders();
        orders = ServiceOrderServiceImpl.filterOrdersByString(orders, input);
        Viewer.viewAll(orders, "Not found!");
    }

    private void selectSOrdersForEmployee(Scanner in) {
        System.out.println("Input order id: ");
        var id = readInt(in);
        var orderOpt = sOrderService.getSOrderByFilter(order -> order.getId() == id);
        if (orderOpt.isEmpty()) {
            System.out.println("Incorrect order index!");
            return;
        }
        ServiceOrder order = orderOpt.get();
        do {
            System.out.println("1 Delete");
            System.out.println("2 Change Status");
            System.out.println("3 -Back");

            var point = readIntOfRange(in, 1, 3);
            switch (point) {
                case 1:
                    showDeleteSOrderForEmployee(in, order);
                    return;
                case 2:
                    showChangeSOrderStatusForEmployee(in, order);
                    return;
                case 3:
                    return;
            }
        } while (true);
    }

    private void showChangeSOrderStatusForEmployee(Scanner in, ServiceOrder order) {
        System.out.println("Service order for change status:");
        System.out.println("Actual status: " + order.getStatus());
        System.out.println(order.getView());
        do {
            System.out.println("1 Complete");
            System.out.println("2 In progress");
            System.out.println("3 Cancel");
            System.out.println("4 -Back");

            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    sOrderService.complete(order);
                    System.out.println("Status was changed to Complete!");
                    return;
                case 2:
                    sOrderService.inProgress(order);
                    System.out.println("Status was changed to In Progress!");
                    return;
                case 3:
                    sOrderService.cancel(order);
                    System.out.println("Status was changed to Cancel");
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showDeleteSOrderForEmployee(Scanner in, ServiceOrder order) {
        temp:
        do {
            System.out.println("Order for delete:");
            Viewer.viewOf(order);
            System.out.println("1 Yes");
            System.out.println("2 No");
            var point = readIntOfRange(in, 1, 2);
            switch (point) {
                case 1:
                    sOrderService.delete(order);
                    System.out.println("Order was deleted!");
                    return;
                case 2:
                    break temp;
            }
        } while (true);
    }

    private void showOptionsUsersForEmployee(Scanner in) {
        do {
            System.out.println("1 Select");
            System.out.println("2 Search");
            System.out.println("3 Sort");
            System.out.println("4 -Back");
            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    showSelectUserForEmployee(in);
                    return;
                case 2:
                    showUserSearchForEmployee(in);
                    return;
                case 3:
                    if (loggedInUser.getRole().equals(Role.MANAGER)) {
                        showUsersSortMenuForEmployee(in, user -> user.getRole().equals(Role.CLIENT));
                        return;
                    }
                    showUsersSortMenuForEmployee(in, user -> true);
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showUserSearchForEmployee(Scanner in) {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        var role = loggedInUser.getRole();
        List<User> users;
        if (Objects.requireNonNull(role) == Role.MANAGER) {
            users = userAuthService.getUsersByFilter(user -> !user.getRole().equals(Role.ADMIN));
        } else {
            users = userAuthService.getAllUsers();
        }
        users = UserAuthServiceImpl.filterUsersByString(users, input);
        Viewer.viewAll(users, "Not found!");
    }

    private void showSelectUserForEmployee(Scanner in) {
        System.out.println("Input client's username: ");
        var username = readStr(in, NOT_BLANK_STR);
        var userOpt = userAuthService.getByFilter(car1 -> Objects.equals(car1.getUsername(), username));
        if (userOpt.isEmpty()) {
            System.out.println("Incorrect client's username!");
            return;
        }
        User user = userOpt.get();
        var role = loggedInUser.getRole();
        if (role == Role.MANAGER) {
            Viewer.viewOf(user);
            return;
        }
        do {
            System.out.println("1 Edit");
            System.out.println("2 Change Role");
            System.out.println("3 -Back");
            var point = readIntOfRange(in, 1, 2);
            switch (point) {
                case 1:
                    editUserForEmployee(in, user);
                    return;
                case 2:
                    showChangeRoleForEmployee(in, user);
                    return;
                case 3:
                    return;
            }
        } while (true);
    }

    private void showChangeRoleForEmployee(Scanner in, User user) {
        System.out.println("User for change role:");
        System.out.println("Actual status: " + user.getRole());
        System.out.println(user.getView());
        do {
            System.out.println("1 Client");
            System.out.println("2 Manager");
            System.out.println("3 Admin");
            System.out.println("4 -Back");

            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    AuditService.logAction(loggedInUser.getUsername(), "Change role!");
                    user.setRole(Role.CLIENT);
                    System.out.println("Role was changed!");
                    return;
                case 2:
                    AuditService.logAction(loggedInUser.getUsername(), "Change role!");
                    user.setRole(Role.MANAGER);
                    System.out.println("Role was changed!");
                    return;
                case 3:
                    AuditService.logAction(loggedInUser.getUsername(), "Change role!");
                    user.setRole(Role.ADMIN);
                    System.out.println("Role was changed!");
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void editUserForEmployee(Scanner in, User user) {
        @AllArgsConstructor
        class TempUser {
            String name;
            String surname;
            String password;
            String phone;
        }
        TempUser tempUser = new TempUser(user.getName(), user.getSurname(), user.getPassword(), user.getPhone());
        temp:
        do {
            System.out.println("What do you have change?");
            System.out.println("1 Name");
            System.out.println("2 Surname");
            System.out.println("3 Password");
            System.out.println("4 Phone");
            System.out.println("5 -Complete");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    System.out.println("New name:");
                    tempUser.name = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    continue;
                case 2:
                    System.out.println("New surname:");
                    tempUser.surname = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    continue;
                case 3:
                    System.out.println("New phone:");
                    tempUser.phone = readStr(in, SIZE_11);
                    continue;
                case 4:
                    System.out.println("New password:");
                    tempUser.password = readStr(in, NOT_BLANK_STR);
                    continue;
                case 5:
                    break;
            }
            do {
                System.out.println("Edited User field:");
                System.out.printf("Username: %s; Name: %s; Surname: %s; Phone: %s; Password: %s%n",
                        user.getUsername(), tempUser.name, tempUser.surname, tempUser.phone, tempUser.password);
                System.out.println("Save or not?");
                System.out.println("1 Yes");
                System.out.println("2 No");
                var point2 = readIntOfRange(in, 1, 2);
                switch (point2) {
                    case 1:
                        user.setName(tempUser.name);
                        user.setSurname(tempUser.surname);
                        user.setPhone(tempUser.phone);
                        user.setPassword(tempUser.password);
                        System.out.println("User was changed!");
                        AuditService.logAction(loggedInUser.getUsername(), "Edit user.");
                        return;
                    case 2:
                        break temp;
                }
            } while (true);
        } while (true);
    }


    private void showUsersForEmployee() {
        System.out.println("Clients:");
        var role = loggedInUser.getRole();
        if (role.equals(Role.MANAGER)) {
            Viewer.viewAll(userAuthService.getUsersByFilter(user -> user.getRole().equals(Role.CLIENT)),
                    "List is empty!");
        } else if (role.equals(Role.ADMIN)) {
            Viewer.viewAll(userAuthService.getUsersByFilter(user -> user.getRole().equals(Role.MANAGER)
                            || user.getRole().equals(Role.ADMIN)
                            || user.getRole().equals(Role.CLIENT)),
                    "List is empty!");
        }
    }

    private void showOrdersForEmployee(Scanner in) {
        System.out.println("All orders: ");
        Viewer.viewAll(orderService.getAllOrders(),
                "No orders!");
        do {
            System.out.println("1 Select");
            System.out.println("2 Search");
            System.out.println("3 Sort");
            System.out.println("4 -Back");
            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    selectOrdersForEmployee(in);
                    return;
                case 2:
                    showOrderSearchForEmployee(in);
                    break;
                case 3:
                    showOrdersForManagerByState(in);
                    break;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showOrdersForManagerByState(Scanner in) {
        do {
            System.out.println("1 In progress");
            System.out.println("2 Completed");
            System.out.println("3 Canceled");
            System.out.println("4 -Back");
            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    showOrdersSortMenuForEmployee(in, order -> order.getStatus().equals(OrderStatus.IN_PROCESS));
                    return;
                case 2:
                    showOrdersSortMenuForEmployee(in, order -> order.getStatus().equals(OrderStatus.COMPLETE));
                    return;
                case 3:
                    showOrdersSortMenuForEmployee(in, order -> order.getStatus().equals(OrderStatus.CANCEL));
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showSOrdersSortMenuForEmployee(Scanner in, Predicate<ServiceOrder> predicate) {
        do {
            var orders = new ArrayList<>(sOrderService.getSOrdersByFilter(predicate));
            if (orders.isEmpty()) {
                System.out.println("Nothing was found for this query!");
                return;
            }
            System.out.println("1 by id");
            System.out.println("2 by date");
            System.out.println("3 by customer");
            System.out.println("4 by car");
            System.out.println("5 -Back");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    orders.sort(Comparator.comparingInt(ServiceOrder::getId));
                    Viewer.viewAll(orders, "");
                    break;
                case 2:
                    orders.sort(Comparator.comparing(ServiceOrder::getDate));
                    Viewer.viewAll(orders, "");
                    break;
                case 3:
                    orders.sort(Comparator.comparing(salesOrder -> salesOrder.getCustomer().getName()));
                    Viewer.viewAll(orders, "");
                    break;
                case 4:
                    orders.sort(Comparator.comparing(salesOrder -> salesOrder.getCar().getModel()));
                    Viewer.viewAll(orders, "");
                    break;
                case 5:
                    break;
            }
        } while (true);
    }

    private void showOrdersSortMenuForEmployee(Scanner in, Predicate<SalesOrder> predicate) {
        do {
            var orders = new ArrayList<>(orderService.getOrdersByFilter(predicate));
            if (orders.isEmpty()) {
                System.out.println("Nothing was found for this query!");
                return;
            }
            System.out.println("1 by id");
            System.out.println("2 by date");
            System.out.println("3 by customer");
            System.out.println("4 by car");
            System.out.println("5 -Back");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    orders.sort(Comparator.comparingInt(SalesOrder::getId));
                    Viewer.viewAll(orders, "");
                    break;
                case 2:
                    orders.sort(Comparator.comparing(SalesOrder::getDate));
                    Viewer.viewAll(orders, "");
                    break;
                case 3:
                    orders.sort(Comparator.comparing(salesOrder -> salesOrder.getCustomer().getName()));
                    Viewer.viewAll(orders, "");
                    break;
                case 4:
                    orders.sort(Comparator.comparing(salesOrder -> salesOrder.getCar().getModel()));
                    Viewer.viewAll(orders, "");
                    break;
                case 5:
                    break;
            }
        } while (true);
    }

    private void showUsersSortMenuForEmployee(Scanner in, Predicate<User> predicate) {
        do {
            var users = new ArrayList<>(userAuthService.getUsersByFilter(predicate));
            if (users.isEmpty()) {
                System.out.println("Nothing was found for this query!");
                return;
            }
            System.out.println("1 by nickname");
            System.out.println("2 by name");
            System.out.println("3 by surname");
            System.out.println("4 by phone");
            System.out.println("5 -Back");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    users.sort(Comparator.comparing(User::getUsername));
                    Viewer.viewAll(users, "");
                    break;
                case 2:
                    users.sort(Comparator.comparing(User::getName));
                    Viewer.viewAll(users, "");
                    break;
                case 3:
                    users.sort(Comparator.comparing(User::getSurname));
                    Viewer.viewAll(users, "");
                    break;
                case 4:
                    users.sort(Comparator.comparing(User::getPhone));
                    Viewer.viewAll(users, "");
                    break;
                case 5:
                    return;
            }
        } while (true);
    }


    private void selectOrdersForEmployee(Scanner in) {
        System.out.println("Input order id: ");
        var id = readInt(in);
        var orderOpt = orderService.getOrderByFilter(car1 -> car1.getId() == id);
        if (orderOpt.isEmpty()) {
            System.out.println("Incorrect car index!");
            return;
        }
        SalesOrder order = orderOpt.get();
        do {
            System.out.println("1 Delete");
            System.out.println("2 Change Status");
            System.out.println("3 -Back");

            var point = readIntOfRange(in, 1, 3);
            switch (point) {
                case 1:
                    showDeleteOrderForEmployee(in, order);
                    return;
                case 2:
                    showChangeOrderStatusForEmployee(in, order);
                    return;
                case 3:
                    return;
            }
        } while (true);
    }


    private void showChangeOrderStatusForEmployee(Scanner in, SalesOrder order) {
        System.out.println("Car for change status:");
        System.out.println("Actual status: " + order.getStatus());
        System.out.println(order.getView());
        do {
            System.out.println("1 Complete");
            System.out.println("2 In progress");
            System.out.println("3 Cancel");
            System.out.println("4 -Back");

            var point = readIntOfRange(in, 1, 4);
            switch (point) {
                case 1:
                    orderService.complete(order);
                    System.out.println("Status was changed to Complete!");
                    return;

                case 2:
                    orderService.inProgress(order);
                    System.out.println("Status was changed to In Progress!");
                    return;
                case 3:
                    orderService.cancel(order);
                    System.out.println("Status was changed to Cancel");
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showDeleteOrderForEmployee(Scanner in, SalesOrder order) {
        temp:
        do {
            System.out.println("Order for delete:");
            Viewer.viewOf(order);
            System.out.println("1 Yes");
            System.out.println("2 No");
            var point = readIntOfRange(in, 1, 2);
            switch (point) {
                case 1:
                    orderService.delete(order);
                    System.out.println("Order was deleted!");
                    return;
                case 2:
                    break temp;
            }
        } while (true);
    }


    private void showCarsForEmployee(Scanner in) {
        System.out.println("All cars: ");
        Viewer.viewAll(carService.getAllCar(),
                "No cars!");
        do {
            System.out.println("1 Add");
            System.out.println("2 Select");
            System.out.println("3 Sort");
            System.out.println("4 Search");
            System.out.println("5 -Back");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    addCarForEmployee(in);
                    break;
                case 2:
                    selectCarForEmployee(in);
                    break;
                case 3:
                    showCarsByStateForEmployee(in);
                    break;
                case 4:
                    showCarSearchForAll(in);
                case 5:
                    return;
            }
        } while (true);
    }

    private void addCarForEmployee(Scanner in) {
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
        System.out.println("1 Save");
        System.out.println("2 Cancel");
        var point = readIntOfRange(in, 1, 2);
        switch (point) {
            case 1:
                carService.add(car);
                System.out.println("Car was add to System!");
                AuditService.logAction(loggedInUser.getUsername(), "Add car");
                return;
            case 2:
                System.out.println("Operation canceled!");
                break;
        }
    }


    private void showOrderSearchForEmployee(Scanner in) {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        List<SalesOrder> orders;
        orders = orderService.getAllOrders();
        orders = OrderServiceImpl.filterOrdersByString(orders, input);
        Viewer.viewAll(orders, "Not found!");
    }

    private void showCarSearchForAll(Scanner in) {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        var role = loggedInUser.getRole();
        List<Car> cars;
        if (Objects.requireNonNull(role) == Role.CLIENT) {
            cars = carService.getCarsByFilter(car -> car.getState().equals(CarState.FOR_SALE));
        } else {
            cars = carService.getAllCar();
        }
        cars = CarServiceImpl.filterCarsByString(cars, input);
        Viewer.viewAll(cars, "Not found!");
    }


    private void selectCarForEmployee(Scanner in) {
        System.out.println("Input car id: ");
        var id = readInt(in);
        var carOpt = carService.getCarByFilter(car1 -> car1.getId() == id);
        if (carOpt.isEmpty()) {
            System.out.println("Incorrect car index!");
            return;
        }
        Car car = carOpt.get();
        do {
            System.out.println("1 Edit");
            System.out.println("2 Delete");
            System.out.println("3 Change Status");
            System.out.println("4 -Back");

            var point = readIntOfRange(in, 1, 4);

            switch (point) {
                case 1:
                    showEditCarForEmployee(in, car);
                    return;
                case 2:
                    showDeleteCarForEmployee(in, car);
                    return;
                case 3:
                    showChangeCarStatusForEmployee(in, car);
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showChangeCarStatusForEmployee(Scanner in, Car car) {
        System.out.println("Car for change status:");
        System.out.println("Actual status: " + car.getState());
        System.out.println(car.getView());
        do {
            System.out.println("1 For Sale");
            System.out.println("2 Not Sale");
            System.out.println("3 Sold");
            System.out.println("4 Service");
            System.out.println("5 -Back");

            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    carService.markForSale(car, loggedInUser);
                    System.out.println("Status was changed to For Sale!");
                    AuditService.logAction(loggedInUser.getUsername(), "Change car status!");
                    return;
                case 2:
                    carService.markForNotSale(car, loggedInUser);
                    System.out.println("Status was changed to Not Sale!");
                    AuditService.logAction(loggedInUser.getUsername(), "Change car status!");
                    return;
                case 3:
                    carService.markForSold(car, loggedInUser);
                    System.out.println("Status was changed to Sold");
                    AuditService.logAction(loggedInUser.getUsername(), "Change car status!");
                    return;
                case 4:
                    carService.markForService(car, loggedInUser);
                    System.out.println("Status was changer to for Service");
                    AuditService.logAction(loggedInUser.getUsername(), "Change car status!");
                    return;
                case 5:
                    return;
            }
        } while (true);
    }

    private void showEditCarForEmployee(Scanner in, Car car) {
        var order = orderService.getOrderByFilter(order1 -> order1.getCar().equals(car));
        if (order.isPresent()) {
            System.out.println("Your can't change car because it in order: \n" + order.get().getView());
            return;
        }
        @AllArgsConstructor
        class TempCar {
            String make;
            String model;
            int year;
            long price;
        }
        TempCar tempCar = new TempCar(car.getMake(), car.getModel(), car.getYear(), car.getPrice());
        temp:
        do {
            System.out.println("What do you have change?");
            System.out.println("1 make");
            System.out.println("2 model");
            System.out.println("3 year");
            System.out.println("4 price");
            System.out.println("5 -complete");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    System.out.println("Your make:");
                    tempCar.make = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    continue;
                case 2:
                    System.out.println("Your model:");
                    tempCar.model = readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE));
                    continue;
                case 3:
                    System.out.println("Your year:");
                    tempCar.year = readInt(in);
                    continue;
                case 4:
                    System.out.println("Your price:");
                    tempCar.price = (readLong(in));
                    continue;
                case 5:
                    break;
            }
            do {
                System.out.println("New car field:");
                System.out.printf("Id: %d; make: %s; model: %s; year: %d; price: %d%n",
                        car.getId(), tempCar.make, tempCar.model, tempCar.year, tempCar.price);
                System.out.println("Save or not?");
                System.out.println("1 Yes");
                System.out.println("2 No");
                var point2 = readIntOfRange(in, 1, 2);
                switch (point2) {
                    case 1:
                        car.setMake(tempCar.make);
                        car.setModel(tempCar.model);
                        car.setYear(tempCar.year);
                        car.setPrice(tempCar.price);
                        AuditService.logAction(loggedInUser.getUsername(), "Edit car");
                        System.out.println("Car was changed!");
                        return;
                    case 2:
                        break temp;
                }
            } while (true);
        } while (true);
    }

    private void showDeleteCarForEmployee(Scanner in, Car car) {
        var order = orderService.getOrderByFilter(order1 -> order1.getCar().equals(car));
        if (order.isPresent()) {
            System.out.println("Your can't delete car because it in order: \n" + order.get().getView());
            return;
        }
        temp:
        do {
            System.out.println("Car for delete:");
            Viewer.viewOf(car);
            System.out.println("1 Yes");
            System.out.println("2 No");
            var point = readIntOfRange(in, 1, 2);
            switch (point) {
                case 1:
                    carService.delete(car);
                    AuditService.logAction(loggedInUser.getUsername(), "Delete car");
                    System.out.println("Car was deleted!");
                    return;
                case 2:
                    break temp;
            }
        } while (true);
    }

    private void showCarsByStateForEmployee(Scanner in) {
        do {
            System.out.println("1 For sale");
            System.out.println("2 For service");
            System.out.println("3 Not sale");
            System.out.println("4 Sold");
            System.out.println("5 -Back");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    showCarSortMenu(in, car -> car.getState().equals(CarState.FOR_SALE));
                    return;
                case 2:
                    showCarSortMenu(in, car -> car.getState().equals(CarState.FOR_SERVICE));
                    return;
                case 3:
                    showCarSortMenu(in, car -> car.getState().equals(CarState.NOT_SALE));
                    return;
                case 4:
                    showCarSortMenu(in, car -> car.getState().equals(CarState.SOLD));
                    return;
                case 5:
                    return;
            }
        } while (true);
    }

    private void showMenuForAdmin(Scanner in) {
        do {
            System.out.println("1 Cars");
            System.out.println("2 Sales Orders");
            System.out.println("3 Service Orders");
            System.out.println("4 Peoples");
            System.out.println("5 Logs");
            System.out.println("6 Profile");
            System.out.println("7 Logout");
            var point = readIntOfRange(in, 1, 7);
            switch (point) {
                case 1:
                    showCarsForEmployee(in);
                    break;
                case 2:
                    showOrdersForEmployee(in);
                    break;
                case 3:
                    showServiceOrdersForEmployee(in);
                    break;
                case 4:
                    showUsersForEmployee();
                    showOptionsUsersForEmployee(in);
                    break;
                case 5:
                    showLogForEmployee();
                    break;
                case 6:
                    Viewer.viewOf(loggedInUser);
                    break;
                case 7:
                    AuditService.logAction(loggedInUser.getUsername(), "Logout");
                    return;
            }
        } while (true);
    }

    private void showLogForEmployee() {
        System.out.println("1 Show logs");
        System.out.println("2 Export");
        var point = readIntOfRange(in, 1, 2);
        switch (point) {
            case 1:
                Viewer.viewAllStrings(AuditService.viewLogs(), "There are no logs!");
                showLogSearchMenu(in);
                break;
            case 2:
                try {
                    System.out.println("Log was exported in default path. " +
                            "File locate in your user's folder!");
                    AuditService.exportLogs("audit_log.txt");
                } catch (IOException e) {
                    System.out.println("Failed to export IO Exception");
                }
                break;
        }
    }

    private void showLogSearchMenu(Scanner in) {
        System.out.println("1 Search");
        System.out.println("2 -Back");
        var point = readIntOfRange(in, 1, 2);
        if (point == 1) {
            showLogByOrder();
        }
    }

    private void showLogByOrder() {
        System.out.println("Input your request:");
        var input = readStr(in, NOT_BLANK_STR);
        var logs = AuditService.filterLogsByString(input);
        Viewer.viewAllStrings(logs, "Not found!");
    }

    private void showMenuForClient(Scanner in) {
        do {
            System.out.println("1 Catalog");
            System.out.println("2 Profile");
            System.out.println("3 Orders");
            System.out.println("4 Logout");
            var point = readIntOfRange(in, 1, 5);
            switch (point) {
                case 1:
                    showCarForSale();
                    showCatalogMenuForClient(in);
                    break;
                case 2:
                    System.out.println("Your profile:");
                    Viewer.viewOf(loggedInUser);
                    break;
                case 3:
                    System.out.println("Your orders:");
                    showOrdersForClient(in);
                    break;
                case 4:
                    return;

            }
        } while (true);
    }


    private void showOrdersForClient(Scanner in) {
        var orders = orderService.getOrdersByFilter(order -> order.getCustomer().equals(loggedInUser));
        Viewer.viewAll(orders, "You don't have any orders!");
        if (orders.isEmpty()) return;
        do {
            System.out.println("1 Cancel order");
            System.out.println("2 Show active orders");
            System.out.println("3 -Back");
            var point = readIntOfRange(in, 1, 2);
            switch (point) {
                case 1:
                    do {
                        System.out.println("Input order number for cancel:");
                        var id = readInt(in);
                        var currentOrder = orders.stream().filter(order -> order.getId() == id).findFirst();
                        if (currentOrder.isPresent()) {
                            var orderForCancel = currentOrder.get();
                            temp:
                            do {
                                System.out.println("Your order for cancel:");
                                Viewer.viewOf(orderForCancel);
                                System.out.println("1 Cancel");
                                System.out.println("2 -Back");
                                var point2 = readIntOfRange(in, 1, 2);
                                switch (point2) {
                                    case 1:
                                        orderForCancel.setStatus(OrderStatus.CANCEL);
                                        AuditService.logAction(loggedInUser.getUsername(), "Cancel order");
                                        System.out.println("Your order canceled!");
                                        return;
                                    case 2:
                                        break temp;
                                }
                            } while (true);
                            return;
                        }
                        System.out.println("Incorrect order ID. Select the correct one from the list.");
                    } while (true);
                case 2:
                    System.out.println("Only active orders:");
                    Viewer.viewAll(orders.stream().filter(order -> order.getStatus().equals(OrderStatus.IN_PROCESS)).toList(),
                            "Your haven't active orders!");
                    break;
                case 3:
                    return;
            }
        } while (true);
    }

    private void showCatalogMenuForClient(Scanner in) {
        do {
            System.out.println("1 Order");
            System.out.println("2 Sort");
            System.out.println("3 Search");
            System.out.println("4 -Back");
            var point = readIntOfRange(in, 1, 3);
            switch (point) {
                case 1:
                    showCreateOrderForClient(in);
                    return;
                case 2:
                    showCarSortMenu(in, car -> car.getState().equals(CarState.FOR_SALE));
                    break;
                case 3:
                    showCarSearchForAll(in);
                    return;
                case 4:
                    return;
            }
        } while (true);
    }

    private void showCarSortMenu(Scanner in, Predicate<Car> predicate) {
        do {
            var cars = new ArrayList<>(carService.getCarsByFilter(predicate));
            if (cars.isEmpty()) {
                System.out.println("Nothing was found for this query!");
                return;
            }
            System.out.println("1 by id");
            System.out.println("2 by make");
            System.out.println("3 by model");
            System.out.println("4 by year");
            System.out.println("5 by price");
            System.out.println("6 -Back");
            var point = readIntOfRange(in, 1, 6);
            switch (point) {
                case 1:
                    cars.sort(Comparator.comparingInt(Car::getId));
                    Viewer.viewAll(cars, "");
                    break;
                case 2:
                    cars.sort(Comparator.comparing(Car::getMake));
                    Viewer.viewAll(cars, "");
                    break;
                case 3:
                    cars.sort(Comparator.comparing(Car::getModel));
                    Viewer.viewAll(cars, "");
                    break;
                case 4:
                    cars.sort(Comparator.comparingInt(Car::getYear));
                    Viewer.viewAll(cars, "");
                    break;
                case 5:
                    cars.sort(Comparator.comparingLong(Car::getPrice));
                    Viewer.viewAll(cars, "");
                    break;
                case 6:
                    return;
            }
        } while (true);
    }

    private void showCreateOrderForClient(Scanner in) {
        System.out.println("Input car's id for order:");
        do {
            var id = readInt(in);
            var car = carService.getCarByFilter(car1 -> car1.getId() == id
                    && car1.getState().equals(CarState.FOR_SALE));
            if (car.isPresent()) {
                var salesOrder = new SalesOrder(loggedInUser, car.get());
                temp:
                do {
                    System.out.println("Your order:");
                    Viewer.viewOf(car.get());
                    System.out.println("1 Send");
                    System.out.println("2 -Back");
                    var point = readIntOfRange(in, 1, 2);
                    switch (point) {
                        case 1:
                            orderService.add(salesOrder);
                            System.out.println("Your order created!");
                            AuditService.logAction(loggedInUser.getUsername(), "Create order.");
                            return;
                        case 2:
                            break temp;
                    }
                } while (true);
                return;
            }
            System.out.println("Incorrect machine ID. Select the correct one from the list.");
        } while (true);
    }

    private void showCarForSale() {
        Viewer.viewAll(carService.getCarsByFilter(car -> car.getState().equals(CarState.FOR_SALE)),
                "No cars for sale have been found!");
    }

    private void showAuthMenu(Scanner in) {
        System.out.println("Please log in or register");
        System.out.println("1 Login");
        System.out.println("2 Register");
        System.out.println("3 Exit");
        var point = readIntOfRange(in, 1, 3);
        switch (point) {
            case 1:
                login(in);
                break;
            case 2:
                register(in);
                break;
            case 3:
                System.out.println("Goodbye! Have a nice day!");
                System.exit(-1);
                break;
        }
    }

    private void register(Scanner in) {
        System.out.println("Registration:");
        String username;
        String password;
        do {
            System.out.println("Input username:");
            username = readStr(in, NOT_BLANK_STR);
            System.out.println("Input password:");
            password = readStr(in, NOT_BLANK_STR);
            var userOpt = userAuthService.register(Role.CLIENT, username, password);
            if (userOpt.isEmpty()) {
                System.out.println("Registration failed username is busy!\nSelect another username!");
                break;
            }
            loggedInUser = userOpt.get();
        } while (true);
        AuditService.logAction(loggedInUser.getUsername(), "Register");
        System.out.println("Registration is successful!");
        System.out.println("Write additional information about your:");
        System.out.println("Input your name: ");
        loggedInUser.setName(readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE)));
        System.out.println("Input your surname: ");
        loggedInUser.setSurname(readStr(in, NOT_BLANK_STR.and(NOT_SMALL_SIZE)));
        System.out.println("Input your phone number: ");
        loggedInUser.setPhone(readStr(in, NOT_BLANK_STR.and(SIZE_11)));
        System.out.println("The information is recorded!");
    }

    private void login(Scanner in) {
        System.out.println("Login:");
        Optional<User> user;
        do {
            System.out.println("Input username:");
            var username = readStr(in, NOT_BLANK_STR);
            System.out.println("Input password:");
            var password = readStr(in, NOT_BLANK_STR);
            user = userAuthService.login(username, password);
            if (user.isEmpty()) {
                System.out.println("Invalid username or password!");
            }
        } while (user.isEmpty());
        loggedInUser = user.get();
        AuditService.logAction(loggedInUser.getUsername(), "Login");
        System.out.println("Access granted! Welcome, " + loggedInUser.getUsername() + " in Super Car's System");
    }

    public static void main(String[] args) {
        new ConsoleUIBlob();
    }

}
