package autoservice.servlet.dtomappers;

import autoservice.model.User;
import autoservice.servlet.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);

    @Mapping(target = "password", ignore = true)
    User toUser(UserDTO userDTO);
}
