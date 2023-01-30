package com.michelin.suricate.services.api;

import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.entities.UserSetting;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.model.enums.UserRoleEnum;
import com.michelin.suricate.repositories.UserRepository;
import com.michelin.suricate.services.mapper.UserMapper;
import com.michelin.suricate.services.specifications.UserSearchSpecification;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

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

        when(userRepository.count())
                .thenReturn(0L);
        when(roleService.getRoleByName(any()))
                .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
                .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.create(user);

        assertThat(actual.getRoles())
                .contains(role);
        assertThat(actual.getUserSettings())
                .contains(userSetting);

        verify(userRepository, times(1))
                .count();
        verify(roleService, times(1))
                .getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        verify(userRepository, times(1))
                .save(user);
        verify(userSettingService, times(1))
                .createDefaultSettingsForUser(user);
    }

    @Test
    void shouldCreateUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.count())
                .thenReturn(15L);
        when(roleService.getRoleByName(any()))
                .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
                .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.create(user);

        assertThat(actual.getRoles())
                .contains(role);
        assertThat(actual.getUserSettings())
                .contains(userSetting);

        verify(userRepository, times(1))
                .count();
        verify(roleService, times(1))
                .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1))
                .save(user);
        verify(userSettingService, times(1))
                .createDefaultSettingsForUser(user);
    }

    @Test
    void shouldThrowExceptionWhenCreateUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.count())
                .thenReturn(15L);
        when(roleService.getRoleByName(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Role 'ROLE_USER' not found");

        verify(userRepository, times(1))
                .count();
        verify(roleService, times(1))
                .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(0))
                .save(any());
        verify(userSettingService, times(0))
                .createDefaultSettingsForUser(any());
    }

    @Test
    void shouldRegisterUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any()))
                .thenReturn(Optional.empty());
        when(userRepository.existsByUsername(any()))
                .thenReturn(false);
        when(userRepository.count())
                .thenReturn(15L);
        when(roleService.getRoleByName(any()))
                .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
                .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getUsername())
                .isEqualTo("username");
        assertThat(actual.getFirstname())
                .isEqualTo("firstname");
        assertThat(actual.getLastname())
                .isEqualTo("lastname");
        assertThat(actual.getEmail())
                .isEqualTo("email");
        assertThat(actual.getAvatarUrl())
                .isEqualTo("avatar");
        Assertions.assertThat(actual.getAuthenticationMethod())
                .isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles())
                .contains(role);
        assertThat(actual.getUserSettings())
                .contains(userSetting);

        verify(userRepository, times(1))
                .findByEmailIgnoreCase("email");
        verify(userRepository, times(1))
                .existsByUsername("username");
        verify(userRepository, times(1))
                .count();
        verify(roleService, times(1))
                .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1))
                .save(argThat(createdUser ->
                        createdUser.getUsername().equals("username") &&
                        createdUser.getFirstname().equals("firstname") &&
                        createdUser.getLastname().equals("lastname") &&
                        createdUser.getEmail().equals("email") &&
                        createdUser.getAvatarUrl().equals("avatar") &&
                        createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
        verify(userSettingService, times(1))
                .createDefaultSettingsForUser(any(User.class));
    }

    @Test
    void shouldRegisterUserUsernameAlreadyTaken() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any()))
                .thenReturn(Optional.empty());
        when(userRepository.existsByUsername(any()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);
        when(userRepository.count())
                .thenReturn(15L);
        when(roleService.getRoleByName(any()))
                .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
                .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getUsername())
                .isEqualTo("username3");
        assertThat(actual.getFirstname())
                .isEqualTo("firstname");
        assertThat(actual.getLastname())
                .isEqualTo("lastname");
        assertThat(actual.getEmail())
                .isEqualTo("email");
        assertThat(actual.getAvatarUrl())
                .isEqualTo("avatar");
        Assertions.assertThat(actual.getAuthenticationMethod())
                .isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles())
                .contains(role);
        assertThat(actual.getUserSettings())
                .contains(userSetting);

        verify(userRepository, times(1))
                .findByEmailIgnoreCase("email");
        verify(userRepository, times(1))
                .existsByUsername("username");
        verify(userRepository, times(1))
                .existsByUsername("username1");
        verify(userRepository, times(1))
                .existsByUsername("username2");
        verify(userRepository, times(1))
                .existsByUsername("username3");
        verify(userRepository, times(1))
                .count();
        verify(roleService, times(1))
                .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1))
                .save(argThat(createdUser ->
                        createdUser.getUsername().equals("username3") &&
                        createdUser.getFirstname().equals("firstname") &&
                        createdUser.getLastname().equals("lastname") &&
                        createdUser.getEmail().equals("email") &&
                        createdUser.getAvatarUrl().equals("avatar") &&
                        createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
        verify(userSettingService, times(1))
                .createDefaultSettingsForUser(any(User.class));
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

        when(userRepository.findByEmailIgnoreCase(any()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getId())
                .isEqualTo(1L);
        assertThat(actual.getUsername())
                .isEqualTo("existingUsername");
        assertThat(actual.getFirstname())
                .isEqualTo("firstname");
        assertThat(actual.getLastname())
                .isEqualTo("lastname");
        assertThat(actual.getEmail())
                .isEqualTo("email");
        assertThat(actual.getAvatarUrl())
                .isEqualTo("avatar");
        Assertions.assertThat(actual.getAuthenticationMethod())
                .isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles())
                .contains(role);
        assertThat(actual.getUserSettings())
                .contains(userSetting);
        assertThat(actual.getProjects())
                .contains(project);

        verify(userRepository, times(1))
                .findByEmailIgnoreCase("email");
        verify(userRepository, times(1))
                .save(argThat(createdUser ->
                        createdUser.getUsername().equals("existingUsername") &&
                        createdUser.getFirstname().equals("firstname") &&
                        createdUser.getLastname().equals("lastname") &&
                        createdUser.getEmail().equals("email") &&
                        createdUser.getAvatarUrl().equals("avatar") &&
                        createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
    }

    @Test
    void shouldUpdate() {
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

        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        User actual = userService.update(user, user.getUsername(), "firstname", "lastname", "email",
                "avatar", AuthenticationProvider.LDAP);

        assertThat(actual.getId())
                .isEqualTo(1L);
        assertThat(actual.getUsername())
                .isEqualTo("existingUsername");
        assertThat(actual.getFirstname())
                .isEqualTo("firstname");
        assertThat(actual.getLastname())
                .isEqualTo("lastname");
        assertThat(actual.getEmail())
                .isEqualTo("email");
        assertThat(actual.getAvatarUrl())
                .isEqualTo("avatar");
        Assertions.assertThat(actual.getAuthenticationMethod())
                .isEqualTo(AuthenticationProvider.LDAP);
        assertThat(actual.getRoles())
                .contains(role);
        assertThat(actual.getUserSettings())
                .contains(userSetting);
        assertThat(actual.getProjects())
                .contains(project);

        verify(userRepository, times(1))
                .save(argThat(createdUser ->
                        createdUser.getId().equals(1L) &&
                        createdUser.getUsername().equals("existingUsername") &&
                        createdUser.getFirstname().equals("firstname") &&
                        createdUser.getLastname().equals("lastname") &&
                        createdUser.getEmail().equals("email") &&
                        createdUser.getAvatarUrl().equals("avatar") &&
                        createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
    }

    @Test
    void shouldGetOne() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        Optional<User> actual = userService.getOne(1L);

        assertThat(actual)
                .isPresent()
                .contains(user);

        verify(userRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldGetOneByUsername() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsernameIgnoreCase(any()))
                .thenReturn(Optional.of(user));

        Optional<User> actual = userService.getOneByUsername("username");

        assertThat(actual)
                .isPresent()
                .contains(user);

        verify(userRepository, times(1))
                .findByUsernameIgnoreCase("username");
    }

    @Test
    void shouldGetOneByEmail() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any()))
                .thenReturn(Optional.of(user));

        Optional<User> actual = userService.getOneByEmail("email");

        assertThat(actual)
                .isPresent()
                .contains(user);

        verify(userRepository, times(1))
                .findByEmailIgnoreCase("email");
    }

    @Test
    void shouldExistsByUsername() {
        User user = new User();
        user.setId(1L);

        when(userRepository.existsByUsername(any()))
                .thenReturn(true);

        boolean actual = userService.existsByUsername("username");

        assertThat(actual)
                .isTrue();

        verify(userRepository, times(1))
                .existsByUsername("username");
    }

    @Test
    void shouldGetAll() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findAll(any(UserSearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(user)));

        Page<User> actual = userService.getAll("search", Pageable.unpaged());

        assertThat(actual)
                .isNotEmpty()
                .contains(user);

        verify(userRepository, times(1))
                .findAll(Mockito.<UserSearchSpecification>argThat(specification -> specification.getSearch().equals("search") &&
                                specification.getAttributes().isEmpty()),
                        Mockito.argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldDeleteUserByUserId() {
        Project project = new Project();
        project.setId(1L);
        Set<Project> projects = Collections.singleton(project);

        User user = new User();
        user.setId(1L);
        user.setProjects(projects);

        doNothing().when(projectService)
                .deleteUserFromProject(any(), any());
        doNothing().when(userRepository)
                .delete(any());

        userService.deleteUserByUserId(user);

        verify(projectService, times(1))
                .deleteUserFromProject(user, project);
        verify(userRepository, times(1))
                .delete(user);
    }

    @Test
    void shouldUpdateUser() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(roleService.getRoleByName(any()))
                .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        Optional<User> actual = userService.updateUser(1L, "username", "firstname", "lastname", "email", Collections.singletonList(UserRoleEnum.ROLE_USER));

        assertThat(actual)
                .isPresent();
        assertThat(actual.get().getUsername())
                .isEqualTo("username");
        assertThat(actual.get().getFirstname())
                .isEqualTo("firstname");
        assertThat(actual.get().getLastname())
                .isEqualTo("lastname");
        assertThat(actual.get().getEmail())
                .isEqualTo("email");
        assertThat(actual.get().getUsername())
                .isEqualTo("username");
        assertThat(actual.get().getRoles())
                .contains(role);

        verify(userRepository, times(1))
                .findById(1L);
        verify(roleService, times(1))
                .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void shouldUpdateUserWhenRoleNotFound() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();

        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        Optional<User> actual = userService.updateUser(1L, "username", "firstname", "lastname", "email", Collections.emptyList());

        assertThat(actual)
                .isPresent();
        assertThat(actual.get().getUsername())
                .isEqualTo("username");
        assertThat(actual.get().getFirstname())
                .isEqualTo("firstname");
        assertThat(actual.get().getLastname())
                .isEqualTo("lastname");
        assertThat(actual.get().getEmail())
                .isEqualTo("email");
        assertThat(actual.get().getUsername())
                .isEqualTo("username");
        assertThat(actual.get().getRoles())
                .isEmpty();

        verify(userRepository, times(1))
                .findById(1L);
        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void shouldNotUpdateUserWhenNotFound() {
        Role role = new Role();
        role.setId(1L);

        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        Optional<User> actual = userService.updateUser(1L, "username", "firstname", "lastname", "email", Collections.singletonList(UserRoleEnum.ROLE_USER));

        assertThat(actual)
                .isNotPresent();

        verify(userRepository, times(1))
                .findById(1L);
        verify(roleService, times(0))
                .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, times(0))
                .save(argThat(user -> user.getId().equals(1L) &&
                        user.getUsername().equals("username") &&
                        user.getFirstname().equals("firstname") &&
                        user.getLastname().equals("lastname") &&
                        user.getEmail().equals("email") &&
                        user.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
    }
}
