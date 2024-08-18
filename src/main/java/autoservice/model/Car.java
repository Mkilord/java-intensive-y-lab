package autoservice.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor()
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Car implements View {

    int id;
    CarState state;
    String make;
    String model;
    int year;
    long price;

    public Car(CarState state, String make, String model, int year, long price) {
        this(0, state, make, model, year, price);
    }

    public Car(String make, String model, int year, long price) {
        this(CarState.FOR_SALE, make, model, year, price);
    }

    @Override
    public String getView() {
        return String.format("Id: %d; Make: %s; Model: %s; Year: %d; Price: %d;",
                id, make, model, year, price);
    }
}
