package org.c4i.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Weighted edge that exposes source, target and weight.
 * @author Arvid Halma
 * @version 26-7-17
 */
public class PropWeightedEdge extends DefaultWeightedEdge {
    private Map<String, Object> props = new LinkedHashMap<>();

    public PropWeightedEdge() {
        super();
    }

    @Override
    public double getWeight() {
        return super.getWeight();
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    @Override
    public Object getTarget() {
        return super.getTarget();
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public PropWeightedEdge setProps(Map<String, Object> props) {
        this.props = props;
        return this;
    }

    public PropWeightedEdge put(String key, Object val){
        if(props == null)
            props = new LinkedHashMap<>();

        props.put(key, val);
        return this;
    }

}
