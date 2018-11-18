package org.c4i.nlp.detect;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ISO 639-3:2007, Codes for the representation of names of languages – Part 3: Alpha-3 code for comprehensive coverage 
 * of languages, is an international standard for language codes in the ISO 639 series. It defines three-letter codes 
 * for identifying languages. The standard was published by ISO on 1 February 2007.[1]
 *
 * ISO 639-3 extends the ISO 639-2 alpha-3 codes with an aim to cover all known natural languages. The extended language 
 * coverage was based primarily on the language codes used in the Ethnologue (volumes 10-14) published by 
 * SIL International, which is now the registration authority for ISO 639-3.[2] It provides an enumeration of languages 
 * as complete as possible, including living and extinct, ancient and constructed, major and minor, written and 
 * unwritten.[1] However, it does not include reconstructed languages such as Proto-Indo-European.[3]
 *
 *
 * source: http://www-01.sil.org/iso639-3/iso-639-3.tab
 * @author Arvid Halma
 */
public class LanguageISO6393 {

    public static String getName(String code3){
        return m.get(code3)[2];
    }

    public static boolean isCode(String code3){
        return m.containsKey(code3);
    }

    public static Set<String> getAllCode3(){
        return m.keySet();
    }

    public static String toCode2(String code3){
        String[] strings = m.get(code3);
        return strings == null ? null : strings[1];
    }

    private static final Map<String, String[]> m = new HashMap<>();

