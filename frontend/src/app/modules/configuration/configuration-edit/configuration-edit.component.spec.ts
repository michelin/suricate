import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigurationEditComponent } from './configuration-edit.component';

describe('ConfigurationEditComponent', () => {
  let component: ConfigurationEditComponent;
  let fixture: ComponentFixture<ConfigurationEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigurationEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigurationEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
