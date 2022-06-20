import { Component, OnInit } from '@angular/core';
import { ProjectFormFieldsService } from '../../../shared/services/frontend/form-fields/project-form-fields/project-form-fields.service';
import { ProjectRequest } from '../../../shared/models/backend/project/project-request';
import { CssService } from '../../../shared/services/frontend/css/css.service';
import { Project } from '../../../shared/models/backend/project/project';
import { ImageUtils } from '../../../shared/utils/image.utils';
import { FileUtils } from '../../../shared/utils/file.utils';
import { ToastTypeEnum } from '../../../shared/enums/toast-type.enum';
import { HttpAssetService } from '../../../shared/services/backend/http-asset/http-asset.service';
import { HttpProjectService } from '../../../shared/services/backend/http-project/http-project.service';
import { SidenavService } from '../../../shared/services/frontend/sidenav/sidenav.service';
import { Router } from '@angular/router';
import { ToastService } from '../../../shared/services/frontend/toast/toast.service';
import { MaterialIconRecords } from '../../../shared/records/material-icon.record';
import { IconEnum } from '../../../shared/enums/icon.enum';
import { HeaderConfiguration } from '../../../shared/models/frontend/header/header-configuration';
import { ButtonTypeEnum } from '../../../shared/enums/button-type.enum';

@Component({
  selector: 'suricate-my-dashboards',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  /**
   * Configuration of the header
   */
  public headerConfiguration: HeaderConfiguration;

  /**
   * Tell when the list of dashboards is loading
   */
  public isLoading: boolean;

  /**
   * The list of material icons
   */
  public materialIconRecords = MaterialIconRecords;

  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * The list of dashboards
   */
  public projects: Project[];

  /**
   * Constructor
   *
   * @param router The router
   * @param httpProjectService The HTTP project service
   * @param projectFormFieldsService The project form fields service
   * @param sidenavService The sidenav service
   * @param toastService The toast service
   */
  constructor(
    private readonly router: Router,
    private readonly httpProjectService: HttpProjectService,
    private readonly projectFormFieldsService: ProjectFormFieldsService,
    private readonly sidenavService: SidenavService,
    private readonly toastService: ToastService
  ) {}

  /**
   * Init method
   */
  ngOnInit(): void {
    this.initHeaderConfiguration();

    this.httpProjectService.getAllForCurrentUser().subscribe((dashboards: Project[]) => {
      this.isLoading = false;
      this.projects = dashboards;
    });
  }

  /**
   * Used to init the header component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'dashboard.list.my',
      actions: [
        {
          icon: IconEnum.ADD,
          color: 'primary',
          variant: 'miniFab',
          type: ButtonTypeEnum.BUTTON,
          tooltip: { message: 'dashboard.create' },
          callback: () => this.openCreateDashboardFormSidenav()
        }
      ]
    };
  }

  /**
   * Display the side nav bar used to create a dashboard
   */
  public openCreateDashboardFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'dashboard.create',
      formFields: this.projectFormFieldsService.generateProjectFormFields(),
      save: (formData: ProjectRequest) => this.saveDashboard(formData)
    });
  }

  /**
   * Create a new dashboard
   *
   * @param formData The data retrieved from the form
   */
  private saveDashboard(formData: ProjectRequest): void {
    formData.cssStyle = CssService.buildCssFile([CssService.buildCssGridBackgroundColor(formData.gridBackgroundColor)]);

    this.httpProjectService.create(formData).subscribe((project: Project) => {
      if (formData.image) {
        const contentType: string = ImageUtils.getContentTypeFromBase64URL(formData.image);
        const blob: Blob = FileUtils.base64ToBlob(
          ImageUtils.getDataFromBase64URL(formData.image),
          ImageUtils.getContentTypeFromBase64URL(formData.image)
        );
        const file: File = FileUtils.convertBlobToFile(blob, `${project.token}.${contentType.split('/')[1]}`, new Date());

        this.httpProjectService.addOrUpdateProjectScreenshot(project.token, file).subscribe();
      }

      this.toastService.sendMessage('dashboard.add.success', ToastTypeEnum.SUCCESS);
      this.router.navigate(['/dashboards', project.token, project.grids[0].id]);
    });
  }

  /**
   * Get the asset url
   *
   * @param assetToken The asset used to build the url
   */
  public getContentUrl(assetToken: string): string {
    return HttpAssetService.getContentUrl(assetToken);
  }
}
