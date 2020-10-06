import { TestBed } from '@angular/core/testing';

import { MockedModelBuilderService } from './mocked-model-builder.service';
import { MockModule } from '../../mock.module';
import { MockStompRService } from '../mock-stomp-r/mock-stomp-r.service';

describe('MockUnitTestsService', () => {
  let service: MockedModelBuilderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      providers: [MockedModelBuilderService]
    });

    service = TestBed.inject(MockedModelBuilderService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });
});
