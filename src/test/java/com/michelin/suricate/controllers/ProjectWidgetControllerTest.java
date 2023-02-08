package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import com.michelin.suricate.model.entities.*;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.api.ProjectWidgetService;
import com.michelin.suricate.services.mapper.ProjectWidgetMapper;
import com.michelin.suricate.utils.exceptions.ApiException;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectWidgetControllerTest {
    @Mock
    private ProjectWidgetService projectWidgetService;

    @Mock
    private ProjectWidgetMapper projectWidgetMapper;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectWidgetController projectWidgetController;

    @Test
    void shouldGetByIdNotFound() {
        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectWidgetController.getById(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("ProjectWidget '1' not found");
    }

    @Test
    void shouldGetById() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(projectWidgetMapper.toProjectWidgetDTO(any()))
                .thenReturn(projectWidgetResponseDto);

        ResponseEntity<ProjectWidgetResponseDto> actual = projectWidgetController.getById(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(projectWidgetResponseDto);
    }

    @Test
    void shouldGetByProjectNotFound() {
        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectWidgetController.getByProject("token"))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldGetByProjectNoGrid() {
        Project project = new Project();
        project.setId(1L);

        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.of(project));

        ResponseEntity<List<ProjectWidgetResponseDto>> actual = projectWidgetController.getByProject("token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldGetByProject() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.of(project));
        when(projectWidgetMapper.toProjectWidgetsDTOs(any()))
                .thenReturn(Collections.singletonList(projectWidgetResponseDto));

        ResponseEntity<List<ProjectWidgetResponseDto>> actual = projectWidgetController.getByProject("token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(projectWidgetResponseDto);
    }

    @Test
    void shouldEditByProjectNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectWidgetController.editByProject(localUser, 1L, projectWidgetRequestDto))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("ProjectWidget '1' not found");
    }

    @Test
    void shouldEditByProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> projectWidgetController.editByProject(localUser, 1L, projectWidgetRequestDto))
                .isInstanceOf(ApiException.class)
                .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldEditByProject() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(true);
        when(projectWidgetMapper.toProjectWidgetDTO(any()))
                .thenReturn(projectWidgetResponseDto);

        ResponseEntity<ProjectWidgetResponseDto> actual = projectWidgetController.editByProject(localUser, 1L, projectWidgetRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(projectWidgetResponseDto);
    }

    @Test
    void shouldAddProjectWidgetToProjectNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectWidgetController.addProjectWidgetToProject(localUser, "token", 1L, projectWidgetRequestDto))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldAddProjectWidgetToProjectNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> projectWidgetController.addProjectWidgetToProject(localUser, "token", 1L, projectWidgetRequestDto))
                .isInstanceOf(ApiException.class)
                .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldAddProjectWidgetToProjectGridNotFound() {
        Project project = new Project();
        project.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> projectWidgetController.addProjectWidgetToProject(localUser, "token", 1L, projectWidgetRequestDto))
                .isInstanceOf(ApiException.class)
                .hasMessage("Grid '1' not found for project token");
    }

    @Test
    void shouldAddProjectWidgetToProject() {
        ProjectWidgetResponseDto projectWidgetResponseDto = new ProjectWidgetResponseDto();
        projectWidgetResponseDto.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ProjectWidgetRequestDto projectWidgetRequestDto = new ProjectWidgetRequestDto();
        projectWidgetRequestDto.setWidgetId(1L);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(projectService.getOneByToken(any()))
                .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(true);
        when(projectWidgetMapper.toProjectWidgetEntity(any(), any()))
                .thenReturn(projectWidget);
        when(projectWidgetMapper.toProjectWidgetDTO(any()))
                .thenReturn(projectWidgetResponseDto);

        ResponseEntity<ProjectWidgetResponseDto> actual = projectWidgetController.addProjectWidgetToProject(localUser, "token", 1L, projectWidgetRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isEqualTo(projectWidgetResponseDto);
    }

    @Test
    void shouldDeleteByIdNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectWidgetController.deleteById(localUser, 1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("ProjectWidget '1' not found");
    }

    @Test
    void shouldDeleteByIdNotAuthorized() {
        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(false);

        assertThatThrownBy(() -> projectWidgetController.deleteById(localUser, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldDeleteById() {
        Project project = new Project();
        project.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setProject(project);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);
        projectWidget.setProjectGrid(projectGrid);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectWidgetService.getOne(any()))
                .thenReturn(Optional.of(projectWidget));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
                .thenReturn(true);

        ResponseEntity<Void> actual = projectWidgetController.deleteById(localUser, 1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
