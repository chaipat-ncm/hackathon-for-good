<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Data Overview
</#macro>

<#macro pageContent>
<style>

    #graphOutput i.la {
        font-size: 10px;
    }

    #map {
        height: 400px;
        z-index: 5;
    }

    #outputPanel, #textInput {
        font-family: Roboto, sans-serif;
        font-weight: normal;
        padding: 1em;
    }

    #textInput:focus {
        outline: none;
        color: black;
    }


</style>
<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Analyse Graph
            </h3>
            <ul class="m-subheader__breadcrumbs m-nav m-nav--inline">
                <li class="m-nav__item m-nav__item--home">
                    <a href="/api/v1/ui/dashboard" class="m-nav__link m-nav__link--icon">
                        <i class="m-nav__link-icon la la-home"></i>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">
												Analyse
											</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">
												Graph
											</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">

    <div class="row">
        <div class="col-sm-12">

            <div class="splitPanel"
                 style="width:100%; box-shadow: 0 1px 15px 1px rgba(69,65,78,.08);margin-bottom: 2.2rem;background: white;">

                <div id="rulePanel" class="split-flex split-horizontal-flex">


                    <div class="m-portlet" m-portlet="true" id="scriptPortlet" style="margin: 0;height:100%;">
                        <div class="m-portlet__head">
                            <div class="m-portlet__head-caption truncate">
                                <div class="m-portlet__head-title truncate">
                                <#--<h3 class="m-portlet__head-text truncate" id="scriptName">
                                    My Script
                                </h3>-->
                                    <input type="text" value="My Script" id="scriptName"
                                           style="width: 100%;height: 100%;border: none;overflow: hidden;text-overflow: ellipsis;font-family: Poppins;"
                                           readonly>
                                </div>
                            </div>

                            <div class="m-portlet__head-tools">
                                <ul class="m-portlet__nav">
                                <#--<li class="m-portlet__nav-item">
                                    <a id="runBtn"
                                       class="m-portlet__nav-link btn btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--pill"
                                       data-toggle="m-tooltip" data-placement="top" title=""
                                       data-original-title="Run script" style="color:white">
                                        <i class="la la-play"></i>
                                    </a>
                                </li>-->

                                    <li class="m-portlet__nav-item">
                                        <a id="runBtn" class="m-portlet__nav-link btn btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--pill" data-toggle="m-tooltip" data-placement="top" title="" d
                                           ata-original-title="Run script" style="color:white;">
                                            <i class="la la-play"></i>
                                        </a>
                                    </li>
                                <#-- <li class="m-portlet__nav-item">
                                     <a m-portlet-tool="fullscreen"
                                        class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill">
                                         <i class="la la-expand"></i>
                                     </a>
                                 </li>-->

                                    <li class="m-portlet__nav-item m-dropdown m-dropdown--inline m-dropdown--arrow m-dropdown--align-right m-dropdown--align-push"
                                        m-dropdown-toggle="hover" aria-expanded="true">
                                        <a href="#"
                                           class="m-portlet__nav-link btn btn-secondary  m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill   m-dropdown__toggle">
                                            <i class="la la-ellipsis-v"></i>
                                        </a>
                                        <div class="m-dropdown__wrapper" style="z-index: 101;">
                                            <span class="m-dropdown__arrow m-dropdown__arrow--right m-dropdown__arrow--adjust"
                                                  style="left: auto; right: 21.5px;"></span>
                                            <div class="m-dropdown__inner">
                                                <div class="m-dropdown__body">
                                                    <div class="m-dropdown__content">

                                                        <ul class="m-nav">
                                                            <li class="m-nav__section m-nav__section--first">
                                                                <span class="m-nav__section-text">Actions</span>
                                                            </li>

                                                            <li class="m-nav__separator m-nav__separator--fit">
                                                            <li class="m-nav__item">
                                                                <a id="newScriptBtn" class="m-nav__link">
                                                                    <i class="m-nav__link-icon la la-file"></i>
                                                                    <span class="m-nav__link-text">New script</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a id="openScriptBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#openCssModal">
                                                                    <i class="m-nav__link-icon la la-folder-open-o"></i>
                                                                    <span class="m-nav__link-text">Open script</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a id="openVersionBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#openVersionModal">
                                                                    <i class="m-nav__link-icon la la-history"></i>
                                                                    <span class="m-nav__link-text">Open previous version</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a id="saveScriptBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#saveCssModal">
                                                                    <i class="m-nav__link-icon la la-floppy-o"></i>
                                                                    <span class="m-nav__link-text">Save script</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a id="downloadScriptBtn" class="m-nav__link">
                                                                    <i class="m-nav__link-icon la la-download"></i>
                                                                    <span class="m-nav__link-text">Download script</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__separator m-nav__separator--fit">
                                                            <li class="m-nav__item">
                                                                <a id="normalizeScriptBtn" class="m-nav__link">
                                                                    <i class="m-nav__link-icon la la-magic"></i>
                                                                    <span class="m-nav__link-text">Normalize script</span>
                                                                </a>
                                                            </li>

                                                            <li class="m-nav__separator m-nav__separator--fit">
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a href="#"
                                                                   class="btn btn-outline-danger m-btn m-btn--pill m-btn--wide btn-sm">Cancel</a>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </li>

                                </ul>
                            </div>
                        </div>
                        <div class="m-portlet__body" style="padding: 0; height: calc(100% - 66px);">
                            <textarea id="scriptEditor" style="display: none;"></textarea>
                        </div>
                    </div>
                </div>


                <div id="textPanel" class="split-flex split-horizontal-flex;" style="overflow: hidden">

                    <div class="m-portlet m-portlet--tabs" m-portlet="true" id="chatPortlet"
                         style="margin: 0;height: 100%;width: 100%;">
                        <div class="m-portlet__head">
                            <div class="m-portlet__head-caption">
                                <div class="m-portlet__head-title truncate">
                                    <input type="text" value="Graph" id="scriptName"
                                           style="width: 100%;height: 100%;border: none;overflow: hidden;text-overflow: ellipsis;font-family: Poppins;"
                                           readonly>
                                </div>

                            </div>

                            <div class="m-portlet__head-tools">

                                <ul class="m-portlet__nav" id="chatTools">

                                    <li class="m-portlet__nav-item">
                                        <div class="input-icon right">
                                            <i class="icon-magnifier"></i>
                                            <input id="networkSrchInput" type="text" class="form-control input-circle" placeholder="search...">
                                        </div>
                                    </li>
                                    <li class="m-portlet__nav-item">
                                        <a id="clearBtn"
                                           class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill"
                                           data-toggle="m-tooltip" data-placement="top" title=""
                                           data-original-title="Clear conversation">
                                            <i class="la la-close"></i>
                                        </a>
                                    </li>
                                    <li class="m-portlet__nav-item">
                                        <a m-portlet-tool="fullscreen"
                                           class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill">
                                            <i class="la la-expand"></i>
                                        </a>
                                    </li>
                                </ul>

                                <ul class="nav nav-tabs m-tabs m-tabs-line   m-tabs-line--right m-tabs-line-danger" style="float: left; display: none; min-width: 122px; margin-left: 5px;" id="matchTools">
                                    <li class="nav-item m-tabs__item">
                                        <a class="nav-link m-tabs__link active" id="textInputBtn">
                                            Input
                                        </a>
                                    </li>
                                    <li class="nav-item m-tabs__item">
                                        <a class="nav-link m-tabs__link" id="outputPanelBtn">
                                            Output
                                        </a>
                                    </li>
                                </ul>



                            </div>
                        </div>
                        <div class="m-portlet__body" id="graphOutput" style="padding: 0; height: calc(100% - 71px);">

                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>


    <div class="row">
        <div class="col-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-info-circle"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Details
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12" id="detailsPanel">

                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-globe"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Location
                            </h3>
                        </div>
                    </div>
                    <div class="m-portlet__head-tools">
                        <ul class="m-portlet__nav">
                            <li class="m-portlet__nav-item">
                                <a m-portlet-tool="fullscreen"
                                   class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill">
                                    <i class="la la-expand"></i>
                                </a>
                            </li>
                        </ul>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-sm-12">
                            <div id="noLocationMsg">No location information available...</div>
                            <div id="map"></div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-cube"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Nodes
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <form class="form-inline">
                                <div class="form-control mb-2 mr-sm-2 m-input-icon m-input-icon--left">
                                    <input type="text" class="form-control m-input" placeholder="Search..." id="nodeSearchInput">
                                    <span class="m-input-icon__icon m-input-icon__icon--left">
                                        <span><i class="la la-search"></i></span>
                                    </span>
                                </div>


                            </form>
                        </div>


                    </div>
                    <div class="row">
                        <div class="col-12" id="nodeDataTableParent">
                            <div id="nodeDataTable"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <div class="row">
        <div class="col-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-database"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Links
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <form class="form-inline">
                                <div class="form-control mb-2 mr-sm-2 m-input-icon m-input-icon--left">
                                    <input type="text" class="form-control m-input" placeholder="Search..." id="linkSearchInput">
                                    <span class="m-input-icon__icon m-input-icon__icon--left">
                                        <span><i class="la la-search"></i></span>
                                    </span>
                                </div>


                            </form>
                        </div>


                    </div>
                    <div class="row">
                        <div class="col-12" id="linkDataTableParent">
                            <div id="linkDataTable"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>



    <div class="row">
        <div class="col-sm-12" id="doc">
            <h2>Documentation</h2>
        </div>
    </div>

