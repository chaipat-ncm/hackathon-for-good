<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - System properties
</#macro>

<#macro pageContent>
<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                System properties <small id="appVersion">(version ?)</small>
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
                        <span class="m-nav__link-text">System</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
                        <span class="m-nav__link-text">Info</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">


    <div class="row">
        <div class="col-md-6">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-language"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                NLP model info
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <div id="nlpInfo"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-6">
            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-hourglass"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Uptime
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <div id="uptime"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="m-portlet">
                <div class="m-portlet__head">
                    <div class="m-portlet__head-caption">
                        <div class="m-portlet__head-title">
												<span class="m-portlet__head-icon">
													<i class="la la-dashboard"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Usage stats
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <div id="usage"></div>
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
													<i class="la la-warning"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Log
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <textarea id="logfile" rows="20" style="width: 100%; background: #f5f5f5; font-family: 'Anonymous Pro', 'Menlo', 'Consolas', 'Bitstream Vera Sans Mono', 'Courier New'; font-size: smaller; monospace; white-space: pre; overflow-wrap: normal; overflow-x: scroll;"></textarea>
                            <p><button id="downloadLogBtn" type="button" class="btn btn-danger"> <i class="la la-download"></i> Download log file</button></p>
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
													<i class="la la-gears"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                System properties
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12">
                            <div id="sysprops"></div>
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

        $.hx.setCurrentPage('#menu-item-system-info')

        function updateDetails() {
            $.get('/api/v1/system/stats', function (data) {
                const memMax = data['rt.memory.max.MB'] * 1;
                const memTotal = data['rt.memory.total.MB'] * 1;
                const memFree = data['rt.memory.free.MB'] * 1;
                const memUsed = memTotal - memFree;

                //const memTotalPct = Math.round(100 * memTotal / memMax)
                const memFreePct = Math.round(100 * memFree / memMax)
                const memUsedPct = Math.round(100 * memUsed / memMax)

                //$('#usage').html($.c4i.toTable(data));
                $('#usage').html('<p><b>'+data['rt.processors.count']+' Cores</b></p><p>' +
                        ('<i class="la la-2x la-cube text-muted"></i> '.repeat(data['rt.processors.count']*1)) +
                        '</p>' +
                        '<p><b>Memory</b><br/>' +
                        '<div class="progress">\n' +
                        '    <div class="progress-bar bg-warning" style="width: '+memUsedPct+'%">\n' +
                        '        <span class="sr-only"> '+memUsedPct+'% used </span>\n' +
                        '    </div>\n' +
                        '    <div class="progress-bar bg-success" style="width: '+memFreePct+'%">\n' +
                        '        <span class="sr-only"> '+memFreePct+'% free </span>\n' +
                        '    </div>\n' +
                        '</div>' +
                        '<p>Used: '+memUsed+' MB, Free: '+memFree+' MB, System max left: '+(memMax-memTotal)+' MB</p>')

                $('#uptime').html('')
                        .append($('<p>' +  data['uptime.duration'].replace(/(\d+)/g, '<b>$1</b>') + '</p>'))
                        .append($('<p class="text-muted"> Started at ' + data['uptime.start']+ '</p>'))
            });

            $.get('/api/v1/system/properties', function (data) {
                data['java.class.path'] = data['java.class.path'].replace(/:/g, ': ');
                data['java.class.path'] = data['java.class.path'].replace(/;/g, '; ');
                $('#sysprops').html($.c4i.toTable(data));
            });

            $.get('/api/v1/system/nlp/info', function (data) {
                $('#nlpInfo').html($.c4i.toTable(data));
            });

            $.get('/api/v1/system/log', function(log){
                $('#logfile').val(log);
            });

            $.get('/api/v1/system/jar/version', function(v){
                $('#appVersion').html("(version " + v + ")");
            });
        }

        updateDetails();

        $("#downloadLogBtn").click(() => window.location='/api/v1/system/log/file');
    })
</script>
</#macro>