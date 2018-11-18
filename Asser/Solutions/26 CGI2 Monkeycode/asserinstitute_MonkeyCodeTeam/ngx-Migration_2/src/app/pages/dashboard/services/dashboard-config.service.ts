import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/operators';
import 'rxjs-compat/add/operator/map';
import {ICountry} from '../Contracts/ICountry';
import {ILandGrab} from '../Contracts/ILandGrab';
import { IYear } from '../Contracts/IYear';

@Injectable({
  providedIn: 'root',
})
export class DashboardConfigService {
  private _dropdownSelectedId: number;
  private _dropdownSelectedName: string;
  private _selecterStartDate: string = '2017-09-24';
  private _selecterEndDate: string = '2018-09-21';
  private _subjectData: string;

  private _markerId: number;
  private _markerName: string;
  private _markerCity: string;
  private _Registrationscore: number;
  private _time: number;
  //private _markerCity: string;


  private _showSentimentChart: boolean = false;


  get showSentimentChart(): boolean {
    return this._showSentimentChart;
  }

  set showSentimentChart(value: boolean) {
    this._showSentimentChart = value;
  }

  get markerId(): number {
    return this._markerId;
  }

  set markerId(value: number) {
    this._markerId = value;
  }
  get markerName(): string {
    return this._markerName;
  }

  set markerName(value: string) {
    this._markerName = value;
  }

  get registrationscore(): number {
    return this._Registrationscore;
  }

  set registrationscore(value: number) {
    this._Registrationscore = value;
  }

  get time(): number {
    return this._time;
  }

  set time(value: number) {
    this._time = value;
  }

  get markerCity(): string {
    return this._markerCity;
  }

  set markerCity(value: string) {
    this._markerCity = value;
  }

  get subjectData(): string {
    return this._subjectData;
  }

  set subjectData(value: string) {
    this._subjectData = value;
  }

  get selecterStartDate(): string {
    return this._selecterStartDate;
  }

  set selecterStartDate(value: string) {
    this._selecterStartDate = value;
  }

  get selecterEndDate(): string {
    return this._selecterEndDate;
  }

  set selecterEndDate(value: string) {
    this._selecterEndDate = value;
  }

  get dropdownSelectedName(): string {
    return this._dropdownSelectedName;
  }

  set dropdownSelectedName(value: string) {
    this._dropdownSelectedName = value;
  }

  get dropdownSelectedId(): number {
    return this._dropdownSelectedId;
  }

  set dropdownSelectedId(value: number) {
    this._dropdownSelectedId = value;
  }


  constructor(private http: HttpClient) {
  }

  

  /**
   * get country's
   * @returns {Observable<any>}
   */
  // public getCountrys() {
  //   return this.http.get('/cgi/twitteranalysis/v1/location')
  //     .map((res: Response) => res)
  //     .pipe(
  //       catchError(DashboardConfigService.handleError));
  // }

  public getpurposes() : ILandGrab[] {
    return [
      {purpose:"Agribusiness"},
      {purpose:"Energy"},
      {purpose:"Finance"},
      {purpose:"Industrial"},
      {purpose:"Government"},
      {purpose:"Real estate"},
{purpose:"Construction"},
{purpose:"Mining"},
{purpose:"Telecommunications"},
{purpose:"Food"},
{purpose:"Prisons"},
{purpose:"Health care"},
    ]
  }

  public getYears(): IYear[]{
    return [
      {year:2010},
      {year:2011},
      {year:2012},
      {year:2013},
      {year:2014},
      {year:2015},
      {year:2016},
      {year:2017},
      {year:2018}
    ]
  }

