<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Variables
</#macro>

<#macro pageContent>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Variables
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
												Variables
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
                                    <a class="m-nav__link" id="openVariables" data-toggle="modal" data-target="#openVarsModal">
                                        <i class="m-nav__link-icon la la-folder-open-o"></i>
                                        <span class="m-nav__link-text">Open variables</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a id="openVersionBtn" class="m-nav__link" data-toggle="modal" data-target="#openVersionModal" >
                                        <i class="m-nav__link-icon la la-history"></i>
                                        <span class="m-nav__link-text">Open previous version</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a class="m-nav__link" id="saveVariables" data-toggle="modal" data-target="#saveVarsModal">
                                        <i class="m-nav__link-icon la la-save"></i>
                                        <span class="m-nav__link-text">Save variables</span>
                                    </a>
                                </li>
                                <li class="m-nav__item">
                                    <a href="#" class="m-nav__link" id="downloadVariables">
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
													<i class="la la-dollar"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Reply variables
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <p>
                        Here you can define variables that can be used in replies, like <code>Hi -> The weather is $WHEATHERSTATE.</code>
                    </p>
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

<link rel="stylesheet" type="text/css" href="/asset5/vendors/custom/handsontable/dist/handsontable.full.min.css">
<script src="/asset5/vendors/custom/handsontable/dist/handsontable.full.min.js"></script>
<script>
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-data-variables')
    })

    var data = [
        ['', ''],
        ['', ''],
    ];



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
    };
    let hot = new Handsontable(document.getElementById('handsontable'), settings);

    $.get('/api/v1/db/vars', function(map){
        // because Object.keys(new Date()).length === 0;
        // we have to do some additional check
        if(!(Object.keys(map).length === 0 && map.constructor === Object)){
            hot.loadData(Object.entries(map))
        }
    })


    $('#downloadVariables').click(() => {
        $.hx.csvDownload( hot.getData(), '\t', 'chitchat-variables.csv')
    })

    let openDocDialog = new OpenDocDialog('body', 'openVarsModal', 'vars', 'Open a variables sheet', function (name, src) {
        hot.loadData($.hx.csvParse(src))
    } );

    let openVerionDocDialog = new OpenHistoryDocDialog('body', 'openVersionModal', 'vars',
            function(){return "Reply variables"}, 'Open previous version', function (name, src) {
                hot.loadData($.hx.csvParse(src))
            } );

    let saveDocDialog = new SaveDocDialog('body', 'saveVarsModal', 'vars', 'Save variables sheet',
            function(){return $.hx.csvString(hot.getData())},
            function(){return "Reply variables"},
            function (name) {
                $.ajax({
                    url: '/api/v1/db/vars/load/',
                    type: 'PUT',
                    success: function (result) {
                        $.hx.notify('Variables updated', 'success')
                    }
                });
            },
            function (name, e) {
                $.hx.notify('The variables could not be saved.<br>' + e, 'danger')
            }
    );

</script>
</#macro>