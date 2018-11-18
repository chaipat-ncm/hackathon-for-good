/**
 Kstem is a morphological analyzer that reduces morphological variants to a
 root form.  For example, `elephants'-&gt;`elephant', `amplification'-&gt;`amplify',
 and `european'-&gt;`europe'.  This type of reduction is the norm in information
 retrieval, and is referred to as "stemming".  Unlike previous stemmers, Kstem
 tries to avoid conflating variants that have different meanings.  For example
 `memorial' is related to `memory', and `memorize' is also related to `memory',
 but reducing those variants to `memory' would also conflate `memorial' with
 `memorize'.  For more information, please see "Viewing Morphology as an Inference
 Process", by Robert Krovetz, Proceedings of the ACM-SIGIR Conference on Research
 and Development in Information Retrieval, pp. 191-202, 1993.

 The primary advantage of Kstem over previous stemmers (e.g, the
 Porter stemmer) is that it returns words instead of truncated word forms.
 It is also much easier to modify, and very flexible.  It will allow any
 word form to be kept as is, and it will allow any word form to be conflated
 to any other word form.  In general, Kstem requires a word to be in the
 lexicon (the basic list of words that the system knows about) before it
 will reduce one word form to another.  Some endings are highly productive,
 and Kstem will remove them even if the root form is not found in the
 lexicon (e.g., `-ness', `-ly').  If a variant is explicitly mentioned in
 the lexicon, it is assumed to be unrelated to the (presumed) root, and
 the conflation is not done.  For example, the lexicon needs to contain
 the word `curly', or it would be reduced to the presumed root, `cur'.
 Similarly, the word `factorial' needs to be included, or it would be
 reduced to `factory' (by analogy to `matrimonial'-&gt;`matrimony').  In
 contrast, we want to allow `immunity'-&gt;`immune', but avoid reducing
 `station'-&gt;`state', or `authority'-&gt;`author'.  This is done by making
 sure that the root form (`immune') is mentioned in the lexicon, and
 omitting any variant (`immunity') that you want to be related to that root.

 There are instances in which we need an explicit mention of which variant
 is related to a root.  For example, irregular morphology (`matrices'-&gt;`matrix')
 or cases in which the reduction would not be permitted due to length
 restrictions (`doing'-&gt;`do').  A direct-conflation file is used to allow
 these reductions to be performed.  This file can also be used to over-ride
 the normal operation of the system.  That is, if the user wants one word
 form to be reduced to another, this file can be used to "hard-wire" that
 result.  It consists of a list of pairs between the original word form and
 the word form that results.  This file should be used with care - it is
 possible to repeatedly stem a file and obtain different results than
 stemming it just once (in general, once a file is stemmed, stemming it
 again will not change it).

 See: http://lexicalresearch.com/kstem-doc.txt
 */
package org.c4i.nlp.normalize.kstem;