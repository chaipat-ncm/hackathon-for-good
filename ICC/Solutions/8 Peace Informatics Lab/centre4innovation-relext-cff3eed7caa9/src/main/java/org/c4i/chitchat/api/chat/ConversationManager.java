package org.c4i.chitchat.api.chat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.util.StringUtil;
import org.parboiled.common.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Group message into conversation, by senderId and time interval
 * @author Arvid Halma
 * @version 4-7-17
 */
public class ConversationManager {

    private Cache<String, Conversation> cache;
    private final Logger logger = LoggerFactory.getLogger(ConversationManager.class);

    public ConversationManager(){
        this(100_000, 10, TimeUnit.MINUTES, ImmutableList.of());
    }

    public ConversationManager(int maxEntries, long maxEntryTime, TimeUnit maxEntryTimeUnit, List<ConversationListener> removalListeners) {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(maxEntries)
                .expireAfterAccess(maxEntryTime, maxEntryTimeUnit)
                .removalListener((RemovalListener<String, Conversation>) removalNotification ->
                        removalListeners.forEach(c -> c.timout(removalNotification.getValue())))
                .build();

        // force cleanup

        /*
            So what are the main differences between the Timer and the ExecutorService solution:

            Timer can be sensitive to changes in the system clock; ScheduledThreadPoolExecutor is not
            Timer has only one execution thread; ScheduledThreadPoolExecutor can be configured with any number of threads
            Runtime Exceptions thrown inside the TimerTask kill the thread, so following scheduled tasks won’t run further; with ScheduledThreadExecutor – the current task will be canceled, but the rest will continue to run
            http://www.baeldung.com/java-timer-and-timertask
         */
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
        new TimerTask() {
            @Override
            public void run() {
                cache.cleanUp();
            }
        }, maxEntryTime, (maxEntryTime+1)/2, maxEntryTimeUnit);
    }

    private String key(String senderId, String recipientId) {
        String[] conversationId = new String[]{senderId, recipientId};
        Arrays.sort(conversationId);
        return StringUtil.truncate(conversationId[0], 62) + " & " + StringUtil.truncate(conversationId[1], 62);
    }

    private String key(Message msg){
        String recipientId = msg.getRecipientId();
        String senderId = msg.getSenderId();
        return key(senderId, recipientId);
    }

    public Conversation update(Message msg){
        String recipientId = msg.getRecipientId();
        String senderId = msg.getSenderId();
        String id = key(senderId, recipientId);

        try {
            Conversation conversation = cache.get(id,
                    () -> new Conversation()
                            .setId(id)
                            .setUserId(senderId)
                            .setBotId(recipientId)
                            .setChannel(msg.getChannel())
            );
            msg.setConversationId(conversation.getId());
            conversation.getMessages().add(msg);
            return conversation;
        } catch (ExecutionException e) {
            logger.error("Error while managing conversations.", e);
            throw new RuntimeException(e);
        }
    }

    public Conversation getConversation(String senderId, String recipientId){
        String id = key(senderId, recipientId);
        try {
            return cache.get(key(senderId,recipientId), () -> new Conversation()
                    .setId(id)
                    .setUserId(senderId)
                    .setBotId(recipientId));
        } catch (ExecutionException e) {
            return null;
        }
    }

    public void reset(String senderId, String recipientId) {
        cache.invalidate(key(senderId, recipientId));
    }

    public void reset() {
        cache.invalidateAll();
    }

}
