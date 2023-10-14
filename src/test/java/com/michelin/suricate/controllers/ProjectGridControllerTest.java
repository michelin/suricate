package com.michelin.suricate.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.ProjectGridService;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.mapper.ProjectGridMapper;
import com.michelin.suricate.utils.exceptions.ApiException;
import com.michelin.suricate.utils.exceptions.GridNotFoundException;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProjectGridControllerTest {
    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectGridService projectGridService;

    @Mock
    private ProjectGridMapper projectGridMapper;

    @InjectMocks
    private ProjectGridController projectGridController;

    @Test
    void shouldCreateNotFound() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectGridController.create(localUser, "token", gridRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldCreateNotAuthorized() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectGridController.create(localUser, "token", gridRequestDto))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldCreate() {
        ProjectGridResponseDto projectGridResponseDto = new ProjectGridResponseDto();
        projectGridResponseDto.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);
        when(projectGridMapper.toProjectGridEntity(any(), any()))
            .thenReturn(projectGrid);
        when(projectGridService.create(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(projectGridMapper.toProjectGridDto(any()))
            .thenReturn(projectGridResponseDto);

        ResponseEntity<ProjectGridResponseDto> actual =
            projectGridController.create(localUser, "token", gridRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(projectGridResponseDto);
    }

    @Test
    void shouldUpdateProjectGridsNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();

        assertThatThrownBy(() -> projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldUpdateProjectGridsNotAuthorized() {
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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();

        assertThatThrownBy(() -> projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldUpdateProjectGridsGridNotFound() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(2L);

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();
        projectGridRequestDto.setGrids(Collections.singletonList(gridRequestDto));

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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        assertThatThrownBy(() -> projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto))
            .isInstanceOf(GridNotFoundException.class)
            .hasMessage("Grid '2' not found for project token");
    }

    @Test
    void shouldUpdateProjectGrids() {
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();
        projectGridRequestDto.setGrids(Collections.singletonList(gridRequestDto));

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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        ResponseEntity<Void> actual =
            projectGridController.updateProjectGrids(localUser, "token", projectGridRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDeleteGridByIdNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectGridController.deleteGridById(localUser, "token", 1L))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Project 'token' not found");
    }

    @Test
    void shouldDeleteGridByIdNotAuthorized() {
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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(false);

        assertThatThrownBy(() -> projectGridController.deleteGridById(localUser, "token", 1L))
            .isInstanceOf(ApiException.class)
            .hasMessage("The user is not allowed to modify this project");
    }

    @Test
    void shouldDeleteGridByIdGridNotFound() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(2L);

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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        assertThatThrownBy(() -> projectGridController.deleteGridById(localUser, "token", 1L))
            .isInstanceOf(GridNotFoundException.class)
            .hasMessage("Grid '1' not found for project token");
    }

    @Test
    void shouldDeleteGridById() {
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

        when(projectService.getOneByToken(any()))
            .thenReturn(Optional.of(project));
        when(projectService.isConnectedUserCanAccessToProject(any(), any()))
            .thenReturn(true);

        ResponseEntity<Void> actual = projectGridController.deleteGridById(localUser, "token", 1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }
}
