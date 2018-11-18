import {Component, OnInit} from '@angular/core';
import {DashboardConfigService} from '../services/dashboard-config.service';
import {ICountry} from '../Contracts/ICountry';
import { ILandGrab } from '../Contracts/ILandGrab';
import { IYear } from '../Contracts/IYear';

@Component({
  selector: 'ngx-filters',
  templateUrl: './filters.component.html',
  styleUrls: ['./filters.component.scss'],
})
export class FiltersComponent implements OnInit {

  /**
   * Object Countrys
   * @type {any[]} [object object]
   */
  countrys: ICountry[];
  purposes: ILandGrab[];
  years:IYear[];

  /**
   *  array with all type country
   * @type {any[]}
   */
  types: any[] = [];

  constructor(private dashboardConfig: DashboardConfigService) {
    this.countrys = this.dashboardConfig.getCountrys();
    this.purposes = this.dashboardConfig.getpurposes();
    this.years=this.dashboardConfig.getYears();
  }

  /**
   * retrieve all country from API
   * @returns {Subscription}
   */
  // selectedCountrys() {
  //   return this.dashboardConfig.getCountrys();      
  // }

  /**
   * console log the selected country ID
   * @param countryId
   */
  onChangeSelectCountry(countryId) {
    console.warn(countryId);
  }

  ngOnInit() {
     //this.selectedCountrys();
  }

}
