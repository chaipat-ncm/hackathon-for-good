<#import "utils.ftl" as u>

<#macro pageTitle>
Relext Script Documentation
</#macro>

<#macro pageContent>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Relext Script Documentation
            </h3>
            <ul class="m-subheader__breadcrumbs m-nav m-nav--inline">
                <li class="m-nav__item m-nav__item--home">
                    <a href="/api/v1/ui/dashboard" class="m-nav__link m-nav__link--icon">
                        <i class="m-nav__link-icon la la-home"></i>
                    </a>
                </li>
                <li class="m-nav__separator">
                    -
                </li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">
												System
											</span>
                    </a>
                </li>
                <li class="m-nav__separator">
                    -
                </li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">
												Docs
											</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="row">
        <div class="col-lg-3" id="toc">
        </div>
        <div class="col-sm-9"">
            <div class="m-portlet m-portlet--mobile">
                <div class="m-portlet__body" id="doc">
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    #toc p {
        margin: 0;
        cursor: pointer;
    }

    #doc {
        font-family: Roboto;
        font-weight: normal;
    }

    #doc pre{
        background: #f8f9fa;
        padding: 1em;
    }
</style>
</#macro>

<#macro pageScript>

<script>
    $.hx.setCurrentPage('#menu-item-system-docs')
    
    new CcsEditor(); // init ccs mode
    const converter = new showdown.Converter()
    converter.setFlavor('github');
    $.get('/ccsdoc.md', doc => {
      let headers = doc.match(/#+.*?#+/g)
        const toc = $('<div id="toc"></div>')
        $('#toc').append(toc)
        headers.forEach(h => {
          let level = h.match(/#+/)[0].length

            let text = h.replace(/#/g,'').trim()
            let anchor = text.replace(/[^\w ]/g, '').replace(/ +/g, '-').toLowerCase();
            let $entry
            if(level === 1){
                $entry = $('<p><b>'+text+'</b></p>');
            } else {
                $entry = $('<p>'+('&nbsp'.repeat(level*4))+text+'</p>');
            }
            $entry.click(() => $.hx.scrollIntoView('#'+anchor, -85))
            toc.append($entry);
        })


      const body = converter.makeHtml(doc);
      $('#doc').append(body);

      // extra styling
      $('#doc pre').get().forEach(pre => {
        pre.className += " cm-s-default"
        CodeMirror.runMode(pre.innerText, "ccsmode", pre)
      })

      $('#doc code').get().forEach(code => {
        code.className += " cm-s-default"
        CodeMirror.runMode(code.innerText, "ccsmode", code)
      })

      $('#doc table').addClass("table table-sm table-bordered table-striped  m-table m-table--head-bg-metal");
    });

</script>

</#macro>