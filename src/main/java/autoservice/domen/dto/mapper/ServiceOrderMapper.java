package autoservice.domen.dto.mapper;

import autoservice.domen.dto.ServiceOrderRequest;
import autoservice.domen.dto.ServiceOrderResponse;
import autoservice.domen.model.ServiceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")

public interface ServiceOrderMapper {

    ServiceOrderMapper INSTANCE = Mappers.getMapper(ServiceOrderMapper.class);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "carId", target = "car.id")
    ServiceOrder toEntity(ServiceOrderRequest request);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "car.id", target = "carId")
    ServiceOrderResponse toResponse(ServiceOrder serviceOrder);

    List<ServiceOrderResponse> toResponseList(List<ServiceOrder> serviceOrders);
}