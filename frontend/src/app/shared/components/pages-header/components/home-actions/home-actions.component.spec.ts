import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeActionsComponent } from './home-actions.component';

describe('HomeActionsComponent', () => {
  let component: HomeActionsComponent;
  let fixture: ComponentFixture<HomeActionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HomeActionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
