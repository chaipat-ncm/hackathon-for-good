<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Data sheets
</#macro>

<#macro pageContent>



<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Data sheets
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
												Data
											</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">
												Sheets
											</span>
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
                                    <a class="m-nav__link" id="newVariables">
                                        <i class="m-nav__link-icon la la-file"></i>
                                        <span class="m-nav__link-text">New datasheet</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a class="m-nav__link" id="openVariables" data-toggle="modal" data-target="#openSheetModal">
                                        <i class="m-nav__link-icon la la-folder-open-o"></i>
                                        <span class="m-nav__link-text">Open datasheet</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a id="openVersionBtn" class="m-nav__link" data-toggle="modal" data-target="#openVersionModal" >
                                        <i class="m-nav__link-icon la la-history"></i>
                                        <span class="m-nav__link-text">Open previous version</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a class="m-nav__link" id="saveVariables" data-toggle="modal" data-target="#saveSheetModal">
                                        <i class="m-nav__link-icon la la-save"></i>
                                        <span class="m-nav__link-text">Save datasheet</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a href="#" class="m-nav__link" id="downloadSheet">
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
        <div class="col-md-12">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-th-list"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Data sheet:&nbsp;<span id="sheetName" style="color: #716aca;">-</span>
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <p>
                        Edit data sheets containing terms and properties that can be used in text matching.
                        Start by <a href="" id="openVariables" data-toggle="modal" data-target="#openSheetModal">opening</a> an existing datasheet. When saving changes, stick to the naming convention <code>{language}/{NAME}</code> (e.g. "en/COUNTRY"). You can use this data in scripts like <code>@place <- CITY</code>.
                    </p>

                    <div class="row">
                        <div class="col-md-12">
                            <div class="pull-right form-control mb-2 mr-sm-2 m-input-icon m-input-icon--left" style="width: 200px">
                                <input type="text" class="form-control m-input" placeholder="Search..." id="searchInput">
                                <span class="m-input-icon__icon m-input-icon__icon--left">
                                    <span id="searchCount" class="text-success" style="width: 35px;margin-right: -30px;font-size: 9px;height: 10px;margin-bottom: -13px;margin-top: 3px;text-align: right;"></span>
                                    <span><i class="la la-search"></i></span>
                                </span>
                            </div>
                        </div>
                    </div>

                    <div style="height: 70vh; overflow: hidden; width: 100%">
                        <div id="handsontable"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>


</div>


</#macro>

<#macro pageScript>

<style>
    .htCore tr:nth-child(1) > td {
        font-weight: bold;
        color: #716aca;
    }
</style>

<link rel="stylesheet" type="text/css" href="/asset5/vendors/custom/handsontable/dist/handsontable.full.min.css">
<script src="/asset5/vendors/custom/handsontable/dist/handsontable.full.min.js"></script>
<script>
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-data-sheets')
    })

    let data = $.hx.get('datasheet', [['', '']]);
    $('#sheetName').text($.hx.get('datasheetname', '-'))

    Handsontable.renderers.registerRenderer('headerRenderer', headerRenderer);
    let settings = {
        data: data,
        fixedRowsTop: 1,
        stretchH: 'last',
        autoWrapRow: true,
        rowHeaders: true,
        colHeaders: true,
        contextMenu: true,
        manualRowMove: true,
        manualColumnResize: true,
        search: true,
        sortIndicator: true,
        columnSorting: true,
        cells: function (row, col) {
            const cellProperties = {};

            if (row === 0) {
                cellProperties.renderer = 'headerRenderer'; // uses function directly
            }

            return cellProperties;
        }
    };
    const hot = new Handsontable(document.getElementById('handsontable'), settings);

    function headerRenderer(instance, td, row, col, prop, value, cellProperties) {
        Handsontable.renderers.TextRenderer.apply(this, arguments);

        if(!/^[a-zA-Z0-9]+$/.test(value) && value.length > 0){
            td.style.background = '#ee9c8c';
            td.style.color = '#600';
            td.title = 'No spaces or other non-alphabetic characters allowed for column names'
        } else {
            td.style.background = '';
            td.title = ''
        }

    }

    const searchField = document.getElementById('searchInput')
    Handsontable.dom.addEvent(searchField, 'keyup', function (event) {
        let search = hot.getPlugin('search');
        let queryResult = search.query(this.value);
        if(queryResult.length) {
            $('#searchCount').text(queryResult.length)
            hot.scrollViewportTo(queryResult[0].row, queryResult[0].col)
        } else {
            $('#searchCount').text('')
            hot.scrollViewportTo(0, 0)
        }

        hot.render();
    });

    /*$.get('/api/v1/db/sheet', function(map){
        // because Object.keys(new Date()).length === 0;
        // we have to do some additional check
        if(!(Object.keys(map).length === 0 && map.constructor === Object)){
            hot.loadData(Object.entries(map))
        }
    })*/


    $('#downloadSheet').click(() => {
        $.hx.csvDownload( hot.getData(), '\t', $('#sheetName').text()+'.csv')
    })

    $('#newVariables').click(() => {
        const datasheet = [['term', 'value'], ['',''], ['',''], ['','']]
        hot.loadData(datasheet)
        $('#sheetName').text('en/NEWSHEET')
        $.hx.set('datasheet', datasheet)
        $.hx.set('datasheetname', 'en/NEWSHEET')
    })


    let openDocDialog = new OpenDocDialog('body', 'openSheetModal', 'datasheet', 'Open a data sheet',
            function (name, src) {
                const datasheet = $.hx.csvParse(src);
                hot.loadData(datasheet)
                $('#sheetName').text(name)
                $.hx.set('datasheet', datasheet)
                $.hx.set('datasheetname', name)
            } );

    let openVerionDocDialog = new OpenHistoryDocDialog('body', 'openVersionModal', 'datasheet',
            function(){return $('#sheetName').text()},
            'Open previous version',
            function (name, src) {
                hot.loadData($.hx.csvParse(src))
            } );

    let saveDocDialog = new SaveDocDialog('body', 'saveSheetModal', 'datasheet', 'Save data sheet',
            function(){return $.hx.csvString(hot.getData())},
            function(){return $('#sheetName').text()},
            function (name) {
                $.ajax({
                    url: '/api/v1/db/sheet/load/'+encodeURIComponent(name),
                    type: 'PUT',
                    success: function (result) {
                        $.hx.notify('Data sheet updated', 'success')
                        const datasheet = hot.getData();
                        $('#sheetName').text(name)
                        $.hx.set('datasheet', datasheet)
                        $.hx.set('datasheetname', name)

                    }
                });
            },
            function (name, e) {
                $.hx.notify('The data sheet could not be saved.<br>Stick to the naming convention <code>{language}/{NAME}</code> (e.g. "en/COUNTRY")' + e, 'danger')
            }
    );

</script>
</#macro>