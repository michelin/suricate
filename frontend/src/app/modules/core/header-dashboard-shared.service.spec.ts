import { TestBed, inject } from '@angular/core/testing';

import { HeaderDashboardSharedService } from './header-dashboard-shared.service';

describe('HeaderDashboardSharedService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [HeaderDashboardSharedService]
    });
  });

  it('should be created', inject([HeaderDashboardSharedService], (service: HeaderDashboardSharedService) => {
    expect(service).toBeTruthy();
  }));
});
