$.hx = $.hx || {}

/**
 * UI components for opening, saving and deleting documents.
 * Depends on jQuery and Metronic5.
 * Arvid Halma
 */

class OpenDocDialog{

    constructor(modalParent = 'body', id = 'openDocModal', docType = 'txt', title = 'Open', openDocCallback = (name, txt)=>{console.log(`Open ${name}: ${txt}`)}) {
        // constructor(modalParent, id, docType, title, openDocCallback) {
        const self = this
        const $modalParent = $(modalParent)
        const docTypeUrl = encodeURIComponent(docType)

        // Create modal
        const modalHtml =
            `<div class="modal fade" id="${id}" tabindex="-1" role="dialog" aria-labelledby="${id}Label" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="${id}Label">${title}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                      <div class="col-sm-12" style="margin-bottom: 15px;">
                          <div class="m-input-icon m-input-icon--left">
                              <input type="text" class="form-control m-input m-input--solid" placeholder="Search..." id="${id}SearchInput">
                              <span class="m-input-icon__icon m-input-icon__icon--left">
                                  <span><i class="la la-search"></i></span>
                              </span>
                          </div>
                      </div>
                      <div class="col-sm-12">
                          <div class="m_datatable m-datatable--default" id="${id}Table"></div>
                      </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>`

        $modalParent.append(modalHtml)
        const $modal = $('#' + id);

        this.openDocName = undefined;

        let openDocTable;
        $modal.on('hide.bs.modal', function (e) {
            if (openDocTable) {
                openDocTable.destroy()
            }
            $(`#${id}SearchInput`, $modal).val('');
            $(`#${id}Table`, $modal).html('');
        })

        // Open modal
        $modal.on('shown.bs.modal', function (e) {
            openDocTable = $(`#${id}Table`, $modal).mDatatable({
                // datasource definition
                data: {
                    type: 'remote',
                    source: {
                        read: {
                            url: `/api/v1/db/textdoc/${docTypeUrl}/meta`,
                            method: 'GET',
                        }
                    },
                    pageSize: -1
                },

                // layout definition
                layout: {
                    theme: 'default', // datatable theme
                    scroll: true, // enable/disable datatable scroll both horizontal and vertical when needed.
                    height: 430, // datatable's body's fixed height
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

                            pageSizeSelect: [5, 10, -1]
                        },

                        info: false
                    }
                },

                // column sorting
                sortable: true,
                pagination: false,

                search: {
                    input: $(`#${id}SearchInput`),
                    delay: 200,
                },
                // inline and bactch editing(cooming soon)
                // editable: false,

                // columns definition
                columns: [{
                    field: "name",
                    title: "Name",
                    template: function (row, index, datatable) {
                        return '<a class="openDocBtn btn btn-sm m-btn m-btn--hover-brand">' + row.name + '</a>';
                    }
                }, {
                    field: "updated",
                    title: "Last modified",
                    template: function (row, index, datatable) {
                        const m = moment(row.updated);
                        return m.format("YYYY-MM-DD") + ' <span style="color: #ccc">(' + m.format("HH:mm") + ')</span>, ' +
                            m.fromNow()
                    }
                }, {
                    field: "Actions",
                    width: 60,
                    title: "Actions",
                    sortable: false,
                    overflow: 'visible',
                    template: function (row, index, datatable) {
                        const dropup = (datatable.getPageSize() - index) <= 4 ? 'dropup' : '';
                        return `<div class="dropdown ${dropup}">
                        <a href="#" class="btn m-btn m-btn--hover-error m-btn--icon m-btn--icon-only m-btn--pill" data-toggle="dropdown">
                            <i class="la la-ellipsis-h"></i>
                        </a>
                          <div class="dropdown-menu dropdown-menu-right">
                            <a class="deleteDocBtn dropdown-item m--font-danger" data-script-name="${row.name}"><i class="la la-trash"></i> Delete </a>
                          </div>
                    </div>`;
                    }
                }
                ]
            });

            // Open doc event
            $modal.on('click', `.openDocBtn`, function () {
                const name = $(this).text();
                $.get(`/api/v1/db/textdoc/${docTypeUrl}/` + encodeURIComponent(name), function (src) {
                    self.openDocName = name;
                    openDocCallback(name, src);
                })
                $modal.modal('hide')
            })

            // Delete doc event
            $modal.on('click', `.deleteDocBtn`, function () {
                const name = $(this).data('script-name');
                $.ajax({
                    url: `/api/v1/db/textdoc/${docTypeUrl}/` + encodeURIComponent(name),
                    type: 'DELETE',
                    success: function (result) {
                        openDocTable.reload()
                    }
                });
            });

        })
    }

    getOpenDocName(){
        return this.openDocName;
    }
}

class OpenHistoryDocDialog{

