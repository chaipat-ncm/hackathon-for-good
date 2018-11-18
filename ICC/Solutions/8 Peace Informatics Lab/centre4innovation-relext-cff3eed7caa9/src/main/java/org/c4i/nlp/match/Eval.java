package org.c4i.nlp.match;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.c4i.chitchat.api.model.LanguageProcessingConfig;
import org.c4i.graph.PropVertex;
import org.c4i.graph.PropWeightedEdge;
import org.c4i.nlp.Nlp;
import org.c4i.nlp.chat.Conversation;
import org.c4i.nlp.chat.Message;
import org.c4i.nlp.normalize.StringNormalizer;
import org.c4i.nlp.normalize.StringNormalizers;
import org.c4i.nlp.tokenize.*;
import org.c4i.util.ArrayUtil;
import org.c4i.util.EmojiAlias;
import org.c4i.util.Histogram;
import org.c4i.util.StringUtil;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.parboiled.common.ImmutableList;
import org.parboiled.common.Tuple2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Apply a script to a text, resulting in matches found and messages to reply.
 *
 * @author Arvid
 * @version 27-4-2016 - 21:24
 */
public class Eval {
    private Nlp nlp;
    private StringNormalizer normalizer;

    public Eval(Nlp nlp) {
        this.nlp = nlp == null ? new Nlp(null, ImmutableMap.of("en", new LanguageProcessingConfig())) : nlp;
        this.normalizer = StringNormalizers.DEFAULT;
    }

    /**
     * Find instances of the patterns defined by the script in the text.
     * @param src a script.
     * @param text the input text to be searched
     * @return a result containing the matched ranges.
     */
    public Result find(String src, String text){
        Script script = Compiler.compile(src, nlp);
        return find(script, text);
    }

    /**
     * Find instances of the patterns defined by the script in the text.
     * @param script a script.
     * @param text the input text to be searched
     * @return a result containing the matched ranges.
     */
    public Result find(Script script, String text){
        /*if(script.getConfig().getLanguages().contains("emoticon")) {
            val = EmojiAlias.matchCodify(val);
        }*/
        text = EmojiAlias.shortCodify(text);

        normalizer = nlp.getNormalizer(script.config);
        Tokenizer tokenizer = nlp.getWordTokenizer(script.config);

        Token[][] tokens = MatchUtil.textToSentenceTokens(text, normalizer, tokenizer, nlp.getSentenceSplitter(script.config));

        Result result = find(script, tokens);

        String finalText = text;
        List<Range> matches = result.getRanges();
        matches.forEach(mr -> mr.updateValue(finalText));
        ReplyRule reply = reply(script, result.getMatches());
        List<String> replies = reply == null ? ImmutableList.of() : reply.randomReply(script, text, matches, nlp);
        List<Message> replyMsgs = replies.stream().map(txt -> new Message().setText(txt)).collect(Collectors.toList());

        result.removeAnonymousMatches();
        matches = result.getRanges();
        String highlight = Eval.highlightWithTags(text, matches);

        return result.setHighlight(highlight).setReplies(replyMsgs);
    }

    /**
     * Find instances of the patterns defined by the script in the text.
     * @param script a script.
     * @param text the input text to be searched
     * @return a result containing the matched ranges.
     */
    public Tuple2<Result, WeightedGraph<PropVertex, PropWeightedEdge>> findGraph(Script script, String sourceName, String text){
        normalizer = nlp.getNormalizer(script.config);
        Tokenizer tokenizer = nlp.getWordTokenizer(script.config);

        Token[][] tokens = MatchUtil.textToSentenceTokens(text, normalizer, tokenizer, nlp.getSentenceSplitter(script.config));

        Tuple2<Result, WeightedGraph<PropVertex, PropWeightedEdge>> tuple = findGraph(script, sourceName, text, tokens, ImmutableList.of());

        String finalText = text;
        Result result = tuple.a;
        List<Range> matches = result.getRanges();
        matches.forEach(mr -> mr.updateValue(finalText));
        ReplyRule reply = reply(script, result.getMatches());
        List<String> replies = reply == null ? ImmutableList.of() : reply.randomReply(script, text, matches, nlp);
        List<Message> replyMsgs = replies.stream().map(txt -> new Message().setText(txt)).collect(Collectors.toList());

        result.removeAnonymousMatches();
        matches = result.getRanges();
        String highlight = Eval.highlightWithTags(text, matches);

        result.setHighlight(highlight).setReplies(replyMsgs);
        return tuple;
    }


