import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RotationCreationComponent } from './rotation-creation.component';

describe('RotationCreationComponent', () => {
  let component: RotationCreationComponent;
  let fixture: ComponentFixture<RotationCreationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RotationCreationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RotationCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
