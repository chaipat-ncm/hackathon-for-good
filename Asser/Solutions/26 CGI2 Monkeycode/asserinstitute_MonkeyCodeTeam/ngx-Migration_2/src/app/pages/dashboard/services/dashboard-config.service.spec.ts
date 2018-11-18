import { TestBed, inject } from '@angular/core/testing';

import { DashboardConfigService } from './dashboard-config.service';

describe('DashboardConfigService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DashboardConfigService]
    });
  });

  it('should be created', inject([DashboardConfigService], (service: DashboardConfigService) => {
    expect(service).toBeTruthy();
  }));
});
