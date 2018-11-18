<#import "utils.ftl" as u>

<#macro pageTitle>
Relext - Script Chatbots
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
                Create Chatbot Script
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

            <div class="splitPanel"
                 style="width:100%; box-shadow: 0 1px 15px 1px rgba(69,65,78,.08);margin-bottom: 2.2rem;background: white;">

                <div id="rulePanel" class="split-flex split-horizontal-flex">


                    <div class="m-portlet" m-portlet="true" id="scriptPortlet" style="margin: 0;height:100%;">
                        <div class="m-portlet__head">
                            <div class="m-portlet__head-caption truncate">
                                <div class="m-portlet__head-title truncate">
                                <#--<h3 class="m-portlet__head-text truncate" id="scriptName">
                                    My Script
                                </h3>-->
                                    <input type="text" value="My Script" id="scriptName"
                                           style="width: 100%;height: 100%;border: none;overflow: hidden;text-overflow: ellipsis;font-family: Poppins;"
                                           readonly>
                                </div>
                            </div>

                            <div class="m-portlet__head-tools">
                                <ul class="m-portlet__nav">
                                    <#--<li class="m-portlet__nav-item">
                                        <a id="runBtn"
                                           class="m-portlet__nav-link btn btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--pill"
                                           data-toggle="m-tooltip" data-placement="top" title=""
                                           data-original-title="Run script" style="color:white">
                                            <i class="la la-play"></i>
                                        </a>
                                    </li>-->

                                    <li class="m-portlet__nav-item">
                                        <a id="runBtn" class="m-portlet__nav-link btn btn-brand m-btn m-btn--icon m-btn--icon-only m-btn--pill" data-toggle="m-tooltip" data-placement="top" title="" data-original-title="Run script" style="color:white; display: none">
                                            <i class="la la-play"></i>
                                        </a>
                                    </li>
                                    <li class="m-portlet__nav-item">
                                        <a m-portlet-tool="fullscreen"
                                           class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill">
                                            <i class="la la-expand"></i>
                                        </a>
                                    </li>

                                    <li class="m-portlet__nav-item m-dropdown m-dropdown--inline m-dropdown--arrow m-dropdown--align-right m-dropdown--align-push"
                                        m-dropdown-toggle="hover" aria-expanded="true">
                                        <a href="#"
                                           class="m-portlet__nav-link btn btn-secondary  m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill   m-dropdown__toggle">
                                            <i class="la la-ellipsis-v"></i>
                                        </a>
                                        <div class="m-dropdown__wrapper" style="z-index: 101;">
                                            <span class="m-dropdown__arrow m-dropdown__arrow--right m-dropdown__arrow--adjust"
                                                  style="left: auto; right: 21.5px;"></span>
                                            <div class="m-dropdown__inner">
                                                <div class="m-dropdown__body">
                                                    <div class="m-dropdown__content">

                                                        <ul class="m-nav">
                                                            <li class="m-nav__section m-nav__section--first">
                                                                <span class="m-nav__section-text">Actions</span>
                                                            </li>

                                                            <li class="m-nav__item">
                                                                <a id="openScriptBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#loadExampleModal">
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
                                                                <a id="openScriptBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#openCssModal">
                                                                    <i class="m-nav__link-icon la la-folder-open-o"></i>
                                                                    <span class="m-nav__link-text">Open script</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a id="openVersionBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#openVersionModal">
                                                                    <i class="m-nav__link-icon la la-history"></i>
                                                                    <span class="m-nav__link-text">Open previous version</span>
                                                                </a>
                                                            </li>
                                                            <li class="m-nav__item">
                                                                <a id="saveScriptBtn" class="m-nav__link"
                                                                   data-toggle="modal" data-target="#saveCssModal">
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
                                                                <a href="#"
                                                                   class="btn btn-outline-danger m-btn m-btn--pill m-btn--wide btn-sm">Cancel</a>
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
                        <div class="m-portlet__body" style="padding: 0; height: calc(100% - 66px);">
                            <textarea id="scriptEditor" style="display: none;"></textarea>
                        </div>
                    </div>
                </div>


                <div id="textPanel" class="split-flex split-horizontal-flex;" style="overflow: hidden">

                    <div class="m-portlet m-portlet--tabs" m-portlet="true" id="chatPortlet"
                         style="margin: 0;height: 100%;width: 100%;">
                        <div class="m-portlet__head">
                            <div class="m-portlet__head-caption">
                                <div class="m-portlet__head-title">
                                    <div>
                                    <#--<span class="m-bootstrap-switch m-bootstrap-switch--pill">
                                        <input  id="testMode" data-switch="true" type="checkbox" data-on-text="Match" data-handle-width="70" data-off-text="Chat" data-on-color="brand" data-off-color="accent">
                                    </span>-->
                                        <div class="btn-group btn-group-toggle" data-toggle="buttons">
                                            <label class="btn m-btn m-btn--pill btn-secondary chatModeBtn active">
                                                <input type="radio" name="runMode" value="chatMode" autocomplete="off" checked
                                                       data-toggle="m-tooltip" data-placement="top" title=""
                                                       data-original-title="Try out a conversation">
                                                <i class="la la-comments"></i> Chat
                                            </label>
                                            <label class="btn m-btn m-btn--pill btn-secondary matchModeBtn">
                                                <input type="radio" name="runMode" value="matchMode" autocomplete="off"
                                                       data-toggle="m-tooltip" data-placement="top" title=""
                                                       data-original-title="See how labels match a text">
                                                <i class="la la-binoculars"></i> Match
                                            </label>
                                        </div>
                                    </div>
                                </div>

                            </div>

                            <div class="m-portlet__head-tools">
                                <ul class="m-portlet__nav" id="chatTools">
                                    <li class="m-portlet__nav-item">
                                        <a id="infoBtn"
                                           class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill"
                                           data-toggle="m-tooltip" data-placement="top" title=""
                                           data-original-title="See matched text">
                                            <i class="la la-info"></i>
                                        </a>
                                    </li>
                                    <li class="m-portlet__nav-item">
                                        <a id="clearBtn"
                                           class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill"
                                           data-toggle="m-tooltip" data-placement="top" title=""
                                           data-original-title="Clear conversation">
                                            <i class="la la-close"></i>
                                        </a>
                                    </li>
                                    <#--<li class="m-portlet__nav-item">
                                        <a m-portlet-tool="fullscreen"
                                           class="m-portlet__nav-link btn btn-secondary m-btn m-btn--hover-brand m-btn--icon m-btn--icon-only m-btn--pill">
                                            <i class="la la-expand"></i>
                                        </a>
                                    </li>-->
                                </ul>

                                <ul class="nav nav-tabs m-tabs m-tabs-line   m-tabs-line--right m-tabs-line-danger" style="float: left; display: none; min-width: 122px; margin-left: 5px;" id="matchTools">
                                    <li class="nav-item m-tabs__item">
                                        <a class="nav-link m-tabs__link active" id="textInputBtn">
                                            Input
                                        </a>
                                    </li>
                                    <li class="nav-item m-tabs__item">
                                        <a class="nav-link m-tabs__link" id="outputPanelBtn">
                                            Output
                                        </a>
                                    </li>
                                </ul>



                            </div>
                        </div>
                        <div class="m-portlet__body" id="chatbody" style="padding: 0; height: calc(100% - 71px);"></div>
                        <div class="m-portlet__body" id="matchbody" style="padding: 0; height: calc(100% - 71px);">
                            <div style="height: 100%">
                                <div class="" style="height: 100%" id="textInputTab">
                                    <textarea id="textInput" style="width: 100%; height: 100%;border: none;" placeholder="Your text goes here..."></textarea>
                                </div>
                                <div class="" style="height: 100%; display: none" id="outputPanelTab">
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
<div class="modal fade" id="loadExampleModal" tabindex="-1" role="dialog" aria-labelledby="loadExampleModalLabel"
     aria-hidden="true">
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
<link rel="stylesheet" href="/asset5/app/js/chitchatclient.css">
<script src="/asset5/app/js/chitchatclient.js" type="application/javascript"></script>

