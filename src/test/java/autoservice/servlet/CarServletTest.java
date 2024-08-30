package autoservice.servlet;

import autoservice.adapter.service.CarService;
import autoservice.adapter.service.RoleException;
import autoservice.model.Car;
import autoservice.model.CarState;
import autoservice.model.Role;
import autoservice.servlet.dto.CarDTO;
import autoservice.servlet.dtomappers.CarMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CarServletTest {

    @Mock
    private CarService carService;

    @Mock
    private ServletConfig config;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse resp;

    @InjectMocks
    private CarServlet carServlet;

    private Car car;
    private Claims claims;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carServlet.init(config);
        carServlet.setCarService(carService);
        car = new Car();
        car.setId(1);
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setYear(2020);
        car.setPrice(30000);
        car.setState(CarState.FOR_SALE);

        claims = mock(Claims.class);
        when(claims.get("role")).thenReturn(Role.ADMIN);
    }

    @Test
    void doGet_listAction() throws Exception {
        when(req.getParameter("action")).thenReturn("list");
        when(req.getAttribute("userClaims")).thenReturn(claims);

        Car car = new Car(1, CarState.FOR_SALE, "Toyota", "Camry", 2020, 30000L);
        when(carService.getAllCarStream(Role.ADMIN)).thenReturn(Stream.of(car));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);

        carServlet.doGet(req, resp);

        verify(resp).setContentType("application/json");

        writer.flush();
        String actualResponse = stringWriter.toString().trim();

        String expectedResponse = "[{\"id\":1,\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2020,\"price\":30000,\"state\":\"FOR_SALE\"}]";
        assertEquals(expectedResponse, actualResponse);
    }


    @Test
    void doGet_searchAction_carFound() throws Exception {
        when(req.getParameter("action")).thenReturn("search");
        when(req.getParameter("searchString")).thenReturn("Toyota");
        when(req.getAttribute("userClaims")).thenReturn(claims);

        Car car = new Car(1, CarState.FOR_SALE, "Toyota", "Camry", 2020, 30000);
        when(carService.getAllCar(Role.ADMIN)).thenReturn(List.of(car));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);

        carServlet.doGet(req, resp);

        verify(resp).setContentType("application/json");

        writer.flush();
        String actualResponse = stringWriter.toString().trim();

        String expectedResponse = "[{\"id\":1,\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2020,\"price\":30000,\"state\":\"FOR_SALE\"}]";

        assertEquals(expectedResponse, actualResponse);
    }


    @Test
    void doGet_searchAction_carNotFound() throws Exception {
        when(req.getParameter("action")).thenReturn("search");
        when(req.getParameter("searchString")).thenReturn("Nonexistent");
        when(carService.getAllCar(Role.ADMIN)).thenReturn(Collections.emptyList());
        when(req.getAttribute("userClaims")).thenReturn(claims);

        carServlet.doGet(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
    }

    @Test
    void doGet_getByIdAction_carFound() throws Exception {
        when(req.getParameter("action")).thenReturn("getById");
        when(req.getParameter("id")).thenReturn("1");
        when(carService.getById(Role.ADMIN, 1)).thenReturn(Optional.of(car));
        when(req.getAttribute("userClaims")).thenReturn(claims);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(resp.getWriter()).thenReturn(writer);

        carServlet.doGet(req, resp);

        verify(resp).setContentType("application/json");
        writer.flush();
        assertEquals("{\"id\":1,\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2020,\"price\":30000,\"state\":\"FOR_SALE\"}",
                stringWriter.toString().trim());
    }

    @Test
    void doGet_getByIdAction_carNotFound() throws Exception {
        when(req.getParameter("action")).thenReturn("getById");
        when(req.getParameter("id")).thenReturn("1");
        when(carService.getById(Role.ADMIN, 1)).thenReturn(Optional.empty());
        when(req.getAttribute("userClaims")).thenReturn(claims);

        carServlet.doGet(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
    }

    @Test
    void doPost_addAction_success() throws Exception {
        when(req.getParameter("action")).thenReturn("add");
        when(req.getAttribute("userClaims")).thenReturn(claims);

        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 30000, CarState.FOR_SALE);
        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        String jsonCarDTO = new ObjectMapper().writeValueAsString(carDTO);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(jsonCarDTO)));
        when(carService.add(Role.ADMIN, car)).thenReturn(true);

        carServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPost_addAction_failure() throws Exception {
        when(req.getParameter("action")).thenReturn("add");
        when(req.getAttribute("userClaims")).thenReturn(claims);

        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 30000, CarState.FOR_SALE);
        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        String jsonCarDTO = new ObjectMapper().writeValueAsString(carDTO);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(jsonCarDTO)));
        when(carService.add(Role.ADMIN, car)).thenReturn(false);

        carServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    void doPost_deleteAction_success() throws Exception {
        when(req.getParameter("action")).thenReturn("delete");
        when(req.getParameter("id")).thenReturn("1");
        when(req.getAttribute("userClaims")).thenReturn(claims);
        when(carService.getById(Role.ADMIN, 1)).thenReturn(Optional.of(car));
        when(carService.delete(Role.ADMIN, car)).thenReturn(true);

        carServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPost_deleteAction_carNotFound() throws Exception {
        when(req.getParameter("action")).thenReturn("delete");
        when(req.getParameter("id")).thenReturn("1");
        when(req.getAttribute("userClaims")).thenReturn(claims);
        when(carService.getById(Role.ADMIN, 1)).thenReturn(Optional.empty());

        carServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
    }

    @Test
    void doPost_updateAction_success() throws Exception {
        when(req.getParameter("action")).thenReturn("update");
        when(req.getAttribute("userClaims")).thenReturn(claims);

        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 30000, CarState.FOR_SALE);
        Car car = CarMapper.INSTANCE.carDTOToCar(carDTO);
        String jsonCarDTO = new ObjectMapper().writeValueAsString(carDTO);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(jsonCarDTO)));
        doNothing().when(carService).editCar(Role.ADMIN, car);

        carServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPost_updateAction_failure() throws Exception {
        when(req.getParameter("action")).thenReturn("update");
        when(req.getAttribute("userClaims")).thenReturn(claims);

        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 30000, CarState.FOR_SALE);
        String jsonCarDTO = new ObjectMapper().writeValueAsString(carDTO);

        when(req.getReader()).thenReturn(new BufferedReader(new StringReader(jsonCarDTO)));

        doThrow(new RoleException("Update failed")).when(carService).editCar(Role.ADMIN, CarMapper.INSTANCE.carDTOToCar(carDTO));

        carServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_FORBIDDEN, "Update failed");
    }


    @Test
    void doPost_changeStatusAction_success() throws Exception {
        when(req.getParameter("action")).thenReturn("changeStatus");
        when(req.getParameter("id")).thenReturn("1");
        when(req.getParameter("newState")).thenReturn(CarState.SOLD.name());
        when(req.getAttribute("userClaims")).thenReturn(claims);
        when(carService.getById(Role.ADMIN, 1)).thenReturn(Optional.of(car));

        doNothing().when(carService).changeStatus(Role.ADMIN, car, CarState.SOLD);

        carServlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPost_changeStatusAction_failure() throws Exception {
        when(req.getParameter("action")).thenReturn("changeStatus");
        when(req.getParameter("id")).thenReturn("1");
        when(req.getParameter("newState")).thenReturn(CarState.SOLD.name());
        when(req.getAttribute("userClaims")).thenReturn(claims);

        when(carService.getById(Role.CLIENT, 1)).thenReturn(Optional.empty());

        carServlet.doPost(req, resp);

        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
    }

}
