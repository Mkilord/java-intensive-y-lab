package autoservice.servlet.dtomappers;


import autoservice.model.Car;
import autoservice.servlet.dto.CarDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for mapping between {@link Car} and {@link CarDTO}.
 */
@Mapper
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    /**
     * Converts a {@link Car} entity to a {@link CarDTO}.
     *
     * @param car the car entity
     * @return the car DTO
     */
    @Mapping(source = "state", target = "state")
    CarDTO carToCarDTO(Car car);

    /**
     * Converts a {@link CarDTO} to a {@link Car} entity.
     *
     * @param carDTO the car DTO
     * @return the car entity
     */
    @Mapping(source = "state", target = "state")
    Car carDTOToCar(CarDTO carDTO);
}
