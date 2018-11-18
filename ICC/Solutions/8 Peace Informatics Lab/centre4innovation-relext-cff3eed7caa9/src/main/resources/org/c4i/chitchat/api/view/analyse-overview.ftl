<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Data Overview
</#macro>

<#macro pageContent>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Data Overview
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
												Overview
											</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="m-portlet">
        <div class="m-portlet__body m-portlet__body--no-padding">
            <div class="row m-row--no-padding m-row--col-separator-xl">
                <div class="col-md-12 col-lg-12 col-xl-4">
                    <!--begin:: Widgets/Stats2-1 -->
                    <div class="m-widget1">
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Facebook conversations</h3>
                                    <span class="m-widget1__desc">Total number of chats</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-brand" id="conv-fb-count">-</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Facebook users</h3>
                                    <span class="m-widget1__desc">Total number of persons using the bot</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-danger" id="conv-fb-user-count">-</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Facebook messages</h3>
                                    <span class="m-widget1__desc">The total number of messages exchanged</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-success" id="conv-fb-message-count">-</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--end:: Widgets/Stats2-1 -->      </div>
                <div class="col-md-12 col-lg-12 col-xl-4">
                    <!--begin:: Widgets/Stats2-1 -->
                    <div class="m-widget1">
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Relext conversations</h3>
                                    <span class="m-widget1__desc">Total number of chats</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-brand" id="conv-chitchat-count">-</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Relext users</h3>
                                    <span class="m-widget1__desc">Total number of persons using the bot</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-danger" id="conv-chitchat-user-count">-</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Relext messages</h3>
                                    <span class="m-widget1__desc">The total number of messages exchanged</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-success" id="conv-chitchat-message-count">-</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--end:: Widgets/Stats2-1 -->      </div>
                <div class="col-md-12 col-lg-12 col-xl-4">
                    <!--begin:: Widgets/Stats2-1 -->
                    <div class="m-widget1">
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Sample data conversations</h3>
                                    <span class="m-widget1__desc">Total number of chats</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-brand" id="conv-sample-count">-</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Sample data users</h3>
                                    <span class="m-widget1__desc">Total number of persons using the bot</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-danger" id="conv-sample-user-count">-</span>
                                </div>
                            </div>
                        </div>
                        <div class="m-widget1__item">
                            <div class="row m-row--no-padding align-items-center">
                                <div class="col">
                                    <h3 class="m-widget1__title">Sample data messages</h3>
                                    <span class="m-widget1__desc">The total number of messages exchanged</span>
                                </div>
                                <div class="col m--align-right">
                                    <span class="m-widget1__number m--font-success" id="conv-sample-message-count">-</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!--end:: Widgets/Stats2-1 -->      </div>
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
													<i class="la la-line-chart"></i>
												</span>
                            <h3 class="m-portlet__head-text">
                                Conversation
                            </h3>
                        </div>
                    </div>

                </div>
                <div class="m-portlet__body">
                    <div class="row">
                        <div class="col-12" id="convFreqPlot" style="height: 400px">

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
        $.hx.setCurrentPage('#menu-item-analyse-overview')

        $.get('/api/v1/db/conversation/stats', function (stats) {
            Object.entries(stats).forEach(([key, value]) => $('#'+key.replace(/\./g,'-')).text(value))
        })

        $.get('/api/v1/db/conversation/freqs', function (data) {
            const trace1 = {
                y: data.sample.map(tv => tv.value),
                x: data.sample.map(tv => tv.t.substring(0, 10)),
                name: 'Sample data',
                type: 'bar'
            };
            const trace2 = {
                y: data.chitchat.map(tv => tv.value),
                x: data.chitchat.map(tv => tv.t.substring(0, 10)),
                name: 'Relext',
                type: 'bar'
            };
            const trace3 = {
                y: data.fb.map(tv => tv.value),
                x: data.fb.map(tv => tv.t.substring(0, 10)),
                name: 'Facebook',
                type: 'bar'
            };

            const layout = {
                paper_bgcolor: "rgb(255,255,255)",
                title: 'Daily conversations',
                xaxis: {title: 'date'},
                yaxis: {title: 'count'},

            };

            Plotly.newPlot('convFreqPlot', [trace1, trace2, trace3], layout);
        })

    })
</script>
</#macro>