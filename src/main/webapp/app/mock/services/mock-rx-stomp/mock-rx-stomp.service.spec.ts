import { TestBed } from '@angular/core/testing';

import { MockRxStompService } from './mock-rx-stomp.service';
import { MockModule } from '../../mock.module';

describe('MockRxStompService', () => {
  let service: MockRxStompService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      providers: [MockRxStompService]
    });

    service = TestBed.inject(MockRxStompService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });
});
