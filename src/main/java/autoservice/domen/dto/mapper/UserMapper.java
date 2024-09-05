package autoservice.domen.dto.mapper;

import autoservice.domen.dto.UserRequest;
import autoservice.domen.dto.UserResponse;
import autoservice.domen.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserRequest userRequest);

    List<UserResponse> toResponseList(List<User> sources);

    List<User> toEntityList(List<UserRequest> requests);
}