<script>
    $(document).ready(function () {
        $.hx.setCurrentPage('#menu-item-create-script')

        let botResult;

        let chatbox = new ChitChatClient(document.getElementById('chatbody'), {
            showLeftUser: true,
            showRightUser: true,
            scrollToLastMessage: true,
        });

        Split(['#rulePanel', '#textPanel'], {
            sizes: [50, 50],
            gutterSize: 8,
            cursor: 'col-resize',
            elementStyle: function (dimension, size, gutterSize) {
                return {'flex-basis': 'calc(' + size + '% - ' + gutterSize + 'px)'}
            },
            gutterStyle: function (dimension, gutterSize) {
                return {'flex-basis': gutterSize + 'px'}
            }
        });

        $('#clearBtn').click(clear);

        $('#infoBtn').click(function () {
            $.hx.notify(botResult.highlight, 'info')
        });

        function setScriptName(name) {
            $('#scriptName').val(name)
            localStorage.setItem('scriptName', name);
        }

        const scriptEditor = new CcsEditor($('#scriptEditor')[0], 'chat.ccs')

        function clear() {
            $.ajax({
                type: "POST",
                url: "/api/v1/channel/devbot/reset",
                contentType: 'text/plain',
                data: "you",
                success: function () {
                    chatbox.clear()
                    // chatbox.focusOnInput()
                    scriptEditor.removeErrorMessage()
                    botResult = {highlight: '<i>none</i>'}
                },
                error: function (e) {
                    $.hx.notify(e, 'danger');
                }
            });
        };
        clear(); //reset cache


        const examples = [
            {
                title: '1. Simple',
                mode: 'match',
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
                mode: 'match',
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
                mode: 'match',
                description: 'Use double quoted words for exact matches.',
                rules:
                        `# Only match all-caps instances
@disease <- "AIDS"`,
                text:
                        `She aids people with AIDS.`,
            },
            {
                title: '4. Sequence with wildcards',
                mode: 'match',
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
                mode: 'match',
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
                mode: 'match',
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
                mode: 'match',
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
            {
                title: '8. Replies',
                mode: 'chat',
                description: 'Give a reply based on the rules that matched.',
                rules:
                        `@vehicle <- car | bike | train  #won't match in this example
@fruit <- pear | (apple & -of_my_eye) | orange

# Multiple answers, randomly picked
@vehicle -> I like fast cars | I use a bike | I don't like to travel
# Use the matched word for this rule
@fruit -> I like @fruit too! | I hate @fruit, though!
# Fallback case if no rule was matched
() -> I don't understand what you mean...
`,
                text:
                        `The phrase apple of my eye refers to something or someone that one cherishes above all others.
Apples is a also a type of fruit though. Oranges are too, by the way.`,

            },
            {
                title: '9. More reply features',
                mode: 'chat',
                description: 'Controlling reply flows with continue',
                rules:

                        `@vehicle <- car | bike | train  #won't match in this example
@fruit <- pear | (apple & -of_my_eye) | orange

-@vehicle & -@fruit {continue} -> IMAGE(https://media.giphy.com/media/mW05nwEyXLP0Y/giphy.gif) & Hi! Tell me a bit about you...
@vehicle & @fruit -> I know all about you now... @fruit and @vehicle are you favorites & Ciao!

# Use the matched word for this rule
@vehicle {continue} -> I like a fast @vehicle
@fruit {continue} -> I like @fruit too!  | I hate @fruit, though!

-@vehicle {repeat} -> What do you use to travel to your work?

# First ask for info in one way, then repeat another way...
-@fruit -> Tell me about what fruit you like...
-@fruit {repeat} -> I want to now if you like apples, pears or oranges

# Fallback case if no rule was matched
() {repeat} -> I don't understand what you mean...
`,
                text:
                        `The phrase apple of my eye refers to something or someone that one cherishes above all others.
Apples is a also a type of fruit though. Oranges are too, by the way.`,

            },

            {
                title: '10. Eliza',
                mode: 'chat',
                description: "ELIZA is an early chatbot form the 1960's, simulating a Rogerian psychotherapist.",
                rules:
                        `---
rule: {reply: {reflect, repeat, within: 1}}
---

'I need +' ->
  "Why do you need @label.wildcard1?" |
  "Would it really help you to get @label.wildcard1?" |
  "Are you sure you need @label.wildcard1?"

'Why dont you *' ->
  "Do you really think I don't @label.wildcard1?" |
  "Perhaps eventually I will @label.wildcard1." |
  "Do you really want me to @label.wildcard1?"

'Why cant I *' ->
  "Do you think you should be able to @label.wildcard1?" |
  "If you could @label.wildcard1, what would you do?" |
  "I don't know -- why can't you @label.wildcard1?" |
  "Have you really tried?"

'I cant *' ->
  "How do you know you can't @label.wildcard1?" |
  "Perhaps you could @label.wildcard1 if you tried." |
  "What would it take for you to @label.wildcard1?"

'I am *' ->
  "Did you come to me because you are @label.wildcard1?" |
  "How long have you been @label.wildcard1?" |
  "How do you feel about being @label.wildcard1?"

'Im *' ->
 "How does being @label.wildcard1 make you feel?" |
  "Do you enjoy being @label.wildcard1?" |
  "Why do you tell me you're @label.wildcard1?" |
  "Why do you think you're @label.wildcard1?"

'Are you *' ->
 "Why does it matter whether I am @label.wildcard1?" |
  "Would you prefer it if I were not @label.wildcard1?" |
  "Perhaps you believe I am @label.wildcard1." |
  "I may be @label.wildcard1 -- what do you think?"

'What *' ->
 "Why do you ask?" |
  "How would an answer to that help you?" |
  "What do you think?"

'How *' ->
 "How do you suppose?" |
  "Perhaps you can answer your own question." |
  "What is it you're really asking?"

'Because *' ->
 "Is that the real reason?" |
  "What other reasons come to mind?" |
  "Does that reason apply to anything else?" |
  "If @label.wildcard1, what else must be true?"

'sorry' ->
 "There are many times when no apology is needed." |
  "What feelings do you have when you apologize?"

'hello' | 'hi' ->
 "Hello... I'm glad you could drop by today." |
  "Hi there... how are you today?" |
  "Hello, how are you feeling today?"

'I think *' ->
 "Do you doubt @label.wildcard1?" |
  "Do you really think so?" |
  "But you're not sure @label.wildcard1?"

'friend' ->
 "Tell me more about your friends." |
  "When you think of a friend, what comes to mind?" |
  "Why don't you tell me about a childhood friend?"

'Yes' | 'yep' | 'indeed' ->
 "You seem quite sure." |
  "OK, but can you elaborate a bit?"

'computer' | 'machine' ->
 "Are you really talking about me?" |
  "Does it seem strange to talk to a computer?" |
  "How do computers make you feel?" |
  "Do you feel threatened by computers?"

'Is it *' ->
 "Do you think it is @label.wildcard1?" |
  "Perhaps it's @label.wildcard1 -- what do you think?" |
  "If it were @label.wildcard1, what would you do?" |
  "It could well be that @label.wildcard1."

'It is *' ->
 "You seem very certain." |
  "If I told you that it probably isn't @label.wildcard1, what would you feel?"

'Can you *' ->
 "What makes you think I can't @label.wildcard1?" |
  "If I could @label.wildcard1, then what?" |
  "Why do you ask if I can @label.wildcard1?"

'Can I *' ->
 "Perhaps you don't want to @label.wildcard1." |
  "Do you want to be able to @label.wildcard1?" |
  "If you could @label.wildcard1, would you?"

'You are *' ->
 "Why do you think I am @label.wildcard1?" |
  "Does it please you to think that I'm @label.wildcard1?" |
  "Perhaps you would like me to be @label.wildcard1." |
  "Perhaps you're really talking about yourself?"

'Youre *' ->
 "Why do you say I am @label.wildcard1?" |
  "Why do you think I am @label.wildcard1?" |
  "Are we talking about you, or me?"

'I dont *' ->
 "Don't you really @label.wildcard1?" |
  "Why don't you @label.wildcard1?" |
  "Do you want to @label.wildcard1?"

'I feel *' ->
 "Good, tell me more about these feelings." |
  "Do you often feel @label.wildcard1?" |
  "When do you usually feel @label.wildcard1?" |
  "When you feel @label.wildcard1, what do you do?"

'I have *' | 'Ive *' ->
 "Why do you tell me that you've @label.wildcard1?" |
  "Have you really @label.wildcard1?" |
  "Now that you have @label.wildcard1, what will you do next?"

'I would *' | 'Id *' ->
 "Could you explain why you would @label.wildcard1?" |
  "Why would you @label.wildcard1?" |
  "Who else knows that you would @label.wildcard1?"

'Is there *' ->
 "Do you think there is @label.wildcard1?" |
  "It's likely that there is @label.wildcard1." |
  "Would you like there to be @label.wildcard1?"

'My *' ->
 "I see, your @label.wildcard1." |
  "Why do you say that your @label.wildcard1?" |
  "When your @label.wildcard1, how do you feel?"

'You *' ->
 "We should be discussing you, not me." |
  "Why do you say that about me?" |
  "Why do you care whether I @label.wildcard1?"

'Why *' ->
 "Why don't you tell me the reason why @label.wildcard1?" |
  "Why do you think @label.wildcard1?"

'I want *' ->
 "What would it mean to you if you got @label.wildcard1?" |
  "Why do you want @label.wildcard1?" |
  "What would you do if you got @label.wildcard1?" |
  "If you got @label.wildcard1, then what would you do?"

'mother' ->
 "Tell me more about your mother." |
  "What was your relationship with your mother like?" |
  "How do you feel about your mother?" |
  "How does this relate to your feelings today?" |
  "Good family relations are important."

'father' ->
 "Tell me more about your father." |
  "How did your father make you feel?" |
  "How do you feel about your father?" |
  "Does your relationship with your father relate to your feelings today?" |
  "Do you have trouble showing affection with your family?"

'child' ->
 "Did you have close friends as a child?" |
  "What is your favorite childhood memory?" |
  "Do you remember any dreams or nightmares from childhood?" |
  "Did the other children sometimes tease you?" |
  "How do you think your childhood experiences relate to your feelings today?"

'quit' ->
 "Thank you for talking with me." |
  "Good-bye." |
  "Thank you, that will be $150.  Have a good day!"

() ->
 "Please tell me more." |
  "Let's change focus a bit... Tell me about your family." |
  "Can you elaborate on that?" |
  "Why do you say that?" |
  "I see." |
  "Very interesting." |
  "I see.  And what does that tell you?" |
  "How does that make you feel?" |
  "How do you feel when you say that?"`
            }
        ];

        // fill examples
        const exampleSelect = $("#loadExampleSelect");
        exampleSelect.append($('<option value="" disabled selected>Examples...</option>'));
        examples.forEach(function (ex, ix) {
            exampleSelect.append($("<option />").val(ix).text(ex.title));
        })
        ;

        // on option select
        function updateExample() {
            let example = examples[exampleSelect.val()];
            if(example.mode === 'chat'){
                $('.chatModeBtn').click()
            } else {
                $('.matchModeBtn').click()
            }

            $('#scriptName').val(example.title)
            scriptEditor.setText(example.rules);
            $.hx.notify(example.description, 'primary', null, 15000);
            $('#textInput').val(example.text);
            $('#outputPanel').html('<i>Click [Run] to see the result...</i>');
            $('.nav-tabs a[href="#1"]').tab('show');
        }

        $('#loadExampleBtn').click(() => {
            updateExample()
            $('#loadExampleModal').modal('hide')
        });

        $('#newScriptBtn').click(function () {
                    scriptEditor.setText("")
                    setScriptName("New script")
                }
        )

        $('#downloadScriptBtn').click(e =>
                saveAs(new Blob([scriptEditor.getText()], {type: "text/plain;charset=utf-8"}), $('#scriptName').val()+".ccs")
        )

        $('#normalizeScriptBtn').click(e => {
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
        });

        let openVerionDocDialog = new OpenHistoryDocDialog('body', 'openVersionModal', 'ccs', function () {
            return openDocDialog.getOpenDocName()
        }, 'Open previous version', function (name, src) {
            scriptEditor.setText(src)
        });

        let saveDocDialog = new SaveDocDialog('body', 'saveCssModal', 'ccs', 'Save script',
                function () {
                    return scriptEditor.getText()
                },
                function () {
                    return $('#scriptName').val()
                },
                function (name) {
                    setScriptName(name)
                    $.hx.notify('Script saved: ' + name, 'success')
                },
                function (name, e) {
                    const resp = JSON.parse(e.responseText);
                    if (resp.message) {
                        const message = resp.message;
                        let lineNr = resp.line - 1;

                        $.hx.notify('There is an error in your script.<br>' + resp.message, 'danger')
                        scriptEditor.setErrorMessage(lineNr, message)
                    } else {
                        $.hx.notify('The script could not be saved.<br>' + e, 'danger')
                    }
                }
        );

        setScriptName(localStorage.getItem('scriptName') || 'New script')


        let onSend = function (params) {
            const formData = new FormData();
            formData.append("script", scriptEditor.getText());
            formData.append("msg",
                    JSON.stringify({
                        id: '' + Date.now(),
                        text: params.text,
                        senderId: "you",
                        recipientId: "devbot",
                        timestamp: params.timestamp
                    }));


            $.ajax({
                type: "POST",
                url: "/api/v1/channel/devbot/reply",
                data: formData,
                processData: false,
                contentType: false,
                success: function (a) {
                    botResult = a
                    a.replies.forEach(msg => {
                        let text = msg.text
                        // render image
                        if (text.startsWith("IMAGE(")) {
                            let imgUrl = text.substring(6, text.length - 1);
                            text = '<img src="' + imgUrl + '" style="display: block;max-width:100%; width: auto; height: auto;"/>'
                        }

                        // render buttons
                        text = text.replace(/BUTTON\( *(.*?) *, *(.*?) *\)/g, '<button type="button" class="btn btn-success chatBtn" data-value="$2">$1</button>')

                        chatbox.addMessageLeft(text, "BOT");
                    })
                },
                error: function (e) {
                    const resp = JSON.parse(e.responseText);
                    if (resp.message) {
                        const message = resp.message;
                        let lineNr = resp.line - 1;

                        $.hx.notify('There is an error in your script.<br>' + resp.message, 'danger')
                        scriptEditor.setErrorMessage(lineNr, message)
                    } else {
                        $.hx.notify('The script could not be saved.<br>' + e, 'danger')
                    }
                }
            })
        };
        chatbox.onSend(onSend, false);
        // link button actions
        $('#chatbody').on('click', '.chatBtn', function(){
            let value = $(this).data('value');
            const btnParams = {
                id: '' + Date.now(),
                text: value,
                senderId: "you",
                recipientId: "devbot",
                timestamp: new Date()
            }
            $(this).parent().children().prop('disabled', true);
            onSend(btnParams)
        })

        new ShowdownAccordion('/ccsdoc.md', '#doc', 1, "cssmode")

        $('input[type=radio][name=runMode]').change(function() {
            if (this.value === 'matchMode') {
                matchMode()
            } else if (this.value === 'chatMode') {
                chatMode()
            }
        })

        function matchMode(){
            $('#chatTools').hide()
            $('#chatbody').hide()
            $('#matchTools').show()
            $('#matchbody').show()
            $('#runBtn').show()
        }

        function chatMode(){
            $('#chatTools').show()
            $('#chatbody').show()
            $('#matchTools').hide()
            $('#matchbody').hide()
            $('#runBtn').hide()
        }


        $('#textInputBtn').click(() => {
            $('#textInputBtn').addClass('active');
            $('#outputPanelBtn').removeClass('active');
            $('#textInputTab').show()
            $('#outputPanelTab').hide()
        })

        $('#outputPanelBtn').click(() => {
            $('#textInputBtn').removeClass('active');
            $('#outputPanelBtn').addClass('active');
            $('#textInputTab').hide()
            $('#outputPanelTab').show()
        })


        $('#runBtn').click(() => {
            scriptEditor.removeErrorMessage()

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
                    $('#outputPanelBtn').click()
                    if(a.replies && a.replies.length > 0){
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
    })
</script>
</#macro>