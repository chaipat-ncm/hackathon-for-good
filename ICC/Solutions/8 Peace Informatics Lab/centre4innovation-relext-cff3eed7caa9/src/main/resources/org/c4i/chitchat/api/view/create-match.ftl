<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Text Matching
</#macro>

<#macro pageContent>
<style>
    #outputPanel, #textInput {
        font-family: Roboto, sans-serif;
        font-weight: normal;
        padding: 1em;
    }

    #textInput:focus {
        outline: none;
        color: black;
    }
</style>

<div class="m-subheader ">
    <div class="d-flex align-items-center">
        <div class="mr-auto">
            <h3 class="m-subheader__title m-subheader__title--separator">
                Script Text Matching
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
											<span class="m-nav__link-text">Create</span>
                    </a>
                </li>
                <li class="m-nav__separator">-</li>
                <li class="m-nav__item">
                    <a href="" class="m-nav__link">
											<span class="m-nav__link-text">Script</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<div class="m-content">
    <div class="row">
        <div class="col-sm-12">

            <div class="splitPanel" style="width:100%; box-shadow: 0 1px 15px 1px rgba(69,65,78,.08);margin-bottom: 2.2rem;background: white;">

                <div id="rulePanel" class="split-flex split-horizontal-flex">


                    <div class="m-portlet m-portlet--responsive-mobile" m-portlet="true" style="margin: 0;height:100%;">
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
                                        <a id="runBtn" class="m-portlet__nav-link btn btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--pill" data-toggle="m-tooltip" data-placement="top" title="" data-original-title="Run script" style="color:white">
                                            <i class="la la-play"></i>
                                        </a>
                                    </li>

                                    <li class="m-portlet__nav-item">
                                        <a m-portlet-tool="fullscreen" class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill">
                                            <i class="la la-expand"></i>
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
                                                                <a id="openScriptBtn" class="m-nav__link" data-toggle="modal" data-target="#loadExampleModal">
                                                                    <i class="m-nav__link-icon la la-star-o"></i>
                                                                    <span class="m-nav__link-text">Open example</span>
                                                                </a>
                                                            </li>

                                                            <li class="m-nav__separator m-nav__separator--fit">
                                                            <li class="m-nav__item">
                                                                <a id="newScriptBtn" class="m-nav__link">
                                                                    <i class="m-nav__link-icon la la-file"></i>
                                                                    <span class="m-nav__link-text">New script</span>
                                                                </a>
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
                                                            <li class="m-nav__item">
                                                                <a id="normalizeScriptBtn" class="m-nav__link">
                                                                    <i class="m-nav__link-icon la la-magic"></i>
                                                                    <span class="m-nav__link-text">Normalize script</span>
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
                        <div class="m-portlet__body" style="padding: 0; height: calc(100% - 71px);">
                            <textarea id="scriptEditor" style="display: none;"></textarea>
                        </div>
                    </div>
                </div>


                <div id="textPanel" class="split-flex split-horizontal-flex;" style="overflow: hidden">

                    <div class="m-portlet m-portlet--tabs" style="margin: 0;height: 100%;width: 100%;">
                        <div class="m-portlet__head">
                            <div class="m-portlet__head-tools">
                                <ul class="nav nav-tabs m-tabs m-tabs-line   m-tabs-line--right m-tabs-line-danger" role="tablist" style="float: left;">
                                    <li class="nav-item m-tabs__item">
                                        <a class="nav-link m-tabs__link active" data-toggle="tab" href="#1" role="tab">
                                            Input
                                        </a>
                                    </li>
                                    <li class="nav-item m-tabs__item">
                                        <a class="nav-link m-tabs__link" data-toggle="tab" href="#2" role="tab">
                                            Output
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="m-portlet__body" style="padding: 0; height: calc(100% - 71px);">
                            <div class="tab-content" style="height: 100%">
                                <div class="tab-pane active" style="height: 100%" id="1">
                                    <textarea id="textInput" style="width: 100%; height: 100%;border: none;" placeholder="Your text goes here..."></textarea>
                                </div>
                                <div class="tab-pane" style="height: 100%" id="2">
                                    <p id="outputPanel" style="overflow-y: scroll;height: 100%; margin: 0"><i>Click <i class="la la-play-circle-o"></i> to see the result...</i></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
    <div class="row">
        <div class="col-sm-12" id="doc">
            <h2>Documentation</h2>
        </div>
    </div>
</div>


