import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListComponent } from '../../components/list/list.component';

describe('ListComponent', () => {
  let component: ListComponent<unknown>;
  let fixture: ComponentFixture<ListComponent<unknown>>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ListComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
