package org.c4i.nlp.match;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.c4i.util.RegexUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A basis for rules in a script
 * @author Arvid Halma
 */
public abstract class Rule {

    protected int line;
    protected LinkedHashMap<String, Object> props;

    public Rule() {
        line = 1;
        props = new LinkedHashMap<>(2);
    }

    public abstract Map<String, Set<String>> validProps();

    public boolean isValidProp(String key) {
        return validProps().containsKey(key);
    }

    public boolean isValidPropValue(String key, Object value) {
        Set<String> vals = validProps().get(key);
        if(vals == null)
            return value == null;
        if(vals.isEmpty()){
            return value != null; // any non-null value is ok
        }
        if(vals.contains("NUMBER") && value instanceof Integer){
            return true;
        }
        if(vals.contains("LIST") && value instanceof List){
            return true;
        }
        if(vals.contains("MAP") && value instanceof Map){
            return true;
        }
        if(vals.contains("STRING") && value instanceof String){
            return true;
        }

        return vals.contains(value.toString());
    }

    public Set<String> validPropValues(String key) {
        return validProps().get(key);
    }

    public Object getProp(String key){
        return props.get(key);
    }

    public Object getProp(String key, Map<String, Object> backup){
        return props.getOrDefault(key, backup != null ? backup.get(key) : null);
    }

    public String getStringProp(String key){
        Object obj = props.get(key);
        return obj == null ? null : obj.toString();
    }

    public String getStringProp(String key, Map<String, Object> backup){
        Object obj = props.get(key);
        if(obj == null){
            if(backup != null){
                obj = backup.get(key);
            }
        }
        return obj == null ? null : obj.toString();
    }

    public Rule setProp(String key, Object value){
        props.put(key, value);
        return this;
    }

    public Rule setProp(String key, boolean value){
        if(value){
            setProp(key);
        } else {
            props.remove(key);
        }
        return this;
    }

    public Rule setProp(String key){
        return setProp(key, null);
    }

    public boolean isProp(String key){
        return props.containsKey(key);
    }

    public boolean isProp(String key, Map<String, Object> backup){
        return props.containsKey(key) || (backup != null && backup.containsKey(key));
    }

    public boolean isProp(String key, Object value){
        return Objects.equals(props.get(key), value);
    }

    public boolean isProp(String key, Object value, Map<String, Object> backup){
        return Objects.equals(props.get(key), value) || (backup != null && Objects.equals(backup.get(key), value));
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public LinkedHashMap<String, Object> getProps() {
        return props;
    }

    public void setProps(LinkedHashMap<String, Object> props) {
        this.props = props;
    }

    private static String propertyString(Map<String, Object> props){
        if(props == null || props.isEmpty())
            return "";
        return props.entrySet().stream().map(entry -> {
            String k = entry.getKey();
            Object v = entry.getValue();
            if(v == null)
                return k;
            if(v instanceof Map)
                return k + ": " + propertyString((Map<String,Object>) v);
            else
                return k + ": " + v;
        }).collect(Collectors.joining(", ", "{", "} "));
    }

    public String propertyString(){
        return propertyString(props);
    }

}
