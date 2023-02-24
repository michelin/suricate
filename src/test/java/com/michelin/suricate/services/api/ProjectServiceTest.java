package com.michelin.suricate.services.api;

import com.michelin.suricate.model.entities.*;
import com.michelin.suricate.model.enums.UpdateType;
import com.michelin.suricate.repositories.ProjectRepository;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.specifications.ProjectSearchSpecification;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private DashboardWebSocketService dashboardWebsocketService;

    @Mock
    private AssetService assetService;

    @Mock
    private ProjectGridService projectGridService;

    @Mock
    private ProjectWidgetService projectWidgetService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldGetAll() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findAll(any(ProjectSearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(project)));

        Page<Project> actual = projectService.getAll("search", Pageable.unpaged());

        assertThat(actual)
                .isNotEmpty()
                .contains(project);

        verify(projectRepository)
                .findAll(Mockito.<ProjectSearchSpecification>argThat(specification -> specification.getSearch().equals("search") &&
                                specification.getAttributes().isEmpty()),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetAllByUser() {
        Project project = new Project();
        project.setId(1L);

        User user = new User();
        user.setId(1L);

        when(projectRepository.findByUsersIdOrderByName(any()))
                .thenReturn(Collections.singletonList(project));

        List<Project> actual = projectService.getAllByUser(user);

        assertThat(actual)
                .isNotEmpty()
                .contains(project);

        verify(projectRepository)
                .findByUsersIdOrderByName(1L);
    }

    @Test
    void shouldGetOneById() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(any()))
                .thenReturn(Optional.of(project));

        Optional<Project> actual = projectService.getOneById(1L);

        assertThat(actual)
                .isPresent()
                .contains(project);

        verify(projectRepository)
                .findById(1L);
    }

    @Test
    void shouldGetOneByToken() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findProjectByToken(any()))
                .thenReturn(Optional.of(project));

        Optional<Project> actual = projectService.getOneByToken("token");

        assertThat(actual)
                .isPresent()
                .contains(project);

        verify(projectRepository)
                .findProjectByToken("token");
    }

    @Test
    void shouldGetTokenByProjectId() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.getToken(any()))
                .thenReturn("token");

        String actual = projectService.getTokenByProjectId(1L);

        assertThat(actual).isEqualTo("token");

        verify(projectRepository)
                .getToken(1L);
    }

    @Test
    void shouldCreateProjectNoToken() {
        Project project = new Project();
        project.setId(1L);

        when(stringEncryptor.encrypt(any()))
                .thenReturn("encrypted");
        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        Project actual = projectService.createProject(project);

        assertThat(actual).isNotNull();
        assertThat(actual.getToken()).isEqualTo("encrypted");

        verify(stringEncryptor)
                .encrypt(any(String.class));
        verify(projectRepository)
                .save(project);
    }

    @Test
    void shouldCreateProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        Project actual = projectService.createProject(project);

        assertThat(actual).isNotNull();
        assertThat(actual.getToken()).isEqualTo("token");

        verify(stringEncryptor, times(0))
                .encrypt(any(String.class));
        verify(projectRepository)
                .save(project);
    }

    @Test
    void shouldCreateProjectForUser() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        Project actual = projectService.createProjectForUser(user, project);

        assertThat(actual).isNotNull();
        assertThat(actual.getUsers())
                .hasSize(1)
                .contains(user);

        verify(stringEncryptor, times(0))
                .encrypt(any(String.class));
        verify(projectRepository)
                .save(project);
    }

    @Test
    void shouldCreateProjectsWithNoScreenshot() {
        User user = new User();
        user.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");
        project.setGrids(Collections.singleton(projectGrid));

        when(projectRepository.findProjectByToken(any()))
                .thenReturn(Optional.empty());
        when(projectGridService.findByIdAndProjectToken(any(), any()))
                .thenReturn(Optional.empty());
        when(projectGridService.create(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(projectWidgetService.findByIdAndProjectGridId(any(), any()))
                .thenReturn(Optional.empty());
        when(projectWidgetService.create(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(projectRepository.findAll(any()))
                .thenReturn(Collections.singletonList(project));

        List<Project> actual = projectService.createUpdateProjects(Collections.singletonList(project), user);

        assertThat(actual)
                .hasSize(1)
                .contains(project);

        verify(projectRepository)
                .findProjectByToken("token");
        verify(projectGridService)
                .findByIdAndProjectToken(1L, "token");
        verify(projectGridService)
                .create(projectGrid);
        verify(projectWidgetService)
                .findByIdAndProjectGridId(1L, null);
        verify(projectRepository)
                .findAll(Mockito.<ProjectSearchSpecification>argThat(specification -> specification.getSearch().equals(StringUtils.EMPTY) &&
                                specification.getAttributes().isEmpty()));
    }

    @Test
    void shouldUpdateProjects() {
        User user = new User();
        user.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setToken("token");
        project.setScreenshot(new Asset());
        project.setGrids(Collections.singleton(projectGrid));

        User existingUser = new User();
        existingUser.setId(1L);

        Asset existingAsset = new Asset();
        existingAsset.setId(1L);

        ProjectWidget existingProjectWidget = new ProjectWidget();
        existingProjectWidget.setId(1L);

        ProjectGrid existingProjectGrid = new ProjectGrid();
        existingProjectGrid.setId(1L);
        existingProjectGrid.setWidgets(Collections.singleton(existingProjectWidget));

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setToken("token");
        existingProject.setUsers(Collections.singleton(existingUser));
        existingProject.setScreenshot(existingAsset);
        existingProject.setGrids(Collections.singleton(existingProjectGrid));

        when(projectRepository.findProjectByToken(any()))
                .thenReturn(Optional.of(existingProject));
        when(projectGridService.findByIdAndProjectToken(any(), any()))
                .thenReturn(Optional.of(existingProjectGrid));
        when(projectGridService.create(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(projectWidgetService.findByIdAndProjectGridId(any(), any()))
                .thenReturn(Optional.of(existingProjectWidget));
        when(projectWidgetService.create(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(projectRepository.findAll(any()))
                .thenReturn(Collections.singletonList(project));

        List<Project> actual = projectService.createUpdateProjects(Collections.singletonList(project), user);

        assertThat(actual)
                .hasSize(1)
                .contains(project);
        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).getScreenshot().getId()).isEqualTo(1L);
        assertThat(actual.get(0).getUsers()).isEqualTo(Collections.singleton(existingUser));
        assertThat(new ArrayList<>(actual.get(0).getGrids()).get(0).getId()).isEqualTo(1L);
        assertThat(new ArrayList<>(new ArrayList<>(actual.get(0).getGrids()).get(0).getWidgets()).get(0).getId())
                .isEqualTo(1L);

        verify(projectRepository)
                .findProjectByToken("token");
        verify(projectGridService)
                .findByIdAndProjectToken(1L, "token");
        verify(projectGridService)
                .create(projectGrid);
        verify(projectWidgetService)
                .findByIdAndProjectGridId(1L, 1L);
        verify(projectRepository)
                .findAll(Mockito.<ProjectSearchSpecification>argThat(specification -> specification.getSearch().equals(StringUtils.EMPTY) &&
                        specification.getAttributes().isEmpty()));
    }

    @Test
    void shouldUpdateProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectService.updateProject(project, "newName", 1, 1, "css");
        assertThat(project.getName()).isEqualTo("newName");
        assertThat(project.getWidgetHeight()).isEqualTo(1);
        assertThat(project.getMaxColumn()).isEqualTo(1);
        assertThat(project.getCssStyle()).isEqualTo("css");

        verify(projectRepository)
                .save(project);
        verify(dashboardWebsocketService)
                .sendEventToProjectSubscribers(eq("token"),
                        argThat(event -> event.getType().equals(UpdateType.REFRESH_DASHBOARD) && event.getDate() != null));
    }
    @Test
    void shouldUpdateProjectNullInputs() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectService.updateProject(project, null, 0, 0, null);

        assertThat(project.getName()).isNull();
        assertThat(project.getWidgetHeight()).isNull();
        assertThat(project.getMaxColumn()).isNull();
        assertThat(project.getCssStyle()).isNull();

        verify(projectRepository)
                .save(project);
        verify(dashboardWebsocketService)
                .sendEventToProjectSubscribers(eq("token"),
                        argThat(event -> event.getType().equals(UpdateType.REFRESH_DASHBOARD) && event.getDate() != null));
    }


    @Test
    void shouldDeleteUserFromProject() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setUsers(new HashSet<>(Collections.singletonList(user)));

        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectService.deleteUserFromProject(user, project);
        assertThat(project.getUsers()).isEmpty();

        verify(projectRepository)
                .save(project);
    }

    @Test
    void shouldConnectedUserAccessToProjectBecauseAdmin() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Project project = new Project();
        project.setId(1L);

        boolean actual = projectService.isConnectedUserCanAccessToProject(project, localUser);

        assertThat(actual)
                .isTrue();
    }

    @Test
    void shouldConnectedUserAccessToProject() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Project project = new Project();
        project.setId(1L);
        project.setUsers(Collections.singleton(user));

        boolean actual = projectService.isConnectedUserCanAccessToProject(project, localUser);

        assertThat(actual)
                .isTrue();
    }

    @Test
    void shouldConnectedUserNotAccessToProject() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Project project = new Project();
        project.setId(1L);

        boolean actual = projectService.isConnectedUserCanAccessToProject(project, localUser);

        assertThat(actual)
                .isFalse();
    }

    @Test
    void shouldDeleteProject() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        projectService.deleteProject(project);

        verify(dashboardWebsocketService)
                .sendEventToProjectSubscribers(eq("token"), argThat(event ->
                        event.getType().equals(UpdateType.DISCONNECT) && event.getDate() != null));
        verify(projectRepository)
                .delete(project);
    }

    @Test
    void shouldAddScreenshot() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(assetService.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));
        when(projectRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectService.addOrUpdateScreenshot(project, new byte[10], "contentType", 10L);

        verify(assetService)
                .save(argThat(asset -> asset.getSize() == 10L &&
                        asset.getContentType().equals("contentType") &&
                        Arrays.equals(asset.getContent(), new byte[10])));
        verify(projectRepository)
                .save(argThat(createdProject -> createdProject.getId().equals(1L) &&
                        createdProject.getToken().equals("token") &&
                        Arrays.equals(createdProject.getScreenshot().getContent(), new byte[10]) &&
                        createdProject.getScreenshot().getSize() == 10L &&
                        createdProject.getScreenshot().getContentType().equals("contentType")));
    }

    @Test
    void shouldUpdateScreenshot() {
        Project project = new Project();
        project.setId(1L);
        project.setToken("token");
        project.setScreenshot(new Asset());

        when(assetService.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        projectService.addOrUpdateScreenshot(project, new byte[10], "contentType", 10L);

        verify(assetService)
                .save(argThat(asset -> asset.getSize() == 10L &&
                        asset.getContentType().equals("contentType") &&
                        Arrays.equals(asset.getContent(), new byte[10])));
        verify(projectRepository, times(0))
                .save(any());
    }
}
