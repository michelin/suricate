package io.suricate.monitoring.model.mapper.role;

import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.entity.user.User;
import org.mapstruct.*;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for User class
 */
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
        @Mapping(target = "roles", qualifiedByName = "toRoleDtoWithoutUsers")
    })
    public abstract UserDto toUserDtoDefault(User user);

    @Named("toUserDtoWithoutRole")
    @Mappings({
        @Mapping(target = "roles", ignore = true)
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
    @IterableMapping(qualifiedByName = "toUserDtoDefault")
    public abstract List<UserDto> toUserDtos(List<User> users);
}
