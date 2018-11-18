package org.c4i.nlp.chat;

import org.c4i.nlp.Nlp;
import org.c4i.nlp.match.Compiler;
import org.c4i.nlp.match.Eval;
import org.c4i.nlp.match.Result;
import org.c4i.nlp.match.Script;

import java.util.List;

/**
 * A bot that uses a {@link Script} to define its replies.
 * @author Arvid Halma
 * @version 9-8-17
 */
public class ScriptBot implements ChatBot{
    Script script;
    Nlp nlp;

    public ScriptBot(Script script, Nlp nlp) {
        this.script = script;
        this.nlp = nlp;
    }

    @Override
    public Result reply(Conversation conversation) {
        return new Eval(nlp).reply(script, conversation);
    }
}
