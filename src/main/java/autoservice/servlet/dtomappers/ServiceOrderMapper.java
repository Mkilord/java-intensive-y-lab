package autoservice.servlet.dtomappers;

import autoservice.model.SalesOrder;
import autoservice.model.ServiceOrder;
import autoservice.servlet.dto.SalesOrderDTO;
import autoservice.servlet.dto.ServiceOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceOrderMapper {
    ServiceOrderMapper INSTANCE = Mappers.getMapper(ServiceOrderMapper.class);

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "customer.id", target = "customerId")
    ServiceOrderDTO orderToDTO(ServiceOrder serviceOrder);

    @Mapping(source = "carId", target = "car.id")
    @Mapping(source = "customerId", target = "customer.id")
    ServiceOrder dtoToOrder(ServiceOrderDTO serviceOrderDTO);
}
