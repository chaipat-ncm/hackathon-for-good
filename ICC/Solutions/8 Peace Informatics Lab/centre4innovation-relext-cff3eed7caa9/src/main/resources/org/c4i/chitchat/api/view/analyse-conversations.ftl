<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Conversations
</#macro>

<#macro pageContent>


<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Conversations
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
                        <span class="m-nav__link-text">Analyse</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
                        <span class="m-nav__link-text">Conversations</span>
                    </a>
                </li>
            </ul>
        </div>
        <div>
            <div class="form-inline m-form__group">

                <div class="input-group m-input-group  m-input-group--pill mr-2" style="box-shadow: 0 3px 20px 0 rgba(113,106,202,.17)!important">
                    <select id="channelSelect" class="form-control m-input custom-select">
                        <option value="" selected>- all -</option>
                        <option value="fb">Facebook</option>
                        <option value="chitchat">Relext</option>
                        <option value="devbot">Devbot</option>
                        <option value="sample">Sample data</option>
                    </select>
                    <div class="input-group-append">
                        <label class="input-group-text m-input--pill" for="channelSelect" style="border-top-right-radius: 1.3rem;border-bottom-right-radius: 1.3rem;background: white;"><i class="la la-plug"></i></label>
                    </div>
                </div>

                <span class="m-subheader__daterange mr-2" id="m_dashboard_daterangepicker" style="box-shadow: 0 3px 20px 0 rgba(113,106,202,.17)!important">
                    <span class="m-subheader__daterange-label">
                        <span class="m-subheader__daterange-title">Today:</span>
                        <span class="m-subheader__daterange-date m--font-brand">May 19</span>
                    </span>
                    <a href="#" class="btn btn-sm btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--custom m-btn--pill">
                        <i class="la la-angle-down"></i>
                    </a>
                </span>


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
                                            <span class="m-nav__section-text">Quick Actions</span>
                                        </li>
                                        <li class="m-nav__item">
                                            <a href="" class="m-nav__link" id="resetFbConversations">
                                                <i class="m-nav__link-icon la la-recycle"></i>
                                                <span class="m-nav__link-text">Reset <b>facebook</b> conversations</span>
                                            </a>
                                        </li>
                                        <li class="m-nav__item">
                                            <a href="" class="m-nav__link">
                                                <i class="m-nav__link-icon la la-recycle"></i>
                                                <span class="m-nav__link-text">Reset <b>devbot</b> conversations</span>
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
    </div>
</div>
<div class="m-content">
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
                                Conversations
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <form class="form-inline">
                                <div class="form-control mb-2 mr-sm-2 m-input-icon m-input-icon--left">
                                    <input type="text" class="form-control m-input" placeholder="Search..." id="convSearchInput">
                                    <span class="m-input-icon__icon m-input-icon__icon--left">
                                        <span><i class="la la-search"></i></span>
                                    </span>
                                </div>

                                <#--<div class="input-group mb-2 mr-sm-2  m-input-group">
                                    <select id="channelSelect" class="form-control m-input custom-select">
                                        <option value="" selected>- all -</option>
                                        <option value="fb">Facebook</option>
                                        <option value="devbot">Devbot</option>
                                    </select>
                                    <div class="input-group-append">
                                        <label class="input-group-text" for="channelSelect"><i class="la la-plug"></i></label>
                                    </div>
                                </div>

                                <div class="input-group mb-2 mr-sm-2  m-input-group" id="m_daterangepicker">
                                    <input type="text" class="form-control" readonly="" placeholder="Select date range">
                                    <div class="input-group-append">
                                      <span class="input-group-text">
                                        <i class="la la-calendar-check-o"></i>
                                      </span>
                                    </div>
                                </div>-->
                            </form>
                        </div>


                    </div>
                    <div class="row">
                        <div class="col-12" id="convDataTableParent">
                            <div id="convDataTable"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="m-portlet" id="labelPortlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-tags"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Labels
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                <#--<div class="row">
                    <div class="col-12">
                        <form class="form-inline">
                            <div class="form-control mb-2 mr-sm-2 m-input-icon m-input-icon--left">
                                <input type="text" class="form-control m-input" placeholder="Search..." id="labelSearchInput">
                                <span class="m-input-icon__icon m-input-icon__icon--left">
                                    <span><i class="la la-search"></i></span>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>-->
                    <div class="row">
                        <div class="col-md-8" id="labelDataTableParent">
                            <div id="labelDataTable"></div>
                        </div>
                        <div class="col-md-4">
                            <div id="convhighlight"></div>
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
													<i class="la la-comments"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Messages
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                <#--<div class="row">
                    <div class="col-12">
                        <form class="form-inline">
                            <div class="form-control mb-2 mr-sm-2 m-input-icon m-input-icon--left">
                                <input type="text" class="form-control m-input" placeholder="Search..." id="msgSearchInput">
                                <span class="m-input-icon__icon m-input-icon__icon--left">
                                    <span><i class="la la-search"></i></span>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>-->
                    <div class="row">
                        <div class="col-12" id="msgDataTableParent">
                            <div id="msgDataTable"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>


