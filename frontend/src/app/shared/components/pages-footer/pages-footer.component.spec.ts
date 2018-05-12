import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PagesFooterComponent } from './pages-footer.component';

describe('PagesFooterComponent', () => {
  let component: PagesFooterComponent;
  let fixture: ComponentFixture<PagesFooterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PagesFooterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PagesFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
