//package autoservice.adapter.ui.common.impl;
//
//import autoservice.adapter.service.CarService;
//import autoservice.adapter.service.MyOrderService;
//import autoservice.adapter.service.impl.AuditService;
//import autoservice.adapter.service.impl.CarServiceImpl;
//import autoservice.adapter.ui.common.AppUI;
//import autoservice.adapter.ui.components.utils.ConsoleUtils;
//import autoservice.adapter.ui.components.utils.ModalMenu;
//import autoservice.adapter.ui.components.utils.Viewer;
//import autoservice.model.*;
//
//import java.util.List;
//import java.util.Scanner;
//
//import static autoservice.adapter.ui.components.menu.SelectAction.EXIT;
//import static autoservice.adapter.ui.components.utils.ConsoleUtils.NOT_BLANK_STR;
//import static autoservice.adapter.ui.components.utils.ConsoleUtils.readStr;
//
//public abstract class AppUIImpl implements AppUI {
//    protected final CarService carService;
//    protected final MyOrderService<SalesOrder> salesOrderService;
//    protected final MyOrderService<ServiceOrder> serviceOrderService;
//    protected final User loggedInUser;
//
//    protected final Scanner in;
//
//    protected AppUIImpl(Scanner in, CarService carService,
//                        MyOrderService<SalesOrder> salesOrderService,
//                        MyOrderService<ServiceOrder> serviceOrderService, User loggedInUser) {
//        this.carService = carService;
//        this.salesOrderService = salesOrderService;
//        this.serviceOrderService = serviceOrderService;
//        this.loggedInUser = loggedInUser;
//        this.in = in;
//    }
//
//    @Override
//    public <T extends Order> void showOrderSearch(List<T> orders) {
//        System.out.println("Input your request:");
//        var input = readStr(in, NOT_BLANK_STR);
//        orders = MyOrderService.filterOrdersByString(orders, input);
//        Viewer.viewAll(orders, "Not found!");
//    }
//
//    @Override
//    public void showCars(List<Car> cars) {
//        Viewer.viewAll(cars, "No cars been found!");
//    }
//
//    @Override
//    public void showCarSearch(List<Car> cars) {
//        System.out.println("Input your request:");
//        var input = readStr(in, NOT_BLANK_STR);
//        cars = CarServiceImpl.filterCarsByString(cars, input);
//        Viewer.viewAll(cars, "Not found!");
//    }
//
//    @Override
//    public void selectCar(List<Car> cars) {
//        System.out.println("Input car's id:");
//        var id = ConsoleUtils.readInt(in);
//        var carOpt = cars.stream().filter(car -> car.getId() == id).findFirst();
//        carOpt.ifPresentOrElse(car -> showCarOptions(carOpt.get()),
//                () -> System.out.println("Car with this id not found!"));
//    }
//
//    @Override
//    public void showProfile() {
//        System.out.println("Your profile:");
//        Viewer.viewOf(loggedInUser);
//    }
//
//    @Override
//    public <T extends Order> void showOrders(List<T> orders, MyOrderService<T> myOrderService) {
//        System.out.println("Orders:");
//        Viewer.viewAll(orders, "Not found!");
//        if (orders.isEmpty()) return;
//        showOrdersMenu(orders, myOrderService);
//    }
//
//    @Override
//    public <T extends Order> void selectOrder(List<T> orders, MyOrderService<T> orderService) {
//        System.out.println("Input order's id:");
//        var id = ConsoleUtils.readInt(in);
//        var orderOpt = orders.stream().filter(order -> order.getId() == id).findFirst();
//        orderOpt.ifPresentOrElse(car -> showOrderOptions(orderOpt.get(), orderService),
//                () -> System.out.println("Order with this id not found!"));
//    }
//
//    @Override
//    public <T extends Order> void showCancelOrder(T order, MyOrderService<T> orderService) {
//        ModalMenu.getYesOrNoDialog(in, "Do you have cancel order?", () -> {
//            orderService.cancel(order);
//            AuditService.logAction(loggedInUser.getUsername(), AuditAction.CANCEL_ORDER, order.getView());
//            System.out.println("Your order canceled!");
//            return EXIT;
//        }, () -> {
//            System.out.println("Operation canceled!");
//            return EXIT;
//        });
//    }
//}
