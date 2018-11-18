package org.c4i.chitchat.api.resource;

import com.github.messenger4j.*;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.exception.MessengerVerificationException;
import com.github.messenger4j.send.*;
import com.github.messenger4j.send.message.RichMediaMessage;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.message.quickreply.QuickReply;
import com.github.messenger4j.send.message.quickreply.TextQuickReply;
import com.github.messenger4j.send.message.richmedia.UrlRichMediaAsset;
import com.github.messenger4j.send.recipient.IdRecipient;
import com.github.messenger4j.send.senderaction.SenderAction;
import com.github.messenger4j.userprofile.UserProfile;
import com.github.messenger4j.webhook.event.*;
import com.github.messenger4j.webhook.event.attachment.Attachment;
import com.github.messenger4j.webhook.event.attachment.LocationAttachment;
import com.github.messenger4j.webhook.event.attachment.RichMediaAttachment;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.c4i.chitchat.api.Config;
import org.c4i.chitchat.api.chat.ConversationListener;
import org.c4i.chitchat.api.chat.ConversationManager;
import org.c4i.chitchat.api.model.*;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.chat.ScriptBot;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.match.Result;
import org.c4i.nlp.match.Script;
import org.c4i.util.StringUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Facebook chatbot endpoint.
 * @author Boaz Manger
 */
