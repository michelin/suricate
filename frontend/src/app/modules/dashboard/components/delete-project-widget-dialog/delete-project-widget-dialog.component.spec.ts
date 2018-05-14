import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteProjectWidgetDialogComponent } from './delete-project-widget-dialog.component';

describe('DeleteProjectWidgetDialogComponent', () => {
  let component: DeleteProjectWidgetDialogComponent;
  let fixture: ComponentFixture<DeleteProjectWidgetDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteProjectWidgetDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteProjectWidgetDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
