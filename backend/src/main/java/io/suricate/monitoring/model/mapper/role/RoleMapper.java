package io.suricate.monitoring.model.mapper.role;

import io.suricate.monitoring.model.dto.user.RoleDto;
import io.suricate.monitoring.model.entity.user.Role;
import org.mapstruct.*;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Role class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        UserMapper.class
    }
)
public abstract class RoleMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Role into a RoleDto
     *
     * @param role The project to transform
     * @return The related role DTO
     */
    @Named("toRoleDtoDefault")
    @Mappings({
        @Mapping(target = "users", qualifiedByName = "toUserDtoWithoutRole")
    })
    public abstract RoleDto toRoleDtoDefault(Role role);

    /**
     * Will be used by "toUserDto" prevent cycle references on User -> Roles -> user
     * @param role The role to tranform
     * @return The role without users
     */
    @Named("toRoleDtoWithoutUsers")
    @Mappings({
        @Mapping(target = "users", ignore = true)
    })
    public abstract RoleDto toRoleDtoWithoutUsers(Role role);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of roles into a list of role dto
     *
     * @param roles The list of roles to transform
     * @return The related roles DTO
     */
    @IterableMapping(qualifiedByName = "toRoleDtoDefault")
    public abstract List<RoleDto> toRoleDtos(List<Role> roles);
}
