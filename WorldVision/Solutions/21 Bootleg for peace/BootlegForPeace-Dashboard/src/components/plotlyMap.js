import React from "react";
import Plot from "react-plotly.js";
import * as d3 from "d3";


// Todo: Load json, put in map, match country names, yolo
import refugeesMonthlyCsv from '../data/cost_distribution_v3 (1).csv';

import countryCodesCsv from '../data/country_codes.csv';

const costCategories = 'Agriculture,Education,Emergency aid,Environment,Infrastructure,Other'.split(',');

const timeSelectorOptions = {
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

function unpack(rows, key) {
  return rows.map(function(row) {
    return row[key];
  });
}

function buildMapData(rows, countryCodesDict) {
  // console.log(rows, rows.map((row) => countryCodesDict[row['Country']]));
  console.log(rows)
  return [
    {
      type: "choropleth",
      locationmode: 'country names',
      locations: unpack(rows, "Country"),
      z: unpack(rows, "Refugees"),
      text: unpack(rows, "Country"),
      colorscale: [
        [0, "rgb(5, 10, 172)"],
        [0.35, "rgb(40, 60, 190)"],
        [0.5, "rgb(70, 100, 245)"],
        [0.6, "rgb(90, 120, 245)"],
        [0.7, "rgb(106, 137, 247)"],
        [1, "rgb(220, 220, 220)"]
      ].reverse(),
      autocolorscale: false,
      reversescale: false,
      marker: {
        line: {
          color: "rgb(180,180,180)",
          width: 0.5
        }
      },
      tick0: 0,
      zmin: 0,
      dtick: 1000,
      colorbar: {
        autotic: false,
        // tickprefix: "$",
        title: "Refugees"
      },
      transforms: [{
        type: 'aggregate',
        groups: unpack(rows, "Country"),
        aggregations: [
          {target: 'z', func: 'sum', enabled: true},
        ]
      }]
    }
  ];
}

function buildTimeData(rawData, skip=10) {
  const refugeeValues = {};

  rawData.forEach(function(row, i) {
      if(i % skip) return;

      const date = `${row.Year}-${row.Month}`;
      if (date in refugeeValues) {
        refugeeValues[date] += parseInt(row.Refugees);
      } else {
        refugeeValues[date] = parseInt(row.Refugees);
      }
  });

  return [{
      mode: 'lines',
      x: Object.keys(refugeeValues),
      y: Object.values(refugeeValues)
  }];
}

class PlotlyMap extends React.Component {
  constructor() {
    super();
    this.state = {
      dateStart: [2015, 1],
      dateEnd: [2016, 1],
      refugeeData: [],
      selectedCountry: 'Select a country for more details'
    };
    this.onClick = this.onClick.bind(this);
    this.updateTime = this.updateTime.bind(this);
  }

  async componentWillMount() {
    const { dateStart, dateEnd } = this.state;
    const countryCodes = await d3.csv(countryCodesCsv);
    const countryCodeDict = {};
    countryCodes.forEach((item => { countryCodeDict[item.Country] = item.ISO; }));

    const rows = await d3.csv(
      refugeesMonthlyCsv
      // "https://raw.githubusercontent.com/plotly/datasets/master/2014_world_gdp_with_codes.csv"
    );

    const betterRows = rows.map(row => ({
      ...row,
      Year: parseInt(row.Year),
      Month: parseInt(row.Month),
      date: new Date(parseInt(row.Year), parseInt(row.Month) - 1),
      Code: countryCodeDict[row.Country]
    }));

    const dateStartDate = new Date(dateStart[0], dateStart[1] - 1);
    const dateEndDate = new Date(dateEnd[0], dateEnd[1] - 1);

    const datedRows = betterRows;
    // .filter((row) => 
    //   row.date >= dateStartDate && row.date < dateEndDate);

    const refugeesSummedPerCountry = {};
    datedRows.forEach((row) => {
      if(row.Country in refugeesSummedPerCountry) {
        refugeesSummedPerCountry[row.Country] += row.Refugees;
      } else {
        refugeesSummedPerCountry[row.Country] = row.Refugees;
      }
    })

    this.setState({
      rows: betterRows,
      mapData: buildMapData(datedRows, countryCodeDict),
      timeData: buildTimeData(betterRows),
      refugeesSummedPerCountry
    });
  }
  onClick(e) {
    if (!(e && e.points && e.points.length >= 0)) return;
    const country = e.points[0].text;
    
    const countryRows = this.state.rows.filter((row) => row.Country === country);

    const x = countryRows.map(row => `${row.Year}-${row.Month}`);

    const refugeeData = [
      {
        x,
        y: [],
        type: 'scatter',
        name: 'Total refugee influx',
      }
    ];

    const costData = costCategories.map((cat) => ({
      x,
      y: [],
      type: 'scatter',
      name: cat,
      stackgroup: 'one',
      groupnorm: 'percent'
    }));

    countryRows.forEach(row => {
      refugeeData[0].y.push(row.Refugees);

      costCategories.forEach((cat, i) => {
        costData[i].y.push(row[cat]);
      });
    });

    this.setState({
      selectedCountry: country,
      refugeeData,
      costData,
    });
  }
  updateTime(e) {
    if (!e.layout.xaxis.range) return;
    const range = e.layout.xaxis.range
      .map(dateStr => new Date(dateStr))
      .map(date => [date.getFullYear(), date.getMonth() + 1]);
    
    if (range[0][0] < 2000) return;
    
    this.setState({
      dateStart: range[0],
      dateEnd: range[1],
    });
  }
  render() {
    const { mapData, refugeeData, costData, timeData, selectedCountry } = this.state;
    return (
      <React.Fragment>
        <div style={{ display: 'inline', float: 'left' }}>
          <h1>Predicting incoming refugees and cost distributions</h1>
          <Plot
            data={mapData}
            layout={{
              width: window.innerWidth / 2,
              height: window.innerHeight * 0.8,
              title: `Refugee Influx, ${this.state.dateStart.join('-')} - ${this.state.dateEnd.join('-')}`,
              // autosize: true,
              // paper_bgcolor='rgba(0,0,0,0)',
              // plot_bgcolor='rgba(0,0,0,0)'
            }}
            onClick={this.onClick}
            style={{ display: 'inline'}}
          />

          {/* <Plot
            data={timeData}
            layout={{
              width: window.innerWidth / 2,
              height: window.innerHeight / 3,
              title: 'Date Selector',
              xaxis: {
                  rangeselector: timeSelectorOptions,
                  rangeslider: {}
              },
              yaxis: {
                  fixedrange: true,
                  title: 'Global refugee influx'
              }
            }}
            onUpdate={this.updateTime}
          /> */}
        </div>
        
        <div style={{ display: 'inline' }}>
          <h2>{selectedCountry}</h2>
          <Plot
            data={refugeeData}
            layout={{
              title: `Refugee influx over time`,
            }}
          />
          <Plot
            data={costData}
            layout={{
              title: `Cost distribution over time`,
            }}
          />
        </div>

      </React.Fragment>
    );
  }
}

export default PlotlyMap;
