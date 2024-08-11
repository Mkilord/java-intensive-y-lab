package autoservice.adapter.ui;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.repository.ServiceOrderRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.repository.impl.OrderRepositoryImpl;
import autoservice.adapter.repository.impl.ServiceOrderRepositoryImpl;
import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.adapter.service.*;
import autoservice.adapter.service.impl.*;
import autoservice.adapter.ui.common.impl.AdminUIImpl;
import autoservice.adapter.ui.common.impl.ClientUIImpl;
import autoservice.adapter.ui.common.impl.ManagerUIImpl;
import autoservice.adapter.ui.components.menu.Menu;
import autoservice.adapter.ui.components.menu.SelectAction;
import autoservice.model.*;

import java.util.Optional;
import java.util.Scanner;

import static autoservice.adapter.ui.components.menu.Menu.GO_BACK_VIEW;
import static autoservice.adapter.ui.components.menu.SelectAction.EXIT;
import static autoservice.adapter.ui.components.utils.ConsoleUtils.*;


public class AppStart {

    private final UserRepository userRepository = new UserRepositoryImpl();
    private final CarRepository carRepository = new CarRepositoryImpl();
    private final OrderRepository orderRepository = new OrderRepositoryImpl(userRepository, carRepository);
    private final ServiceOrderRepository serviceOrderRepository = new ServiceOrderRepositoryImpl(userRepository,carRepository);

    private final UserService userService = new UserServiceImpl(userRepository);
    private final CarService carService = new CarServiceImpl(carRepository);
    private final UserAuthService userAuthService = new UserAuthServiceImpl(userRepository);
    private final MyOrderService<SalesOrder> salesOrderService = new SalesOrderServiceImpl(orderRepository, carRepository);
    private final MyOrderService<ServiceOrder> serviceOrderService = new ServiceOrderServiceImpl(serviceOrderRepository);
    public final Scanner in;
    public User loggedInUser;

    public AppStart() {
        this.in = new Scanner(System.in);
        start();
    }

    private void start() {
        while (true) {
            //Todo отладка
//            showAuthMenu();
            loggedInUser = new User(Role.ADMIN, "ivan", "123123");
            loggedInUser.setName("Ivan");
            loggedInUser.setSurname("Gorshenkov");
            loggedInUser.setPhone("89106770551");
            switch (loggedInUser.getRole()) {
                case CLIENT ->
                        new ClientUIImpl(in, carService, salesOrderService, serviceOrderService, loggedInUser).showMainMenu();
                case MANAGER ->
                        new ManagerUIImpl(in, carService, salesOrderService, serviceOrderService, userService, loggedInUser).showMainMenu();
                case ADMIN ->
                        new AdminUIImpl(in, carService, salesOrderService, serviceOrderService, userService, loggedInUser).showMainMenu();
            }
        }
    }

    public void showAuthMenu() {
        Menu.create(
                        "Login",
                        "Register",
                        GO_BACK_VIEW)
                .withHeader("Please log in or register")
                .print();
        SelectAction.create(() -> {
            login();
            return EXIT;
        }, () -> {
            register();
            return EXIT;
        }, () -> {
            System.out.println("Goodbye! Have a nice day!");
            System.exit(-1);
            return EXIT;
        }).read(in);
    }

    private void register() {
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
        AuditService.logAction(loggedInUser.getUsername(), AuditAction.REGISTER);
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

    private void login() {
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
        AuditService.logAction(loggedInUser.getUsername(), AuditAction.LOG_IN);
        System.out.println("Access granted! Welcome, " + loggedInUser.getUsername() + " in Super Car's System");
    }
}
