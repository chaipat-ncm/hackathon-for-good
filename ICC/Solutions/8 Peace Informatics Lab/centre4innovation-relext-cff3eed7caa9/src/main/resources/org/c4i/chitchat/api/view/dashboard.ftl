<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Dashboard
</#macro>

<#macro pageContent>
<style>
    .m-widget7.m-widget7--skin-dark .m-widget7__desc {
        margin-top: 1rem;
        margin-bottom: 1rem;
    }

    .scriptBg {
        background: linear-gradient(142deg, #716aca, #9816f4, #00c5dc);
        background-size: 200% 200%;

        -webkit-animation: BgAnimation 19s ease infinite;
        -moz-animation: BgAnimation 19s ease infinite;
        animation: BgAnimation 19s ease infinite;
    }

    .matchBg {
        background: linear-gradient(142deg, #f4e75c, #00c5dc, #716aca);
        background-size: 200% 200%;

        -webkit-animation: BgAnimation 19s ease infinite;
        -moz-animation: BgAnimation 19s ease infinite;
        animation: BgAnimation 19s ease infinite;
    }

    .manageBg {
        background: linear-gradient(142deg, #928ee9, #b6b6b9,  #f4cb2c);
        background-size: 200% 200%;

        -webkit-animation: BgAnimation 19s ease infinite;
        -moz-animation: BgAnimation 19s ease infinite;
        animation: BgAnimation 19s ease infinite;
    }

    @-webkit-keyframes BgAnimation {
        0%{background-position:14% 0%}
        50%{background-position:87% 100%}
        100%{background-position:14% 0%}
    }
    @-moz-keyframes BgAnimation {
        0%{background-position:14% 0%}
        50%{background-position:87% 100%}
        100%{background-position:14% 0%}
    }
    @keyframes BgAnimation {
        0%{background-position:14% 0%}
        50%{background-position:87% 100%}
        100%{background-position:14% 0%}
    }
</style>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Relext
            </h3>
            <ul class="m-subheader__breadcrumbs m-nav m-nav--inline">

                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">
												Relation extraction application
											</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="row">
        <div class="col-12">
            <h1>Hackathon for good - ICC Challenge</h1>
            <h2>Relation Extraction</h2>
        </div>
    </div>
</div>
</#macro>

<#macro pageScript>
<script>
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-dashboard')
    })

</script>

</#macro>