import {Component, Injector} from '@angular/core';
import {ListComponent} from "../../shared/components/list/list.component";
import {IconEnum} from "../../shared/enums/icon.enum";
import {DatePipe} from "@angular/common";
import {ButtonTypeEnum} from "../../shared/enums/button-type.enum";
import {
  ImportExportFormFieldsService
} from "../../shared/services/frontend/form-fields/import-export-form-fields/import-export-form-fields.service";
import {ImportExportProject} from "../../shared/models/backend/import-export/import-export-project";
import {ImageUtils} from "../../shared/utils/image.utils";
import {ToastTypeEnum} from "../../shared/enums/toast-type.enum";
import {ImportExport} from "../../shared/models/backend/import-export/import-export";
import {HttpImportExportService} from "../../shared/services/backend/http-import-export/http-import-export.service";

@Component({
  templateUrl: '../../shared/components/list/list.component.html',
  styleUrls: ['../../shared/components/list/list.component.scss']
})
export class ExportsComponent extends ListComponent<ImportExport> {
  /**
   * The list of icons
   */
  public iconEnum = IconEnum;

  /**
   * Constructor
   * @param httpImportExportService The HTTP export service
   * @param importExportFormFieldsService The export form field service
   * @param datePipe The date pipe
   * @param injector The injector
   */
  constructor(
    private readonly httpImportExportService: HttpImportExportService,
    private readonly importExportFormFieldsService: ImportExportFormFieldsService,
    public datePipe: DatePipe,
    protected injector: Injector
  ) {
    super(httpImportExportService, injector);

    this.initHeaderConfiguration();
  }

  /**
   * Function used to configure the header of the list component
   */
  private initHeaderConfiguration(): void {
    this.headerConfiguration = {
      title: 'export.list',
      actions: [
        {
          icon: IconEnum.FILE_DOWNLOAD,
          color: 'primary',
          variant: 'miniFab',
          type: ButtonTypeEnum.BUTTON,
          tooltip: { message: 'export.data' },
          callback: () => this.exportData()
        },
        {
          icon: IconEnum.FILE_UPLOAD,
          color: 'primary',
          variant: 'miniFab',
          type: ButtonTypeEnum.BUTTON,
          tooltip: { message: 'import.data' },
          callback: () => this.openImportDataFormSidenav()
        }
      ]
    };
  }

  /**
   * Export application data
   */
  private exportData(): void {
    this.httpImportExportService.exportData().subscribe((dataExport: ImportExport) => {
      const element = document.createElement('a');
      element.setAttribute('href', 'data:application/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(dataExport, null, 2)));
      element.setAttribute('download', `export_${this.datePipe.transform(new Date(), 'yyyy-MM-dd_HH-mm')}`);
      element.style.display = 'none';
      document.body.appendChild(element);
      element.click();
      document.body.removeChild(element);
    });
  }

  /**
   * Import application data
   */
  private openImportDataFormSidenav(): void {
    this.sidenavService.openFormSidenav({
      title: 'import.data',
      formFields: this.importExportFormFieldsService.generateImportDataFormFields(),
      save: (formData: FormData) => this.importDashboard(formData)
    });
  }

  /**
   * Create a new dashboard
   *
   * @param formData The data retrieved from the form
   */
  private importDashboard(formData: FormData): void {
    const importData: ImportExport = JSON.parse(atob(ImageUtils.getDataFromBase64URL(formData[ImportExportFormFieldsService.projectImportFileFormFieldKey])));
    this.httpImportExportService.importDashboard(importData).subscribe(() => {
      this.toastService.sendMessage('import.success', ToastTypeEnum.SUCCESS);
      this.router.navigate(['/home']);
    });
  }
}
