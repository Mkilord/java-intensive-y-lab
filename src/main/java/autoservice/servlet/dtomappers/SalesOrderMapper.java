package autoservice.servlet.dtomappers;

import autoservice.model.SalesOrder;
import autoservice.servlet.dto.SalesOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
@Mapper
public interface SalesOrderMapper {
    SalesOrderMapper INSTANCE = Mappers.getMapper(SalesOrderMapper.class);

    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "customer.id", target = "customerId")

    SalesOrderDTO orderToDTO(SalesOrder order);

    @Mapping(source = "carId", target = "car.id")
    @Mapping(source = "customerId", target = "customer.id")
    SalesOrder dtoToOrder(SalesOrderDTO dto);
}
