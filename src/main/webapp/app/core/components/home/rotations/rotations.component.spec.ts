import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RotationsComponent } from './rotations.component';
import { MockModule } from '../../../../mock/mock.module';

describe('MyRotationsComponent', () => {
  let component: RotationsComponent;
  let fixture: ComponentFixture<RotationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MockModule],
      declarations: [RotationsComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RotationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
