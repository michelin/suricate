import { TestBed } from '@angular/core/testing';

import { MockStompRService } from './mock-stomp-r.service';
import { MockModule } from '../../mock.module';
import { MockedModelBuilderService } from '../mocked-model-builder/mocked-model-builder.service';

describe('MockStompRService', () => {
  let service: MockStompRService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      providers: [MockStompRService]
    });

    service = TestBed.inject(MockStompRService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });
});
