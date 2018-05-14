import {Component, Inject, OnInit} from '@angular/core';
import {ProjectWidget} from '../../../../shared/model/dto/ProjectWidget';
import {MAT_DIALOG_DATA} from '@angular/material';

/**
 * Dialog used for displaying "Yes / No" popup
 */
@Component({
  selector: 'app-delete-project-widget-dialog',
  templateUrl: './delete-project-widget-dialog.component.html',
  styleUrls: ['./delete-project-widget-dialog.component.css']
})
export class DeleteProjectWidgetDialogComponent implements OnInit {

  /**
   * The project widget to delete
   */
  projectWidget: ProjectWidget;

  /**
   * Constructor
   *
   * @param data The data give to the dialog
   */
  constructor(@Inject(MAT_DIALOG_DATA) private data: any) { }

  ngOnInit() {
    this.projectWidget = this.data.projectWidget;
  }

}
