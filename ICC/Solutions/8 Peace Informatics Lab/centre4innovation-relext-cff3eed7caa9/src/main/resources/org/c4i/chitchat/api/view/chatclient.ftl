<#macro pageContent>
<!DOCTYPE html>
<!--
   ________    _ __  ________          __
  / ____/ /_  (_) /_/ ____/ /_  ____ _/ /_
 / /   / __ \/ / __/ /   / __ \/ __ `/ __/
/ /___/ / / / / /_/ /___/ / / / /_/ / /_
\____/_/ /_/_/\__/\____/_/ /_/\__,_/\__/

Leiden university - Centre for Innovation - HumanityX

-->
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>
        Relext Client
    </title>
    <meta name="description" content="Modal examples">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="stylesheet" href="/asset5/app/js/chitchatclient.css">

    <style>
        html, body {
            box-sizing: border-box;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

        body {
            background-color: #ebedf4;
            font-family: "Open Sans", sans-serif;
            display: flex;
            flex-direction: column;
        }

        #welcome {
            min-width: 400px;
            max-width: 800px;
            width: 50vw;
            margin: 20px auto 0px auto;
        }

        #chitchatclient {
            flex: 1;
            max-width: 800px;
            min-width: 400px;
            width: 50vw;
            min-height: 400px;
            margin: 20px auto 50px auto;
            background-color: white;
            border-radius: 20px;
            box-shadow: 0 5px 15px 1px rgba(69,65,78,.15)
        }

        .btn {
            border: 0;
            border-radius: 0.25rem;
            background: var(--chat-right-bg-color);
            color: white;
            font-size: 100%;
            white-space: nowrap;
            text-decoration: none;
            padding: 10px 25px;
            margin: 0.25rem;
            cursor: pointer;
        }

        button:hover,
        button:focus {
            background: var(--chat-right-bg-color-darker);
        }

        button:focus {
            outline: 1px solid #fff;
            outline-offset: -4px;
        }

        button:active {
            transform: scale(0.99);
        }
    </style>

    <link rel="shortcut icon" href="/asset5/app/media/img/favicon.ico"/>
</head>

<body>
<div id="welcome">

    <img alt="" width="60px" src="/asset5/app/media/img/chitchat-logo2.svg"
         style="margin-right: 4px;vertical-align: bottom;"/>
    <span style="font-size: 40px; font-weight: lighter; color: #4f30a2">Relext</span>
</div>

<div id="chitchatclient"></div>

<script src="/asset5/app/js/chitchatclient.js" type="application/javascript"></script>
<script src="/asset5/vendors/custom/jquery/jquery.min.js" type="application/javascript"></script>

<script>
    document.addEventListener('DOMContentLoaded', function () {

        // Create new unique userId
        const userId = 'user' + Date.now();

        // Create client
        let parent = document.getElementById('chitchatclient');
        let chitChatClient = new ChitChatClient(parent, {
            showLeftUser: true,
            showRightUser: false,
            scrollToLastMessage: true,
            rightUserInitials: "ME",
            leftUserInitials: "BOT",
        });

        let onSend = function (params) {
            const formData = new FormData();
            formData.append("msg",
                    JSON.stringify({
                        id: '' + Date.now(),
                        text: params.text,
                        senderId: userId,
                        recipientId: "chitchat",
                        timestamp: params.timestamp
                    }));


            $.ajax({
                type: "POST",
                url: "/api/v1/channel/chitchat/reply",
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

                        chitChatClient.addMessageLeft(text, "BOT");
                    })
                },
                error : function(e){
                    chitChatClient.addInfo("<div style='color'">The chatbot is not available")
                }
            })
        };
        chitChatClient.onSend(onSend);

        // link button actions
        $('#chitchatclient').on('click', '.chatBtn', function(){
            let value = $(this).data('value');
            const btnParams = {
                id: '' + Date.now(),
                text: value,
                senderId: userId,
                recipientId: "chitchat",
                timestamp: new Date()
            }
            $(this).parent().children().prop('disabled', true);
            onSend(btnParams)
        })

        const welcomeMsg = new URL(window.location.href).searchParams.get('welcome');
        if(welcomeMsg){
            welcomeMsg.split("&").forEach(msg =>
                chitChatClient.addMessageLeft(msg)
            )
        }


    });

</script>
</body>
</html>
</#macro>