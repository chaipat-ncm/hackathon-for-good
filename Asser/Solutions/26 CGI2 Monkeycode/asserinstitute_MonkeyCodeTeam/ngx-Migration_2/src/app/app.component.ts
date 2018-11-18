/**
 * @license
 * Copyright Akveo. All Rights Reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
import { Component, OnInit } from '@angular/core';
import { AnalyticsService } from './@core/utils/analytics.service';
import {Router} from '@angular/router';
import {NbMenuService} from '@nebular/theme';

@Component({
  selector: 'ngx-app',
  template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit {

  constructor(private analytics: AnalyticsService, private router: Router, private menuService: NbMenuService) {
  }

  /**
   * When the user logout destroy all data inside the localstorage.
   * Return user the the inlog page.
   * @param title
   */
  onContecxtItemSelection(title) {
    if (title === 'Log out') {
      localStorage.removeItem('currentUser');
      localStorage.clear();
      this.router.navigate(['auth/logout'])
    }
  }

  ngOnInit() {
    this.menuService.onItemClick().subscribe((event) => {
      this.onContecxtItemSelection(event.item.title);
    });
    this.analytics.trackPageViews();
  }
}
