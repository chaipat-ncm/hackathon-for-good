<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Chitchat Settings
</#macro>

<#macro pageContent>
<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Chitchat settings
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
                        <span class="m-nav__link-text">Live channels</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
                        <span class="m-nav__link-text">Chitchat</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">

    <div class="row">
        <div class="col-md-8">
            <div class="m-portlet" m-portlet="true" id="scriptPortlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption truncate">
                        <div class="m-portlet__head-title truncate">
                        <#--<h3 class="m-portlet__head-text truncate" id="scriptName">
                            My Script
                        </h3>-->
                            <input type="text" value="My Script" id="scriptName" style="width: 100%;height: 100%;border: none;overflow: hidden;text-overflow: ellipsis;font-family: Poppins;" readonly>
                        </div>
                    </div>

                    <div class="m-portlet__head-tools">
                        <ul class="m-portlet__nav">
                            <li class="m-portlet__nav-item">
                                <a id="openScriptBtn" data-toggle="modal" data-target="#openCssModal" class="m-portlet__nav-link btn btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--pill" data-toggle="m-tooltip" data-placement="top" title="" data-original-title="Run script" style="color:white">
                                    <i class="la la-folder-open-o"></i>
                                </a>
                            </li>

                            <li class="m-portlet__nav-item m-dropdown m-dropdown--inline m-dropdown--arrow m-dropdown--align-right m-dropdown--align-push" m-dropdown-toggle="hover" aria-expanded="true">
                                <a href="#" class="m-portlet__nav-link btn btn-secondary  m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill   m-dropdown__toggle">
                                    <i class="la la-ellipsis-v"></i>
                                </a>
                                <div class="m-dropdown__wrapper" style="z-index: 101;">
                                    <span class="m-dropdown__arrow m-dropdown__arrow--right m-dropdown__arrow--adjust" style="left: auto; right: 21.5px;"></span>
                                    <div class="m-dropdown__inner">
                                        <div class="m-dropdown__body">
                                            <div class="m-dropdown__content">

                                                <ul class="m-nav">
                                                    <li class="m-nav__section m-nav__section--first">
                                                        <span class="m-nav__section-text">Actions</span>
                                                    </li>


                                                    <li class="m-nav__item">
                                                        <a id="openScriptBtn" class="m-nav__link" data-toggle="modal" data-target="#openCssModal">
                                                            <i class="m-nav__link-icon la la-folder-open-o"></i>
                                                            <span class="m-nav__link-text">Open script</span>
                                                        </a>
                                                    </li>
                                                    <li class="m-nav__item">
                                                        <a id="openVersionBtn" class="m-nav__link" data-toggle="modal" data-target="#openVersionModal">
                                                            <i class="m-nav__link-icon la la-history"></i>
                                                            <span class="m-nav__link-text">Open previous version</span>
                                                        </a>
                                                    </li>
                                                    <li class="m-nav__item">
                                                        <a id="saveScriptBtn" class="m-nav__link" data-toggle="modal" data-target="#saveCssModal">
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
                                                        <a href="#" class="btn btn-outline-danger m-btn m-btn--pill m-btn--wide btn-sm">Cancel</a>
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
                <div class="m-portlet__body" style="padding: 0; height: 300px;">
                    <textarea id="scriptEditor" style="display: none;"></textarea>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-comment-o"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Live
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <p>Use current script as live Chitchat script.</p>
                            <p>
                            <div id="setLiveBtn" class="btn btn-danger"><i class="la la-flash"></i> Use this script</div>
                            <a href="/api/v1/ui/chatclient" class="btn btn-primary"><i class="la la-external-link"></i> Test</a>
                            </p>
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
        $.hx.setCurrentPage('#menu-item-live-chitchat')

        const scriptEditor = new CcsEditor($('#scriptEditor')[0], 'demo.ccs')

        const facebookLiveScript = 'Relext live script';
        const noLiveScript = 'No live script';

        function setScriptName(name) {
            $('#scriptName').val(name)
            $.hx.set('scriptName', name);
        }

        $('#downloadScriptBtn').click( e =>
                saveAs(new Blob([scriptEditor.getText()], {type: "text/plain;charset=utf-8"}), $('#scriptName').val()+".ccs")
        )

        $('#setLiveBtn').click( e => {
          // save under new name
            const formData = new FormData();
            const name = facebookLiveScript;
            formData.append("name", name);
            formData.append("body", scriptEditor.getText());

            $.ajax({
                type: "POST",
                url: "/api/v1/db/textdoc/ccs/" + encodeURIComponent(name),
                data: formData,
                processData: false,
                contentType: false,
                success: function (a) {
                    $.post('/api/v1/channel/chitchat/script/reload', function(){
                        $.hx.notify('The script is now live!', 'success')
                    })
                },
                error: function (e) {
                }
            })




        })

        let openDocDialog = new OpenDocDialog('body', 'openCssModal', 'ccs', 'Open script', function (name, src) {
            scriptEditor.setText(src)
            setScriptName(name)
        } );

        let openVerionDocDialog = new OpenHistoryDocDialog('body', 'openVersionModal', 'ccs', function(){return openDocDialog.getOpenDocName()}, 'Open previous version', function (name, src) {
            scriptEditor.setText(src)
        } );



        let saveDocDialog = new SaveDocDialog('body', 'saveCssModal', 'ccs', 'Save script',
                function(){return scriptEditor.getText()},
                function(){
                    let name = $('#scriptName').val()
                    if(name === noLiveScript)
                        name = 'New script'
                    return name
                },
                function (name) {
                    setScriptName(name)
                    $.hx.notify('Script saved: ' + name, 'success')
                },
                function (name, e) {
                    const resp = JSON.parse(e.responseText);
                    if(resp.message) {
                        const message = resp.message;
                        let lineNr = resp.line - 1;

                        $.hx.notify('There is an error in your script.<br>' + resp.message, 'danger')
                        scriptEditor.setErrorMessage(lineNr, message)
                    } else {
                        $.hx.notify('The script could not be saved.<br>' + e, 'danger')
                    }
                }
        );

        $.get('/api/v1/db/textdoc/ccs/' + encodeURIComponent(facebookLiveScript), function (src) {
            setScriptName(name);
            scriptEditor.setText(src)
        }).fail(function() {
            setScriptName(noLiveScript);
            scriptEditor.setText("# Open the script you want to use...")
        });



    })
</script>

</#macro>