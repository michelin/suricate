import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormSidenavComponent } from './form-sidenav.component';
import { MockModule } from '../../../mock/mock.module';

describe('FormSidenavComponent', () => {
  let component: FormSidenavComponent;
  let fixture: ComponentFixture<FormSidenavComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [FormSidenavComponent]
      }).compileComponents();

      fixture = TestBed.createComponent(FormSidenavComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
