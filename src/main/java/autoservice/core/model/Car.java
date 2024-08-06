package autoservice.core.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Car implements View {
    private static int idCounter = 1;
    private final int id;
    private String make;
    private String model;
    private int year;
    private long price;
    private CarState state = CarState.FOR_SALE;

    public Car(String make, String model, int year, long price) {
        this.id = idCounter++;
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
    }

    @Override
    public String getView() {
        return String.format("Id: %d; Make: %s; Model: %s; Year: %d; Price: %d;",
                id, make, model, year, price);
    }
}
