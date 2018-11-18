package org.c4i.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.c4i.nlp.match.ScriptConfig;

import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * @author Arvid Halma
 * @version 14-2-18
 */
public class YamlDemo {
    public static void main(String[] args) {
        read("a");
        read("a: 4");
        read("{a: 4}");
        read("root : {a: 4}");
        read("{a: b, c: 423}");
        read("{a: b, e, c: 423}");





    }

    public static void read(String s){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try{

            System.out.println();
            System.out.println(s);
            System.out.println("====");
            System.out.println(mapper.readTree(s));
            System.out.println(mapper.readValue(s, LinkedHashMap.class));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