    constructor(modalParent = 'body', id = 'openVersionDocModal', docType = 'txt', getDocName = () => undefined, title = 'Open version', openDocCallback = (name, txt)=>{console.log(`Open version ${name}: ${txt}`)}) {
        const $modalParent = $(modalParent)
        const docTypeUrl = encodeURIComponent(docType)
        // const docNameUrl = encodeURIComponent(docName)

        // Create modal
        const modalHtml =
            `<div class="modal fade" id="${id}" tabindex="-1" role="dialog" aria-labelledby="${id}Label" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="${id}Label">${title}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                      <div class="col-sm-12" style="margin-bottom: 15px;">
                          <div class="m-input-icon m-input-icon--left">
                              <input type="text" class="form-control m-input m-input--solid" placeholder="Search..." id="${id}SearchInput">
                              <span class="m-input-icon__icon m-input-icon__icon--left">
                                  <span><i class="la la-search"></i></span>
                              </span>
                          </div>
                      </div>
                      <div class="col-sm-12">
                          <div class="m_datatable m-datatable--default" id="${id}Table"></div>
                      </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>`

        $modalParent.append(modalHtml)
        const $modal = $('#' + id);

        let openDocTable;
        $modal.on('hide.bs.modal', function (e) {
            if (openDocTable) {
                openDocTable.destroy()
            }
            $(`#${id}SearchInput`, $modal).val('');
            $(`#${id}Table`, $modal).html('');
        })

        // Open modal
        $modal.on('shown.bs.modal', function (e) {
            openDocTable = $(`#${id}Table`, $modal).mDatatable({
                // datasource definition
                data: {
                    type: 'remote',
                    source: {
                        read: {
                            url: `/api/v1/db/textdoc/${docTypeUrl}/${encodeURIComponent(getDocName())}/meta`,
                            method: 'GET',
                        }
                    },
                    pageSize: -1
                },

                // layout definition
                layout: {
                    theme: 'default', // datatable theme
                    scroll: true, // enable/disable datatable scroll both horizontal and vertical when needed.
                    height: 430, // datatable's body's fixed height
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

                            pageSizeSelect: [5, 10, -1]
                        },

                        info: false
                    }
                },

                // column sorting
                sortable: true,
                pagination: false,

                search: {
                    input: $(`#${id}SearchInput`),
                    delay: 200,
                },
                // inline and bactch editing(cooming soon)
                // editable: false,

                // columns definition
                columns: [{
                    field: "name",
                    title: "Name",
                    template: function (row, index, datatable) {
                        return `<a class="openDocBtn btn btn-sm m-btn m-btn--hover-brand" data-doc-id="${row.id}">${row.name}</a>`;
                    }
                }, {
                    field: "updated",
                    title: "Last modified",
                    template: function (row, index, datatable) {
                        const m = moment(row.updated);
                        return m.format("YYYY-MM-DD") + ' <span style="color: #ccc">(' + m.format("HH:mm") + ')</span>, ' +
                            m.fromNow()
                    }
                }
                ]
            });

            // Open doc event
            $modal.on('click', `.openDocBtn`, function () {
                const id = $(this).data('doc-id');
                const name = $(this).text();
                $.get(`/api/v1/db/textdoc/id/` + encodeURIComponent(id) + '/body', function (src) {
                    openDocCallback(name, src);
                })
                $modal.modal('hide')
            })


        })
    }

}

class SaveDocDialog {
    constructor(modalParent, id, docType, title, getBody, getSuggestedName, onSuccess, onError) {
        const $modalParent = $(modalParent)
        const docTypeUrl = encodeURIComponent(docType)
        const self = this
        const modalHtml = `<div class="modal fade" id="${id}" tabindex="-1" role="dialog" aria-labelledby="${id}Label" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="${id}Label">${title}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="recipient-name" class="col-form-label">Name</label>
                            <input type="text" class="form-control" id="${id}Input" value="${getSuggestedName()}"> 
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                    <button id="${id}ConfirmBtn" type="button" class="btn btn-primary btn-brand">Save</button>
                </div>
            </div>
        </div>
    </div>`

        $modalParent.append(modalHtml)
        const $modal = $('#' + id);

        this.docName = undefined;

        // Open modal
        $modal.on('shown.bs.modal', function (e) {
            // updateName
            $(`#${id}Input`).val(getSuggestedName() || self.docName)
        })

        function save(e){
            const formData = new FormData();
            const name = $(`#${id}Input`).val();
            self.docName = name;
            formData.append("name", name);
            formData.append("body", getBody());
            $modal.modal('hide')

            $.ajax({
                type: "POST",
                url: "/api/v1/db/textdoc/"+docTypeUrl+"/" + encodeURIComponent(name),
                data: formData,
                processData: false,
                contentType: false,
                success: function (a) {
                    if(onSuccess)
                        onSuccess(name)
                },
                error: function (e) {
                    if(onError)
                        onError(name, e)
                }
            })

        };

        $(`#${id}ConfirmBtn`).click(save);

        $(`#${id}Input`).keypress(function(e){
            if (!e) e = window.event;
            let keyCode = e.keyCode || e.which;
            if (keyCode == '13'){
                // Enter pressed
                save();
                return false;
            }
        })
    }
}