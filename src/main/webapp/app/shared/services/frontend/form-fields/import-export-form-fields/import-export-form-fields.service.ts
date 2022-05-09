import {Injectable} from "@angular/core";
import {FormField} from "../../../../models/frontend/form/form-field";
import {DataTypeEnum} from "../../../../enums/data-type.enum";
import {Validators} from "@angular/forms";
import {CustomValidator} from "../../../../validators/custom-validator";

/**
 * Service used to build the form fields related to an export
 */
@Injectable({ providedIn: 'root' })
export class ImportExportFormFieldsService {
    /**
     * Key of the form field for project import file
     */
    public static readonly projectImportFileFormFieldKey = 'importFile';

    /**
     * Get the list of form fields for a dashboard import
     */
    public generateImportDataFormFields(): FormField[] {
        return [
            {
                key: ImportExportFormFieldsService.projectImportFileFormFieldKey,
                label: 'dashboard.import.file.label',
                type: DataTypeEnum.FILE,
                validators: [Validators.required, CustomValidator.fileHasJsonFormat()]
            }
        ];
    }
}