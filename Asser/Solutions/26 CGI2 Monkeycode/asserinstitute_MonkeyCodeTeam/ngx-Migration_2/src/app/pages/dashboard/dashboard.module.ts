import { NgModule } from '@angular/core';


import { ThemeModule } from '../../@theme/theme.module';
import { DashboardComponent } from './dashboard.component';
import { FiltersComponent } from './filters/filters.component';
import { LefleatComponent } from './lefleat/lefleat.component';
import { ChartComponent } from './chart/chart.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { MatTabsModule } from '@angular/material';
import {LeafletMarkerClusterModule} from '@asymmetrik/ngx-leaflet-markercluster';
import {TimelineComponent} from '../dashboard/timeline/timeline.component';
import {MatExpansionModule} from '@angular/material';

@NgModule({
  imports: [
    ThemeModule,
    NgSelectModule,
    FormsModule,
    LeafletModule.forRoot(),
    MatTabsModule,
    LeafletMarkerClusterModule,
    MatExpansionModule,
  ],
  declarations: [
    DashboardComponent,
    FiltersComponent,
    LefleatComponent,
    ChartComponent,
    TimelineComponent,
  ],
})
export class DashboardModule { }
