/**
 * Convert a markdown file to a bootstrap accordion widget using showdown.js.
 *
 * Arvid Halma
 */

class ShowdownAccordion {
  constructor(mdFile, target, splitOnHeaderLevel=1, codemirrorMode) {
    this.accordion = $('<div class="m-accordion m-accordion--bordered" id="md-accordion" role="tablist" aria-multiselectable="true"></div>');

    const converter = new showdown.Converter()
    converter.setFlavor('github');

    $.get(mdFile, doc => {
      const hLevel = '#'.repeat(splitOnHeaderLevel);
      const parts = doc.split(new RegExp(`(?=${hLevel} .+? ${hLevel}\r?\n)`, 'g'));
      parts.forEach((part, ix) => {
        const body = converter.makeHtml(part);
        const title = part.match(new RegExp(`${hLevel} (.+?) ${hLevel}`))[1]
        this.accordion.append(`
        <div class="m-accordion__item">
                <div class="m-accordion__item-head" role="tab" id="heading${ix}">
                     <a class="collapsed" role="button" data-toggle="collapse" data-parent="#md-accordion" href="#collapse${ix}" aria-expanded="false" aria-controls="collapse${ix}">
                    <span class="m-accordion__item-title">
                            ${title}
                    </span>
                    </a>
                </div>
                <div id="collapse${ix}" class="m-accordion__item-body collapse" role="tabpanel" aria-labelledby="heading${ix}">
                    <div class="m-accordion__item-content">
                        ${body}
                    </div>
                </div>
            </div>`)
      })
    })

    $(target).append(this.accordion);

    if(codemirrorMode) {
      $('#md-accordion').on('shown.bs.collapse', () => {
          $('#md-accordion pre').get().forEach(pre => {
            pre.className += " cm-s-default"
            CodeMirror.runMode(pre.innerText, "ccsmode", pre)
          })

          $('#md-accordion code').get().forEach(pre => {
            pre.className += " cm-s-default"
            CodeMirror.runMode(pre.innerText, "ccsmode", pre)
          })

          $('#md-accordion table').addClass("table table-sm table-bordered table-striped  m-table m-table--head-bg-metal");

        }
      )
    }

    this.closeAll()
  }

  closeAll(){
    $('.collapse', this.accordion).collapse('hide');
  }



}