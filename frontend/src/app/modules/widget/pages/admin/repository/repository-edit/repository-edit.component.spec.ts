import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryEditComponent } from './repository-edit.component';

describe('RepositoryEditComponent', () => {
  let component: RepositoryEditComponent;
  let fixture: ComponentFixture<RepositoryEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
