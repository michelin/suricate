package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.UserSetting;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.repositories.UserRepository;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private RoleService roleService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserSettingService userSettingService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateAdminUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.count()).thenReturn(0L);
        when(roleService.getRoleByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(userSettingService.createDefaultSettingsForUser(any())).thenReturn(Collections.singletonList(userSetting));

        User actual = userService.create(user);
        assertThat(actual.getRoles()).contains(role);
        assertThat(actual.getUserSettings()).contains(userSetting);

        verify(userRepository, times(1)).count();
        verify(roleService, times(1)).getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        verify(userRepository, times(1)).save(user);
        verify(userSettingService, times(1)).createDefaultSettingsForUser(user);
    }

    @Test
    void shouldCreateUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.count()).thenReturn(15L);
        when(roleService.getRoleByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(userSettingService.createDefaultSettingsForUser(any())).thenReturn(Collections.singletonList(userSetting));

        User actual = userService.create(user);
        assertThat(actual.getRoles()).contains(role);
        assertThat(actual.getUserSettings()).contains(userSetting);

        verify(userRepository, times(1)).count();
        verify(roleService, times(1)).getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1)).save(user);
        verify(userSettingService, times(1)).createDefaultSettingsForUser(user);
    }

    @Test
    void shouldThrowExceptionWhenCreateUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.count()).thenReturn(15L);
        when(roleService.getRoleByName(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Role 'ROLE_USER' not found");

        verify(userRepository, times(1)).count();
        verify(roleService, times(1)).getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(0)).save(any());
        verify(userSettingService, times(0)).createDefaultSettingsForUser(any());
    }

    @Test
    void shouldRegisterUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.count()).thenReturn(15L);
        when(roleService.getRoleByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(userSettingService.createDefaultSettingsForUser(any())).thenReturn(Collections.singletonList(userSetting));

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getEmail()).isEqualTo("email");
        assertThat(actual.getAvatarUrl()).isEqualTo("avatar");
        assertThat(actual.getAuthenticationMethod()).isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles()).contains(role);
        assertThat(actual.getUserSettings()).contains(userSetting);

        verify(userRepository, times(1)).findByEmailIgnoreCase("email");
        verify(userRepository, times(1)).existsByUsername("username");
        verify(userRepository, times(1)).count();
        verify(roleService, times(1)).getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userSettingService, times(1)).createDefaultSettingsForUser(any(User.class));
    }


    @Test
    void shouldRegisterUserUsernameAlreadyTaken() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());
        when(userRepository.existsByUsername(any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(userRepository.count()).thenReturn(15L);
        when(roleService.getRoleByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(userSettingService.createDefaultSettingsForUser(any())).thenReturn(Collections.singletonList(userSetting));

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getUsername()).isEqualTo("username3");
        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getEmail()).isEqualTo("email");
        assertThat(actual.getAvatarUrl()).isEqualTo("avatar");
        assertThat(actual.getAuthenticationMethod()).isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles()).contains(role);
        assertThat(actual.getUserSettings()).contains(userSetting);

        verify(userRepository, times(1)).findByEmailIgnoreCase("email");
        verify(userRepository, times(1)).existsByUsername("username");
        verify(userRepository, times(1)).existsByUsername("username1");
        verify(userRepository, times(1)).existsByUsername("username2");
        verify(userRepository, times(1)).existsByUsername("username3");
        verify(userRepository, times(1)).count();
        verify(roleService, times(1)).getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userSettingService, times(1)).createDefaultSettingsForUser(any(User.class));
    }

    @Test
    void shouldUpdateRegisteredUser() {
        Project project = new Project();
        project.setId(1L);

        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("existingUsername");
        user.setRoles(Collections.singleton(role));
        user.setUserSettings(Collections.singleton(userSetting));
        user.setProjects(Collections.singleton(project));

        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getUsername()).isEqualTo("existingUsername");
        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getEmail()).isEqualTo("email");
        assertThat(actual.getAvatarUrl()).isEqualTo("avatar");
        assertThat(actual.getAuthenticationMethod()).isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles()).contains(role);
        assertThat(actual.getUserSettings()).contains(userSetting);
        assertThat(actual.getProjects()).contains(project);

        verify(userRepository, times(1)).findByEmailIgnoreCase("email");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
