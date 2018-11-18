package org.c4i.nlp.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.parboiled.common.ImmutableList;

import java.util.List;
import java.util.Objects;

/**
 * Options for compiling and evaluating rule sets.
 * @author Arvid Halma
 * @version 8-6-2017 - 10:30
 */
public class ScriptConfig {

    @JsonProperty
    private List<String> languages = ImmutableList.of("en");

    @JsonProperty
    private Boolean optimizeRuleLogic = true;

    @JsonProperty("rule")
    private RuleProperties ruleProperties = new RuleProperties();



    public List<String> getLanguages() {
        return languages;
    }

    public ScriptConfig setLanguages(List<String> languages) {
        this.languages = languages;
        return this;
    }

    public Boolean getOptimizeRuleLogic() {
        return optimizeRuleLogic;
    }

    public ScriptConfig setOptimizeRuleLogic(Boolean optimizeRuleLogic) {
        this.optimizeRuleLogic = optimizeRuleLogic;
        return this;
    }

    public RuleProperties getRuleProperties() {
        return ruleProperties;
    }

    public ScriptConfig setRuleProperties(RuleProperties ruleProperties) {
        this.ruleProperties = ruleProperties;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScriptConfig that = (ScriptConfig) o;
        return Objects.equals(languages, that.languages) &&
                Objects.equals(optimizeRuleLogic, that.optimizeRuleLogic) &&
                Objects.equals(ruleProperties, that.ruleProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languages, optimizeRuleLogic, ruleProperties);
    }

    @Override
    public String toString() {
        return "ScriptConfig{" +
                "languages=" + languages +
                ", optimizeRuleLogic=" + optimizeRuleLogic +
                ", ruleProperties=" + ruleProperties +
                '}';
    }
}
