import { Component, OnInit } from '@angular/core';
import {MatDialog, MatDialogRef} from '@angular/material';
import {AddWidgetDialogComponent} from '../add-widget-dialog/add-widget-dialog.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-dashboard-actions',
  templateUrl: './dashboard-actions.component.html',
  styleUrls: ['./dashboard-actions.component.css']
})
export class DashboardActionsComponent implements OnInit {

  addWidgetDialogRef: MatDialogRef<AddWidgetDialogComponent>;
  projectId: number;

  constructor(private dialog: MatDialog,
              private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.projectId = params['id'];
    });
  }

  openAddWidgetDialog() {
    this.addWidgetDialogRef = this.dialog.open(AddWidgetDialogComponent, {
      minWidth: 900,
      data: { projectId: this.projectId}
    });
  }
}
