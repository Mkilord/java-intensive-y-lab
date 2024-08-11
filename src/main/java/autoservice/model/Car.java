package autoservice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Car implements View {
    private int id;
    private String make;
    private String model;
    private int year;
    private long price;
    private CarState state = CarState.FOR_SALE;

    public Car(String make, String model, int year, long price) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
    }

    public Car(int id, String make, String model, int year, long price, CarState state) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.state = state;
    }

    @Override
    public String getView() {
        return String.format("Id: %d; Make: %s; Model: %s; Year: %d; Price: %d;",
                id, make, model, year, price);
    }
}
