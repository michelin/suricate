package io.suricate.monitoring.model.mapper.role;

import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.entity.user.User;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for User class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        RoleMapper.class
    }
)
public abstract class UserMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a User into a UserDto
     *
     * @param user The user to transform
     * @return The related user DTO
     */
    @Named("toUserDtoDefault")
    @Mappings({
        @Mapping(target = "roles", qualifiedByName = "toRoleDtosWithoutUsers"),
        @Mapping(target = "fullname", expression = "java(String.format(\"%s %s\", user.getFirstname(), user.getLastname()))"),
        @Mapping(target = "password", ignore = true)
    })
    public abstract UserDto toUserDtoDefault(User user);

    @Named("toUserDtoWithoutRole")
    @Mappings({
        @Mapping(target = "roles", ignore = true),
        @Mapping(target = "fullname", expression = "java(String.format(\"%s %s\", user.getFirstname(), user.getLastname()))"),
        @Mapping(target = "password", ignore = true)
    })
    public abstract UserDto toUserDtoWithoutRole(User user);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of Users into a list of UserDto
     *
     * @param users The list of user to transform
     * @return The related users DTO
     */
    @Named("toUserDtosDefault")
    @IterableMapping(qualifiedByName = "toUserDtoDefault")
    public abstract List<UserDto> toUserDtosDefault(List<User> users);

    /**
     * Tranform a list of Users into a list of UserDto without role
     *
     * @param users The list of user to transform
     * @return The related users DTO
     */
    @Named("toUserDtosWithoutRole")
    @IterableMapping(qualifiedByName = "toUserDtoWithoutRole")
    public abstract List<UserDto> toUserDtosWithoutRole(List<User> users);
}
