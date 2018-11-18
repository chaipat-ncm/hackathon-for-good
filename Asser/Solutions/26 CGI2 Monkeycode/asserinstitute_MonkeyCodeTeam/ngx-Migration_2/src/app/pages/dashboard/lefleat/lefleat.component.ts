import { Component, OnInit, Input } from '@angular/core';
import * as L from 'leaflet';
import {latLng, LatLng, map} from 'leaflet';
import {DashboardConfigService} from '../services/dashboard-config.service';
import 'leaflet.markercluster';

@Component({
  selector: 'ngx-lefleat',
  templateUrl: './lefleat.component.html',
  styleUrls: ['./lefleat.component.scss'],
})
export class LefleatComponent implements OnInit {

  nexrad = L.tileLayer.wms("http://localhost:8080/geoserver/asser/wms", {
    layers: 'asser:Cropland2000_5m',
    format: 'image/png',
    transparent: true,
    attribution: ""
});

africa_cpi = L.tileLayer.wms("http://localhost:8080/geoserver/asser/wms", {
  layers: 'asser:Africa_CPI',
  format: 'image/png',
  transparent: true,
  attribution: ""
});

Africa_Disgarded_LGD = L.tileLayer.wms("http://localhost:8080/geoserver/asser/wms", {
  layers: 'asser:Africa_Disgarded_LGD',
  format: 'image/png',
  transparent: true,
  attribution: ""
});

Africa_EaseOfDoingBusiness = L.tileLayer.wms("http://localhost:8080/geoserver/asser/wms", {
  layers: 'asser:Africa_EaseOfDoingBusiness',
  format: 'image/png',
  transparent: true,
  attribution: ""
});
Africa_LandMatrixData = L.tileLayer.wms("http://localhost:8080/geoserver/asser/wms", {
  layers: 'asser:Africa_LandMatrixData',
  format: 'image/png',
  transparent: true,
  attribution: ""
});
Africa_LGD = L.tileLayer.wms("http://localhost:8080/geoserver/asser/wms", {
  layers: 'asser:Africa_LGD',
  format: 'image/png',
  transparent: true,
  attribution: ""
});


  constructor(private dashboardConfig: DashboardConfigService) {
    //super(dashboardConfig);
   }

   // Define our base layers so we can reference them multiple times
   mbAttr = 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
   '<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
   'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>';
 mbUrl = 'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNp' +
   'ejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw';

 grayscale = L.tileLayer(this.mbUrl, {id: 'mapbox.light', attribution: this.mbAttr});
 streets = L.tileLayer(this.mbUrl, {id: 'mapbox.streets', attribution: this.mbAttr});

  // Layers control object with our two base layers and the three overlay layers
  layersControl = {
    baseLayers: {
      'Street Maps': this.streets,
      'Grays Maps': this.grayscale,
    },
    overlays: {
      'Cropland': this.nexrad,
      'Africa_CPI':this.africa_cpi,
      'Africa_Disgarded_LGD':this.Africa_Disgarded_LGD,
      'Africa_EaseOfDoingBusiness':this.Africa_EaseOfDoingBusiness,
      'Africa_LandMatrixData':this.Africa_LandMatrixData,
      'Africa_LGD':this.Africa_LGD
    },
  };

    // Set the initial set of displayed layers (we could also use the leafletLayers input binding for this)

    optionsSpec: any = {
      zoom: 2,
      center: [-6.270353,34.823454],
    };
    @Input('leafletCenter')
    center = latLng(this.optionsSpec.center);
  
    @Input('leafletZoom')
    zoom = this.optionsSpec.zoom;
  
    options = {
      layers: [this.grayscale],
      zoom: this.zoom,
      center: latLng(this.optionsSpec.center),
    };

     // Marker cluster stuff
  markerClusterGroup: L.MarkerClusterGroup;
  @Input('markerClusterData')
  markerClusterData: any[] = [];
  @Input('markerClusterOptions')
  markerClusterOptions: L.MarkerClusterGroupOptions;

   /**
   * set center map position and zoom position
   */
  addCountryPosition() {
    if (this.dashboardConfig.dropdownSelectedName == 'undefined') {
      this.center = this.getLatLon();
      this.zoom = 5;
    }
    if (this.dashboardConfig.dropdownSelectedName == 'Morocco') {
      // Morocco
      this.center = this.getLatLon();
      this.zoom = 6;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Morocco') {
      // Libya
      this.center = this.getLatLon();
      this.zoom = 5;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Libya') {
      // Algeria
      this.center = this.getLatLon();
      this.zoom = 4;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Algiers') {
      // Algiers
      this.center = this.getLatLon();
      this.zoom = 10;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Béjaïa') {
      // Béjaïa
      this.center = this.getLatLon();
      this.zoom = 10;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Adrar') {
      // Adrar
      this.center = this.getLatLon();
      this.zoom = 9;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Defla') {
      // Defla
      this.center = this.getLatLon();
      this.zoom = 9;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Batna') {
      // Batna
      this.center = this.getLatLon();
      this.zoom = 10;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Béchar') {
      // Béchar
      this.center = this.getLatLon();
      this.zoom = 10;
    } else if (this.dashboardConfig.dropdownSelectedName == 'Aïn Témouchent') {
      // Aïn Témouchent
      this.center = this.getLatLon();
      this.zoom = 10;
    }
  }