<#-- Example modal -->
<div class="modal fade" id="loadExampleModal" tabindex="-1" role="dialog" aria-labelledby="loadExampleModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="loadExampleModalLabel">Open example script</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="loadExampleSelect">Select an example script</label>
                        <select class="form-control" id="loadExampleSelect">

                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <button id="loadExampleBtn" type="button" class="btn btn-brand">Open</button>
            </div>
        </div>
    </div>
</div>

</#macro>

<#macro pageScript>
<script>
    $(document).ready(() => {
        $.hx.setCurrentPage('#menu-item-script-match')

        Split(['#rulePanel', '#textPanel'], {
            sizes: [50, 50],
            gutterSize: 8,
            cursor: 'col-resize',
            elementStyle: function (dimension, size, gutterSize) {
                return { 'flex-basis': 'calc(' + size + '% - ' + gutterSize + 'px)' }
            },
            gutterStyle: function (dimension, gutterSize) {
                return { 'flex-basis':  gutterSize + 'px' }
            }
        })

        function setScriptName(name) {
            $('#scriptName').val(name)
            $.hx.set('scriptName', name);
        }

        const scriptEditor = new CcsEditor($('#scriptEditor')[0], 'demo.ccs')

        $('#runBtn').click(() => {
            scriptEditor.removeErrorMessage()
            function activeTab(tab){
                $('.nav-tabs a[href="#' + tab + '"]').tab('show');
            }

            let formData = new FormData();
            formData.append("script", scriptEditor.getText());
            formData.append("text", $('#textInput').val());

            $.ajax({
                type: "POST",
                url: "/api/v1/channel/devbot/match",
                data: formData,
                processData: false,
                contentType: false,
                success: (a) => {
                    console.log(a);
                    $('#outputPanel').html(a.highlight);
                    $('.nav-tabs a[href="#2"]').tab('show')
                    if(a.replies){
                        a.replies.forEach(msg => {
                            $.hx.notify(msg.text, msg.text  ? 'brand' : 'danger')
                        })
                    }

                },
                error: (e) => {
                    e = JSON.parse(e.responseText);
                    let message = e.message;
                    let lineNr = e.line - 1;
                    if(lineNr !== undefined){
                        scriptEditor.setErrorMessage(lineNr, message)
                    }
                    $.hx.notify('There is an error in your script.<br>' + e.message, 'danger')

                }
            });
        })


        const examples = [
            {
                title: '1. Simple',
                description: 'Look for simple occurrences in the sentences.',
                rules:
                        `# Find fruit occurrences
@fruit <- apple OR orange`,
                text:
                        `The phrase apple of my eye refers to something or someone that one cherishes above all others.
Apples is a also a type of fruit though. Oranges are too, by the way.`,

            },

            {
                title: '2. Logical expressions',
                description: 'Refine the rule with logical operators and sequence matching.',
                rules:
                        `# Find actual fruit occurrences
@fruit1 <- (apple AND  NOT of_my_eye) OR orange

# You can also write this, if you prefer operators
@fruit2 <- (apple &  -of_my_eye) | orange
`,
                text:
                        `The phrase apple of my eye refers to something or someone that one cherishes above all others.
Apples is a also a type of fruit though. Oranges are too, by the way.`,
            },
            {
                title: '3. Exact matching',
                description: 'Use double quoted words for exact matches.',
                rules:
                        `# Only match all-caps instances
@disease <- "AIDS"`,
                text:
                        `She aids people with AIDS.`,
            },
            {
                title: '4. Sequence with wildcards',
                description: 'Use wildcards for matches where word order matters, but leaves room for additional words.',
                rules:
                        `# exacly one words between 'little' and 'girl'.
@chica <- little_?_girl

# one or more words between 'in' and 'village'
@village_based <- in_+_village

# zero or more words in between...
@grandma <- my_*_grandmother
`,
                text:
                        `Once upon a time there lived in a certain village a little country girl,
the prettiest creature that ever was seen. Her mother was very fond of
her, and her grandmother loved her still more. This good woman made
for her a little red riding-hood, which became the girl so well that
everybody called her Little Red Riding-hood.
One day her mother, having made some custards, said to her, "Go,
my dear, and see how your grandmother does, for I hear she has been
very ill; carry her a custard and this little pot of butter."
Little Red Riding-hood set out immediately to go to her
grandmother's, who lived in another village.
As she was going through the wood, she met Gaffer Wolf, who had a
very great mind to eat her up; but he dared not, because of some fagotmakers
hard by in the forest. He asked her whither she was going. The
poor child, who did not know that it was dangerous to stay and hear a
wolf talk, said to him, "I am going to see my grandmother, and carry her a
custard and a little pot of butter from my mamma."
"Does she live far off?" said the Wolf...`,
            },
            {
                title: '5. Match scope',
                description: 'Normally, rules are applied per sentence, but you can also match against the entire text.',
                rules:
                        `@fruit_fail <- apple & orange
@fruit_success {within: all} <- apple & orange`,
                text:
                        `This sentence is about apples.
And this one about oranges.`,
            },
            {
                title: '6. References',
                description: 'Reuse labels you defined in other rules for hierarchy.',
                rules:
                        `@mother <- mamma OR mother OR grandmother

# reuse @mother definition...
@woman <- @mother OR girl OR woman
`,
                text:
                        `Once upon a time there lived in a certain village a little country girl,
the prettiest creature that ever was seen. Her mother was very fond of
her, and her grandmother loved her still more. This good woman made
for her a little red riding-hood, which became the girl so well that
everybody called her Little Red Riding-hood.
One day her mother, having made some custards, said to her, "Go,
my dear, and see how your grandmother does, for I hear she has been
very ill; carry her a custard and this little pot of butter."
Little Red Riding-hood set out immediately to go to her
grandmother's, who lived in another village.
As she was going through the wood, she met Gaffer Wolf, who had a
very great mind to eat her up; but he dared not, because of some fagotmakers
hard by in the forest. He asked her whither she was going. The
poor child, who did not know that it was dangerous to stay and hear a
wolf talk, said to him, "I am going to see my grandmother, and carry her a
custard and a little pot of butter from my mamma."
"Does she live far off?" said the Wolf...`,

            },
            {
                title: '7. Enitity recognition',
                description: 'Find certain classes of words and their properties',
                rules:
                        `@how_many <- NUMBER
@when <- DATETIME
@where <- CITY.country == SE
`,
                text:
                        `News from last week: The benefits that come with owning a dog are clear-- physical activity, support, companionship -- but owning a dog could literally be saving your life.

Owners of hunting breeds, including terriers, retrievers, and scent hounds, were most protected from cardiovascular disease and death. However, owning any dog will reduce an owners risk of death, just to different extents, said Tove Fall, senior author of the study and Associate Professor in Epidemiology at Uppsala University.

The study looked at over 3.4 million Swedish individuals between the ages of 40 and 80 sampled from a national database and the Swedish Twin Register over a 12-year study period.`,

            },



        ];

        // fill examples
        const exampleSelect = $( "#loadExampleSelect" );
        exampleSelect.append($('<option value="" disabled selected>Examples...</option>'));
        examples.forEach((ex, ix) => {
            exampleSelect.append($("<option />").val(ix).text(ex.title));
        });

        // on option select
        function updateExample(){
            let example = examples[exampleSelect.val()];
            $('#scriptName').val(example.title)
            scriptEditor.setText(example.rules)
            $.hx.notify(example.description, 'primary', null, 15000);
            $('#textInput').val(example.text);
            $('#outputPanel').html('<i>Click <i class="la la-play-circle-o"></i> to see the result...</i>');
            $('.nav-tabs a[href="#1"]').tab('show');
        }

        $('#loadExampleBtn').click(() => {
            updateExample()
            $('#loadExampleModal').modal('hide')
        });

        /*$('#nextExample').click(() => {
            $("option:selected", exampleSelect)
                    .prop("selected", false)
                    .next()
                    .prop("selected", true);
            updateExample()
        });

        $('#prevExample').click(() => {
            $("option:selected", exampleSelect)
                    .prop("selected", false)
                    .prev()
                    .prop("selected", true);
            updateExample()
        });*/

        $('#newScriptBtn').click( function(){
                    scriptEditor.setText("")
                    setScriptName("New script")
                }
        )

        $('#downloadScriptBtn').click( e =>
                saveAs(new Blob([scriptEditor.getText()], {type: "text/plain;charset=utf-8"}), $('#scriptName').val()+".ccs")
        )

        $('#normalizeScriptBtn').click( e => {
            let formData = new FormData();
            formData.append("script", scriptEditor.getText());

            $.ajax({
                type: "POST",
                url: "/api/v1/script/normalize",
                data: formData,
                processData: false,
                contentType: false,
                success: (a) => {
                    scriptEditor.setText(a);

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
                function(){return $('#scriptName').val()},
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

        setScriptName($.hx.get('scriptName', 'New script'))

        new ShowdownAccordion('/ccsdoc.md', '#doc', 1, "cssmode")

    })
</script>
</#macro>