package autoservice.adapter.config;

import autoservice.adapter.repository.CarRepository;
import autoservice.adapter.repository.OrderRepository;
import autoservice.adapter.repository.UserRepository;
import autoservice.adapter.service.CarService;
import autoservice.adapter.service.UserAuthService;
import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.service.impl.CarServiceImpl;
import autoservice.adapter.repository.impl.OrderRepositoryImpl;
import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.model.Car;
import autoservice.model.Role;
import autoservice.model.SalesOrder;
import autoservice.model.User;

/**
 * Utility class for loading repositories with default data.
 */
public class Loader {
    /**
     * Creates a {@link UserRepository} and populates it with default user data.
     *
     * @return a {@link UserRepository} populated with default user data
     */
    public static UserRepository getUserRepoWithDefaultData() {
        var userRepo = new UserRepositoryImpl();
        /*Clients*/
        userRepo.create(getUser(Role.CLIENT, "john_doe", "password123", "John", "Doe", "89001234567"));
        userRepo.create(getUser(Role.CLIENT, "jane_smith", "qwerty789", "Jane", "Smith", "89009876543"));
        userRepo.create(getUser(Role.CLIENT, "alex_brown", "abc123", "Alex", "Brown", "89001112233"));
        userRepo.create(getUser(Role.CLIENT, "maria_white", "password321", "Maria", "White", "89005556677"));
        userRepo.create(getUser(Role.CLIENT, "peter_black", "zxcvbnm", "Peter", "Black", "89002223344"));
        userRepo.create(getUser(Role.CLIENT, "lisa_yellow", "asdfgh", "Lisa", "Yellow", "89004445566"));
        userRepo.create(getUser(Role.CLIENT, "tom_green", "qwertyu", "Tom", "Green", "89006667788"));
        userRepo.create(getUser(Role.CLIENT, "susan_blue", "poiuytrewq", "Susan", "Blue", "89007778899"));
        userRepo.create(getUser(Role.CLIENT, "mark_red", "lkjhgfdsa", "Mark", "Red", "89008889900"));
        userRepo.create(getUser(Role.CLIENT, "nancy_gray", "mnbvcxz", "Nancy", "Gray", "89009990011"));
        userRepo.create(getUser(Role.CLIENT, "oliver_king", "king123", "Oliver", "King", "89001230987"));
        userRepo.create(getUser(Role.CLIENT, "emily_clark", "emily2023", "Emily", "Clark", "89004567890"));
        userRepo.create(getUser(Role.CLIENT, "daniel_jones", "daniel456", "Daniel", "Jones", "89003216548"));
        userRepo.create(getUser(Role.CLIENT, "sophie_wilson", "sophie987", "Sophie", "Wilson", "89002135647"));
        userRepo.create(getUser(Role.CLIENT, "jack_taylor", "jackpass", "Jack", "Taylor", "89006543210"));
        /*Managers*/
        userRepo.create(getUser(Role.MANAGER, "amelia_evans", "amelia654", "Amelia", "Evans", "89009871234"));
        userRepo.create(getUser(Role.MANAGER, "charlie_moore", "charlie789", "Charlie", "Moore", "89003456789"));
        userRepo.create(getUser(Role.MANAGER, "lucy_harris", "lucy111", "Lucy", "Harris", "89005678901"));
        userRepo.create(getUser(Role.MANAGER, "harry_white", "harry000", "Harry", "White", "89004321987"));
        /*Admins*/
        userRepo.create(getUser(Role.CLIENT, "ivan", "12345", "Ivan", "Doe", "89001234567"));
        userRepo.create(getUser(Role.ADMIN, "ella_lewis", "ella999", "Ella", "Lewis", "89001239876"));
        userRepo.create(getUser(Role.ADMIN, "michael_scott", "michael444", "Michael", "Scott", "89008765432"));
        userRepo.create(getUser(Role.ADMIN, "chloe_adams", "chloe333", "Chloe", "Adams", "89007654321"));
        return userRepo;
    }

    /**
     * Creates a {@link CarRepository} and populates it with default car data.
     *
     * @return a {@link CarRepository} populated with default car data
     */
    public static CarRepository getCarRepoWithDefaultData() {
        var carRepo = new CarRepositoryImpl();
        carRepo.create(getCar("Toyota", "Camry", 2015, 15000000));
        carRepo.create(getCar("Honda", "Civic", 2018, 18000000));
        carRepo.create(getCar("Ford", "Mustang", 2020, 35000000));
        carRepo.create(getCar("Chevrolet", "Malibu", 2017, 16000000));
        carRepo.create(getCar("Nissan", "Altima", 2019, 20000000));
        carRepo.create(getCar("BMW", "3 Series", 2021, 40000000));
        carRepo.create(getCar("Audi", "A4", 2020, 38000000));
        carRepo.create(getCar("Mercedes-Benz", "C-Class", 2018, 30000000));
        carRepo.create(getCar("Volkswagen", "Jetta", 2016, 14000000));
        carRepo.create(getCar("Hyundai", "Elantra", 2019, 17000000));
        carRepo.create(getCar("Subaru", "Outback", 2017, 22000000));
        carRepo.create(getCar("Mazda", "CX-5", 2020, 27000000));
        carRepo.create(getCar("Tesla", "Model 3", 2021, 45000000));
        carRepo.create(getCar("Kia", "Optima", 2018, 16000000));
        carRepo.create(getCar("Lexus", "ES", 2019, 35000000));
        return carRepo;
    }

    /**
     * Creates an {@link OrderRepository} and populates it with default order data.
     *
     * @param userService the service to retrieve users
     * @param carService  the service to retrieve cars
     * @return an {@link OrderRepository} populated with default order data
     */
    public static OrderRepository getOrderRepoWithDefaultData(UserAuthService userService, CarService carService) {
        var orderRepo = new OrderRepositoryImpl();
        orderRepo.create(getOrder(userService.getByUsername("ivan").get(), carService.getById(2).get()));
        orderRepo.create(getOrder(userService.getByUsername("daniel_jones").get(), carService.getById(3).get()));
        orderRepo.create(getOrder(userService.getByUsername("emily_clark").get(), carService.getById(5).get()));
        return orderRepo;
    }

    /**
     * Creates a {@link CarService} with default car data.
     *
     * @return a {@link CarService} populated with default car data
     */
    public static CarService getCarServiceWithDefaultData() {
        return new CarServiceImpl(getCarRepoWithDefaultData());
    }

    /**
     * Creates a {@link SalesOrder} with the given customer and car.
     *
     * @param customer the customer who placed the order
     * @param car      the car that was ordered
     * @return a new {@link SalesOrder}
     */
    private static SalesOrder getOrder(User customer, Car car) {
        return new SalesOrder(customer, car);
    }

    /**
     * Creates a {@link Car} with the given details.
     *
     * @param make  the make of the car
     * @param model the model of the car
     * @param year  the year the car was manufactured
     * @param price the price of the car
     * @return a new {@link Car}
     */
    private static Car getCar(String make, String model, int year, long price) {
        return new Car(make, model, year, price);
    }

    /**
     * Creates a {@link User} with the given details.
     *
     * @param role     the role of the user
     * @param username the username of the user
     * @param password the password of the user
     * @param name     the name of the user
     * @param surname  the surname of the user
     * @param phone    the phone number of the user
     * @return a new {@link User}
     */
    private static User getUser(Role role, String username, String password,
                                String name, String surname, String phone) {
        var out = new User(role, username, password);
        out.setName(name);
        out.setSurname(surname);
        out.setPhone(phone);
        return out;
    }
}