    /**
     * Apply a rule in CNF to a list of tokens
     * @param text the list of tokens (val) to search in
     * @param rule match rule
     * @param script context of other rules
     * @param result container for matches that is updated
     * @return whether the rule matches or not
     */
    public List<Range> findRule(
            final Token[] text,
            final LabelRule rule,
            final Script script,
            final Map<String, List<Range>> result)
    {
        // backup props for rule
        Map<String, Object> scriptLabelProperties = null;
        if(script != null) {
            scriptLabelProperties = script.getConfig().getRuleProperties().getLabel();
        }

        final boolean firstOnly = rule.isProp("match", "first", scriptLabelProperties);
        final int T = text.length;

        if(T == 0){
            return new ArrayList<>(0);
        }

        final Range fullRange = new Range(rule.head,
                text[0].getLocation(), text[T - 1].getLocation(),
                text[0].getCharStart(), text[T - 1].getCharEnd(),
                new LinkedHashMap<>(0))
                .setFromNegation(true).setSection(text[T - 1].getSection());

        if(rule.isProp("set")){
            Object set = rule.getProp("set");
            if(set instanceof Map) {
                ((Map<?,?>)set).forEach((k, v) -> fullRange.props.put(Objects.toString(k), Objects.toString(v)));
            }
        }

        if(rule.expression == null || rule.expression.length == 0){
            // no constraints, match entire sentence
            return list(fullRange);
        }

        // Use 'tabling': remember intermediate results
        HashMap<Literal, List<Range>> cache = new HashMap<>();

        // Especially in CNF, literals may occur more often. The result should only contain each range once.
        HashSet<Range> matchSet = new HashSet<>();

        AtomicInteger matchGroup = new AtomicInteger();

        for (Literal[] disjunction : rule.expression) {
            List<Range> disjunctionRange = new ArrayList<>(0);

            nextOperand: for (Literal operand : disjunction) {
                if (cache.containsKey(operand)) {
                    // the disjunct was evaluated before...
                    List<Range> cachedRange = cache.get(operand);
                    if (cachedRange != null && !cachedRange.isEmpty()) {
                        // already known to be true
                        disjunctionRange.addAll(cachedRange);
                        if(firstOnly) {
                            break;
                        } else {
                            // still do other disjuncts
                            continue;
                        }
                    } else {
                        continue; // already known to be false
                    }
                }

                List<Range> operandRange = new ArrayList<>(0);
                if (operand.meta == '@' && script != null) {
                    // label reference
                    String ref = operand.tokens[0].getWord();
                    if (result != null && result.containsKey(ref)) {
                        // label already known to be true
                        if(!operand.negated) {
                            // copy result, and finish since disjunction is now true
                            disjunctionRange = result.get(ref);
                            break;
                        } else {
                            // label was found, but now we want the negation, so look for other disjuncts/operands
                            continue;
                        }
                    } else {
                        final List<LabelRule> labelRules = script.rules.get(ref);
                        // possibly a dynamic label (addLabel), without rule
                        if (labelRules != null) {
                            // perform label lookup
                            for (LabelRule labelRule : labelRules) {

                                List<Range> lookupResult = findRule(text, labelRule, script, result);
                                if (!operand.negated) {
                                    if (!lookupResult.isEmpty()) {
                                        // copy result, and finish since disjunction is now true
                                        disjunctionRange = lookupResult;
                                        break nextOperand;
                                    }
                                } else {
                                    if (lookupResult.isEmpty()) {
                                        // lookup did not match, then the negation does
                                        disjunctionRange = list(fullRange);
                                        break nextOperand;
                                    }
                                }
                            }
                        }
                    }

                } else if (operand.meta == '=' && script != null) {
                    // comparison
                    disjunctionRange.addAll(evalCompare(rule.head, text, script, result, operand, false));
                } else {
                    // normal word/literal matching
                    operandRange = findLiteral(text, operand, rule.head, matchGroup, 0, script.getPlugins(), firstOnly);
                    // add wildcard0, a.k.a. entire input text as property?
                    // String sentence = TokenUtil.toSentence(text);
                    // disjunctionRange.forEach(r -> r.props.put("wildcard0", sentence));
                    disjunctionRange.addAll(operandRange);
                }

                // the disjunct has been evaluated
                cache.put(operand, operandRange);

                if (firstOnly && !disjunctionRange.isEmpty()) {
                    // matched! next disjunction please...
                    break;
                }
            }

            // the disjunction has been evaluated
            if (disjunctionRange.isEmpty()) {
                // disjunction failed, therefore cnf is false. Disregard possible previous matches...
                matchSet.clear();
                break;
            } else {
                // update result
                for (Range disjunct : disjunctionRange) {
                    if (matchSet.stream().allMatch(r -> r.intersect(disjunct) == null)) {
                        // add disjoint range (didn't overlap with any previous)
                        matchSet.add(disjunct);
                    } else {
                        // shrink overlapping earlier ranges
                        matchSet.forEach(r -> {
                            Range intersect = r.intersect(disjunct);
                            if (intersect != null) {
                                r.tokenStart = intersect.tokenStart;
                                r.tokenEnd = intersect.tokenEnd;
                                r.charStart = intersect.charStart;
                                r.charEnd = intersect.charEnd;
                                r.props = intersect.props;
                                r.section = intersect.section;
                            }
                        });
                    }
                }
            }
        }
        if(rule.isProp("set")){
            Object set = rule.getProp("set");
            if(set instanceof Map) {
                ((Map<?,?>)set).forEach((k, v) -> matchSet.forEach(range -> range.props.put(Objects.toString(k), Objects.toString(v))));
            }
        }

        return new ArrayList<>(matchSet);
    }


    /**
     * Return all matching labels
     * @param script containing label rules
     * @param tokens the text
     * @return match result
     */
    private Result find(final Script script, final Token[] tokens){
        return find(script, new Token[][]{tokens});
    }

    /**
     * Return all matching labels
     * @param script containing label rules
     * @param sents the text split in sentences
     * @return match result
     */
    private Result find(final Script script, final Token[][] sents){
        return find(script, sents, Collections.emptyList());
    }

