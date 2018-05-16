import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProjectWidgetDialogComponent } from './edit-project-widget-dialog.component';

describe('EditProjectWidgetDialogComponent', () => {
  let component: EditProjectWidgetDialogComponent;
  let fixture: ComponentFixture<EditProjectWidgetDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditProjectWidgetDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProjectWidgetDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
