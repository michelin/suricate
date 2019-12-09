import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ButtonsComponent } from '../../components/buttons/buttons.component';

describe('ButtonsComponent', () => {
  let component: ButtonsComponent<unknown>;
  let fixture: ComponentFixture<ButtonsComponent<unknown>>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ButtonsComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