@Path("/channel/fb")
@Api("/channel/fb")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class FacebookResource {
    private Config config;
    private Map<String, Messenger> messengerMap;

    private final Logger logger = LoggerFactory.getLogger(FacebookResource.class);

    private ScriptBot chatBot;
    private ConversationManager chairman;
    private List<ConversationListener> listeners;

    public FacebookResource(Config config, ConversationManager chairman, List<ConversationListener> listeners) {
        this.config = config;
        this.chairman = chairman;
        this.listeners = listeners;
        this.messengerMap = new HashMap<>();

        for (FacebookCredentials c : this.config.facebookSettings.credentials) {
            Messenger tempMess = Messenger.create(c.getAccessToken(), c.getAppSecret(), c.getVerificationToken());
            this.messengerMap.put(c.getPageId(), tempMess);
            logger.debug("Initializing MessengerReceiveClient - appSecret: {} | verifyToken: {}", c.getAppSecret(),
                    c.getVerificationToken());
        }
    }

    @GET
    @Path("/config/info")
    @ApiOperation(
            response = String.class,
            value = "Get token information")
    @Consumes({MediaType.APPLICATION_JSON})
    public List<Map<String,String>> configInfo() {
       return this.config.facebookSettings.credentials.stream().map(c ->
               ImmutableMap.of(
                       "appId", c.getAppId(),
                       "pageId", c.getPageId(),
                       "accessToken", StringUtil.truncate(c.getAccessToken(), 4) +"...",
                       "accessSecret", StringUtil.truncate(c.getAppSecret(), 4) +"...",
                       "verificationToken", StringUtil.truncate(c.getVerificationToken(), 4) +"..."
                       ))
               .collect(Collectors.toList());
    }

    @POST
    @Path("/script/reload")
    @ApiOperation(
            value = "Load the latest 'Facebook live script' from the database and use it as the current chatbot",
            response = Boolean.class)
    public void loadLiveScript(){
        TextDoc src = config.dao.textDocDao.getLastUpdatedByName("ccs", "Facebook live script");
        Script script = Compiler.compile(src.getBody(), config.getNlp());
        this.chatBot = new ScriptBot(script, config.getNlp());
    }

    @DELETE
    @Path("/conversation/reset")
    @ApiOperation(
            value = "Reset all conversation states for this channel",
            notes = "All previous messages in conversations will be ignored.",
            response = Boolean.class)
    public void resetConversations() {
        chairman.reset();
    }

    @GET
    @Path("/webhook")
    @ApiOperation(
            response = String.class,
            value = "Verification of the webhook")
    @Consumes({MediaType.APPLICATION_JSON})
    public String verifyToken(@QueryParam("hub.mode") String mode,
                              @QueryParam("hub.verify_token") String verifyToken,
                              @QueryParam("hub.challenge") String challenge) {

        logger.info("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}",
                mode, verifyToken, challenge);

        try {
            // Using a random Messenger object. This works as long as the verification token is the same for every object.
            this.messengerMap.entrySet().iterator().next().getValue().verifyWebhook(mode, verifyToken);
            logger.info("Webhook verification succeeded.");
            return challenge;

        } catch (MessengerVerificationException e) {
            logger.error("Webhook verification failed: {}", e.getMessage());
            return "error";
        }
    }

    @POST
    @Path("/webhook")
    @ApiOperation(
            value = "Facebook callback endpoint to process messages",
            response = String.class)
    public String callback(final String payload,
                           @HeaderParam("X-Hub-Signature") final String signature) throws IOException {

        logger.info("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);

        String pageId = config.getObjectMapper().readTree(payload).get("entry").get(0).get("id").asText();

        Messenger messenger = this.messengerMap.get(pageId);

        try {
            messenger.onReceiveEvents(payload, Optional.of(signature), event -> {
                if (event.isTextMessageEvent() | event.isQuickReplyMessageEvent()) {

                    Message mess = null;

                    if (event.isQuickReplyMessageEvent()) {
                        mess = getMessage(event.asQuickReplyMessageEvent());
                        mess.setText(event.asQuickReplyMessageEvent().payload());
                    } else if (event.isTextMessageEvent()) {
                        mess = getMessage(event.asTextMessageEvent());
                    }

                    final Message message = mess;


                    Conversation conversation = chairman.update(message);
                    message.setConversationId(conversation.getId());

                    logger.info("Received text message from '{}' at '{}' with content: {} (mid: {})",
                            message.getSenderId(), message.getTimestamp(), message.getText(), message.getId());

                    listeners.forEach(cl -> cl.onReceive(conversation, message));

                    try {
                        String senderId = message.getSenderId();
                        sendAction(messenger, senderId, SenderAction.MARK_SEEN);

                        Result result = chatBot.reply(conversation);
                        if (result.getMatches().containsKey("RESET")) {
                            // a reset label was triggered
                            chairman.reset(message.getSenderId(), "fb");
                            listeners.forEach(cl -> cl.reset(conversation));
                        }
                        sendAction(messenger, senderId, SenderAction.TYPING_ON);

                        Pattern iPattern = Pattern.compile("IMAGE\\((.*?)\\)");
                        Pattern bPattern = Pattern.compile("BUTTON\\((.*?) *, *(.*?)\\)");

                        List<Message> replies = result.getReplies();

                        if (replies.size() > 1) {
                            for (ListIterator<Message> i = replies.listIterator(); i.hasNext(); ) {
                                Message m = i.next();

                                if (m.getText().contains("BUTTON") & i.hasPrevious()) {
                                    replies.get(i.previousIndex() - 1).setText(replies.get(i.previousIndex() - 1).getText() + " " + m.getText());
                                    i.remove();
                                }
                            }
                        }

                        for (Message reply : replies) {
                            chairman.update(reply);

                            String text = reply.getText();
                            Matcher matcher = iPattern.matcher(text);
                            String type;
                            URL url = null;

                            if (matcher.find()) {
                                url = new URL(matcher.group(1));
                                type = "image";
                            } else {
                                type = "text";
                            }

                            if (text.contains("BUTTON")) {
                                Matcher matcherb = bPattern.matcher(text);
                                List<QuickReply> textQuickReplies = new ArrayList<>();
                                while (matcherb.find()) {
                                    textQuickReplies.add(TextQuickReply.create(matcherb.group(1), matcherb.group(2)));
                                }
                                reply.setText(bPattern.matcher(reply.getText()).replaceAll(""));
                                if (type.equals("text")) {
                                    sendText(messenger, reply, MessagingType.RESPONSE, Optional.of(textQuickReplies));
                                } else if (type.equals("image")) {
                                    sendRichMessage(messenger, reply, MessagingType.RESPONSE, UrlRichMediaAsset.Type.IMAGE,
                                            url, Optional.of(textQuickReplies));
                                }
                            } else if (type.equals("text")) {
                                sendText(messenger, reply, MessagingType.RESPONSE);
                            } else if (type.equals("image")) {
                                sendRichMessage(messenger, reply, MessagingType.RESPONSE, UrlRichMediaAsset.Type.IMAGE,
                                        url, Optional.empty());

                            }
                        }

                        listeners.forEach(cl -> cl.onSend(conversation, result));

                        sendAction(messenger, senderId, SenderAction.TYPING_OFF);

                        // todo: save conv
                    } catch (MessengerApiException | MessengerIOException e) {
                        logger.debug("Could not send message, an unexpected error occured.");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else if (event.isAttachmentMessageEvent()) {
                    final AttachmentMessageEvent attachmentMessageEvent = event.asAttachmentMessageEvent();
                    for (Attachment attachment : attachmentMessageEvent.attachments()) {
                        if (attachment.isRichMediaAttachment()) {
                            final RichMediaAttachment richMediaAttachment = attachment.asRichMediaAttachment();
                            final RichMediaAttachment.Type type = richMediaAttachment.type();
                            final URL url = richMediaAttachment.url();
                            logger.debug("Received rich media attachment of type '{}' with url: {}", type, url);
                        }
                        if (attachment.isLocationAttachment()) {
                            final LocationAttachment locationAttachment = attachment.asLocationAttachment();
                            final double longitude = locationAttachment.longitude();
                            final double latitude = locationAttachment.latitude();
                            logger.debug("Received location information (long: {}, lat: {})", longitude, latitude);
                        }
                    }
                } else if (event.isPostbackEvent()) {
                    final PostbackEvent postbackEvent = event.asPostbackEvent();
                    logger.warn("Received PostbackEvent: {} with payload {}", postbackEvent.title(),
                            postbackEvent.payload());
                }
            });
        } catch (MessengerVerificationException e) {
            return "Error";
        }
        return "OK";
    }

    private Message getMessage(TextMessageEvent event) {
        Message message = new Message();
        message.setId(event.messageId());
        message.setText(event.text());
        message.setSenderId(event.senderId());
        message.setRecipientId(event.recipientId());
        message.setTimestamp(new DateTime(event.timestamp().toEpochMilli()));
        message.setIncoming(true);
        message.setChannel("fb");
        return message;
    }

    private Message getMessage(QuickReplyMessageEvent event) {
        Message message = new Message();
        message.setId(event.messageId());
        message.setText(event.text());
        message.setSenderId(event.senderId());
        message.setRecipientId(event.recipientId());
        message.setTimestamp(new DateTime(event.timestamp().toEpochMilli()));
        message.setIncoming(true);
        message.setChannel("fb");
        return message;
    }

    private void sendAction(Messenger messenger, String recipientId, SenderAction action) throws MessengerApiException, MessengerIOException {
        final SenderActionPayload sendPayload = SenderActionPayload.create(recipientId, action);
        messenger.send(sendPayload);
    }

    private void sendText(Messenger messenger, Message message, MessagingType type) throws MessengerApiException, MessengerIOException {
        sendText(messenger, message, type, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    private void sendText(Messenger messenger, Message message, MessagingType type, Optional<List<QuickReply>> quickReplies) throws MessengerApiException, MessengerIOException {
        sendText(messenger, message, type, Optional.empty(), Optional.empty(), Optional.empty(), quickReplies);
    }

    private void sendText(Messenger messenger, Message message, MessagingType type, Optional<String> metaData,
                          Optional<NotificationType> notificationType, Optional<MessageTag> messageTag,
                          Optional<List<QuickReply>> quickReplies)
            throws MessengerApiException, MessengerIOException {

        final TextMessage textMessage = TextMessage.create(message.getText(), quickReplies, metaData);
        final MessagePayload messagePayload = MessagePayload.create(IdRecipient.create(message.getRecipientId()), type,
                textMessage, notificationType, messageTag);
        messenger.send(messagePayload);
    }

    private void sendRichMessage(Messenger messenger, Message message, URL url) throws MessengerApiException, MessengerIOException {
        sendRichMessage(messenger, message, MessagingType.RESPONSE, UrlRichMediaAsset.Type.IMAGE, url, Optional.empty());
    }

    private void sendRichMessage(Messenger messenger, Message message, MessagingType type, UrlRichMediaAsset.Type assetType, URL url,
                                 Optional<List<QuickReply>> quickReplies) throws MessengerApiException, MessengerIOException {
        final UrlRichMediaAsset urlRichMediaAsset = UrlRichMediaAsset.create(assetType, url);
        final MessagePayload messagePayload = MessagePayload.create(IdRecipient.create(message.getRecipientId()), type,
                RichMediaMessage.create(urlRichMediaAsset, quickReplies, Optional.empty()));
        messenger.send(messagePayload);
    }

    private FBUserProfile queryUserProfile(Messenger messenger, String userId) throws MessengerApiException, MessengerIOException {
        UserProfile userProfile = messenger.queryUserProfile(userId);
        return new FBUserProfile(userProfile.firstName(), userProfile.lastName(),
                userProfile.locale(), userProfile.timezoneOffset(), userProfile.gender());
    }


}