    private final static String DATA = "aar	aa	Afar\n"+
                    "abk	ab	Abkhazian\n"+
                    "afr	af	Afrikaans\n"+
                    "aka	ak	Akan\n"+
                    "amh	am	Amharic\n"+
                    "ara	ar	Arabic\n"+
                    "arg	an	Aragonese\n"+
                    "asm	as	Assamese\n"+
                    "ava	av	Avaric\n"+
                    "ave	ae	Avestan\n"+
                    "aym	ay	Aymara\n"+
                    "aze	az	Azerbaijani\n"+
                    "bak	ba	Bashkir\n"+
                    "bam	bm	Bambara\n"+
                    "bel	be	Belarusian\n"+
                    "ben	bn	Bengali\n"+
                    "bis	bi	Bislama\n"+
                    "bod	bo	Tibetan\n"+
                    "bos	bs	Bosnian\n"+
                    "bre	br	Breton\n"+
                    "bul	bg	Bulgarian\n"+
                    "cat	ca	Catalan\n"+
                    "ces	cs	Czech\n"+
                    "cha	ch	Chamorro\n"+
                    "che	ce	Chechen\n"+
                    "chu	cu	Church Slavic\n"+
                    "chv	cv	Chuvash\n"+
                    "cor	kw	Cornish\n"+
                    "cos	co	Corsican\n"+
                    "cre	cr	Cree\n"+
                    "cym	cy	Welsh\n"+
                    "dan	da	Danish\n"+
                    "deu	de	German\n"+
                    "div	dv	Dhivehi\n"+
                    "dzo	dz	Dzongkha\n"+
                    "ell	el	Modern Greek (1453-)\n"+
                    "eng	en	English\n"+
                    "epo	eo	Esperanto\n"+
                    "est	et	Estonian\n"+
                    "eus	eu	Basque\n"+
                    "ewe	ee	Ewe\n"+
                    "fao	fo	Faroese\n"+
                    "fas	fa	Persian\n"+
                    "fij	fj	Fijian\n"+
                    "fin	fi	Finnish\n"+
                    "fra	fr	French\n"+
                    "fry	fy	Western Frisian\n"+
                    "ful	ff	Fulah\n"+
                    "gla	gd	Scottish Gaelic\n"+
                    "gle	ga	Irish\n"+
                    "glg	gl	Galician\n"+
                    "glv	gv	Manx\n"+
                    "grn	gn	Guarani\n"+
                    "guj	gu	Gujarati\n"+
                    "hat	ht	Haitian\n"+
                    "hau	ha	Hausa\n"+
                    "hbs	sh	Serbo-Croatian"+
                    "heb	he	Hebrew\n"+
                    "her	hz	Herero\n"+
                    "hin	hi	Hindi\n"+
                    "hmo	ho	Hiri Motu\n"+
                    "hrv	hr	Croatian\n"+
                    "hun	hu	Hungarian\n"+
                    "hye	hy	Armenian\n"+
                    "ibo	ig	Igbo\n"+
                    "ido	io	Ido\n"+
                    "iii	ii	Sichuan Yi\n"+
                    "iku	iu	Inuktitut\n"+
                    "ile	ie	Interlingue\n"+
                    "ina	ia	Interlingua (International Auxiliary Language Association)\n"+
                    "ind	id	Indonesian\n"+
                    "ipk	ik	Inupiaq\n"+
                    "isl	is	Icelandic\n"+
                    "ita	it	Italian\n"+
                    "jav	jv	Javanese\n"+
                    "jpn	ja	Japanese\n"+
                    "kal	kl	Kalaallisut\n"+
                    "kan	kn	Kannada\n"+
                    "kas	ks	Kashmiri\n"+
                    "kat	ka	Georgian\n"+
                    "kau	kr	Kanuri\n"+
                    "kaz	kk	Kazakh\n"+
                    "khm	km	Khmer\n"+
                    "kik	ki	Kikuyu\n"+
                    "kin	rw	Kinyarwanda\n"+
                    "kir	ky	Kirghiz\n"+
                    "kom	kv	Komi\n"+
                    "kon	kg	Kongo\n"+
                    "kor	ko	Korean\n"+
                    "kua	kj	Kuanyama\n"+
                    "kur	ku	Kurdish\n"+
                    "lao	lo	Lao\n"+
                    "lat	la	Latin\n"+
                    "lav	lv	Latvian\n"+
                    "lim	li	Limburgan\n"+
                    "lin	ln	Lingala\n"+
                    "lit	lt	Lithuanian\n"+
                    "ltz	lb	Luxembourgish\n"+
                    "lub	lu	Luba-Katanga\n"+
                    "lug	lg	Ganda\n"+
                    "mah	mh	Marshallese\n"+
                    "mal	ml	Malayalam\n"+
                    "mar	mr	Marathi\n"+
                    "mkd	mk	Macedonian\n"+
                    "mlg	mg	Malagasy\n"+
                    "mlt	mt	Maltese\n"+
                    "mon	mn	Mongolian\n"+
                    "mri	mi	Maori\n"+
                    "msa	ms	Malay (macrolanguage)\n"+
                    "mya	my	Burmese\n"+
                    "nau	na	Nauru\n"+
                    "nav	nv	Navajo\n"+
                    "nbl	nr	South Ndebele\n"+
                    "nde	nd	North Ndebele\n"+
                    "ndo	ng	Ndonga\n"+
                    "nep	ne	Nepali (macrolanguage)\n"+
                    "nld	nl	Dutch\n"+
                    "nno	nn	Norwegian Nynorsk\n"+
                    "nob	nb	Norwegian Bokmål\n"+
                    "nor	no	Norwegian\n"+
                    "nya	ny	Nyanja\n"+
                    "oci	oc	Occitan (post 1500)\n"+
                    "oji	oj	Ojibwa\n"+
                    "ori	or	Oriya (macrolanguage)\n"+
                    "orm	om	Oromo\n"+
                    "oss	os	Ossetian\n"+
                    "pan	pa	Panjabi\n"+
                    "pli	pi	Pali\n"+
                    "pol	pl	Polish\n"+
                    "por	pt	Portuguese\n"+
                    "pus	ps	Pushto\n"+
                    "que	qu	Quechua\n"+
                    "roh	rm	Romansh\n"+
                    "ron	ro	Romanian\n"+
                    "run	rn	Rundi\n"+
                    "rus	ru	Russian\n"+
                    "sag	sg	Sango\n"+
                    "san	sa	Sanskrit\n"+
                    "sin	si	Sinhala\n"+
                    "slk	sk	Slovak\n"+
                    "slv	sl	Slovenian\n"+
                    "sme	se	Northern Sami\n"+
                    "smo	sm	Samoan\n"+
                    "sna	sn	Shona\n"+
                    "snd	sd	Sindhi\n"+
                    "som	so	Somali\n"+
                    "sot	st	Southern Sotho\n"+
                    "spa	es	Spanish\n"+
                    "sqi	sq	Albanian\n"+
                    "srd	sc	Sardinian\n"+
                    "srp	sr	Serbian\n"+
                    "ssw	ss	Swati\n"+
                    "sun	su	Sundanese\n"+
                    "swa	sw	Swahili (macrolanguage)\n"+
                    "swe	sv	Swedish\n"+
                    "tah	ty	Tahitian\n"+
                    "tam	ta	Tamil\n"+
                    "tat	tt	Tatar\n"+
                    "tel	te	Telugu\n"+
                    "tgk	tg	Tajik\n"+
                    "tgl	tl	Tagalog\n"+
                    "tha	th	Thai\n"+
                    "tir	ti	Tigrinya\n"+
                    "ton	to	Tonga (Tonga Islands)\n"+
                    "tsn	tn	Tswana\n"+
                    "tso	ts	Tsonga\n"+
                    "tuk	tk	Turkmen\n"+
                    "tur	tr	Turkish\n"+
                    "twi	tw	Twi\n"+
                    "uig	ug	Uighur\n"+
                    "ukr	uk	Ukrainian\n"+
                    "urd	ur	Urdu\n"+
                    "uzb	uz	Uzbek\n"+
                    "ven	ve	Venda\n"+
                    "vie	vi	Vietnamese\n"+
                    "vol	vo	Volapük\n"+
                    "wln	wa	Walloon\n"+
                    "wol	wo	Wolof\n"+
                    "xho	xh	Xhosa\n"+
                    "yid	yi	Yiddish\n"+
                    "yor	yo	Yoruba\n"+
                    "zha	za	Zhuang\n"+
                    "zho	zh	Chinese\n"+
                    "zul	zu	Zulu\n";


    static {
        for (String line : DATA.split("\n")) {
            String[] split = line.split("\t");
            m.put(split[0], split);
        }
    }

}
