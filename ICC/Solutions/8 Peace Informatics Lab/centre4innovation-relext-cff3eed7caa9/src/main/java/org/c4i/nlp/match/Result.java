package org.c4i.nlp.match;

import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.util.Timestamped;
import org.joda.time.DateTime;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A text match result: matched label rules, highlighted result, replies, timing information.
 * @author Arvid Halma
 * @version 11-8-2017 - 23:14
 */
public class Result implements Timestamped {

    String highlight;
    Map<String, List<Range>> matches;
    List<Message> replies;
    Map<String, Long> profile;
    Conversation conversation;

    public Result() {
        this.matches = new HashMap<>(2);
        this.replies = new ArrayList<>(0);
    }

    public Result(String highlight, Map<String, List<Range>> matches, List<Message> replies, Map<String, Long> profile) {
        this.highlight = highlight;
        this.matches = matches;
        this.replies = replies;
        this.profile = profile;
    }

    @Override
    public DateTime getTimestamp() {
        if(conversation == null || conversation.getMessages().isEmpty())
            return DateTime.now();

        return conversation.getMessages().get(0).getTimestamp();
    }

    public String getHighlight() {
        return highlight;
    }

    public Result setHighlight(String highlight) {
        this.highlight = highlight;
        return this;
    }

    public Result addMatch(Range range){
        if(!matches.containsKey(range.label)){
            matches.put(range.label, new ArrayList<>(2));
        }

        List<Range> ranges = matches.get(range.label);
        if(!ranges.contains(range)) {
            ranges.add(range);
        }
        return this;
    }

    public boolean containsAnyLabel(Collection<String> labels){
        for (String label : labels) {
            if(matches.containsKey(label))
                return true;
        }
        return false;
    }

    public boolean containsLabel(String label){
        return matches.containsKey(label);
    }

    public boolean containsAllLabel(Collection<String> labels){
        return matches.keySet().containsAll(labels);
    }

    public Map<String, List<Range>> getMatches() {
        return matches;
    }

    public Result setMatches(Map<String, List<Range>> matches) {
        this.matches = matches;
        return this;
    }

    public Result removeAnonymousMatches(){
        final Set<String> anonymous = this.matches.keySet().stream().filter(head -> head.startsWith("label")).collect(Collectors.toSet());
        anonymous.forEach(head -> this.matches.remove(head));
        return this;
    }

    public Stream<Range> getRangeStream(){
        return matches.values().stream().flatMap(List::stream);
    }

    public List<Range> getRanges(){
        return getRangeStream().collect(Collectors.toList());
    }

    public List<Message> getReplies() {
        return replies;
    }

    public Result setReplies(List<Message> replies) {
        this.replies = replies;
        return this;
    }

    public Result setReplies(List<String> replies, Conversation conversation) {
        this.replies = new ArrayList<>();
        for (String reply : replies) {
            this.replies.add(Message.createReply(conversation, reply));
        }

        return this;
    }

    public Map<String, Long> getProfile() {
        return profile;
    }

    public Result setProfile(Map<String, Long> profile) {
        this.profile = profile;
        return this;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Result setConversation(Conversation conversation) {
        this.conversation = conversation;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "highlight='" + highlight + '\'' +
                ", matches=" + matches +
                ", replies=" + replies +
                ", profile=" + profile +
                ", conversation=" + conversation +
                '}';
    }
}
