/**
 * Pure JS chat client widget.
 * @author Arvid Halma, Center for Innovation, Leiden University
 */
class ChitChatClient {

    /**
     * Create and show a new chat client.
     * @param parent {HTMLElement} container
     * @param options {object} with fields to customize the client.
     * {
        showLeftUser: true,
        showRightUser: false,
        scrollToLastMessage: true,
        onSend: () => {},
        rightUserInitials: "ME",
        leftUserInitials: "YOU",
        }
     */
    constructor(parent, options) {
        const self = this
        this.parent = parent
        this.parent.classList.add('chitchatclient')
        this.options = Object.assign({
            showLeftUser: true,
            showRightUser: false,
            scrollToLastMessage: true,
            onSend: () => {},
            rightUserInitials: "ME",
            leftUserInitials: "YOU",
            leftMessageClass: "animate fadeInLeft",
            rightMessageClass: "animate fadeInRight",
        }, options)

        if(!this.options.showRightUser){
            parent.classList.add('norightuser')
        }

        if(!this.options.showLeftUser){
            parent.classList.add('noleftuser')
        }

        // internal state
        this.leftRightState = 0 // -1 = left, 1 = right
        this.lastMessageContainer = undefined

        // build dom
        this.messagesDiv = document.createElement('div')
        this.messagesDiv.classList.add('messages')
        this.parent.appendChild(this.messagesDiv)

        this.sendDiv = document.createElement('div')
        this.sendDiv.classList.add('send')
        this.sendDiv.innerHTML += `<input type="text" placeholder="Type here..."> <a class="sendBtn">
            <svg xmlns="http://www.w3.org/2000/svg"  version="1.1" x="0px" y="0px" viewBox="0 0 486.736 486.736" style="enable-background:new 0 0 486.736 486.736;" xml:space="preserve" width="20px" height="20px">
            <g><path d="M481.883,61.238l-474.3,171.4c-8.8,3.2-10.3,15-2.6,20.2l70.9,48.4l321.8-169.7l-272.4,203.4v82.4c0,5.6,6.3,9,11,5.9   l60-39.8l59.1,40.3c5.4,3.7,12.8,2.1,16.3-3.5l214.5-353.7C487.983,63.638,485.083,60.038,481.883,61.238z" fill="#FFFFFF"/></g>
            </svg>
            </a>`
        this.parent.appendChild(this.sendDiv)


        let $input = parent.querySelector('.send input')

        function onSend() {
            const msg = $input.value
            if (msg.length === 0) {
                return
            }
            let timestamp = new Date()
            const timestampStr = timestamp.toTimeString().substring(0, 5)
            self.addMessageRight(msg, self.options.rightUserInitials, timestampStr)
            $input.value = ''
            self.options.onSend({text:msg, timestamp:timestamp, from:self.options.rightUserInitials})
        }

        parent.querySelector('.sendBtn').addEventListener('click', onSend)

        $input.addEventListener("keydown", event => {
            if (event.which === 13 || event.keyCode === 13) {
                onSend()
                return false
            }
            return true
        })

    }

    /**
     * Render an incoming message (message received).
     * @param msg {string} html content
     * @param user {string} user initials (space for 2-3 characters).
     *   If undefined, the default this.options.leftUserInitials is used.
     * @param timestr {string} an indication of when this message arrived.
     *   If it is undefined the current time will be used.
     */
    addMessageLeft(msg, user, timestr) {
        window.setTimeout(() => {
            if(!timestr){
                timestr = new Date().toTimeString().substring(0, 5)
            }
            const userHtml = this.options.showLeftUser ? `<div class="user">${user || this.options.leftUserInitials}</div>` : ''
            const arrowHtml = this.leftRightState !== -1 ? `<div class="arrow"><svg height="10" width="20"><polygon points="0,0 20,0 10,10" style="stroke:none;stroke-width:0" /></svg></div>` : ''
            if(this.leftRightState !== -1){
                // last message not from left user
                this.lastMessageContainer = document.createElement('div')
                this.lastMessageContainer.classList.add('left')
                this.lastMessageContainer.innerHTML += userHtml
                this.lastMessageContainer.innerHTML += `<div class="msg ${this.options.leftMessageClass}">${arrowHtml} ${msg}<div class="timestamp">${timestr}</div></div>`
                this.messagesDiv.appendChild(this.lastMessageContainer)
            } else {
                // append message from left user
                const msgDiv = document.createElement('div')
                msgDiv.classList.add('msg', ...this.options.leftMessageClass.split(' '))
                msgDiv.innerHTML += `${msg}<div class="timestamp">${timestr}</div>`
                this.lastMessageContainer.appendChild(msgDiv);
            }
            if(this.options.scrollToLastMessage){
                this.messagesDiv.scrollTo({"behavior": "smooth", "top": 1000000})
            }
            this.leftRightState = -1

        }, 500)
    }

    /**
     * Render an outgoing message (message sent).
     * @param msg {string} html content
     * @param user {string} user initials (space for 2-3 characters).
     *   If undefined, the default this.options.rightUserInitials is used.
     * @param timestr {string} an indication of when this message was sent.
     *   If it is undefined the current time will be used.
     */
    addMessageRight(msg, user, timestr) {
        window.setTimeout(() => {
            const userHtml = this.options.showRightUser? `<div class="user">${user || this.options.rightUserInitials}</div>` : ''
            const arrowHtml = this.leftRightState !== 1 ? `<div class="arrow"><svg height="10" width="20"><polygon points="0,0 20,0 10,10" style="stroke:none;stroke-width:0" /></svg></div>` : ''

            if(this.leftRightState !== 1){
                // last message not from right user
                this.lastMessageContainer = document.createElement('div')
                this.lastMessageContainer.classList.add('right')
                this.lastMessageContainer.innerHTML += userHtml
                this.lastMessageContainer.innerHTML += `<div class="msg ${this.options.rightMessageClass}">${arrowHtml} ${msg}<div class="timestamp">${timestr}</div></div>`
                this.messagesDiv.appendChild(this.lastMessageContainer)
            } else {
                // append message from right user
                // append message from left user
                const msgDiv = document.createElement('div')
                msgDiv.classList.add('msg', ...this.options.rightMessageClass.split(' '))
                msgDiv.innerHTML += `${msg}<div class="timestamp">${timestr}</div>`
                this.lastMessageContainer.appendChild(msgDiv);
            }

            if(this.options.scrollToLastMessage){
                this.messagesDiv.scrollTo({"behavior": "smooth", "top": 1000000})
            }

            this.leftRightState = 1
        }, 0)
    }

    /**
     * A centered notification
     * @param msg {string} html message.
     */
    addInfo(msg) {
        const infoElm = document.createElement('div')
        infoElm.classList.add('info')
        infoElm.innerHTML = msg
        this.messagesDiv.appendChild(infoElm)
        this.leftRightState = 0
    }

    /**
     * Remove all messages
     */
    clear(){
        this.messagesDiv.innerHTML = ""
    }

    /**
     * Set hook for when a message was send by the user.
     *
     * @param f {Function} to be called. It receives an object as argument with the following fields.
     * {text:msg, timestamp:timestamp, from:self.options.rightUserInitials}
     */
    onSend(f){
        this.options.onSend = f
    }


}