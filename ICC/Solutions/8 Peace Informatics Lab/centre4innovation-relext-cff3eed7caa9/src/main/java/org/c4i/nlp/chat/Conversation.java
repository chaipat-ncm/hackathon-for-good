package org.c4i.nlp.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A sequence of {@link Message}s.
 * @author Arvid Halma
 * @version 4-7-17
 */
public class Conversation {
    @JsonProperty
    String id;

    @JsonProperty
    List<Message> messages;

    @JsonProperty
    String botId;

    @JsonProperty
    String userId;

    @JsonProperty
    String lang;

    @JsonProperty
    private String channel;


    public Conversation() {
        this.id = UUID.randomUUID().toString();
        this.messages = new ArrayList<>();
    }

    public Conversation(String id) {
        this.id = id;
        this.messages = new ArrayList<>();
    }

    public Conversation(Conversation org){
        this.id = org.id;
        this.messages = new ArrayList<>(org.messages);
        this.botId = org.botId;
        this.userId = org.userId;
        this.lang = org.lang;
    }

    public String getUserText(){
        return messages.stream()
                .filter(Message::getIncoming)
                .map(Message::getText)
                .collect(Collectors.joining("\n\n"));
    }

    public String getId() {
        return id;
    }

    public Conversation setId(String id) {
        this.id = id;
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Conversation setMessages(List<Message> messages) {
        this.messages = messages;
        return this;
    }

    public String getBotId() {
        return botId;
    }

    public Conversation setBotId(String botId) {
        this.botId = botId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Conversation setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public Conversation setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public Conversation setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public Message lastMessage(){
        if(messages.isEmpty()){
            return null;
        }
        return messages.get(messages.size()-1);
    }

    public String toSingleLineString(){
        return messages.stream().map(msg -> "["+msg.getSenderId() + "]: "
                + msg.getText().replace("\n", "\\n")).collect(Collectors.joining("\\n "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Conversation that = (Conversation) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id='" + id + '\'' +
                ", messages.size()=" + messages.size() +
                ", botId='" + botId + '\'' +
                ", userId='" + userId + '\'' +
                ", lang='" + lang + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
