/**
 * Simplified Plotly components.
 * Depends on jQuery and Plotly.js.
 * Arvid Halma
 */

$.hx = $.hx || {}


$.hx.fluidplot = function (elementId, data, layout) {
    const d3 = Plotly.d3;

    const WIDTH_IN_PERCENT_OF_PARENT = 100,
          HEIGHT_IN_PERCENT_OF_PARENT = 100;

    const gd3 = d3.select(elementId)
        .append('div')
        .style({
            width: WIDTH_IN_PERCENT_OF_PARENT + '%',
            'margin-left': (100 - WIDTH_IN_PERCENT_OF_PARENT) / 2 + '%',
            height: HEIGHT_IN_PERCENT_OF_PARENT + '%',
            'margin-bottom': (100 - HEIGHT_IN_PERCENT_OF_PARENT) / 2 + '%'
        });

    const gd = gd3.node();

    Plotly.newPlot(gd, data, layout, {
        displaylogo: false,
        modeBarButtonsToRemove: ['sendDataToCloud', 'zoom2d', 'pan2d', 'select2d', 'lasso2d', 'zoomIn2d',
            'zoomOut2d', 'autoScale2d', 'hoverClosestCartesian', 'hoverCompareCartesian']
    }).then(function() {
        Plotly.Plots.resize(gd);
        });

    window.addEventListener('resize', function() {
        Plotly.Plots.resize(gd);
    });

    return gd;
};



$.hx.barplot = function (elementId, xs, ys, title = undefined, color = '#635a80', layout) {

    let x2 = xs.map(x => x.length > 9 ? x.substring(0, 8) + '…' : x);

    const layoutz = layout || {
        autosize: true,
        hovermode: 'closest',
        xaxis: {
            tickvals: xs,
            ticktext: x2,
            automargin: true,
            tickangle: 45
        },
        margin: {
            t: 0,
            l: 30,
            r: 30,
            b: 60,
            pad: 4
        }
    };

    let yx = [];
    for(let y = 0; y < xs.length; y++){
        yx.push(xs[y] + ": " + ys[y]);
    }

    const data = [
        {
            x: xs,
            y: ys,
            text: yx,
            hoverinfo: 'text',
            marker: {
                color: color
            },
            type: 'bar'
        }
    ];

    return $.hx.fluidplot(elementId, data, layoutz);

};



