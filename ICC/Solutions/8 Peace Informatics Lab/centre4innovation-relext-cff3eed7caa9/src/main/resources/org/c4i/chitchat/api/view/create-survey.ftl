<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Script Builder
</#macro>

<#macro pageContent>

<style>
    .inspire-tree .btn li [class*=" fa-"], li [class*=" glyphicon-"], li [class*=" icon-"], li [class^=fa-], li [class^=glyphicon-], li [class^=icon-] {
        text-align: left;
        width: auto;
    }

    .inspire-tree .btn {
        margin-top: -3px;
    }

    .inspire-tree li a {
        font-family: "Open Sans",sans-serif;
        text-decoration: none;
        color: #333;
    }
</style>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Create Survey Script
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
											<span class="m-nav__link-text">Create</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">Survey</span>
                    </a>
                </li>
            </ul>
        </div>
        <div class="m-dropdown m-dropdown--inline m-dropdown--arrow m-dropdown--align-right m-dropdown--align-push" m-dropdown-toggle="hover" aria-expanded="true">
            <a href="#" class="m-portlet__nav-link btn btn-lg btn-secondary  m-btn m-btn--outline-2x m-btn--air m-btn--icon m-btn--icon-only m-btn--pill  m-dropdown__toggle">
                <i class="la la-plus m--hide"></i>
                <i class="la la-ellipsis-h"></i>
            </a>
            <div class="m-dropdown__wrapper" style="z-index: 101;">
                <span class="m-dropdown__arrow m-dropdown__arrow--right m-dropdown__arrow--adjust" style="left: auto; right: 21.5px;"></span>
                <div class="m-dropdown__inner">
                    <div class="m-dropdown__body">
                        <div class="m-dropdown__content">
                            <ul class="m-nav">
                                <li class="m-nav__section m-nav__section--first m--hide">
                                    <span class="m-nav__section-text">Actions</span>
                                </li>
                                <li class="m-nav__item">
                                    <a class="m-nav__link" id="openFile" data-toggle="modal" data-target="#openFileModal">
                                        <i class="m-nav__link-icon la la-folder-open-o"></i>
                                        <span class="m-nav__link-text">Open survey</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a id="openVersionBtn" class="m-nav__link" data-toggle="modal" data-target="#openVersionModal" >
                                        <i class="m-nav__link-icon la la-history"></i>
                                        <span class="m-nav__link-text">Open previous version</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a class="m-nav__link" id="saveFile" data-toggle="modal" data-target="#saveFileModal">
                                        <i class="m-nav__link-icon la la-save"></i>
                                        <span class="m-nav__link-text">Save survey</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a href="#" class="m-nav__link" id="downloadFile">
                                        <i class="m-nav__link-icon la la-download"></i>
                                        <span class="m-nav__link-text">Download as CSV</span>
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="row">
        <div class="col-lg-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-clipboard"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Survey converter: <span id="sheetName"style="color: #716aca;">Survey</span>
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <p>
                                Write out questions and their multiple-choice answers, and link them via the "go to" field.
                            </p>
                            <div style="height: 400px; overflow: hidden; width: 100%">
                                <div id="handsontable"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="m-portlet__foot m-portlet__foot">
                    <div class="m-form__actions m-form__actions">
                        <form class="form-inline">

                        <div class="form-group mr-2">
                            <label for="styleSelect" class="mr-2">Style</label>
                            <select class="form-control" id="styleSelect">
                                <option value="plain">Plain text options</option>
                                <option value="buttons">Button options</option>
                            </select>
                        </div>
                        <div class="btn btn-success" id="surveyBtn"><i class="la la-gear"></i> Convert</div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-gear"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Resulting script
                            </h3>
                        </div>
                    </div>

                    <div class="m-portlet__head-tools">
                        <ul class="m-portlet__nav">

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
                <div class="m-portlet__body" style="padding: 0;">
                    <div class="row">
                        <div class="col-12">
                            <textarea id="scriptEditor" style="display: none;"></textarea>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<#--
<!-- BEGIN PAGE TITLE&ndash;&gt;
<h1 class="page-title"> Chat Script Builder
</h1>
<!-- END PAGE TITLE&ndash;&gt;
&lt;#&ndash;<p id="description"></p>&ndash;&gt;
<!-- END PAGE HEADER&ndash;&gt;
<div class="alert alert-warning" role="alert">
    Work in progress. Not functional yet...
</div>

