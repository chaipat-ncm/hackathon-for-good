package org.c4i.nlp.ner;

import org.c4i.nlp.match.Range;
import org.c4i.nlp.tokenize.Token;

import java.util.List;

/**
 * Named Entity Recognition
 * The Name Finder can detect named entities and numbers in text.
 * @author Arvid Halma
 * @version 1-8-17
 */
public interface NamedEntityRecognition {

    List<Range> find(List<Token> tokens);

}
