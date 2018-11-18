package org.c4i.chitchat;


import org.c4i.nlp.tokenize.OpenSentenceSplitter;
import org.c4i.nlp.tokenize.RegexSentenceSplitter;
import org.c4i.nlp.tokenize.SentenceSplitter;

import java.io.File;
import java.io.IOException;

/**
 * @author Arvid
 * @version 6-4-2015 - 12:06
 */
public class SentenceSplitterDemo {
    public static void main(String[] args) throws IOException {
        String text;
        OpenSentenceSplitter openSentenceSplitter = new OpenSentenceSplitter(new File("data/nlp/en/en-sent.bin"));
        RegexSentenceSplitter regexSentenceSplitter = new RegexSentenceSplitter();

        text = "Mr. Smith bought cheapsite.com for 1.5 million dollars, i.e. he paid a lot for it. Did he mind? Adam Jones Jr. thinks he didn't. In any case, this isn't true... Well, with a probability of .9 it isn't.";
        test(text, regexSentenceSplitter);
        test(text, openSentenceSplitter);

        text = "It works with exclamation marks as well!! Yeaaaah! really, you see?";
        test(text, regexSentenceSplitter);
        test(text, openSentenceSplitter);

        text = "2 they came to kill they came to kill 3 \n" +
                "\n" +
                "They came To kill\n" +
                "\n" +
                "Weapons confiscated from anti-balaka and ex-Seleka fighters by African \n" +
                "Union peacekeepers in the town of Bossangoa. December 10, 2013. \n";
        test(text, regexSentenceSplitter);
        test(text, openSentenceSplitter);

        text = "قالت مصادر في مكتب المدعي العام التركي إن النيابة العامة أوقفت عملية البحث عن جثة خاشقجي، بعد أكثر من شهر على قتله في قنصلية بلاده في إسطنبول.\n" +
                "\n" +
                "وقالت تلك المصادر إن كل نقاط المراقبة والبحث عن جثة خاشقجي التي وضعتها السلطات التركية أصبحت لا قيمة لها، بعد وصول المحققين الأتراك إلى قناعة تامة بأن الجثة تم التخلص منها بإذابتها بالكامل بواسطة أحماض كيميائية.\n" +
                "\n" +
                "وأفادت المصادر ذاتها أن الكيميائي أحمد الجنوبي وخبير السموم خالد الزهراني كانا ضمن فريق التحقيق السعودي المشترك الذي تشكل الشهر الماضي مع فريق تحقيق تركي، للبحث في مصير خاشقجي، وأنهما قاما بطمس الأدلة على مدى سبعة أيام.";
        test(text, regexSentenceSplitter);
        test(text, openSentenceSplitter);

    }

    public static void test(String text, SentenceSplitter splitter){


        System.out.println();
        System.out.println("===== " + splitter + " ======");
        for (String sentence : splitter.split(text)) {
            System.out.println("sentence = " + sentence);
        }
    }
}