  /**
   * Get the latitude and longitude position
   * @returns {LatLng} Return latitude and longitude.
   */
  public getLatLon() {

    if (this.dashboardConfig.dropdownSelectedName === 'undefined') {
      return latLng(51.924420, 4.477733);
    }
    if (this.dashboardConfig.dropdownSelectedName == 'Morocco') {
      // Morocco
      return latLng([31.7917, -7.0926]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Libya') {
      // Libya
      return latLng([26.3351, 17.2283]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Algeria') {
      // Algeria
      return latLng([28.0339, 1.6596]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Algiers') {
      // Algiers
      return latLng([36.7538, 3.0588]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Béjaïa') {
      // Béjaïa
      return latLng([36.7509, 5.056]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Adrar') {
      // Adrar
      return latLng([27.9716, 0.1870]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Defla') {
      // Defla
      return latLng([36.2610, 2.2343]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Batna') {
      // Batna
      return latLng([35.5610, 6.1739]);
    } else if (this.dashboardConfig.dropdownSelectedName == 'Béchar') {
      // Béchar
      return latLng([31.6167, 2.2195]);
    }
    if (this.dashboardConfig.dropdownSelectedName == 'Aïn Témouchent') {
      // Aïn Témouchent
      return latLng([36.7538, 3.0588]);
    }
    this.center = latLng([51.924420, 4.477733]);
    return latLng([51.924420, 4.477733]);
  }
  

  ngOnInit() {
    this.generateData();
  }

  markerClusterReady(group: L.MarkerClusterGroup) {

    this.markerClusterGroup = group;

  }

  generateData() {
    const data: any[] = [];
   // if (this.dashboardConfig.dropdownSelectedName == 'Algiers') {
      for (let i = 0; i < this.markers.length; i++) {

        const icon = L.icon({
          iconSize: [25, 41],
          iconAnchor: [13, 15],
          iconUrl: 'leaflet/marker-icon.png',
          shadowUrl: 'leaflet/marker-shadow.png',
        });

        const popup =
          '<br/><b>' + this.markers[i].name + '</b> ' +
          '<br/>' +
          '<br/><b>Country:</b> ' + this.markers[i].country +
          '<br/><b>CPI Score 2017:</b> ' + this.markers[i].CPIScore2017 +
          '<br/><b>Rank 2017</b>: ' + this.markers[i].Rank2017 +
          '<br/><b>' + "Property Registration Score" + '</b> ' +
          '<br/><b>Registering Property Score:</b> ' + this.markers[i].RegisteringPropertyScore +
          '<br/><b>Time (days):</b>: ' + this.markers[i].Time +
          '<br/>'
          // '<br/><b><a class="show-more-btn" href="" data-toggle="modal" data-target="#exampleModalCenter" ' +
          // '<span class="fa fa-file-o"></span> showMore' +
          // '</a></b>';
          '<br/>'
          '<br/><b>' + "Under Property Registration Score" + '</b> ' +
          '<br/>' 
          

        data.push(L.marker([this.markers[i].lat, this.markers[i].lng], {icon}).bindPopup(popup)
          .on('click', () => {
            this.dashboardConfig.markerId = this.markers[i].id;
            this.dashboardConfig.markerName = this.markers[i].name;
            this.dashboardConfig.markerCity = this.markers[i].country;
            this.dashboardConfig.registrationscore = this.markers[i].RegisteringPropertyScore;
            this.dashboardConfig.time = this.markers[i].Time;

            this.showMore(i)
          })
          .on('popupopen', () => {
            document.querySelector('.show-more-btn')
              .addEventListener('click', () => {
                setTimeout(() => {
                  this.dashboardConfig.showSentimentChart = true;
                }, 1)

              });
          }));
      }
    //}
    this.markerClusterData = data;

  }

  public showMore(id: number) {
    console.warn(this.markers[id].id);
  }

  markers = [
    {
      'id': 0,
      'name': 'Corruption Perception Index',
      'country': 'Mozambique',
      'CPIScore2017': 25,
      'Rank2017': 153,
      'RegisteringPropertyScore':52.94,
      'Time':43,
      'lat': -25.953724,
      'lng': 32.588711,
    }, {
      'id': 1,
      'name': 'Corruption Perception Index',
      'country': 'Tanzania',
      'CPIScore2017': 36,
      'Rank2017': 103,
      'RegisteringPropertyScore':0,
      'Time':0,
      'lat': -6.270353,
      'lng': 34.823454,
    }, {
      'id': 2,
      'name': 'Corruption Perception Index',
      'country': 'Ethiopia',
      'CPIScore2017':35,
      'Rank2017': 107,
      'RegisteringPropertyScore': 51.33,
      'Time':52,
      'lat': 8.62622,
      'lng': 39.616032,
    }, {
      'id': 3,
      'name': 'Corruption Perception Index',
      'country': 'Democratic Republic of the Congo',
      'CPIScore2017': 21,
      'Rank2017': 161,
      'RegisteringPropertyScore':47.14,
      'Time':38,
      'lat': -0.6605788,
      'lng': 14.8965794,
    }, {
      'id': 4,
      'name': 'Corruption Perception Index',
      'country': 'Nigeria',
      'CPIScore2017': 27,
      'Rank2017': 148,
      'RegisteringPropertyScore':27.75,
      'Time':105,
      'lat': 	6.465422,
      'lng': 	3.406448,
    }];

  

}
