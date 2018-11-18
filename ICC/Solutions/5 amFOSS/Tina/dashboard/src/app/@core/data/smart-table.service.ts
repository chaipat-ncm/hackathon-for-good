import { Injectable } from '@angular/core';

@Injectable()
export class SmartTableService {

  data = [{
    Source: 'Killings',
    Target: 'Aishta OUMAR',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '1',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'Hawa YAMA Qursi',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '2',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'ABDULRAHIM',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '3',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'Fadi LNU',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '4',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'Nami YAWA',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '5',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'FNU MINIEKO',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '6',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'Zeituna LNU',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '7',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'SEIZE DIX SEPT',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '8',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'Bouba GAI',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '9',
    Label: 'Victim',
    Weight: '1',
  }, {
    Source: 'Killings',
    Target: 'Abdullahi JIBO',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '10',
    Label: 'Victim',
    Weight: '1',
  },
  {
    Source: 'Killings',
    Target: 'Bali GOURO',
    Type: 'Directed',
    Kind: 'Victim',
    ID: '11',
    Label: 'Victim',
    Weight: '1',
  }];
  getData() {
    return this.data;
  }
}
