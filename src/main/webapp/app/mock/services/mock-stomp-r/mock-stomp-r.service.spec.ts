import { TestBed } from '@angular/core/testing';

import { MockStompRService } from './mock-stomp-r.service';

describe('MockStompRService', () => {
  let service: MockStompRService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MockStompRService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });
});