<div class="row">
  <div class="col-sm-12">
      <div class="portlet light portlet-fit bordered">
          <div class="portlet-title tabbable-line">
              <div class="caption">
                  <i class="fa fa-wrench font-green"></i>
                  <span class="caption-subject bold font-green uppercase"> Define </span>
              </div>
              <ul class="nav nav-tabs">

                  <li class="active">
                      <a href="#portlet_tab1" data-toggle="tab" aria-expanded="true"> 1. Taxonomy </a>
                  </li>
                  <li class="">
                      <a href="#portlet_tab2" data-toggle="tab" aria-expanded="false"> 2. Replies </a>
                  </li>
              </ul>
          </div>
          <div class="portlet-body">
              <div class="tab-content">
                  <div class="tab-pane active" id="portlet_tab1">
                          <h4>Tree editor</h4>
                          <div class="tree"></div>
                  </div>
                  <div class="tab-pane" id="portlet_tab2">
                      <h4>Answers</h4>

                      <table class="table table-bordered">

                          <tr>
                              <td>
                                  <span style="writing-mode: vertical-rl;">foooo</span>
                              </td>
                              <td>
                                  <span style="writing-mode: vertical-rl;">barr ar oo</span>
                              </td>
                              <td>

                              </td>
                          </tr>
                          <tr>
                              <td >
                                  <input type="checkbox">
                              </td>
                              <td >
                                  <input type="checkbox">
                              </td>
                              <td>
                                  <input type="text" placeholder="reply 1">
                              </td>
                          </tr>

                          <tr>
                              <td >
                                  <span class="btn btn-small" data-state="1">1</span>
                              </td>
                              <td >
                                  <input type="checkbox">
                              </td>
                              <td>
                                  <input type="text" placeholder="reply 1">
                              </td>
                          </tr>
                      </table>
                  </div>
              </div>
          </div>
      </div>

  </div>
</div>-->
<#--
<div class="row">
    <div class="col-md-12">
        <!-- BEGIN MARKERS PORTLET&ndash;&gt;
        <div class="portlet light portlet-fit bordered">
            <div class="portlet-title">
                <div class="caption">
                    <i class="fa fa-question-circle font-grey-gallery"></i>
                    <span class="caption-subject font-grey-gallery bold uppercase">Survey converter</span>
                </div>
            </div>
            <div class="portlet-body">
                <p><textarea id="surveyText" style="font-family: Consolas, monospace; width: 100%" rows="20" ></textarea></p>
                <div class="btn btn-success" id="surveyBtn"><i class="fa fa-gear"></i> Convert</div>
                <p>
                <pre id="surveyScript">

                </pre>
                </p>
            </div>
        </div>
        <!-- END MARKERS PORTLET&ndash;&gt;
    </div>
</div>-->

</#macro>

<#macro pageScript>

<#--<script src="/asset5/vendors/custom/lodash.js" type="text/javascript"></script>-->
<script src="/asset5/vendors/custom/inspire-tree/inspire-tree.min.js" type="text/javascript"></script>
<script src="/asset5/vendors/custom/inspire-tree/inspire-tree-dom.min.js" type="text/javascript"></script>
<link href="/asset5/vendors/custom/inspire-tree/inspire-tree-light.css" rel="stylesheet" type="text/css" />


<link rel="stylesheet" type="text/css" href="/asset5/vendors/custom/handsontable/dist/handsontable.full.min.css">
<script src="/asset5/vendors/custom/handsontable/dist/handsontable.full.min.js"></script>

