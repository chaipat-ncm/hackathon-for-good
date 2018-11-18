<#macro mapPanel colorClass>
<div class="portlet light portlet-fit bordered">
    <div class="portlet-title">
        <div class="caption">
            <i class="icon-bar-chart ${colorClass} hide"></i>
            <span class="caption-subject ${colorClass} bold uppercase">Active Retailers</span>
            <span class="caption-helper"></span>
        </div>
        <div class="actions">
            <a id="saveMapBtn" class="btn btn-circle btn-icon-only btn-default" data-original-title="" title=""><i class="fa fa-file-image-o"></i></a>
            <a class="btn btn-circle btn-icon-only btn-default fullscreen" href="javascript:;" data-original-title="" title=""> </a>
        </div>
    </div>
    <div class="portlet-body">
        <div id="gmap_marker" class="gmaps" style="height:100%; min-height: 600px"> </div>
    </div>
</div>
</#macro>

<#macro tablePanel icon colorClass title>
    <@panel icon=icon colorClass=colorClass title=title>
        <table id="reddatatable" class="table table-striped table-bordered" width="100%"></table>
    </@panel>
</#macro>

<#macro decriptionPanel colorClass>
<div class="portlet light portlet-fit bordered">
    <div class="portlet-title">
        <div class="caption">
            <i class="fa fa-info-circle ${colorClass}"></i>
            <span class="caption-subject ${colorClass} bold uppercase">Description</span>
        </div>
        <div class="actions">
            <a class="btn btn-circle btn-icon-only btn-default" data-toggle="collapse" href="#moreInfo" title="More...">
                <i class="fa fa-plus"></i>
            </a>
        </div>
    </div>
    <div class="portlet-body">
        <#nested>
    </div>
</div>
</#macro>

<#macro panel icon colorClass title>
<div class="portlet light portlet-fit bordered">
    <div class="portlet-title">
        <div class="caption">
            <i class="fa ${icon} ${colorClass}"></i>
            <span class="caption-subject ${colorClass} bold uppercase">${title}</span>
        </div>
    </div>
    <div class="portlet-body">
        <#nested>
    </div>
</div>
</#macro>