import { TestBed } from '@angular/core/testing';

import { MockUnitTestsService } from './mock-unit-tests.service';
import { MockModule } from '../../mock.module';

describe('MockUnitTestsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      providers: [MockUnitTestsService]
    });
  });

  it('should create', () => {
    const service: MockUnitTestsService = TestBed.inject(MockUnitTestsService);
    expect(service).toBeTruthy();
  });
});
