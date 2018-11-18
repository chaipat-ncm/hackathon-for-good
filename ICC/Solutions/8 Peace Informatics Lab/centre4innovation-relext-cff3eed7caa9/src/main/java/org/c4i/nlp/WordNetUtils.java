package org.c4i.nlp;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This is a wrapper for the MIT WordNet inteface that simplifies basic operations
 * such as retrieving synonyms and hypernyms for a word.
 *
 * @author dmitriy dligach
 *
 */
public class WordNetUtils {

    public static final String wordNetPath = "data/nlp/en/wordnet/3.0/dict";
    private static IDictionary iDictionary;

    /**
     * A simple way to get the head word of a phrase.
     * @param s word
     * @return last word
     */
    public static String getHeadWord(String s) {

        String[] elements = s.split(" ");
        return elements[elements.length - 1];
    }

    /**
     * Initialize WordNet dictionary.
     * @return IDictionary loaded instance
     */
    public static IDictionary getDictionary() {
        try {
            if(iDictionary == null) {
                URL url = new URL("file", null, wordNetPath);
                iDictionary = new Dictionary(url);
                iDictionary.open();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return iDictionary;
    }

    public static WordTest getWordTest(){
        return getWordTest(getDictionary());
    }

    public static WordTest getWordTest(IDictionary dictionary){
        return string -> dictionary != null && (
                dictionary.getIndexWord(string, POS.NOUN) != null ||
                dictionary.getIndexWord(string, POS.VERB) != null ||
                dictionary.getIndexWord(string, POS.ADJECTIVE) != null ||
                dictionary.getIndexWord(string, POS.ADVERB) != null);
    }

    /**
     * Get a list of possible stems. Assume we are looking up a noun.
     * @param word word
     * @param posTag tag
     * @param iDictionary dictionary
     * @return possible stems
     */
    public static synchronized List<String> getStems(String word, String posTag, IDictionary iDictionary) {

        POS pos = POS.getPartOfSpeech(posTag.charAt(0));
        if (pos == null) {
            return new ArrayList<String>();
        }

        WordnetStemmer wordnetStemmer = new WordnetStemmer(iDictionary);
        List<String> stems = wordnetStemmer.findStems(word, pos);

        return stems;
    }

    /**
     * Retrieve a set of synonyms for a word. Use only the first sense if useFirstSense flag is true.
     * @param iDictionary dictionary instance
     * @param word word
     * @param posTag tag
     * @param firstSenseOnly include only some synonyms
     * @return set of synonms
     */
    public static synchronized HashSet<String> getSynonyms(IDictionary iDictionary, String word, String posTag, boolean firstSenseOnly) {

        // need a set to avoid repeating words
        HashSet<String> synonyms = new HashSet<String>();

        POS pos = POS.getPartOfSpeech(posTag.charAt(0));
        if (pos == null) {
            return synonyms;
        }

        IIndexWord iIndexWord = iDictionary.getIndexWord(word, pos);
        if (iIndexWord == null) {
            return synonyms; // no senses found
        }

        // iterate over senses
        for (IWordID iWordId : iIndexWord.getWordIDs()) {
            IWord iWord = iDictionary.getWord(iWordId);

            ISynset iSynset = iWord.getSynset();
            for (IWord synsetMember : iSynset.getWords()) {
                synonyms.add(synsetMember.getLemma());
            }

            if (firstSenseOnly) {
                break;
            }
        }

        return synonyms;
    }


    /**
     * Retrieve a set of hypernyms for a word. Use only the first sense if useFirstSense flag is true.
     * @param word word
     * @return set of hypernyms
     */
    public static HashSet<String> getHypernyms(String word) {
        HashSet<String> hypernyms = getHypernyms(getDictionary(), word, "n", true);
        if(hypernyms.isEmpty()) {
            hypernyms.addAll(getHypernyms(getDictionary(), word, "v", true));
        }
        return hypernyms;

    }

    /**
     * Retrieve a set of hypernyms for a word. Use only the first sense if useFirstSense flag is true.
     *
     * @param dict dictionary
     * @param word word
     * @return set of hypernyms
     */
    public static HashSet<String> getHypernyms(IDictionary dict, String word) {
        HashSet<String> hypernyms = getHypernyms(dict, word, "n", true);
        if(hypernyms.isEmpty()) {
            hypernyms.addAll(getHypernyms(dict, word, "v", true));
        }
        return hypernyms;

    }

    /**
     * Retrieve a set of hypernyms for a word. Use only the first sense if useFirstSense flag is true.
     * @param dict dictionary
     * @param word word
     * @param posTag tag
     * @param firstSenseOnly include only some hypernyms
     * @return set of hypernyms
     */
    public static synchronized HashSet<String> getHypernyms(IDictionary dict, String word, String posTag, boolean firstSenseOnly) {

        HashSet<String> hypernyms = new HashSet<String>();

        POS pos = POS.getPartOfSpeech(posTag.charAt(0));
        if (pos == null) {
            return hypernyms;
        }

        IIndexWord iIndexWord = dict.getIndexWord(word, pos);
        if (iIndexWord == null) {
            return hypernyms; // no senses found
        }

        // iterate over senses
        for (IWordID iWordId : iIndexWord.getWordIDs()) {
            IWord iWord1 = dict.getWord(iWordId);
            ISynset iSynset = iWord1.getSynset();

            // multiple hypernym chains are possible for a synset
            for (ISynsetID iSynsetId : iSynset.getRelatedSynsets(Pointer.HYPERNYM)) {
                List<IWord> iWords = dict.getSynset(iSynsetId).getWords();
                for (IWord iWord2 : iWords) {
                    String lemma = iWord2.getLemma();
                    hypernyms.add(lemma.replace(' ', '_')); // also get rid of spaces
                }
            }

            if (firstSenseOnly) {
                break;
            }
        }

        return hypernyms;
    }


    public static HashSet<String> getHyperHypernyms(String word) {
        HashSet<String> hypernyms = getHyperHypernyms(getDictionary(), word, "n", true);
        if(hypernyms.isEmpty()) {
            hypernyms.addAll(getHyperHypernyms(getDictionary(), word, "v", true));
        }
        return hypernyms;

    }

    public static synchronized HashSet<String> getHyperHypernyms(IDictionary dict, String word, String posTag, boolean firstSenseOnly) {

        HashSet<String> hypernyms = new HashSet<String>();

        POS pos = POS.getPartOfSpeech(posTag.charAt(0));
        if (pos == null) {
            return hypernyms;
        }

        IIndexWord iIndexWord = dict.getIndexWord(word, pos);
        if (iIndexWord == null) {
            return hypernyms; // no senses found
        }

        // iterate over senses
        for (IWordID iWordId : iIndexWord.getWordIDs()) {
            IWord iWord1 = dict.getWord(iWordId);
            ISynset iSynset = iWord1.getSynset();

            for (ISynsetID iSynsetId1 : iSynset.getRelatedSynsets(Pointer.HYPERNYM)) {
                for (ISynsetID iSynsetId2 : dict.getSynset(iSynsetId1).getRelatedSynsets(Pointer.HYPERNYM)) {
                    List<IWord> iWords = dict.getSynset(iSynsetId2).getWords();
                    for (IWord iWord2 : iWords) {
                        String lemma = iWord2.getLemma();
                        hypernyms.add(lemma.replace(' ', '_')); // also get rid of spaces
                    }
                }
            }

            if (firstSenseOnly) {
                break;
            }
        }

        return hypernyms;
    }
}