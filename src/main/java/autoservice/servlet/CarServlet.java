package autoservice.servlet;

import autoservice.adapter.repository.impl.CarRepositoryImpl;
import autoservice.adapter.service.CarService;
import autoservice.adapter.service.RoleException;
import autoservice.adapter.service.impl.CarServiceImpl;
import autoservice.model.Car;
import autoservice.model.CarState;
import autoservice.model.Role;
import autoservice.servlet.dto.CarDTO;
import autoservice.servlet.dtomappers.CarMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;

@WebServlet("/api/cars")
public class CarServlet extends HttpServlet {
    @Setter
    private CarService carService;
    private final CarMapper carMapper = CarMapper.INSTANCE;

    @Override
    public void init(ServletConfig config) {
        this.carService = new CarServiceImpl(new CarRepositoryImpl());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var role = getRole(req);
        resp.setContentType("application/json");

        var action = req.getParameter("action");

        if ("list".equals(action)) {
            var carDTOs = carService.getAllCarStream(role).map(carMapper::carToCarDTO).toList();
            @Cleanup
            var writer = resp.getWriter();
            writer.write(toJson(carDTOs));
        } else if ("search".equals(action)) {
            String searchString = req.getParameter("searchString");
            var cars = CarServiceImpl.filterCarsByString(carService.getAllCar(role), searchString);
            if (cars.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
                return;
            }
            var carDTOs = cars.stream().map(carMapper::carToCarDTO).toList();
            @Cleanup
            var writer = resp.getWriter();
            writer.write(toJson(carDTOs));

        } else if ("getById".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            var carOpt = carService.getById(role, id);
            if (carOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
                return;
            }
            @Cleanup
            var writer = resp.getWriter();
            writer.write(carOpt.map(carMapper::carToCarDTO).map(this::toJson).orElse("{}"));
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var role = getRole(req);
        if (role != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, RoleException.PERMISSION_ERROR_MSG);
            return;
        }

//        resp.setContentType("application/json");
        var action = req.getParameter("action");

        if ("add".equals(action)) {
            var mapper = new ObjectMapper();
            var carDTO = mapper.readValue(req.getReader(), CarDTO.class);
            var car = carMapper.carDTOToCar(carDTO);

            try {
                boolean success = carService.add(role, car);
                if (success) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_CONFLICT);

            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            }
        } else if ("delete".equals(action)) {
            var id = Integer.parseInt(req.getParameter("id"));
            var carOpt = carService.getById(role, id);
            if (carOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
                return;
            }

            try {
                boolean success = carService.delete(role, carOpt.get());
                if (success) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            }
        } else if ("update".equals(action)) {
            var objectMapper = new ObjectMapper();
            var carDTO = objectMapper.readValue(req.getReader(), CarDTO.class);

            Car car = carMapper.carDTOToCar(carDTO);
            try {
                carService.editCar(role, car);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            }
        } else if ("changeStatus".equals(action)) {
            var id = Integer.parseInt(req.getParameter("id"));
            var carOpt = carService.getById(role, id);

            if (carOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
                return;
            }
            var newState = CarState.valueOf(req.getParameter("newState"));

            Car car = carOpt.get();
            try {
                carService.changeStatus(role, car, newState);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (RoleException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private Role getRole(HttpServletRequest req) {
        var userClaims = (Claims) req.getAttribute("userClaims");
        return (Role) userClaims.get("role");
    }

    @SneakyThrows(JsonProcessingException.class)
    private String toJson(Object object) {
        var mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
