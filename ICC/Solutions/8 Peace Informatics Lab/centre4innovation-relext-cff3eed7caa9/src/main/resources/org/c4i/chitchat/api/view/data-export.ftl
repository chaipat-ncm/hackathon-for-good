<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Data Import/Export
</#macro>

<#macro pageContent>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Data Import/Export
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
												Import/Export
											</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="row">
        <div class="col-lg-6">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-download"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Data Export
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <p>Export all conversation data as a zip file.</p>
                            <form class="m-form">
                                <div class="m-form__group form-group row">

                                    <label class="col-4 col-form-label">Anonymise IDs</label>
                                    <div class="col-4">
                                      <span class="m-switch m-switch--icon">
                                        <label>
						                        <input type="checkbox" checked="checked" name="" id="anonymiseDownload">
						                        <span></span>
						                        </label>
						                    </span>
                                    </div>
                                    <div class="col-4">
                                        <div class="btn btn-brand pull-right" id="downloadBtn" data-toggle="confirmation" data-singleton="true"><i class="la la-file-zip-o"></i> Download</div>
                                    </div>
                                </div>
                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-upload"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Import Sample Data
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <p>This data is used on the analytics page.</p>
                            <form id="sampleUpload" class="m-dropzone dropzone dz-clickable"
                                  action="/api/v1/db/sample/upload" enctype="multipart/form-data" method="POST" type="files">
                                <div class="m-dropzone__msg dz-message needsclick">
                                    <h3 class="m-dropzone__msg-title">Drop files here or click to upload.</h3>
                                    <span class="m-dropzone__msg-desc">Only .csv are allowed to upload. Click <a href="/asset5/files/format.csv">here</a> for the expected format. </span>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-6">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-remove"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Delete data
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <p>Delete data per channel</p>
                            <form class="m-form">
                                <div class="m-form__group form-group row">
                                    <label class="col-8 col-form-label">Facebook conversations</label>
                                    <div class="col-4">
                                      <span class="m-switch m-switch--icon m-switch--danger">
                                        <label>
                                        <input type="checkbox" name="" id="fbDelete">
                                        <span></span>
                                        </label>
						                           </span>
                                    </div>
                                </div>
                                <div class="m-form__group form-group row">
                                    <label class="col-8 col-form-label">Relext conversations</label>
                                    <div class="col-4">
                                      <span class="m-switch m-switch--icon m-switch--danger">
                                        <label>
                                        <input type="checkbox" name="" id="ccDelete">
                                        <span></span>
                                        </label>
						                           </span>
                                    </div>
                                </div>
                                <div class="m-form__group form-group row">
                                    <label class="col-8 col-form-label">Sample conversations</label>
                                    <div class="col-4">
                                      <span class="m-switch m-switch--icon m-switch--danger">
                                        <label>
                                        <input type="checkbox" name="" id="sampleDelete">
                                        <span></span>
                                        </label>
						                           </span>
                                    </div>
                                </div>
                                <div class="m-form__group form-group row">
                                    <label class="col-8 col-form-label">DevBot conversations</label>
                                    <div class="col-4">
                                      <span class="m-switch m-switch--icon m-switch--danger">
                                        <label>
                                        <input type="checkbox" name="" id="devbotDelete">
                                        <span></span>
                                        </label>
						                           </span>
                                    </div>
                                </div>

                            </form>
                            <hr/>
                            <div class="row">
                            <div class="col-12">
                                <div class="btn btn-danger pull-right" id="deleteBtn"><i class="la la-trash-o"></i> Delete</div>
                            </div>
                            </div>
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
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-data-export')

        // multiple file upload
        Dropzone.options.sampleUpload = {
            acceptedFiles: ".csv,.txt",
            paramName: "file", // The name that will be used to transfer the file
            maxFiles: 100,
            maxFilesize: 512, // MB
            addRemoveLinks: true,
            uploadMultiple: true,
            accept: function(file, done) {
                $.hx.notify("Only upload a .txt or .csv file.", "danger");
                done()
            },
            init: function () {
                this.on("error", function (file) {
                    $.hx.notify("Error uploading file", "danger");
                });
                this.on("completemultiple", function (file) {
                    $.hx.notify("Sample data uploaded successfully", "success");
                });
            }

        };

        function download(url) {
            document.getElementById('download_iframe').src = url;
        }

        $('#downloadBtn').click(() => {
            const ano = $('#anonymiseDownload').is(':checked') ? '/anonymous' : ''
            download('/api/v1/db/conversation/export' + ano)
        });


        $("#deleteBtn").click(function () {
            let channels = []
            if($('#fbDelete').is(':checked') )
                channels.push('fb')
            if($('#ccDelete').is(':checked') )
                channels.push('chitchat')
            if($('#sampleDelete').is(':checked') )
                channels.push('sample')
            if($('#devbotDelete').is(':checked') )
                channels.push('devbot')

            for (const channel of channels) {
                $.ajax({
                    method: 'DELETE',
                    url: "/api/v1/db/conversation?" + $.param({"channel": channel}),
                    success: function () {
                        $.hx.notify("Successfully deleted conversations for: " + channel, 'success');
                    },
                    error: function () {
                        $.hx.notify("An error occurred for channel: " + channel, 'danger');
                    }
                });
            }
        });
    });
</script>
</#macro>