</div>



</#macro>

<#macro pageScript>

<script>
    $(document).ready(() => {

        let convTable, msgTable, rangeTable;

        $.hx.setCurrentPage('#menu-item-analyse-conversation')

        $.hx.pagedaterangepicker(update);

        $('#convSearchInput').val($.hx.getUrlParam('keyword'))

        $('#channelSelect')
                .val($.hx.get('channel', ''))
                .change(function(){
                    let selectedVal = $(this).find(":selected").val();
                    $.hx.set('channel', selectedVal)
                    update()
                })

        function update() {

            $.get('/api/v1/db/conversation/between', {
                from: $.hx.get('startDate', moment().subtract('days', 6)).toISOString(),
                to: $.hx.get('endDate', moment()).toISOString(),
                channel: $.hx.get('channel', '')
            }, function (convs) {
                // remove url query params
                window.history.pushState({}, document.title, "/api/v1/ui/analyse/conversations");


                if (convTable ) {
                    convTable.destroy()
                    $('#convDataTableParent').html('<div id="convDataTable"></div>');
                }
                if (msgTable ) {
                    msgTable.destroy()
                    $('#msgDataTableParent').html('<div id="msgDataTable"></div>');
                }
                if (rangeTable ) {
                    rangeTable.destroy()
                    $('#labelDataTableParent').html('<div id="labelDataTable"></div>');
                    $('#convhighlight').html('')

                }
                convTable = $('#convDataTable').mDatatable({
                    // datasource definition
                    data: {
                        type: 'local',
                        source: convs,
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
                        input: $('#convSearchInput'),
                        delay: 200,
                    },
                    // inline and bactch editing(cooming soon)
                    // editable: false,

                    // columns definition
                    columns: [
                        {field: "id", title: " ", width: 40, sortable: false, template: function (row, index, datatable) {
                                // return '<a class="conversationDetailBtn">' + row.id + '</a>'
                                return '<a href="#" class="conversationDetailBtn btn btn-primary m-btn m-btn--icon m-btn--icon-only m-btn--pill"\
                                           data-conv-id="'+row.id+'">\
                                        <i class="la la-comments"></i>\
                                        </a>'
                            }},
                        {field: "from", title: "time", width: 400, template: function (row, index, datatable) {
                                const t1 = moment(row.from);
                                const t2 = moment(row.to);
                                return t1.format("YYYY-MM-DD") + ' <span style="color: #ccc">(' + t1.format("HH:mm") + ')</span> - ' +
                                        t2.format("YYYY-MM-DD") + ' <span style="color: #ccc">(' + t2.format("HH:mm") + ')</span>, ' + t2.fromNow()
                            }},
                        {field: "userid", title: "user"},
                        {field: "labels", title: "labels", template: function (row, index, datatable) {
                                return !row.labels ? '-' : row.labels.split(', ').map(label => {
                                    const color = $.hx.stringToColour(label);
                                    return '<div style="color: white;text-shadow: 0 1px 2px black;padding:2px 5px; margin: 1px; border-radius:5px;display: inline-block;background: '+color+' ">'+label+'</div>'}).join(' ')
                            }},
                        {field: "channel", title: "channel"},
                        {
                            field: "Actions",
                            width: 60,
                            title: "Actions",
                            sortable: false,
                            overflow: 'visible',
                            template: function (row, index, datatable) {
                                const dropup = (datatable.getPageSize() - index) <= 4 ? 'dropup' : '';
                                return '<div class="dropdown '+ dropup+'">\
                                  <a href="#" class="btn m-btn m-btn--hover-error m-btn--icon m-btn--icon-only m-btn--pill" data-toggle="dropdown">\
                                      <i class="la la-ellipsis-h"></i>\
                                  </a>\
                                    <div class="dropdown-menu dropdown-menu-right">\
                                      <a class="deleteConvBtn dropdown-item m--font-danger" data-conv-id="'+ row.id+'"><i class="la la-trash"></i> Delete </a>\
                                    </div>\
                              </div>';
                            }
                        }
                    ]
                });
                $('#convDataTable')
                        .on('click', `.conversationDetailBtn`, function () {
                            const convId = $(this).data('conv-id');
                            showConvDetails(convId)
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

                        })

            })


        }


        function showConvDetails(convId) {
            $.get('/api/v1/db/conversation/'+encodeURIComponent(convId) + '/message', function (msgs) {
                if (msgTable ) {
                    msgTable.destroy()
                    $('#msgDataTableParent').html('<div id="msgDataTable"></div>');
                }
                // $('#msgDataTable').html('');
                msgTable = $('#msgDataTable').mDatatable({
                    // datasource definition
                    data: {
                        type: 'local',
                        source: msgs,
                        pageSize: -1
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
                            info: false
                        }
                    },

                    // column sorting
                    sortable: true,
                    pagination: false,

                    search: {
                        input: $('#msgSearchInput'),
                        delay: 200,
                    },
                    // inline and bactch editing(cooming soon)
                    // editable: false,

                    // columns definition
                    columns: [
                        /*{field: "id", title: "conversation", template: function (row, index, datatable) {
                                return '<span class="conversationDetailBtn">' + row.id + '</span>'
                            }},*/
                        {field: "timestamp", title: "time", template: function (row, index, datatable) {
                                const m = moment(row.timestamp);
                                return m.format("YYYY-MM-DD") + ' <span style="color: #ccc">(' + m.format("HH:mm") + ')</span>, ' + m.fromNow()
                            }},
                        {field: "senderId", title: "from"},
                        {field: "text", title: "message", template: function (row, index, datatable) {
                                return row.incoming ?
                                        '<div style="padding: 1em; border-radius: 8px;color: #000;text-align:left;background:#f2f3f8">' + row.text + '</div>' :
                                        '<div style="padding: 1em; border-radius: 8px;color: #fff;text-align:right;background: #716aca">' + row.text + '</div>'
                            }},

                    ]
                });


            })
            $.get('/api/v1/db/conversation/'+encodeURIComponent(convId) + '/range', function (ranges) {
                if (rangeTable ) {
                    rangeTable.destroy()
                    $('#labelDataTableParent').html('<div id="labelDataTable"></div>');
                    $('#convhighlight').html('')
                }
                // $('#labelDataTable').html('');
                rangeTable = $('#labelDataTable').mDatatable({
                    // datasource definition
                    data: {
                        type: 'local',
                        source: ranges,
                        pageSize: -1
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
                            info: false
                        }
                    },

                    // column sorting
                    sortable: true,
                    pagination: false,

                    search: {
                        input: $('#labelSearchInput'),
                        delay: 200,
                    },
                    // inline and bactch editing(cooming soon)
                    // editable: false,

                    // columns definition
                    columns: [
                        /*{field: "id", title: "conversation", template: function (row, index, datatable) {
                                return '<span class="conversationDetailBtn">' + row.id + '</span>'
                            }},*/
                        {field: "label", title: "label", template: function (row, index, datatable) {
                                const color = $.hx.stringToColour(row.label);
                                return '<div style="color: white;text-shadow: 0 1px 2px black;padding:2px 5px; margin: 1px; border-radius:5px;display: inline-block;background: '+color+' ">'+row.label+'</div>'
                            }},
                        {field: "value", title: "value"},
                        {field: "props", title: "properties", sortable: false, template: function (row, index, datatable) {
                                //return '<pre>'+ JSON.stringify(row.props, null, ' ') + '</pre>';//$.hx.toTable(row.props).html();
                                return Object.entries(row.props).map(([key, value]) => key + ': <b>'+value+'</b>').join('<br/>')
                            }},


                    ]
                });

                $.hx.scrollIntoView('#labelPortlet', -85);
            })

            $.get('/api/v1/db/textdoc/id/'+encodeURIComponent(convId), function (doc) {
                $('#convhighlight').html(doc && doc.body ? doc.body.replace(/<br\/>/g, '<br/><br/>') : '')
            })

        }

        $("#resetLocalConversations").click(function() {
            $.ajax({
                url: '/api/v1/channel/devbot/conversation/reset',
                type: 'DELETE',
                success: function(result) {
                    $.hx.notify('Successful reset.', 'succes')
                    update()
                }
            });
        });
        $("#resetFbConversations").click(function() {
            $.ajax({
                url: '/api/v1/bot/chitchat/conversation/reset',
                type: 'DELETE',
                success: function(result) {
                    $.hx.notify('Successful reset.', 'succes')
                    update()
                }
            });
        });

    })
</script>

</#macro>