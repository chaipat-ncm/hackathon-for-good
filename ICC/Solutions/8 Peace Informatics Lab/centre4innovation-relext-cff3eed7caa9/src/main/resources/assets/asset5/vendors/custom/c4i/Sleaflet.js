/**
 * Simplified Leaflet map utilities.
 * Created by arvid on 23-2-17.
 */
class Sleaflet{

  constructor(targetId, accessToken, lat, lon, zoom, darkTheme){
    let map = L.map(targetId, {
      preferCanvas: true
    });
    map.setView([lat, lon], zoom);

    let theme = darkTheme ? 'dark' : 'light';
    L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/'+theme+'-v9/tiles/256/{z}/{x}/{y}?access_token={accessToken}', {
      attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>, Imagery &copy; <a href="http://mapbox.com">Mapbox</a>',
      maxZoom: 18,
      accessToken: accessToken
    }).addTo(map);

    // "Checks if the map container size changed and updates the map if so [...]"
    // for when when parent containers are resized (e.g. fullscreen)
    setInterval(function(){
      map.invalidateSize()
    }, 400);


    this.markers = [];
    this.clusterGroup = L.markerClusterGroup({singleMarkerMode: true});
    this._map = map;
  }

  clearMarkers(){
    // clear all old markers
    this.markers.forEach(mm => this._map.removeLayer(mm));
    this.clusterGroup.clearLayers(this.markers);
    this.markers = [];
  }

  zoomToMarkers() {
    if (this.markers.length > 0) {
      let bounds = L.featureGroup(this.markers);
      console.log(bounds);
      this._map.fitBounds(bounds.getBounds());
    }
  }

  addCircle(lat, lon, radius, color, message, opacity, cluster){
    const circle = L.circle([lat, lon], {
      color: color || '#f03',
      fillColor: color || '#f03',
      fillOpacity: opacity === undefined ? 0.5 : opacity,
      weight: 2,
      radius: radius
    });

    if (message) {
      let popup = new L.popup({maxHeight: 250})
          .setContent(message);
      circle.bindPopup(popup);
    }

    this.markers.push(circle);

    if (!cluster) {
      circle.addTo(this._map);
    }

  }

  addMarker(lat, lon, message, cluster){
    const marker = L.marker([lat, lon]);

      if (message) {
          let popup = new L.popup({maxHeight: 250})
              .setContent(message);
          marker.bindPopup(popup);
      }

      this.markers.push(marker);

      if (!cluster) {
          marker.addTo(this._map);
      }
  }

  addCluster() {
    this.clusterGroup.addLayers(this.markers);
    this._map.addLayer(this.clusterGroup);
  }

  addLine(latlngs, weight, color){

    const polyline = L.polyline(latlngs, {
      color: color || '#f03',
      weight: weight || 2,
      opacity: 0.5,
    }).addTo(this._map);
    this.markers.push(polyline);
  }

  addGradientLineSegment(latlngs, weight, color1, color2){
    if(!(latlngs[0] && latlngs[1]))
      return

    // additional third element (z value) in each point; this determines which color from the palette to use.
    latlngs[0].push(0);
    latlngs[1].push(1);

    const polyline = L.hotline(latlngs, {
      palette: {0:color1, 1: color2},
      weight: weight || 2,
      opacity: 0.5,
      outlineColor: 'none'
    }).addTo(this._map);
    this.markers.push(polyline);
  }

  addGeoJson(shape, color){
    L.geoJSON(shape, {
      style: function (feature) {
        return {color: color || feature.properties.color, weight: 1};
      }
    }).bindPopup(function (layer) {
      // return `${layer.feature.properties.name} (${layer.feature.properties['name:en']})`;
      return `${layer.feature.properties.adm1} &gt; ${layer.feature.properties.adm2})`;
    }).addTo(this._map);
  }
}