<script>
$(document).ready( () => {
    $.hx.setCurrentPage('#menu-item-create-survey')
    /*var treeData = JSON.parseYaml(localStorage.getItem("builderTreeData"))
            || [{
        text: 'Features',
        children: [
            {text: 'Robust API'},
            {text: 'Bla'},
            {text: 'Fooooo'},
        ]
    }]


    var tree = new InspireTree({
        editable: true,
        data: treeData, // Array, callback, or promise
    });
    new InspireTreeDOM(tree, {
        dragAndDrop: true,
        target: '.tree'
    });

    tree.nodes().expandDeep()

    tree.mute(['node.rendered', 'node.state.changed']);
    tree.onAny(function() {
        console.log(this.event, arguments);


        let builderTreeData = tree.toArray();
        console.log(builderTreeData);
        localStorage.setItem("builderTreeData" , JSON.stringify(builderTreeData))
    });*/


    const scriptEditor = new CcsEditor($('#scriptEditor')[0], 'chat.ccs')

    scriptEditor.setText("# Click [Convert]")

    $('#surveyBtn').click(() => {
      let csv = $.hx.csvString(hot.getData());
      let formData = new FormData();
      formData.append("csv", csv);
      formData.append("style", $('#styleSelect').val());

        $.ajax({
            type: "POST",
            url: "/api/v1/script/survey/csv",
            data: formData,
            processData: false,
            contentType: false,
            success: (src) => {
                scriptEditor.setText(src);

            }
        })
    })


    let data = [
        ['q1', 'How are you?', 'good', '1. Very good!', ''],
        ['', '', 'bad', '2. Very bad', 'q2'],
        ['', '', 'soso', '3. Soso', 'q2'],
        ['q2', 'Why are you feeling like this?', 'nocoffee', '1. I didn\'t have coffee', ''],
        ['', '', 'unknown', '2. I don\'t know', ''],
    ];

    // maps function to lookup string
    Handsontable.renderers.registerRenderer('identifierRenderer', identifierRenderer);
    let settings = {
        data: data,
        comments: true,
        fixedRowsTop: 1,
        stretchH: 'last',
        autoWrapRow: true,
        rowHeaders: true,
        contextMenu: true,
        manualRowMove: true,
        manualColumnResize: true,
        colHeaders: [
            'Q id',
            'Q text',
            'A id',
            'A text',
            'Goto Q id',
        ],
        columns: [
            {
                data: 0,
                type: 'text',
                width: 60
            },
            {
                data: 1,
                type: 'text',
                width: 300
            },
            {
                data: 2,
                type: 'text',
                width: 60
            },
            {
                data: 3,
                type: 'text',
                width: 300
            },
            {
                data: 4,
                type: 'text',
                width: 60
            },
        ],
        afterSelection: function (row, col, row2, col2) {
            const meta = this.getCellMeta(row2, col2);

            if (meta.readOnly) {
                this.updateSettings({fillHandle: false});
            }
            else {
                this.updateSettings({fillHandle: true});
            }
        },
        cells: function (row, col) {
            const cellProperties = {};
            const data = this.instance.getData();

            if (col === 0 || col === 2 || col === 4) {
                cellProperties.renderer = 'identifierRenderer'; // uses function directly
            }

            return cellProperties;
        }
    };
    let hot = new Handsontable(document.getElementById('handsontable'), settings);

    function identifierRenderer(instance, td, row, col, prop, value, cellProperties) {
        Handsontable.renderers.TextRenderer.apply(this, arguments);

        if(!/^[a-zA-Z0-9]+$/.test(value) && value.length > 0){
            td.style.background = '#ee9c8c';
            td.style.color = '#600';
            td.title = 'No spaces or other non-alphabetic characters allowed'
        } else {
            td.style.background = '';
            td.title = ''
        }

    }


    /*$.get('/api/v1/db/vars', function(map){
        // because Object.keys(new Date()).length === 0;
        // we have to do some additional check
        if(!(Object.keys(map).length === 0 && map.constructor === Object)){
            hot.loadData(Object.entries(map))
        }
    })*/


    $('#downloadFile').click(() => {
        $.hx.csvDownload( hot.getData(), '\t', $('#sheetName').text()+'.csv')
    })

    let openDocDialog = new OpenDocDialog('body', 'openFileModal', 'survey', 'Open a survey', function (name, src) {
        $('#sheetName').text(name)
        hot.loadData($.hx.csvParse(src))
    } );

    let openVerionDocDialog = new OpenHistoryDocDialog('body', 'openVersionModal', 'survey',
            function(){return $('#sheetName').text()}, 'Open previous version', function (name, src) {
                hot.loadData($.hx.csvParse(src))
            } );

    let saveSurveyDocDialog = new SaveDocDialog('body', 'saveFileModal', 'survey', 'Save survey',
            function(){return $.hx.csvString(hot.getData())},
            function(){return $('#sheetName').text()},
            function (name) {
                $('#sheetName').text(name)
                $.hx.notify('Survey updated', 'success')
            },
            function (name, e) {
                $.hx.notify('The survey could not be saved.<br>' + e, 'danger')
            }
    );


    let saveCcsDocDialog = new SaveDocDialog('body', 'saveCssModal', 'ccs', 'Save script',
            function () {
                return scriptEditor.getText()
            },
            function () {
                return $('#sheetName').text()
            },
            function (name) {
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

})
</script>
</#macro>