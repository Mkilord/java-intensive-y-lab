package autoservice.adapter.controller;

import autoservice.adapter.service.MyOrderService;
import autoservice.domen.dto.SalesOrderRequest;
import autoservice.domen.dto.SalesOrderResponse;
import autoservice.domen.dto.SearchRequest;
import autoservice.domen.dto.mapper.SalesOrderMapper;
import autoservice.domen.model.SalesOrder;
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
@Tag(name = "Заказы")
@Secured({"CLIENT","ADMIN", "MANAGER"})
@RequestMapping("/orders")
public class SalesOrderController {

    SalesOrderMapper salesOrderMapper;
    MyOrderService<SalesOrder> salesOrderService;

    @GetMapping("/getAllOrders")
    @Operation(summary = "Получение списка всех заказов")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<SalesOrderResponse>> getAllOrders() {
        var orders = salesOrderService.getAll();
        var orderResponses = salesOrderMapper.toResponseList(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/getOrdersByStatus")
    @Operation(summary = "Получение списка заказов по статусу")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<SalesOrderResponse>> getOrdersByStatus(@RequestParam OrderStatus status) {
        var orders = salesOrderService.getEntitiesByFilter(order -> order.getStatus().equals(status));
        var orderResponses = salesOrderMapper.toResponseList(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @PostMapping("/search")
    @Operation(summary = "Получение списка заказов по строке поиска")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<SalesOrderResponse>> searchOrders(@RequestBody @Valid SearchRequest searchRequest) {
        var searchSource = searchRequest.getSearchSource();
        var orders = salesOrderService.getByString(salesOrderService.getAll(), searchSource);
        var orderResponses = salesOrderMapper.toResponseList(orders);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение заказа по ID")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<SalesOrderResponse> getOrderById(@PathVariable int id) {
        var order = salesOrderService.getById(id);
        var orderResponse = salesOrderMapper.toResponse(order);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping
    @Operation(summary = "Создание нового заказа")
    @Secured("ADMIN")
    public ResponseEntity<SalesOrderResponse> createOrder(@RequestBody @Valid SalesOrderRequest salesOrderRequest) {
        var order = salesOrderMapper.toEntity(salesOrderRequest);
        var createdOrder = salesOrderService.create(order);
        var orderResponse = salesOrderMapper.toResponse(createdOrder);
        return ResponseEntity.ok(orderResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации о заказе")
    @Secured("ADMIN")
    public ResponseEntity<Void> updateOrder(@PathVariable int id, @RequestBody @Valid SalesOrderRequest salesOrderRequest) {
        var order = salesOrderMapper.toEntity(salesOrderRequest);
        order.setId(id);
        salesOrderService.update(order);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление заказа по ID")
    @Secured("ADMIN")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        var order = salesOrderService.getById(id);
        salesOrderService.delete(order);
        return ResponseEntity.noContent().build();
    }
}

