package autoservice.servlet;

import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.repository.impl.ServiceOrderRepositoryImpl;
import autoservice.adapter.repository.impl.UserRepositoryImpl;
import autoservice.adapter.service.MyOrderService;
import autoservice.adapter.service.OrderException;
import autoservice.adapter.service.RoleException;
import autoservice.adapter.service.UserService;
import autoservice.adapter.service.impl.ServiceOrderServiceImpl;
import autoservice.adapter.service.impl.UserServiceImpl;
import autoservice.model.OrderStatus;
import autoservice.model.ServiceOrder;
import autoservice.model.User;
import autoservice.servlet.dto.ServiceOrderDTO;
import autoservice.servlet.dtomappers.ServiceOrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/serviceorders")
public class ServiceOrderServlet extends HttpServlet {
    private final MyOrderService<ServiceOrder> serviceOrderService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ServiceOrderServlet() {
        var carRepo = new CarRepositoryImpl();
        var userRepo = new UserRepositoryImpl();
        this.userService = new UserServiceImpl(userRepo);
        this.serviceOrderService = new ServiceOrderServiceImpl(new ServiceOrderRepositoryImpl(userRepo, carRepo), carRepo);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            var user = getUser(request);
            var orderDTO = parseOrderDTO(request);
            var order = ServiceOrderMapper.INSTANCE.dtoToOrder(orderDTO);

            boolean success = serviceOrderService.add(user.getRole(), order);

            response.setStatus(success ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
        } catch (RoleException | OrderException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            var user = getUser(request);
            var orderId = Integer.parseInt(request.getParameter("orderId"));
            var orderOpt = serviceOrderService.getOrderByFilter(user, o -> o.getId() == orderId);

            if (orderOpt.isPresent()) {
                ServiceOrder order = orderOpt.get();
                boolean success = serviceOrderService.delete(user.getRole(), order);
                response.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            }
        } catch (RoleException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            var user = getUser(request);
            var orderId = Integer.parseInt(request.getParameter("orderId"));
            var status = OrderStatus.valueOf(request.getParameter("status"));

            var orderOpt = serviceOrderService.getOrderByFilter(user, o -> o.getId() == orderId);

            if (orderOpt.isPresent()) {
                ServiceOrder order = orderOpt.get();
                switch (status) {
                    case COMPLETE:
                        serviceOrderService.complete(user.getRole(), order);
                        break;
                    case CANCEL:
                        serviceOrderService.cancel(order);
                        break;
                    case IN_PROGRESS:
                        serviceOrderService.inProgress(user.getRole(), order);
                        break;
                }
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            }
        } catch (RoleException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            var user = getUser(request);
            var orders = serviceOrderService.getAllOrders(user);

            var orderDTOs = orders.stream()
                    .map(ServiceOrderMapper.INSTANCE::orderToDTO)
                    .collect(Collectors.toList());

            response.setContentType("application/json");
            response.getWriter().write(convertToJson(orderDTOs));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private User getUser(HttpServletRequest request) throws ServletException {
        var claims = (Claims) request.getAttribute("userClaims");
        var userOpt = userService.getUserByID((Integer) claims.get("userId"));
        if (userOpt.isEmpty()) {
            throw new ServletException("Invalid user id from token!");
        }
        return userOpt.get();
    }

    private ServiceOrderDTO parseOrderDTO(HttpServletRequest request) throws IllegalArgumentException {
        var orderDTO = new ServiceOrderDTO();
        try {
            String carIdParam = request.getParameter("carId");
            if (carIdParam == null || carIdParam.isEmpty()) {
                throw new IllegalArgumentException("Car ID is required");
            }
            int carId = Integer.parseInt(carIdParam);
            if (carId <= 0) {
                throw new IllegalArgumentException("Car ID must be a positive integer");
            }
            orderDTO.setCarId(carId);

            String customerIdParam = request.getParameter("customerId");
            if (customerIdParam == null || customerIdParam.isEmpty()) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            int customerId = Integer.parseInt(customerIdParam);
            if (customerId <= 0) {
                throw new IllegalArgumentException("Customer ID must be a positive integer");
            }
            orderDTO.setCustomerId(customerId);

            String statusParam = request.getParameter("status");
            if (statusParam == null || statusParam.isEmpty()) {
                throw new IllegalArgumentException("Status is required");
            }
            try {
                OrderStatus status = OrderStatus.valueOf(statusParam);
                orderDTO.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + statusParam, e);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format", e);
        }

        return orderDTO;
    }

    private String convertToJson(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }
}
