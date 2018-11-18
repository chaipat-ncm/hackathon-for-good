class Chatbox {
  on(event,callback) {
    if(!this._triggers[event])
      this._triggers[event] = [];
    this._triggers[event].push( callback );
  };

  triggerHandler(event,params) {
    if(this._triggers[event] ) {
      for(let i in this._triggers[event] )
        this._triggers[event][i](params);
    }
  };

  constructor(name, imgPath) {
    this._triggers = {};

    this.username = name;
    this.imagePath = imgPath;

    $('.chatbox .chat-user').height($('.chatbox').outerHeight() - $('.chatbox .chat-user-form').outerHeight());

    this.scrollToBottom();

    this.on('newOutGoingMessage', this.onOutGoingMessage, false);

    const $this = this;
    $(".chatbox .chat-user-form .input-group-btn .btn").click(function() {
      $this.newOutGoingMessage($this);
    });
    $(".chatbox .chat-user-form .form-control").keypress(function (i) {
      if (13 == i.which) {
        $this.newOutGoingMessage($this);
      }
    });
  }

  clear() {
    $('.chatbox .chat-user-messages').html('');
  }

  onOutGoingMessage(params) {
    if(params.type !== 'button')
      params.$this.addMessage(params.$this, params.$this.username, params.text, params.$this.imagePath, "out");
  }

  newOutGoingMessage($this) {
    let text = $(".chatbox .chat-user-form .form-control").val();
    if(text === '') {
      return;
    }
    $this.triggerHandler('newOutGoingMessage', {'$this': $this, 'text': text});
  };

  newIncomingMessage($this, username, text, imgPath) {
    $this.addMessage($this, username, text, imgPath, "in");
  }

  generateMessage(inOrOut, timestamp, username, message, imgPath) {
    return `<div class="post ${inOrOut}">
            <img class="avatar" alt="" src="${imgPath}"/>
            <div class="message">
            <span class="arrow"></span>
            <a href="#" class="name">${username}</a>&nbsp;
            <span class="datetime">${timestamp}</span>
            <span class="body">${message}</span>
            </div>
            </div>`;
  }

  addMessage($this, username, text, imagepath, inOrOut, appendOnly) {
    const timestamp = new Date;
    let timestampStr = timestamp.toISOString()
    try {
      timestampStr = timestamp.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
    } catch (e) {
    }

    // render image
    if(text.startsWith("IMAGE(")){
      let imgUrl = text.substring(6, text.length - 1);
      text = `<img src="${imgUrl}" style="display: block;max-width:100%; width: auto; height: auto;"/>`
    }

    // render buttons
    text = text.replace(/BUTTON\( *(.*?) *, *(.*?) *\)/g, '<div class="btn btn-success chatBtn" data-value="$2">$1</div>')

    const message = $this.generateMessage(inOrOut, timestampStr, username, text, imagepath);

    $('.chatbox .chat-user .chat-user-messages').append(message);

    const self = this;

    // link button actions
      $('#chatbody').on('click', '.chatBtn', function(){
      let value = $(this).data('value');
      // $this.triggerHandler('newOutGoingMessage', {'$this': $this, 'text':  value});
      const event = 'newOutGoingMessage'
      const params = {'$this': $this, 'text':  value, 'type': 'button'}
      if(self._triggers[event] ) {
        for(let i in self._triggers[event] )
          self._triggers[event][i](params);
      }
    })

    if(!appendOnly) {
      $this.scrollToBottom();
      $(".chatbox .chat-user-form .form-control").val('');
      $this.focusOnInput();
    }
  }

  focusOnInput() {
    $(".chatbox .chat-user-form .form-control").focus();
  }

  scrollToBottom() {
    $('.chatbox .chat-user').animate({ scrollTop: $('.chatbox .chat-user .chat-user-messages').height() }, "slow");
  }
}