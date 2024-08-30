package autoservice.adapter.controller;

import autoservice.adapter.service.impl.UserService;
import autoservice.domen.dto.SearchRequest;
import autoservice.domen.dto.UserRequest;
import autoservice.domen.dto.UserResponse;
import autoservice.domen.dto.mapper.UserMapper;
import autoservice.domen.model.enums.Role;
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
@Tag(name = "Люди")
@Secured({"ADMIN", "MANAGER"})
@RequestMapping("/people")
public class PeopleController {

    UserMapper userMapper;
    UserService userService;

    @GetMapping("/getPeople")
    @Operation(summary = "Получение списка людей")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<UserResponse>> getPeople() {
        var users = userService.getAll();
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @GetMapping("/getClients")
    @Operation(summary = "Получение списка клиентов")
    @Secured({"ADMIN", "MANAGER"})
    public ResponseEntity<List<UserResponse>> getClients() {
        var users = userService.getEntitiesByFilter(user -> user.getRole().equals(Role.CLIENT));
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @PostMapping("/getSearch")
    @Operation(summary = "Возвращает людей по строке поиска")
    @Secured({"ADMIN"})
    public ResponseEntity<List<UserResponse>> getPeopleByString(@RequestBody @Valid SearchRequest searchRequest) {
        var searchSource = searchRequest.getSearchSource();
        var users = userService.getByString(userService.getAll(), searchSource);
        return ResponseEntity.ok(userMapper.toResponseList(users));
    }

    @GetMapping("/getCurrent")
    @Operation(summary = "Возвращает текущего пользователя")
    @Secured({"ADMIN", "MANAGER"})
    public UserResponse getCurrentHuman() {
        return userMapper.toResponse(userService.getCurrentUser());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя по ID")
    @Secured({"ADMIN", "MANAGER"})
    public UserResponse getUserById(@PathVariable int id) {
        var user = userService.getById(id);
        return userMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации о пользователе")
    @Secured("ADMIN")
    public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody @Valid UserRequest userRequest) {
        var user = userMapper.toEntity(userRequest);
        user.setId(id);
        userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя по ID")
    @Secured("ADMIN")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        var user = userService.getById(id);
        userService.delete(user);
        return ResponseEntity.noContent().build();
    }
}