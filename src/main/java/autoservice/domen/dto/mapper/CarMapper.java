package autoservice.domen.dto.mapper;

import autoservice.domen.dto.CarRequest;
import autoservice.domen.dto.CarResponse;
import autoservice.domen.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    CarResponse toResponse(Car car);

    @Mapping(target = "id", ignore = true)
    Car toEntity(CarRequest carRequest);

    List<CarResponse> toResponseList(List<Car> cars);
}