</div>


<#-- Example modal -->
<div class="modal fade" id="loadExampleModal" tabindex="-1" role="dialog" aria-labelledby="loadExampleModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="loadExampleModalLabel">Open example script</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="loadExampleSelect">Select an example script</label>
                        <select class="form-control" id="loadExampleSelect">

                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button id="loadExampleBtn" type="button" class="btn btn-brand">Open</button>
            </div>
        </div>
    </div>
</div>
</#macro>

<#macro pageScript>

<script>
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-analyse-graph')
        $('#linkSearchInput').val($.hx.getUrlParam('keyword'))
        let linkTable, nodeTable;
        let sleaflet = new Sleaflet('map', 'pk.eyJ1IjoiYWhhbG1hIiwiYSI6ImNpeWp2dGMzbjAwMHIyd3I0dHV4bXE4dHUifQ.6e6CLc5hZdRItmVWkA_N3g',
                0, 0, 3);

        Split(['#rulePanel', '#textPanel'], {
            sizes: [50, 50],
            gutterSize: 8,
            cursor: 'col-resize',
            elementStyle: function (dimension, size, gutterSize) {
                return {'flex-basis': 'calc(' + size + '% - ' + gutterSize + 'px)'}
            },
            gutterStyle: function (dimension, gutterSize) {
                return {'flex-basis': gutterSize + 'px'}
            }
        });

        function setScriptName(name) {
            $('#scriptName').val(name)
            localStorage.setItem('scriptName', name);
        }

        const scriptEditor = new CcsEditor($('#scriptEditor')[0], 'chat.ccs')

        $('#newScriptBtn').click(function () {
                    scriptEditor.setText("")
                    setScriptName("New script")
                }
        )

        $('#downloadScriptBtn').click(e =>
                saveAs(new Blob([scriptEditor.getText()], {type: "text/plain;charset=utf-8"}), $('#scriptName').val()+".ccs")
        )

        $('#normalizeScriptBtn').click(e => {
            let formData = new FormData();
            formData.append("script", scriptEditor.getText());

            $.ajax({
                type: "POST",
                url: "/api/v1/script/normalize",
                data: formData,
                processData: false,
                contentType: false,
                success: (a) => {
                    scriptEditor.setText(a);
                }
            })
        })


        let openDocDialog = new OpenDocDialog('body', 'openCssModal', 'ccs', 'Open script', function (name, src) {
            scriptEditor.setText(src)
            setScriptName(name)
        });

        let openVerionDocDialog = new OpenHistoryDocDialog('body', 'openVersionModal', 'ccs', function () {
            return openDocDialog.getOpenDocName()
        }, 'Open previous version', function (name, src) {
            scriptEditor.setText(src)
        });

        let saveDocDialog = new SaveDocDialog('body', 'saveCssModal', 'ccs', 'Save script',
                function () {
                    return scriptEditor.getText()
                },
                function () {
                    return $('#scriptName').val()
                },
                function (name) {
                    setScriptName(name)
                    $.hx.notify('Script saved: ' + name, 'success')
                },
                function (name, e) {
                    const resp = JSON.parse(e.responseText);
                    if (resp.message) {
                        const message = resp.message;
                        let lineNr = resp.line - 1;

                        $.hx.notify('There is an error in your script.<br>' + resp.message, 'danger')
                        scriptEditor.setErrorMessage(lineNr, message)
                    } else {
                        $.hx.notify('The script could not be saved.<br>' + e, 'danger')
                    }
                }
        );

        setScriptName(localStorage.getItem('scriptName') || 'New script')

        new ShowdownAccordion('/ccsdoc.md', '#doc', 1, "cssmode")


        const panZoomConfig = {
            zoomFactor: 0.05, // zoom factor per zoom tick
            zoomDelay: 45, // how many ms between zoom ticks
            minZoom: 0.1, // min zoom level
            maxZoom: 10, // max zoom level
            fitPadding: 50, // padding when fitting
            panSpeed: 10, // how many ms in between pan ticks
            panDistance: 10, // max pan distance per tick
            panDragAreaSize: 75, // the length of the pan drag box in which the vector for panning is calculated (bigger = finer control of pan speed and direction)
            panMinPercentSpeed: 0.25, // the slowest speed we can pan by (as a percent of panSpeed)
            panInactiveArea: 8, // radius of inactive area in pan drag box
            panIndicatorMinOpacity: 0.5, // min opacity of pan indicator (the draggable nib); scales from this to 1.0
            zoomOnly: false, // a minimal version of the ui only with zooming (useful on systems with bad mousewheel resolution)
            fitSelector: undefined, // selector of elements to fit
            animateOnFit: function(){ // whether to animate on fit
                return false;
            },
            fitAnimationDuration: 1000, // duration of animation on fit

            // icon class names
            sliderHandleIcon: 'la la-minus',
            zoomInIcon: 'la la-plus',
            zoomOutIcon: 'la la-minus',
            resetIcon: 'la la-expand'
        }

        function tapGraphForDetails(cyto){
            cyto.on('tap', function(evt){
                const data = evt.target.data()
                console.log(data);
                $('#detailsPanel').html($.c4i.toTable(data));
                if(data.source !== undefined){
                    // clicked an edge


                    /*$.get('/api/v1/cdr/call/'+data.source+'/to/'+data.target, {
                        fromDate: fromDateStr,
                        toDate: toDateStr,
                    }, calls => {
                        updateCallTable(calls)
                    })*/

                } else {
                    // clicked a vertex

                    /*$.get('/api/v1/cdr/call/'+data.id+'/summary', {
                        fromDate: fromDateStr,
                        toDate: toDateStr,
                    }, summary => {
                        console.log( summary )
                        showCallDetails(summary)
                    })*/


                }
            })
        }


        function updateGraph(graph){
            let dataElts = graph.nodes.map( v => {

                if(v.type == 'actor') {
                    return {
                        data: {
                            id: v.id,
                            type: v.type,
                            betweenness: v.betweenness,
                            category: v.cat,
                        }
                        , classes: 'outline'

                    }
                } else if(v.type == 'loc') {
                    return {
                        data: {
                            id: v.id,
                            type: v.type,
                            betweenness: v.betweenness,
                            country: v.country,
                            latitude: v.lat,
                            longitude: v.lon,
                            snippet: v.snippet,
                        }
                        ,classes: 'outline'

                    }
                } else {
                    return {
                        data: {
                            id: v.id,
                            type: v.type,
                            betweenness: v.betweenness,
                            snippet: v.snippet,
                        }
                        ,classes: 'outline'
                    }

                }});

            const maxEdgeW = Math.max(...graph.edges.map(edge => edge.w));

            dataElts = dataElts.concat(graph.edges.map( edge => {
                return {
                    data: {
                        source: edge.a,
                        target: edge.b,
                        type: edge.type,
                        doc: edge.src,
                        snippet: edge.snippet,
                    },
                    style: {
                        'width': 3 * (edge.w * 1) / maxEdgeW
                    }
                }
            }));


            const cyto = cytoscape({
                container: document.getElementById('graphOutput'), // container to render in
                elements: dataElts,
                style: [ // the stylesheet for the graph
                    {
                        selector: 'node[type = "actor"]',
                        style: {
                            'width': 30,
                            'height': 30,
                            'label': 'data(id)',
                            'text-valign': 'center',
                            'color': 'white',
                            'text-outline-width': 2,
                            'text-outline-color': 'mapData(betweenness, 0, 1, #3598dc, #e53e49)',
//                    'fill': 'red',
//                    'display':'inline',
                            'background-color': 'mapData(betweenness, 0, 1, #3598dc, #e53e49)',
                            'font-size': '10px'
                        }
                    },
                    {
                        selector: 'node[type = "verb"]',
                        style: {
                            'width': 30,
                            'height': 30,
                            'label': 'data(id)',
                            'text-valign': 'center',
                            'color': 'white',
                            'text-outline-width': 2,
                            'text-outline-color': 'mapData(betweenness, 0, 1, #FFC733, #e53e49)',
//                    'fill': 'red',
//                    'display':'inline',
                            'background-color': 'mapData(betweenness, 0, 1, #FFC733, #e53e49)',
                            'font-size': '10px'
                        }
                    },
                    {
                        selector: 'node[type = "loc"]',
                        style: {
                            'width': 30,
                            'height': 30,
                            'label': 'data(id)',
                            'text-valign': 'center',
                            'color': 'white',
                            'text-outline-width': 2,
                            'text-outline-color': 'mapData(betweenness, 0, 1, #30D14C, #e53e49)',
//                    'fill': 'red',
//                    'display':'inline',
                            'background-color': 'mapData(betweenness, 0, 1, #30D14C, #e53e49)',
                            'font-size': '10px'
                        }
                    },
                    {
                        selector: 'edge',
                        style: {
                            'width': 6,
                            'line-color': '#333',
//                    'target-arrow-color': '#333',
//                    'target-arrow-shape': 'triangle',
                            'curve-style': 'haystack'
                        }
                    },
                ],

                layout: {
                    name: 'euler',
//                animate: false, // whether to animate changes to the layout
                    animate: 'end', // whether to animate changes to the layout
//                maxSimulationTime: 4000,
                    maxIterations: 250,
                }
            });

            // window.setInterval(cyto.resize, 1000);

            $('#networkSrchInput').on('input', () => {
                let id = $('#networkSrchInput').val()
                cyto.fit( cyto.$("[id *= '"+id+"']"), 50 )
            })

//            $('#fitGraphBtn').click(cyto.fit)
            $('#saveGraphBtn').click(() =>
            {
                let img = cyto.jpg({
                    maxWidth:10000,
                    maxHeight:10000,
                })
                $.hx.saveAs(img, 'graph'+Math.floor(Date.now() / 1000)+'.jpg');
            })
//            $('#fullScreenBtn').click(() => {
//              window.setTimeout(cyto.resize, 1000);
//            })

            $('#networkSrchInput').on('input', () => {
                let id = $('#networkSrchInput').val()
                cyto.fit( cyto.$("[id *= '"+id+"']"), 50 )
            })

            tapGraphForDetails(cyto)
            cyto.panzoom( panZoomConfig );

        }

        function updateEdgeTable(graph){
            let edges = graph.edges;

            if (linkTable ) {
                linkTable.destroy()
                linkTable = undefined
                // $('#msgDataTableParent').html('<div id="msgDataTable"></div>');
            }

            linkTable = $('#linkDataTable').mDatatable({
                // datasource definition
                data: {
                    type: 'local',
                    source: edges,
                    pageSize: 10
                },

                // layout definition
                layout: {
                    theme: 'default', // datatable theme
                    scroll: true, // enable/disable datatable scroll both horizontal and vertical when needed.
                    // height: 430, // datatable's body's fixed height
                    footer: false // display/hide footer
                },

                toolbar: {
                    layout: ['pagination', 'info'],
                    placement: ['bottom'],  //'top', 'bottom'
                    items: {
                        pagination: {
                            type: 'default',

                            pages: {
                                desktop: {
                                    layout: 'default',
                                    pagesNumber: 6
                                },
                                tablet: {
                                    layout: 'default',
                                    pagesNumber: 3
                                },
                                mobile: {
                                    layout: 'compact'
                                }
                            },

                            navigation: {
                                prev: true,
                                next: true,
                                first: true,
                                last: true
                            },

                            pageSizeSelect: [5, 10, 50, -1]
                        },

                        info: true
                    }
                },

                // column sorting
                sortable: true,
                pagination: true,

                search: {
                    input: $('#linkSearchInput'),
                    delay: 200,
                },
                // inline and bactch editing(cooming soon)
                // editable: false,

                // columns definition
                columns: [

                    {field: "a", width: 80, title: "from", template: row => row.a},
                    {field: "b", width: 80, title: "to", template: row => row.b},
                    // {field: "w", title: "centrality", template: row => row.w},
                    {field: "src", width: 120, title: "source", template: row => row.src},
                    {field: "snippet", title: "snippet", template: row => row.snippet},


                ]
            });
            /*$('#linkDataTable')
                    .on('click', `.conversationDetailBtn`, function () {
                        const convId = $(this).data('conv-id');
                        updateMessageTable(convId)
                        $.hx.scrollIntoView('#msgPortlet', -85);
                    })
                    .on('click', `.deleteConvBtn`, function () {
                        const convId = $(this).data('conv-id');
                        $.ajax({
                            url: '/api/v1/db/conversation/' + encodeURIComponent(convId),
                            type: 'DELETE',
                            success: function(result) {
                                $.hx.notify('Conversation deleted.', 'success')
                                update()
                            }
                        });

                    })*/
        }

        function updateNodeTable(graph){
            let nodes = graph.nodes;

            if (nodeTable ) {
                nodeTable.destroy()
                nodeTable = undefined
                // $('#msgDataTableParent').html('<div id="msgDataTable"></div>');
            }

            nodeTable = $('#nodeDataTable').mDatatable({
                // datasource definition
                data: {
                    type: 'local',
                    source: nodes,
                    pageSize: 10
                },

                // layout definition
                layout: {
                    theme: 'default', // datatable theme
                    scroll: true, // enable/disable datatable scroll both horizontal and vertical when needed.
                    // height: 430, // datatable's body's fixed height
                    footer: false // display/hide footer
                },

                toolbar: {
                    layout: ['pagination', 'info'],
                    placement: ['bottom'],  //'top', 'bottom'
                    items: {
                        pagination: {
                            type: 'default',

                            pages: {
                                desktop: {
                                    layout: 'default',
                                    pagesNumber: 6
                                },
                                tablet: {
                                    layout: 'default',
                                    pagesNumber: 3
                                },
                                mobile: {
                                    layout: 'compact'
                                }
                            },

                            navigation: {
                                prev: true,
                                next: true,
                                first: true,
                                last: true
                            },

                            pageSizeSelect: [5, 10, 50, -1]
                        },

                        info: true
                    }
                },

                // column sorting
                sortable: true,
                pagination: true,

                search: {
                    input: $('#nodeSearchInput'),
                    delay: 200,
                },
                // inline and bactch editing(cooming soon)
                // editable: false,

                // columns definition
                columns: [

                    {field: "id", width: 100, title: "term", template: row => row.id},
                    {field: "type", width: 40, title: "type", template: row => row.type},
                    {field: "betweenness", width: 40, title: "centrality", template: row => Number.parseFloat(row.betweenness).toFixed(3)},
                    {field: "src", width: 140, title: "source", template: row => row.src},
                    {field: "snippet", title: "snippet", template: row => row.snippet},


                ]
            });
            /*$('#linkDataTable')
                    .on('click', `.conversationDetailBtn`, function () {
                        const convId = $(this).data('conv-id');
                        updateMessageTable(convId)
                        $.hx.scrollIntoView('#msgPortlet', -85);
                    })
                    .on('click', `.deleteConvBtn`, function () {
                        const convId = $(this).data('conv-id');
                        $.ajax({
                            url: '/api/v1/db/conversation/' + encodeURIComponent(convId),
                            type: 'DELETE',
                            success: function(result) {
                                $.hx.notify('Conversation deleted.', 'success')
                                update()
                            }
                        });

                    })*/
        }


        $('#runBtn').click(() => {
            scriptEditor.removeErrorMessage()

            let formData = new FormData();
            formData.append("script", scriptEditor.getText());
            //formData.append("text", $('#textInput').val());

            $.ajax({
                type: "POST",
                url: "/api/v1/db/graph",
                data: formData,
                processData: false,
                contentType: false,
                success: (graph) => {
                    console.log(graph);

                    updateGraph(graph)
                    updateEdgeTable(graph)
                    updateNodeTable(graph)
                    updateMap(graph)

                },
                error: (e) => {
                    e = JSON.parse(e.responseText);
                    let message = e.message;
                    let lineNr = e.line - 1;
                    if(lineNr !== undefined){
                        scriptEditor.setErrorMessage(lineNr, message)
                    }
                    $.hx.notify('There is an error in your script.<br>' + e.message, 'danger')

                }
            });



        })

        function updateMap(graph){
            $('#map').show()
            let locationCount = 0;

            for (let node of graph.nodes) {
                if(node.lat * 1){
                    // found a location
                    locationCount++;
                    // sleaflet.addCircle(node.lat * 1, node.lon * 1, 2000, 'rgb(11, 98, 164)', node.id, 0.25, true)
                    sleaflet.addMarker(node.lat * 1, node.lon * 1, node.id, true)
                }
            }
            if(locationCount) {
                $('#map').show()
                $('#noLocationMsg').hide()
                sleaflet.addCluster()
                sleaflet.zoomToMarkers()
            } else {
                $('#map').hide()
                $('#noLocationMsg').show()

            }

        }



    })
</script>
</#macro>