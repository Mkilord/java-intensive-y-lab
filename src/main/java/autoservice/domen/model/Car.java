package autoservice.domen.model;

import autoservice.domen.model.enums.CarState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Car {
    int id;
    CarState state;
    String make;
    String model;
    int year;
    long price;
}
