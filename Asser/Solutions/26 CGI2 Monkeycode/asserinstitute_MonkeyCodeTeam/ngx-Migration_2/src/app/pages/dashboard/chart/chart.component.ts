import { Component, OnInit, ViewChild } from '@angular/core';
import {Chart} from 'chart.js';
import {MatTabsModule} from '@angular/material/tabs'

@Component({
  selector: 'ngx-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss'],
})
export class ChartComponent implements OnInit {

  @ViewChild('lineChart') private chartRef;   
  @ViewChild('barChart') private chartRef_bar;
  @ViewChild('radarChart') private chartRef_radar;

  //LGP
  @ViewChild('lineChart2') private chartRef2;   
  @ViewChild('barChart2') private chartRef_bar2;
  @ViewChild('radarChart2') private chartRef_radar2;

  //MIT
  @ViewChild('lineChart3') private chartRef3;   
  @ViewChild('barChart3') private chartRef_bar3;
  @ViewChild('radarChart3') private chartRef_radar3;
  
 
   chart: any;    

  constructor() { }
  // LineChart: any;
  
  ngOnInit() {
    // this.LineChart = new chart(
    //   'lineChart',{
    //     type: 'line',
    //     data :{
    //       labels:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],
    //       datasets: [{
    //         label: "Number of Land Grab Deals",
    //         data:[10,9,26,57,76,34,98,65,55,29,11,94],
    //         fill:false,
    //         lineTension:0.2,
    //         borderColor:"red",
    //         borderWidth:1,
    //       }]
    //     },
    //     options: {
    //       title : {
    //         text: "Number of Land Grab Deals",
    //         display: true
    //       },
    //       scale:{
    //         yAxes : [{
    //           ticks :{
    //             beginAtZero:true
    //           }
    //         }]
    //       }
    //     }
    // });

    this.chart = new Chart(this.chartRef.nativeElement, {
      type: 'bar',
      data: {
        labels: ["Ethiopia","Congo","Tanzania","Mozambique","Nigeria"], // your labels array
        datasets: [
          {
            label:"Failed Land Grab Deals (in Hectares)",
            data: [37200,44000,106300,24234,165000], // your data array
            borderColor: '#00AEFF',
            backgroundColor:'#00A1F1',
            fill: true,
            lineTension:0.2,
            borderWidth:1
          }
        ]
      },
      options: {
        title : {
            text: "Failed Land Grab Deals",
            display: true
          },
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            
            display: true
            
          }],
          yAxes: [{
            display: true
          }],
        }
      }
    });

    this.chart = new Chart(this.chartRef_bar.nativeElement, {
      type: 'bar',
      data: {
        labels: ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"], // your labels array
        datasets: [
          {
            label:"Corruption Perception Index",
            data: [15,31,34,23,87,34,98,65,55,29,11,94], // your data array
            borderColor: '#00AEFF',
            backgroundColor:'#00A1F1',
            fill: true,
            lineTension:0.2,
            borderWidth:1
          }
        ]
      },
      options: {
        title : {
            text: "Corruption Perception Index",
            display: true
          },
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            display: true
          }],
          yAxes: [{
            display: true
          }],
        }
      }
    });
    this.chart = new Chart(this.chartRef_radar.nativeElement, {
      type: 'radar',
      data: {
        labels: ['Registering Property Score', 'Registering Property rank', 'Procedures (number)', 'Time (days)'],
        datasets: [{
            data: [57.56, 97, 90, 72.6],
            borderColor: '#00AEFF',
            fill: true
        }]
      },
      options: {
        title : {
            text: "Internation Land Registration Information",
            display: true
          },
        legend: {
          display: false
        },
        scale: {
          // Hides the scale
          display: true
      }
      }
    });

    //LGP
    this.chart = new Chart(this.chartRef2.nativeElement, {
      type: 'line',
      data: {
        labels: ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"], // your labels array
        datasets: [
          {
            label:"Number of Land Grab Deals",
            data: [10,9,26,57,76,34,98,65,55,29,11,94], // your data array
            borderColor: '#00AEFF',
            fill: false,
            lineTension:0.2,
            borderWidth:1
          }
        ]
      },
      options: {
        title : {
            text: "Number of Land Grab Deals",
            display: true
          },
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            display: true
          }],
          yAxes: [{
            display: true
          }],
        }
      }
    });

    this.chart = new Chart(this.chartRef_bar2.nativeElement, {
      type: 'bar',
      data: {
        labels: ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"], // your labels array
        datasets: [
          {
            label:"Corruption Perception Index",
            data: [15,31,34,23,87,34,98,65,55,29,11,94], // your data array
            borderColor: '#00AEFF',
            fill: false,
            lineTension:0.2,
            borderWidth:1
          }
        ]
      },
      options: {
        title : {
            text: "Corruption Perception Index",
            display: true
          },
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            display: true
          }],
          yAxes: [{
            display: true
          }],
        }
      }
    });
    this.chart = new Chart(this.chartRef_radar2.nativeElement, {
      type: 'radar',
      data: {
        labels: ['Registering Property Score', 'Registering Property rank', 'Procedures (number)', 'Time (days)'],
        datasets: [{
            data: [57.56, 97, 90, 72.6],
            borderColor: '#00AEFF',
            fill: true
        }]
      },
      options: {
        title : {
            text: "Internation Land Registration Information",
            display: true
          },
        legend: {
          display: false
        },
        scale: {
          // Hides the scale
          display: true
      }
      }
    });

    //MIT
    this.chart = new Chart(this.chartRef3.nativeElement, {
      type: 'line',
      data: {
        labels: ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"], // your labels array
        datasets: [
          {
            label:"Number of Land Grab Deals",
            data: [10,9,26,57,76,34,98,65,55,29,11,94], // your data array
            borderColor: '#00AEFF',
            fill: false,
            lineTension:0.2,
            borderWidth:1
          }
        ]
      },
      options: {
        title : {
            text: "Number of Land Grab Deals",
            display: true
          },
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            display: true
          }],
          yAxes: [{
            display: true
          }],
        }
      }
    });

    this.chart = new Chart(this.chartRef_bar3.nativeElement, {
      type: 'bar',
      data: {
        labels: ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"], // your labels array
        datasets: [
          {
            label:"Corruption Perception Index",
            data: [15,31,34,23,87,34,98,65,55,29,11,94], // your data array
            borderColor: '#00AEFF',
            fill: false,
            lineTension:0.2,
            borderWidth:1
          }
        ]
      },
      options: {
        title : {
            text: "Corruption Perception Index",
            display: true
          },
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            display: true
          }],
          yAxes: [{
            display: true
          }],
        }
      }
    });
    // this.chart = new Chart(this.chartRef_radar3.nativeElement, {
    //   type: 'radar',
    //   data: {
    //     labels: ['Registering Property Score', 'Registering Property rank', 'Procedures (number)', 'Time (days)'],
    //     datasets: [{
    //         data: [57.56, 97, 90, 72.6],
    //         borderColor: '#00AEFF',
    //         fill: true
    //     }]
    //   },
    //   options: {
    //     title : {
    //         text: "Internation Land Registration Information",
    //         display: true
    //       },
    //     legend: {
    //       display: false
    //     },
    //     scale: {
    //       // Hides the scale
    //       display: true
    //   }
    //   }
    // });
  }

  
}


