<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - REST API Documentation
</#macro>

<#macro pageContent>
    <#include "/api/v1/swagger" parse=false>
</#macro>

<#macro pageScript>
<script>
    //$.arif.pageInit('#navApiDocs');
    $(document).ready(function () {

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
