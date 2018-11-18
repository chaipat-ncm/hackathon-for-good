package org.c4i.nlp.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * A text message with meta information (sender, recipient, timestamp...)
 * @author Arvid Halma, Wouter Eekhout
 * @version 4-7-17
 */
public class Message {
    @JsonProperty
    private String id;
    @JsonProperty
    private String conversationId;
    @JsonProperty
    private String senderId;
    @JsonProperty
    private String recipientId;
    @JsonProperty
    private String text;
    @JsonProperty
    private DateTime timestamp;
    @JsonProperty
    private Boolean incoming;
    @JsonProperty
    private String channel;

    public Message() {
        timestamp = new DateTime();
        id = UUID.randomUUID().toString();
    }

    public static Message createReply(Conversation conversation, String text){
        Message reply = new Message();
        reply.setId(java.util.UUID.randomUUID().toString());
        reply.setTimestamp(DateTime.now());
        reply.setChannel(conversation.getChannel());
        reply.setRecipientId(conversation.getUserId());
        reply.setSenderId(conversation.getBotId());
        reply.setConversationId(conversation.getId());
        reply.setIncoming(false);
        reply.setText(text);
        reply.setConversationId(conversation.getId());
        return reply;
    }


    public String getId() {
        return id;
    }

    public Message setId(String id) {
        this.id = id;
        return this;
    }

    public String getConversationId() {
        return conversationId;
    }

    public Message setConversationId(String coversationId) {
        this.conversationId = coversationId;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public Message setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public Message setRecipientId(String recipientId) {
        this.recipientId = recipientId;
        return this;
    }

    public String getText() {
        return text;
    }

    public Message setText(String text) {
        this.text = text;
        return this;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }


    public Message setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Boolean getIncoming() {
        return incoming;
    }

    public Message setIncoming(Boolean incoming) {
        this.incoming = incoming;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", coversationId='" + conversationId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", text='" + text + '\'' +
                ", channel='" + channel + "'" +
                ", timestamp=" + timestamp +
                ", incoming=" + incoming +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return id != null ? id.equals(message.id) : message.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
