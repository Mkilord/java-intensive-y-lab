package autoservice.adapter.controller;

import autoservice.adapter.service.MyOrderService;
import autoservice.domen.dto.ServiceOrderRequest;
import autoservice.domen.dto.ServiceOrderResponse;
import autoservice.domen.dto.SearchRequest;
import autoservice.domen.dto.mapper.ServiceOrderMapper;
import autoservice.domen.model.ServiceOrder;
import autoservice.domen.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Заказы на обслуживание")
@Secured({"CLIENT", "ADMIN", "MANAGER"})
@RequestMapping("/service-orders")
public class ServiceOrderController {

    ServiceOrderMapper serviceOrderMapper;
    MyOrderService<ServiceOrder> serviceOrderService;

    @GetMapping("/getAllServiceOrders")
    @Operation(summary = "Получение списка всех заказов на обслуживание")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<ServiceOrderResponse>> getAllServiceOrders() {
        var orders = serviceOrderService.getAll();
        var orderResponses = serviceOrderMapper.toResponseList(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/getServiceOrdersByStatus")
    @Operation(summary = "Получение списка заказов на обслуживание по статусу")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<ServiceOrderResponse>> getServiceOrdersByStatus(@RequestParam OrderStatus status) {
        var orders = serviceOrderService.getEntitiesByFilter(order -> order.getStatus().equals(status));
        var orderResponses = serviceOrderMapper.toResponseList(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @PostMapping("/search")
    @Operation(summary = "Поиск заказов на обслуживание по строке поиска")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<ServiceOrderResponse>> searchServiceOrders(@RequestBody @Valid SearchRequest searchRequest) {
        var searchSource = searchRequest.getSearchSource();
        var orders = serviceOrderService.getByString(serviceOrderService.getAll(), searchSource);
        var orderResponses = serviceOrderMapper.toResponseList(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение заказа на обслуживание по ID")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<ServiceOrderResponse> getServiceOrderById(@PathVariable int id) {
        var order = serviceOrderService.getById(id);
        var orderResponse = serviceOrderMapper.toResponse(order);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping
    @Operation(summary = "Создание нового заказа на обслуживание")
    @Secured("ADMIN")
    public ResponseEntity<ServiceOrderResponse> createServiceOrder(@RequestBody @Valid ServiceOrderRequest serviceOrderRequest) {
        var order = serviceOrderMapper.toEntity(serviceOrderRequest);
        var createdOrder = serviceOrderService.create(order);
        var orderResponse = serviceOrderMapper.toResponse(createdOrder);
        return ResponseEntity.ok(orderResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации о заказе на обслуживание")
    @Secured("ADMIN")
    public ResponseEntity<Void> updateServiceOrder(@PathVariable int id, @RequestBody @Valid ServiceOrderRequest serviceOrderRequest) {
        var order = serviceOrderMapper.toEntity(serviceOrderRequest);
        order.setId(id);
        serviceOrderService.update(order);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление заказа на обслуживание по ID")
    @Secured("ADMIN")
    public ResponseEntity<Void> deleteServiceOrder(@PathVariable int id) {
        var order = serviceOrderService.getById(id);
        serviceOrderService.delete(order);
        return ResponseEntity.noContent().build();
    }
}
