import React from "react";
import Plot from "react-plotly.js";
import * as d3 from "d3";

const xField = 'Date';
const yField = 'Mean_TemperatureC';

const selectorOptions = {
    buttons: [{
        step: 'month',
        stepmode: 'backward',
        count: 1,
        label: '1m'
    }, {
        step: 'month',
        stepmode: 'backward',
        count: 6,
        label: '6m'
    }, {
        step: 'year',
        stepmode: 'todate',
        count: 1,
        label: 'YTD'
    }, {
        step: 'year',
        stepmode: 'backward',
        count: 1,
        label: '1y'
    }, {
        step: 'all',
    }],
};

// function buildMapData(rows, countryCodesDict) {
//   console.log(rows, rows.map((row) => countryCodesDict[row['Country']]));
//   return [
//     {
//       type: "choropleth",
//       locations: rows.map((row) => countryCodesDict[row['Country']]),
//       z: unpack(rows, "Refugees"),
//       text: unpack(rows, "Country"),
//       colorscale: [
//         [0, "rgb(5, 10, 172)"],
//         [0.35, "rgb(40, 60, 190)"],
//         [0.5, "rgb(70, 100, 245)"],
//         [0.6, "rgb(90, 120, 245)"],
//         [0.7, "rgb(106, 137, 247)"],
//         [1, "rgb(220, 220, 220)"]
//       ],
//       autocolorscale: false,
//       reversescale: true,
//       marker: {
//         line: {
//           color: "rgb(180,180,180)",
//           width: 0.5
//         }
//       },
//       tick0: 0,
//       zmin: 0,
//       dtick: 1000,
//       colorbar: {
//         autotic: false,
//         // tickprefix: "$",
//         title: "Refugees"
//       }
//     }
//   ];
// }

// class PlotlyMap extends React.Component {
//   constructor() {
//     super();
//     this.state = {};
//   }

//   async componentWillMount() {
//     const countryCodes = await d3.csv(countryCodesCsv);
//     const countryCodeDict = {};
//     countryCodes.forEach((item => { countryCodeDict[item.Country] = item.ISO; }));

//     const rows = await d3.csv(
//       refugeesMonthlyCsv
//       // "https://raw.githubusercontent.com/plotly/datasets/master/2014_world_gdp_with_codes.csv"
//     );
//     const datedRows = rows.filter((row) => 
//       row['Year'] === '2015'
//       && row['Month'] === '1');




//     this.setState({
//       mapData: buildMapData(datedRows, countryCodeDict),
//     });
//   }

//   render() {
//     const { mapData } = this.state;
//     console.log(mapData && mapData[0].locations)
//     return (
//       <React.Fragment>
//         <Plot
//           data={mapData}
//           layout={{
//             width: window.innerWidth,
//             height: window.innerHeight,
//             title: "A Fancy Plot"
//           }}
//         />
        

//       </React.Fragment>
//     );
//   }
// }

// export default PlotlyMap;
