<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - About
</#macro>

<#macro pageContent>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                About
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
												About
											</span>
                    </a>
                </li>

            </ul>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="row">
        <div class="col-lg-12">
            <p>Relext is developed for the Hackathonfogood.org.</p>
            <p>It is based on ChitChat, created by the Centre for Innovation/Leiden University in collaboration with</p>
            <ul>
                <li><b>Free Press Unlimited</b>
                <li><b>Radio Dabanga</b>
                <li><b>World Food Programme</b>
            </ul>
        </div>
    </div>
</div>
</div>
</#macro>

<#macro pageScript>
<script>
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-system-about')
    })
</script>
</#macro>