    /**
     * Return all matching labels
     * @param script containing label rules
     * @param sents the text split in sentences
     * @param premises presumably true label context. Those will also show in the result
     * @return match result
     */
    private Result find(final Script script, final Token[][] sents, final List<Range> premises){
        Result result = new Result();
        premises.forEach(result::addMatch);

        final Map<String, Object> scriptLabelProperties = script.getConfig().getRuleProperties().getLabel();
        final Map<String, Long> profile = new HashMap<>();

        for (int i = 0, sentsLength = sents.length; i < sentsLength; i++) {
            final int sentIx = i;

            if(sents[i].length == 0){
                continue;
            }

            script.rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
                long t0 = System.nanoTime();
                final String head = rule.head;

                final Object within = rule.getProp("within", scriptLabelProperties);
                int lastN = 1;
                if("all".equals(within)){
                    lastN = sents.length; // all;
                } else if(within instanceof Integer){
                    lastN = (Integer)within;
                }

                /*if((sentIx + 1) - lastN < 0 || sentIx == sentsLength - 1){
                    return; // only process when there is enough sentences skipped, or there are no more sentences left
                }*/

                Token[] scope = ArrayUtil.concatAll(Token.class, sents, sentIx + 1 - lastN, sentIx + 1);
                Range scopeRange = new Range(null
                        , scope[0].getLocation(), scope[0].getLocation()+sentsLength,
                        scope[0].getCharStart(), scope[scope.length-1].getCharEnd())
                        .setSection(scope[scope.length-1].getSection());


                Map<String, List<Range>> scopeRangeMap = new HashMap<>();
                result.getRangeStream().filter(scopeRange::contains).forEach(range -> {
                    if(!scopeRangeMap.containsKey(range.label)){
                        scopeRangeMap.put(range.label, new ArrayList<>(2));
                    }
                    scopeRangeMap.get(range.label).add(range);
                });

                List<Range> ranges = findRule(scope, rule, script, scopeRangeMap);

                // update timing info
                if(!profile.containsKey(head)){
                    profile.put(head, 0L);
                }
                profile.put(head, profile.get(head) + (System.nanoTime() - t0));

                // update result
                for (Range r : ranges) {
                    Range range = new Range(r).setLabel(head);
//                    scopeResult.addMatch(range);
                    result.addMatch(range);
                }
            });
        }
        return result.setProfile(profile);
    }

    /**
     * Return all matching labels
     * @param script containing label rules
     * @param sents the text split in sentences
     * @param premises presumably true label context. Those will also show in the result
     * @return match result
     */
    private Tuple2<Result, WeightedGraph<PropVertex, PropWeightedEdge>> findGraph(final Script script, final String sourceName, final String text, final Token[][] sents, final List<Range> premises){
        Result result = new Result();
        final WeightedGraph<PropVertex, PropWeightedEdge> g = new ListenableUndirectedWeightedGraph<>(PropWeightedEdge.class);


        StringNormalizer normalizer = nlp.getNormalizer(script.getConfig());

        premises.forEach(result::addMatch);

        final Map<String, Object> scriptLabelProperties = script.getConfig().getRuleProperties().getLabel();
        final Map<String, Long> profile = new HashMap<>();

        for (int i = 0, sentsLength = sents.length; i < sentsLength; i++) {
            final int sentIx = i;

            if(sents[i].length == 0){
                continue;
            }

            script.rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
                long t0 = System.nanoTime();
                final String head = rule.head;

                final Object within = rule.getProp("within", scriptLabelProperties);
                int lastN = 1;
                if("all".equals(within)){
                    lastN = sents.length; // all;
                } else if(within instanceof Integer){
                    lastN = (Integer)within;
                }

                /*if((sentIx + 1) - lastN < 0 || sentIx == sentsLength - 1){
                    return; // only process when there is enough sentences skipped, or there are no more sentences left
                }*/

                Token[] scope = ArrayUtil.concatAll(Token.class, sents, sentIx + 1 - lastN, sentIx + 1);
                Range scopeRange = new Range(null
                        , scope[0].getLocation(), scope[0].getLocation()+sentsLength,
                        scope[0].getCharStart(), scope[scope.length-1].getCharEnd())
                        .setSection(scope[scope.length-1].getSection());


                Map<String, List<Range>> scopeRangeMap = new HashMap<>();
                result.getRangeStream().filter(scopeRange::contains).forEach(range -> {
                    if(!scopeRangeMap.containsKey(range.label)){
                        scopeRangeMap.put(range.label, new ArrayList<>(2));
                    }
                    scopeRangeMap.get(range.label).add(range);
                });

                List<Range> ranges = findRule(scope, rule, script, scopeRangeMap);

                // update timing info
                if(!profile.containsKey(head)){
                    profile.put(head, 0L);
                }
                profile.put(head, profile.get(head) + (System.nanoTime() - t0));

                // update result
                for (Range r : ranges) {
                    Range range = new Range(r).setLabel(head);
//                    scopeResult.addMatch(range);
                    result.addMatch(range);
                }

                ranges.forEach(mr -> mr.updateValue(text));

                if("rel".equals(head)){
                    // add actors

                    int charStart = scope[0].getCharStart();
                    String highlight = highlightWithTags(text.substring(charStart, scope[scope.length-1].getCharEnd()), charStart, ranges);

                    Set<PropVertex> actors = new HashSet<>();
                    Set<PropVertex> locs = new HashSet<>();
                    Set<PropVertex> verbs = new HashSet<>();
                    for (Range r : ranges) {
                        String type = r.getProps().get("type");
                        if("actor".equals(type)){
                            PropVertex actor = new PropVertex(normalizer.normalize(r.getValue()));
                            actors.add(actor);
                            actor.getProps().put("snippet", highlight);
                            actor.getProps().put("src", sourceName);
                            actor.getProps().put("type", "actor");
                            actor.getProps().put("cat", r.getProps().get("cat"));
                            if(!g.containsVertex(actor)) {
                                g.addVertex(actor);
                            }
                        } else if("loc".equals(type)){
                            PropVertex loc = new PropVertex(normalizer.normalize(r.getValue()));
                            locs.add(loc);
                            Map<String, Object> props = loc.getProps();
                            props.put("src", sourceName);
                            props.put("type", "loc");
                            props.put("snippet", highlight);
                            props.put("lat", r.getProps().get("latitude"));
                            props.put("lon", r.getProps().get("longitude"));
                            props.put("country", r.getProps().get("country"));

                            if(!g.containsVertex(loc)) {
                                g.addVertex(loc);
                            }
                        } else if("verb".equals(type)){
                            PropVertex verb = new PropVertex(normalizer.normalize(r.getValue()));
                            verbs.add(verb);
                            Map<String, Object> props = verb.getProps();
                            props.put("src", sourceName);
                            props.put("type", "verb");
                            props.put("snippet", highlight);
                            props.put("en_term", r.getProps().get("en_term"));

                            if(!g.containsVertex(verb)) {
                                g.addVertex(verb);
                            }
                        }
                    }

                    // add combos
                    for (PropVertex verb : verbs) {
                        for (PropVertex actor : actors) {
                            PropWeightedEdge e = g.addEdge(verb, actor);
                            g.getEdge(verb, actor).put("snippet", highlight);
                            g.getEdge(verb, actor).put("src", sourceName);
                        }
                    }
                    for (PropVertex verb : verbs) {
                        for (PropVertex loc : locs) {
                            PropWeightedEdge e = g.addEdge(verb, loc);
                            g.getEdge(verb, loc).put("snippet", highlight);
                            g.getEdge(verb, loc).put("src", sourceName);
                        }
                    }
                    for (PropVertex loc : locs) {
                        for (PropVertex actor : actors) {
                            PropWeightedEdge e = g.addEdge(loc, actor);
                            g.getEdge(loc, actor).put("snippet", highlight);
                            g.getEdge(loc, actor).put("src", sourceName);
                        }
                    }


                    /*for (Range r : ranges) {
                        String type = r.getProps().get("type");
                        if("verb".equals(type)){
                            PropVertex verb = new PropVertex(normalizer.normalize(r.getValue()));
                            verb.getProps().put("type", "verb");
                            verb.getProps().put("src", sourceName);


                            if(!g.containsVertex(verb)) {
                                g.addVertex(verb);
                            }
                            for (PropVertex actor : actors) {
                                PropWeightedEdge e = g.addEdge(verb, actor);
                                g.getEdge(verb, actor).put("snippet", highlight);
                                g.getEdge(verb, actor).put("src", sourceName);

                            }
                            for (PropVertex loc : locs) {
                                PropWeightedEdge e = g.addEdge(verb, loc);
                                g.getEdge(verb, loc).put("snippet", highlight);
                                g.getEdge(verb, loc).put("src", sourceName);

                            }
                        }
                    }*/
                }
            });
        }
        return new Tuple2<>(result.setProfile(profile), g);
    }



    public Result findOld(final Script script, final Token[][] sents, final List<Range> premises){
        Result result = new Result();

        Token[] fullText = ArrayUtil.concatAll(Token.class, sents);
        Map<String, Object> scriptLabelProperties = script.getConfig().getRuleProperties().getLabel();

        Map<String, Long> profile = new HashMap<>();
        for (int i = 0, sentsLength = sents.length; i < sentsLength; i++) {
            Token[] sentence = sents[i];
            if(sentence.length == 0){
                continue;
            }

            Range sentenceRange = new Range(null
                    , sentence[0].getLocation(), sentence[0].getLocation()+sentsLength,
                    sentence[0].getCharStart(), sentence[sentence.length-1].getCharEnd())
                    .setSection(sentence[sentence.length-1].getSection());

            final Result sentResult = new Result();

            // add relevant premises
            premises.stream().filter(sentenceRange::contains).forEach(prem -> {
                result.addMatch(prem);sentResult.addMatch(prem);
            });

            final boolean first = i == 0;
            script.rules.values().stream().flatMap(Collection::stream).forEach(rule -> {
                List<Range> ranges;
                final String head = rule.head;
                long t0 = System.nanoTime();

                // execute find
                if (rule.isProp("scope", "all", scriptLabelProperties) && first) {
                    ranges = findRule(fullText, rule, script, sentResult.getMatches());
                } else {
                    ranges = findRule(sentence, rule, script, sentResult.getMatches());
                }

                // update timing info
                if(!profile.containsKey(head)){
                    profile.put(head, 0L);
                }
                profile.put(head, profile.get(head) + (System.nanoTime() - t0));

                // update result
                for (Range r : ranges) {
                    Range range = new Range(r).setLabel(head);
                    sentResult.addMatch(range);
                    result.addMatch(range);
                }
            });
        }
        return result.setProfile(profile);
    }


    /**
     * Find a pattern (as {@link Token}[]) in the text (as {@link Token}[]).
     * @param text the input text
     * @param pattern the pattern to look for
     * @return a list of matches
     */
    public List<Range> findFirst(Token[] text,  Token[] pattern){
        Literal lit = new Literal(pattern, false, 'a');
        return findLiteral(text, lit, null, new AtomicInteger(), 0, ImmutableList.of(), true);
    }

    /**
     * Find an atomic pattern (as {@link Literal}) in the text (as {@link Token}[]).
     * @param text the input text
     * @param pattern the pattern to look for
     * @param matchLabel the label to apply to the resulting range when the pattern is found
     * @param matchGroup numbering offset for wildcards that the pattern may contain.
     *                   the matching groups will be labeled in Range.props.
     * @param tokenOffset where to start in the text
     * @param plugins other matching implementations for specific handling
     * @param firstOnly don't proceed to find more instances after a first one has matched
     * @return a list of matches
     */
    private List<Range> findLiteral(
            final Token[] text,
            final Literal pattern,
            final String matchLabel,
            final AtomicInteger matchGroup,
            final int tokenOffset,
            final List<EntityPlugin> plugins,
            final boolean firstOnly)
    {
        final int T = text.length;
        final int P = pattern.tokens.length;

        int matchStart = Integer.MAX_VALUE;
        int matchEnd = -1;

        boolean pNeg = pattern.negated;
        List<Range> result = new ArrayList<>(0);

        if(T == 0){
            return result;
        }

        final Map<String, String> props = new LinkedHashMap<>();

        final Range fullRange = new Range(matchLabel,
                text[0].getLocation(), text[T - 1].getLocation(),
                text[0].getCharStart(), text[T - 1].getCharEnd(),
                props).setFromNegation(true).setSection(text[T - 1].getSection());
        int textStartOffset = 0;

        // walk over the val, word by word
        nextS : for (int si = tokenOffset; si < T; si++) {
            int pi;

            // walk over the query pattern, word by word
            nextP: for (pi = 0; pi < P; pi++) {

                Token p = pattern.tokens[pi];
                String pWord = p.getWord();

                // if the pattern is now beyond the end of the val...
                if(si + pi - textStartOffset >= T){
                    // ... but the text end pattern is applicable...
                    if("TEXTEND".equals(pattern.tokens[P-1].getWord()) && p.isMatchOnNormalized()) {
                        if(pi == P - 1) {
                            // end of pattern
                            if (!pNeg) {
                                matchStart = Math.min(si, matchStart);
                                matchEnd = Math.max(si + pi, matchEnd) - 1;
                                result.add(new Range(
                                        matchLabel,
                                        matchStart, matchEnd,
                                        text[matchStart].getCharStart(), text[matchEnd - 1].getCharEnd(),
                                        props).setSection(text[matchEnd - 1].getSection())
                                );
                            }
                        }  else {
                            if(pNeg)
                                result.add(fullRange);
                        }
                        return result;
                    }

                    // fail
                    if(pNeg && result.isEmpty())
                        result.add(fullRange);
                    return result;
                }
                Token s = text[si + pi - textStartOffset];

                if("TEXTSTART".equals(pWord) && p.isMatchOnNormalized()){
                    if(si != 0){
                        // text start check is not matching
                        if(pNeg) {
                            result.add(fullRange);
                        }
                        return result;
                    } else {
                        pi++;
                        if(pi >= P){
                            // only a START token
                            if(!pNeg) {
                                result.add(fullRange);
                            }
                            return result;
                        }
                        p = pattern.tokens[pi];
                        pWord = p.getWord();
                        textStartOffset = 1;
                    }
                }



                // check wild card patterns
                if ("?".equals(pWord)) {
                    matchStart = Math.min(si, matchStart);
                    matchEnd = Math.max(si + pi + 1, matchEnd);
                    props.put("wildcard"+matchGroup.incrementAndGet(), text[matchEnd-1].getWord());
                    continue nextP;
                } else if ("+".equals(pWord) || "*".equals(pWord)) {
                    pi++;
                    Token pWildCard = p;
                    int wildCardStepBack = "*".equals(pWildCard.getWord()) ? 1 : 0; // off-by-one correction for * vs +
                    if(pi == P){
                        // wildcard as last pattern token
                        matchStart = Math.min(si, matchStart);
//                        matchEnd = Math.max(text.length, matchEnd);
                        matchEnd = matchStart;
                        final int startSection = text[matchStart].getSection();
                        for (int i = matchStart; i < text.length; i++) {
                            if(text[i].getSection() == startSection){
                                matchEnd = i + 1;
                            } else {
                                break;
                            }
                        }
                        props.put("wildcard"+matchGroup.incrementAndGet(), TokenUtil.toSentence(text, si+pi-1, matchEnd));
                        if(!pNeg) {
                            result.add(new Range(
                                    matchLabel,
                                    matchStart, matchEnd,
                                    text[matchStart].getCharStart(), text[matchEnd-1].getCharEnd(),
                                    props).setSection(text[matchEnd - 1].getSection())
                            );
                        }
                        return result;
                    }

                    p = pattern.tokens[pi];  // next p

                    if(pi + si > T){
                        // no more tokens in sentence, but pattern expects token
                        if(pNeg)
                            result.add(fullRange);
                        return result;
                    }

                    // find first match for p
                    int wildcardStart = si + pi - 1;
                    for (int k = si + pi - wildCardStepBack; k < T; k++) {
                        if(text[k].equals(p)){
                            si = k;
                            matchEnd = si + 1;
                            props.put("wildcard"+matchGroup.incrementAndGet(),
                                    TokenUtil.toSentence(text, wildcardStart, matchEnd-1));
                            continue nextP;
                        }
                    }
                    // next p not found
                    matchStart = Integer.MAX_VALUE;
                    matchEnd = -1;
                    continue nextS;
                }

                // let plugins give it a try first
                boolean usePlugin = false;
                Literal pLit = new Literal(p);
                if(p.isMatchOnNormalized() && StringUtil.startsWithUpperCase(pWord)) {
                    // only give them a try if the pattern looks promising (start with capital, approximate lit type)
                    for (EntityPlugin entityPlugin : plugins) {
                        if (entityPlugin.accept(pLit)) {
                            usePlugin = true;
                            List<Range> ranges = entityPlugin.find(text, pLit, matchLabel, si + pi);
                            if (!ranges.isEmpty()) {
                                Range range = ranges.get(0);
                                matchStart = Math.min(matchStart, range.tokenStart);
                                matchEnd = Math.max(matchEnd, range.tokenEnd);
                                props.putAll(range.props);
                                si += range.tokenEnd - range.tokenStart - 1; // skip words already matched by plugin
                                break;
                            } else {
                                matchStart = Integer.MAX_VALUE;
                                matchEnd = -1;
                                continue nextS;
                            }
                        }
                    }
                }

                // if plugins didn't match, try literal matching
                if(!usePlugin) {
                    // literal check
                    if (!s.equals(p)) {
                        // not matching
                        matchStart = Integer.MAX_VALUE;
                        matchEnd = -1;
                        continue nextS;
                    } else {
                        // match
                        matchStart = Math.min(si, matchStart);
                        matchEnd = Math.max(si + pi + 1, matchEnd);
                    }
                }
            }

            if(pi == P){
                // full pattern matched
                if(matchEnd == -1){
                    if(pNeg) {
                        result.add(fullRange);
                    }
                } else {
                    if(!pNeg) {
                        result.add(new Range(
                                matchLabel,
                                matchStart, matchEnd,
                                text[matchStart].getCharStart(), text[matchEnd - 1 - textStartOffset].getCharEnd(),
                                new LinkedHashMap<>(props)).setSection(text[matchEnd - 1 - textStartOffset].getSection())
                        );
                        // reset for next round
                        matchStart = Integer.MAX_VALUE;
                        matchEnd = -1;
                    }
                }
                if(firstOnly || pNeg) {
                    return result;
                }
            }
        }

        if(pNeg) {
            result.add(fullRange);
        }
        return result;
    }

    /**
     * Check whether a given label is in a result
     * @param result matches returned from a previous find() or reply()
     * @param label the label to look for
     * @return true iff the result contains the label
     */
    public boolean contains(List<Range> result, String label){
        return result.stream().anyMatch(mr -> label.equals(mr.label));
    }

    /**
     * Find the first answer that is applicable
     * @param script containing {@link ReplyRule} definitions
     * @param allInfo matches returned from a previous find() or reply()
     * @return a reply or null
     */
    private ReplyRule reply(final Script script, Map<String, List<Range>> allInfo){
        Map<String, List<Range>> info;
        final int maxSection = allInfo.values().stream().flatMap(Collection::stream).mapToInt(r -> r.section).max().orElse(0);
        for (ReplyRule reply : script.replies) {


            final Object within = reply.getProp("within", script.config.getRuleProperties().reply);
            int lastN = 0; // all
            if("last".equals(within)){
                lastN = 1;
            } else if(within instanceof Integer){
                lastN = (Integer)within;
            }
            final int finalLastN = lastN;

            if(lastN > 0){
                info = new HashMap<>();
                for (Map.Entry<String, List<Range>> entry : allInfo.entrySet()) {
                    final List<Range> ranges = entry.getValue();
                    final List<Range> onRanges = ranges.stream().filter(range -> range.section > maxSection - finalLastN).collect(Collectors.toList());
                    if(!onRanges.isEmpty()){
                        info.put(entry.getKey(), onRanges);
                    }
                }
            } else {
                // implicit default: {within: all}
                info = allInfo;
            }

            if (matchReply(reply.rule, info)) {
                // the reply matches

                if (reply.isProp("within", "update")) {
                    List<Range> newInfoRanges = info.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

                    // check if a reply would trigger (new info in last message)
                    if (info.isEmpty() && !reply.isFallback()) {
                        // don't use this reply, since the last message didn't add anything
                        continue;
                    } else {
                        // there is new info, lets see if this reply possibly refers to this new info
                        boolean replyPossiblyOK = false;
                        for (Literal[] literals : reply.rule.expression) {
                            for (Literal literal : literals) {
                                for (Range range : newInfoRanges) {
                                    if (literal.meta == '@' && literal.tokens[0].getWord().equals(range.label)) {
                                        // there is *some* reference to newly gained info
                                        replyPossiblyOK = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (replyPossiblyOK) {
                            return reply;
                        }
                    }
                }

                return reply;
            }
        }
        return null;
    }

    /**
     * Evaluate the script: match labels and find the first answer that is applicable
     * @param script containing {@link ReplyRule} definitions
     * @param conversation a list of incoming and outgoing messages
     * @return matches and replies
     */
    public Result reply(final Script script, Conversation conversation){
        Script rinseAndRepeatSet = new Script(script);
        Tokenizer wordTokenizer = nlp.getWordTokenizer(script.config);
        normalizer = nlp.getNormalizer(script.config);
        SentenceSplitter sentenceSplitter = nlp.getSentenceSplitter(script.config);

        Map<String, Object> scriptReplyProperties = script.getConfig().getRuleProperties().getReply();

        Result result = null;
        String inText = "";
        List<String> currentReplies = new ArrayList<>();

        Object addLabel = null;
        String addText = null;

        List<Message> messages = conversation.getMessages();
        List<Range> allAddLabels = new ArrayList<>();
        int msgInIx = 0;
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            if(message == null){
                continue;
            }

            if (message.getIncoming()) {
                final int msgIx = msgInIx++;

                // prepare text input
                inText += message.getText() + (addText != null ? " " + addText : " ") + "\n\n";
                Token[][] conversationTokens = MatchUtil.textToSentenceTokensWithSections(inText, normalizer, wordTokenizer, sentenceSplitter);
                String finalInText = inText;

                handleAddLabel(addLabel, allAddLabels, conversationTokens, finalInText, null);

                result = find(rinseAndRepeatSet, conversationTokens, allAddLabels);

                result.getRangeStream().forEach(mr -> mr.updateValue(finalInText));
                boolean kontinue = true;
                addLabel = null; // reset addLabel, only to be assigned again with "continue & addLabel"
                while (kontinue) {
                    handleAddLabel(addLabel, allAddLabels, conversationTokens, finalInText, result);

                    ReplyRule reply = reply(rinseAndRepeatSet, result.getMatches());

                    if (reply != null) {
                        addLabel = reply.getProp("addLabel", scriptReplyProperties);
                        addText = reply.getStringProp("addText", scriptReplyProperties);
                        // all text processing has been done. replies only work on matches.

                        if (!reply.isProp("repeat", scriptReplyProperties)) {
                            rinseAndRepeatSet.replies.remove(reply);
                        }

                        if(reply.isProp("removeLabel")){
                            Object removeLabel = reply.getProp("removeLabel");
                            if(removeLabel instanceof String) {
                                String label = (String) removeLabel;
                                result.matches.remove(label);
                            } else if(removeLabel instanceof List) {
                                List labels = (List) removeLabel;
                                for (Object label : labels) {
                                    result.matches.remove(label.toString());
                                }
                            }
                        }

                        if(reply.isFallback()){
                            Token[][] msgTokens = MatchUtil.selectLastSection(conversationTokens);
                            if(msgTokens.length > 0) {
                                Token[] lastSent = msgTokens[msgTokens.length - 1];
                                if(lastSent.length > 0) {
                                    Token lastToken = lastSent[lastSent.length - 1];
                                    Token firstMsgToken = msgTokens[0][0];
                                    Range fallbackRange = new Range("FALLBACK",
                                            firstMsgToken.getLocation(), lastToken.getLocation(),
                                            firstMsgToken.getCharStart(), lastToken.getCharEnd());
                                    fallbackRange.updateValue(finalInText);
                                    allAddLabels.add(fallbackRange);
                                }
                            }
                        }

                        if(i == messages.size() - 1){
                            // last input
                            currentReplies.addAll(reply.randomReply(script, inText, result.getRanges(), nlp));
                        }
                        kontinue = reply.isProp("continue", scriptReplyProperties);
                    } else {
                        kontinue = false;
                    }
                }
            }
        }

        if(result != null){
            result.removeAnonymousMatches();
            List<Range> ranges = result.getRanges();
            ranges.forEach(r -> r.conversationId = conversation.getId());
            result.setHighlight(highlightWithTags(inText, ranges));
            result.setReplies(currentReplies, conversation);
        }
        return result;
    }

    /**
     *
     * @param addLabel label(s) to be added. Either String or List of Strings.
     * @param allAddLabels list with ranges to update with addLabel to be added
     * @param conversationTokens the tokenized text
     * @param finalInText the raw text
     * @param result the Result, where addLabels can be added as well.
     */
    private void handleAddLabel(Object addLabel, List<Range> allAddLabels, Token[][] conversationTokens, String finalInText, @Nullable Result result) {
        if (addLabel != null) {
            // pending label to assign, determine range...
            Token[][] msgTokens = MatchUtil.selectLastSection(conversationTokens);
            if(msgTokens.length > 0) {
                Token[] lastSent = msgTokens[msgTokens.length - 1];
                if(lastSent.length > 0) {
                    List addLabelList = ImmutableList.of();
                    if(addLabel instanceof String){
                        addLabelList = ImmutableList.of(addLabel);
                    } else if(addLabel instanceof List){
                        addLabelList = (List) addLabel;
                    }
                    for (Object label : addLabelList) {
                        Token lastToken = lastSent[lastSent.length - 1];
                        Token firstMsgToken = msgTokens[0][0];
                        Range addLabelRange = new Range(label.toString(),
                                firstMsgToken.getLocation(), lastToken.getLocation(),
                                firstMsgToken.getCharStart(), lastToken.getCharEnd());
                        addLabelRange.updateValue(finalInText);
                        if(result != null)
                            result.addMatch(addLabelRange);
                        allAddLabels.add(addLabelRange);
                    }

                }
            }

        }
    }

    /**
     * A plain text string where matches are marked
     * @param orgText the input text
     * @param ranges a list of matches
     * @return highlighted version of the orgText
     */
    public static String highlight(String orgText, List<Range> ranges){
        StringBuilder sb = new StringBuilder();
        Collections.sort(ranges);
        for (int i = 0; i < orgText.length(); i++) {
            for (Range range : ranges) {
                if(range.charStart == i){
                    sb.append(range.label == null ? "MATCH" : range.label.toUpperCase())
                            .append(range.props == null || range.props.isEmpty() ? "" : range.props)
                            .append("{");
                }
                if(range.charEnd == i){
                    sb.append("}");
                }
            }
            char c = orgText.charAt(i);
            sb.append(c);
        }

        for (Range range : ranges) {
            if(range.charEnd == orgText.length()){
                sb.append("}");
            }
        }

        return sb.toString();
    }

    /**
     * A HTML string where matches are marked
     * @param orgText the input text
     * @param ranges a list of matches
     * @return highlighted version of the orgText
     */
    public static String highlightWithTags(String orgText, List<Range> ranges){
        StringBuilder sb = new StringBuilder();
        Collections.sort(ranges);
        for (int i = 0; i < orgText.length(); i++) {
            for (Range range : ranges) {
                if(range.charStart == i){
                    sb.append("<span class=\"match ").append(range.label)
                            .append("\"><span class=\"rule\">").append(range.label)
                            .append(range.props == null || range.props.isEmpty() ? "" : range.props).append("</span> ");
                }
                if(range.charEnd == i){
                    sb.append("</span>");
                }
            }
            char c = orgText.charAt(i);
            if(c == '\n'){
                sb.append("<br/>");
            }
            sb.append(c);
        }

        for (Range range : ranges) {
            if(range.charEnd == orgText.length()){
                sb.append("</span>");
            }
        }

        return sb.toString();
    }

    /**
     * A HTML string where matches are marked
     * @param orgText the input text
     * @param ranges a list of matches
     * @return highlighted version of the orgText
     */
    public static String highlightWithTags(String orgText, int textOffset, List<Range> ranges){
        StringBuilder sb = new StringBuilder();
        Collections.sort(ranges);
        for (int i = 0; i < orgText.length(); i++) {
            for (Range range : ranges) {
                int j = i + textOffset;
                if(range.charStart == j){
                    sb.append("<span class=\"match ").append(range.label)
                            .append("\"><span class=\"rule\">").append(range.label)
                            .append(range.props == null || range.props.isEmpty() ? "" : range.props).append("</span> ");
                }
                if(range.charEnd == j){
                    sb.append("</span>");
                }
            }
            char c = orgText.charAt(i);
            if(c == '\n'){
                sb.append("<br/>");
            }
            sb.append(c);
        }

        for (Range range : ranges) {
            if(range.charEnd == orgText.length() + textOffset){
                sb.append("</span>");
            }
        }

        return sb.toString();
    }

    public static Histogram<String> histogram(List<Range> eval){
        return new Histogram<>(eval.stream().map(r -> r.label));
    }

    /**
     * Apply a rule in CNF to a list of tokens
     * @param text the list of tokens (val) to search in
     * @param rule match rule
     * @return whether the rule matches or not
     */
    public boolean contains(final Token[] text, final LabelRule rule){
        return !findRule(text, rule, null, null).isEmpty();
    }

    private boolean contains(final Token[] text, final Literal lit){
        return !findLiteral(text, lit, null, new AtomicInteger(0), 0, ImmutableList.of(), true).isEmpty();
    }

    /**
     * Apply a rule in CNF to a list of tokens
     * @param rule match rule
     * @return whether the rule matches or not
     */
    private boolean matchReply(final LabelRule rule, final Map<String, List<Range>> result){
        if(rule.expression == null || rule.expression.length == 0){
            // no constraints, match entire val
            return true;
        }

        for (Literal[] disjunction : rule.expression) {
            boolean disjunctionRange = false;
            for (Literal lit : disjunction) {
                if(lit.meta == '@'){
                    String ref = lit.tokens[0].getWord();
                    disjunctionRange = result.containsKey(ref) ^ lit.negated;
                    if(disjunctionRange){
                        // known to be true
                        break;
                    }
                } else if(lit.meta == '=' && result != null){
                    disjunctionRange = !evalCompare(rule.head, null, null, result, lit, true).isEmpty();
                    if(disjunctionRange){
                        // known to be true
                        break;
                    }

                } else {
                    throw new IllegalStateException("Only @references allowed in reply matchers (this rule should have been rewritten by the compiler)!");
                }
            }

            if(!disjunctionRange){
                // this prop is false, therefore the cnf is false
                return false;
            }
        }

        return true;
    }

    private static Object parseValue(final String s){
        if(s == null || s.length() == 0 || !(Character.isDigit(s.charAt(0)) || s.charAt(0) == '-')){
            return s;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            // ok, no double, fine...
        }
        return s;
    }

    /**
     * Make sure that if <code>tokens</code> is a reference or NER class, the matches are evaluated (if they were not already)
     * @param label the label to assign for new matches
     * @param text the content to search in
     * @param script the script
     * @param result already evaluated matches found so far
     * @param operand the pattern to look for (literal, reference or NER)
     * @return all matched ranges
     */
    private List<Range> evalCompareOperands(final String label, Token[] text, Script script, Map<String, List<Range>> result, Token[] operand) {
        if(operand.length != 1){
            // it is not a @reference or NER class
            return ImmutableList.of();
        }

        String word = operand[0].getWord();
        int propSep = word.indexOf('.');
        if(propSep > 0) {
            String firstPart = word.substring(0, propSep);
            if (firstPart.startsWith("@")) {
                // @reference.property
                String ref = firstPart.substring(1);
                if (!result.containsKey(ref)) {
                    // @reference was not evaluated before, let's do that now...
                    List<Range> ranges = new ArrayList<>();
                    for (LabelRule labelRule : script.rules.getOrDefault(ref, ImmutableList.of())) {
                        ranges.addAll(findRule(text, labelRule, script, result));
                    }
                    return ranges;
                } // else, it was already evaluated, so skip
            } else if (StringUtils.isAllUpperCase(firstPart)) {
                // firstPart can be a NER class, e.g. NUMBER that yields properties.
                return findLiteral(text, new Literal(new Token(firstPart)), label, new AtomicInteger(), 0, script.getPlugins(), false);
            }
        }
        return ImmutableList.of();
    }

    /**
     * Gather all matches from result for the given operand
     * @param label the label to assign to the returned matches
     * @param result already evaluated matches found so far
     * @param operand the pattern to look for (literal, reference or NER)
     * @return all matched ranges
     */
    private Map<Object, List<Range>> getAllCompareOperands(String label, Map<String, List<Range>> result, Token[] operand, Token operator) {
        Map<Object, List<Range>> operands = new HashMap<>();
        if(operand.length != 1){
            // it is not a @reference or NER class
            operands.put(parseValue(TokenUtil.toSentenceMaybeNormalized(operand)), ImmutableList.of());
            return operands;
        }

        String a = operand[0].getWord();
        if(a.startsWith("@")){
            // reference
            int propSep = a.indexOf('.');
            String ref = a.substring(1, propSep);
            if(!result.containsKey(ref)) {
                return operands;
            }
            String prop = a.substring(propSep+1, a.length());
            List<Range> ranges = result.get(ref);
            if(ranges == null || ranges.isEmpty())
                return operands;

            // find all
            for (Range range : ranges) {
                Map<String, String> props = range.props;
                if (props.containsKey(prop)) {
                    final Object key = parseValue(props.get(prop));
                    operands.putIfAbsent(key, new ArrayList<>(2));
                    operands.get(key).add(range);

                } else if ("text".equals(prop)) {
                    final Object key = parseValue(range.value);
                    operands.putIfAbsent(key, new ArrayList<>(2));
                    operands.get(key).add(range);
                }
            }
        } else if(StringUtil.startsWithUpperCase(a)) {
            // it can be a NER class, e.g. NUMBER that yields properties
            int propSep = a.indexOf('.');
            if(propSep >= 0){
                String nerType = a.substring(0, propSep).toLowerCase();
                String prop = a.substring(propSep+1, a.length());
                List<Range> ranges = result.get(label);
                if(ranges == null || ranges.isEmpty())
                    return operands;

                // find all
                for (Range range : ranges) {
                    Map<String, String> props = range.props;
                    String val = null;
                    if (Objects.equals(props.get("type"), nerType) && props.containsKey(prop)) {
                        val = props.get(prop);
                    } else if ("text".equals(prop)) {
                        val = range.value;
                    }

                    if(val != null) {
                        // exact version
                        Object key = parseValue(val);
                        operands.putIfAbsent(key, new ArrayList<>(2));
                        operands.get(key).add(range);

                        if(operator.isMatchOnNormalized()) {
                            // normalized version
                            final List<Token> valTokens = new MatchingWordTokenizer().tokenize(val);
                            normalizer.normalizeTokens(valTokens);
                            String normVal = TokenUtil.toSentenceMaybeNormalized(valTokens);
                            operands.putIfAbsent(normVal, new ArrayList<>(2));
                            operands.get(normVal).add(range);
                        }
                    }
                }
            } else {
                // literal value
                operands.put(parseValue(TokenUtil.toSentenceMaybeNormalized(operand)), null);
            }

        } else {
            // literal value
            operands.put(parseValue(TokenUtil.toSentenceMaybeNormalized(operand)), null);
        }

        return operands;
    }

    private List<Range> evalCompare(
            final String label,
            final Token[] text,
            Script script,
            final Map<String, List<Range>> result,
            final Literal compareLit,
            boolean readOnly)
    {
        List<Range> ranges = new ArrayList<>(2);
        final int opIx = compareLit.getMarker();
        Token op = compareLit.tokens[opIx];

        // get operands
        final Token[] leftTokens = ArrayUtil.subArray(compareLit.tokens, 0, opIx);
        final Token[] rightTokens = ArrayUtil.subArray(compareLit.tokens, opIx+1, compareLit.tokens.length);

        if(!readOnly) {
            // make sure references and NERs are evaluated
            List<Range> rangesA = evalCompareOperands(label, text, script, result, leftTokens);
            List<Range> rangesB = evalCompareOperands(label, text, script, result, rightTokens);

            Consumer<Range> resultUpdater = r -> {
                if (!result.containsKey(r.label)) {
                    result.put(r.label, new ArrayList<>(2));
                }
                result.get(r.label).add(r);
            };
            rangesA.forEach(resultUpdater);
            rangesB.forEach(resultUpdater);
        }

        // get all matches for each of the operands
        Map<Object, List<Range>> as = getAllCompareOperands(label, result, leftTokens, op);
        Map<Object, List<Range>> bs = getAllCompareOperands(label, result, rightTokens, op);

        // find all matches that satisfy the comparator
        // todo: null pointer ex. with full range
        //Range someRange = new Range(label, 0, text.length, text[0].getCharStart(), text[text.length - 1].getCharEnd());
        Range someRange = new Range(label, 0, 1, 0, 1);
        // Range someRange = new Range(label, 0, 1, 0, 1);
        for (Object a : as.keySet()) {
            for (Object b : bs.keySet()) {
                if(evalCompare(op.getWord(), a, b)){
                    // match, now get a relevant range...
                    boolean updated = false;
                    List<Range> rs = as.get(a);
                    if(rs != null && !rs.isEmpty()){
                        ranges.addAll(rs);
                        updated = true;
                    }

                    rs = bs.get(b);
                    if(rs != null && !rs.isEmpty()){
                        ranges.addAll(rs);
                        updated = true;
                    }

                    if(!updated){
                        ranges.add(someRange);
                    }
                }
            }
        }
        return ranges;
    }

    private boolean evalCompare(String op, Object a, Object b){
        if(a == null || b == null){
            return false;
        }

        if(a instanceof Double && b instanceof Double){
            switch (op) {
                case "<": return (double)a < (double)b;
                case ">": return (double)a > (double)b;
                case "<=": return (double)a <= (double)b;
                case ">=": return (double)a >= (double)b;
                case "==": return (double)a == (double)b;
                case "!=": return (double)a != (double)b;
            }
        }

        if(a instanceof String && b instanceof String){
            String as = (String) a;
            String bs = (String) b;
            switch (op) {
                // sub string
                case "<": return bs.contains(as);
                case ">": return as.contains(bs);
                // start/end string
                case "<=": return bs.startsWith(as);
                case ">=": return as.startsWith(bs);
                // equals
                case "==": return as.equals(bs);
                case "!=": return !as.equals(bs);
            }
        }

        if(a instanceof String && b instanceof Double){
            double alen = ((String)a).length();
            switch (op) {
                case "<": return alen < (double)b;
                case ">": return alen > (double)b;
                case "<=": return alen <= (double)b;
                case ">=": return alen >= (double)b;
                case "==": return alen == (double)b;
                case "!=": return alen != (double)b;
            }
        }

        if(a instanceof Double && b instanceof String){
            double blen = ((String)b).length();
            switch (op) {
                case "<": return (double)a < blen;
                case ">": return (double)a > blen;
                case "<=": return (double)a <= blen;
                case ">=": return (double)a >= blen;
                case "==": return (double)a == blen;
                case "!=": return (double)a != blen;
            }
        }

        return false;
    }


    private static <V> ArrayList<V> list(V value){
        ArrayList<V> list = new ArrayList<>(1);
        list.add(value);
        return list;
    }

}
