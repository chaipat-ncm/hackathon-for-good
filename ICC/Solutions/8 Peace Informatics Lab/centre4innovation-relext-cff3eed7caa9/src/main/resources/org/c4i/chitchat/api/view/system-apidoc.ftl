<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - REST API Documentation
</#macro>

<#macro pageContent>
<style>
    .embed-container {
        position: relative;
        width: 100%;
        height: 100%;
        overflow: hidden;
    }

    .embed-container iframe,
    .embed-container object,
    .embed-container embed {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    #docFrame .swagger-ui .topbar {
        padding: 8px 0;
        background-color: #ddd;
    }


</style>
<iframe id="docFrame" src="/api/v1/swagger" frameborder="0" width="100%" height="1600px" allowfullscreen  ></iframe>
</#macro>

<#macro pageScript>
<script>
    //$.arif.pageInit('#navApiDocs');
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-system-apidoc')
        $('iframe').load( function() {
            $('iframe').contents().find("head")
                    .append($(`<style type='text/css'>
                    .swagger-section #input_baseUrl {
                        width: auto;
                    }
                    .swagger-section #header {
                        background-color: #ddd;
                    }
                    </style>`));
        });
    })
</script>
</#macro>
