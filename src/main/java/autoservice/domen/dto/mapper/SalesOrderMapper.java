package autoservice.domen.dto.mapper;

import autoservice.domen.dto.SalesOrderRequest;
import autoservice.domen.dto.SalesOrderResponse;
import autoservice.domen.model.SalesOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalesOrderMapper {
    SalesOrderMapper INSTANCE = Mappers.getMapper(SalesOrderMapper.class);

    @Mappings({
            @Mapping(source = "customerId", target = "customer.id"),
            @Mapping(source = "carId", target = "car.id")
    })
    SalesOrder toEntity(SalesOrderRequest request);

    @Mappings({
            @Mapping(source = "customer.id", target = "customerId"),
            @Mapping(source = "car.id", target = "carId")
    })
    SalesOrderResponse toResponse(SalesOrder order);

    List<SalesOrderResponse> toResponseList(List<SalesOrder> orders);

    List<SalesOrder> toEntityList(List<SalesOrderRequest> requests);
}
