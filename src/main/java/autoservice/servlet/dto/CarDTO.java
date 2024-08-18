package autoservice.servlet.dto;

import autoservice.model.CarState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for {@link autoservice.model.Car}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {
    private int id;
    private String make;
    private String model;
    private int year;
    private long price;
    private CarState state;
}