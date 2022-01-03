import { inject, TestBed } from '@angular/core/testing';

import { RotationService } from './rotation.service';
import { MockModule } from '../../../mock/mock.module';

describe('RotationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      providers: [RotationService]
    });
  });

  it('should create', inject([RotationService], (service: RotationService) => {
    expect(service).toBeTruthy();
  }));
});
