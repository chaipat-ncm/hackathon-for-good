import React, { Component } from "react"
import ReactDOM from "react-dom"
import {
  ComposableMap,
  ZoomableGroup,
  Geographies,
  Geography,
} from "react-simple-maps";

class WorldMap extends Component {

  constructor() {
    super()

    this.state = {
      zoom: 1,
    }

    this.handleZoomIn = this.handleZoomIn.bind(this);
    this.handleZoomOut = this.handleZoomOut.bind(this);
    this.handleZoom = this.handleZoom.bind(this);
  }
  componentWillMount() {
    document.addEventListener('mousewheel', this.handleZoom);
  }
  componentWillUnmount() {
    document.removeEventListener('mousewheel', this.handleZoom);
  }
  handleZoom(e) {
    const { minZoom, maxZoom, zoomStrength } = this.props;
    let zoom = this.state.zoom * (1 + Math.sign(e.wheelDelta) * zoomStrength)
    zoom = Math.min(maxZoom, Math.max(minZoom, zoom));
    this.setState({ zoom });
  }
  handleZoomIn() {
    this.setState({
      zoom: this.state.zoom * 2,
    })
  }
  handleZoomOut() {
    this.setState({
      zoom: this.state.zoom / 2,
    });
  }
  handleMoveStart(currentCenter) {
    console.log("New center: ", currentCenter)
  }
  handleMoveEnd(newCenter) {
    console.log("New center: ", newCenter)
  }
  render() {
    const { mapUrl } = this.props;
    return(
      <div>
        {/* <button onClick={ this.handleZoomIn }>{ "Zoom in" }</button>
        <button onClick={ this.handleZoomOut }>{ "Zoom out" }</button>
        <hr /> */}

        <ComposableMap
          width={document.body.clientWidth}
          height={window.innerHeight}
        >
          <ZoomableGroup
            onMoveStart={this.handleMoveStart}
            onMoveEnd={this.handleMoveEnd}
            zoom={this.state.zoom}
          >
            <Geographies geography={mapUrl}>
              {(geographies, projection) => geographies.map((geography, i) => (
                <Geography
                  key={ i }
                  geography={ geography }
                  projection={ projection }
                  style={{
                    default: {
                      fill: "#b1cefb",
                      stroke: "#FFFFFF",
                      strokeWidth: 0.75,
                      outline: "none",
                    },
                    hover: {
                      fill: "#CFD8DC",
                      stroke: "#FFFFFF",
                      strokeWidth: 0.75,
                      outline: "none",
                    },
                    pressed: {
                      fill: "#FF5722",
                      stroke: "#FFFFFF",
                      strokeWidth: 0.75,
                      outline: "none",
                    },
                  }}
                  onClick={() => alert('hoi')}
                />
              ))}
            </Geographies>
          </ZoomableGroup>
        </ComposableMap>
      </div>
    )
  }
}

WorldMap.defaultProps = {
  width: '100%',
  height: '100%',
  zoomStrength: 0.1,
  minZoom: 0.5,
  maxZoom: 10,
  mapUrl: 'https://raw.githubusercontent.com/zcreativelabs/react-simple-maps/master/topojson-maps/world-50m-simplified.json'
};

export default WorldMap;