  public getCountrys() : ICountry[] {
    return [
      {id:106,name: "Tanzania"},
      {id:108,name: "Ethiopia"},      
      {id:102,name: "Zambia"},
      {id:145,name: "Kenya"},
      {id:150,name: "Nigeria"},
      {id:130,name: "Gambia"},
      {id:127,name: "Mali"},
      {id:72,name: "South Africa"},
      {id:86,name: "Benin"},
      {id:105,name: "Mongolia"},
      {id:116,name: "Niger"},
      {id:154,name: "Mozambique"},
      {id:160,name: "Zimbabwe"},
      {id:162,name: "Congo"},
      {id:152,name: "Uganda"},
      {id:83,name: "Morocco"},
      {id:112,name: "Algeria"},
      {id:167,name: "Angola"},
      {id:1,name:"New Zealand"},
{id:2,name: "Denmark"},
{id:3,name: "Finland"},
{id:4,name: "Norway"},
{id:5,name: "Switzerland"},
{id:6,name: "Singapore"},
{id:7,name: "Sweden"},
{id:8,name: "Canada"},
{id:9,name: "Luxembourg"},
{id:10,name: "Netherlands"},
{id:11,name: "United Kingdom"},
{id:12,name: "Germany"},
{id:13,name: "Australia"},
{id:14,name: "Hong Kong"},
{id:15,name: "Iceland"},
{id:16,name: "Austria"},
{id:17,name: "Belgium"},
{id:18,name: "United States of America"},
{id:19,name: "Ireland"},
{id:20,name: "Japan"},
{id:21,name: "Estonia"},
{id:22,name: "United Arab Emirates"},
{id:23,name: "France"},
{id:24,name: "Uruguay"},
{id:25,name: "Barbados"},
{id:26,name: "Bhutan"},
{id:27,name: "Chile"},
{id:28,name: "Bahamas"},
{id:29,name: "Portugal"},
{id:30,name: "Qatar"},
{id:31,name: "Taiwan"},
{id:32,name: "Brunei Darussalam"},
{id:33,name: "Israel"},
{id:34,name: "Botswana"},
{id:35,name: "Slovenia"},
{id:36,name: "Poland"},
{id:37,name: "Seychelles"},
{id:38,name: "Costa Rica"},
{id:39,name: "Lithuania"},
{id:40,name: "Latvia"},
{id:41,name: "Saint Vincent and the Grenadines"},
{id:42,name: "Cyprus"},
{id:43,name: "Czech Republic"},
{id:44,name: "Dominica"},
{id:45,name: "Spain"},
{id:46,name: "Georgia"},
{id:47,name: "Malta"},
{id:48,name: "Cabo Verde"},
{id:49,name: "Rwanda"},
{id:50,name: "Saint Lucia"},
{id:51,name: "Korea, South"},
{id:52,name: "Grenada"},
{id:53,name: "Namibia"},
{id:54,name: "Italy"},
{id:55,name: "Mauritius"},
{id:56,name: "Slovakia"},
{id:57,name: "Croatia"},
{id:58,name: "Saudi Arabia"},
{id:59,name: "Greece"},
{id:60,name: "Jordan"},
{id:61,name: "Romania"},
{id:62,name: "Cuba"},
{id:63,name: "Malaysia"},
{id:64,name: "Montenegro"},
{id:65,name: "Sao Tome and Principe"},
{id:66,name: "Hungary"},
{id:67,name: "Senegal"},
{id:68,name: "Belarus"},
{id:69,name: "Jamaica"},
{id:70,name: "Oman"},
{id:71,name: "Bulgaria"},
{id:73,name: "Vanuatu"},
{id:74,name: "Burkina Faso"},
{id:75,name: "Lesotho"},
{id:76,name: "Tunisia"},
{id:77,name: "China"},
{id:78,name: "Serbia"},
{id:79,name: "Suriname"},
{id:80,name: "Trinidad and Tobago"},
{id:81,name: "Ghana"},
{id:82,name: "India"},
{id:84,name: "Turkey"},
{id:85,name: "Argentina"},
{id:87,name: "Kosovo"},
{id:88,name: "Kuwait"},
{id:89,name: "Solomon Islands"},
{id:90,name: "Swaziland"},
{id:91,name: "Albania"},
{id:92,name: "Bosnia and Herzegovina"},
{id:93,name: "Guyana"},
{id:94,name: "Sri Lanka"},
{id:95,name: "Timor-Leste"},
{id:96,name: "Brazil"},
{id:97,name: "Colombia"},
{id:98,name: "Indonesia"},
{id:99,name: "Panama"},
{id:100,name: "Peru"},
{id:101,name: "Thailand"},
{id:103,name: "Bahrain"},
{id:104,name: "Côte D'Ivoire"},
{id:107,name: "Armenia"},
{id:109,name: "Macedonia"},
{id:110,name: "Vietnam"},
{id:111,name: "Philippines"},
{id:113,name: "Bolivia"},
{id:114,name: "El Salvador"},
{id:115,name: "Maldives"},
{id:117,name: "Ecuador"},
{id:118,name: "Egypt"},
{id:119,name: "Gabon"},
{id:120,name: "Pakistan"},
{id:121,name: "Togo"},
{id:122,name: "Azerbaijan"},
{id:123,name: "Djibouti"},
{id:124,name: "Kazakhstan"},
{id:125,name: "Liberia"},
{id:126,name: "Malawi"},
{id:128,name: "Nepal"},
{id:129,name: "Moldova"},
{id:131,name: "Iran"},
{id:132,name: "Myanmar"},
{id:133,name: "Sierra Leone"},
{id:134,name: "Ukraine"},
{id:135,name: "Dominican Republic"},
{id:136,name: "Honduras"},
{id:137,name: "Kyrgyzstan"},
{id:138,name: "Laos"},
{id:139,name: "Mexico"},
{id:140,name: "Papua New Guinea"},
{id:141,name: "Paraguay"},
{id:142,name: "Russia"},
{id:143,name: "Bangladesh"},
{id:144,name: "Guatemala"},
{id:146,name: "Lebanon"},
{id:147,name: "Mauritania"},
{id:148,name: "Comoros"},
{id:149,name: "Guinea"},
{id:151,name: "Nicaragua"},
{id:153,name: "Cameroon"},
{id:155,name: "Madagascar"},
{id:156,name: "Central African Republic"},
{id:157,name: "Burundi"},
{id:158,name: "Haiti"},
{id:159,name: "Uzbekistan"},
{id:161,name: "Cambodia"},
{id:163,name: "Democratic Republic of the Congo"},
{id:164,name: "Tajikistan"},
{id:165,name: "Chad"},
{id:166,name: "Eritrea"},
{id:168,name: "Turkmenistan"},
{id:169,name: "Iraq"},
{id:170,name: "Venezuela"},
{id:171,name: "Korea, North"},
{id:172,name: "Equatorial Guinea"},
{id:173,name: "Guinea Bissau"},
{id:174,name: "Libya"},
{id:175,name: "Sudan"},
{id:176,name: "Yemen"},
{id:177,name: "Afghanistan"},
{id:178,name: "Syria"},
{id:179,name: "South Sudan"},
{id:180,name: "Somalia"}       
];
}

  /**
   *
   * @param {HttpErrorResponse} error
   * @returns {Observable<never>}
   */
  private static handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // return an observable with a user-facing error message
    return throwError(
      'Something bad happened; please try again later.');
  };
}
