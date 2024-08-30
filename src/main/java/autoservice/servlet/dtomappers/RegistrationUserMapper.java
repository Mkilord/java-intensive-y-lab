package autoservice.servlet.dtomappers;

import autoservice.model.Role;
import autoservice.model.User;
import autoservice.servlet.dto.RegistrationUserDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistrationUserMapper {

    RegistrationUserMapper INSTANCE = Mappers.getMapper(RegistrationUserMapper.class);

    RegistrationUserDTO userToUserDTO(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    User userDTOToUser(RegistrationUserDTO registrationUserDTO);

    @AfterMapping
    default void afterMapping(@MappingTarget User user) {
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }
    }
}
