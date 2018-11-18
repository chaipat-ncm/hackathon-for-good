package org.c4i.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Vertex with dynamic (lazily initialized) map with named scores.
 * @author Arvid Halma
 * @version 17-9-2017 - 20:04
 */
public class ScoredVertex {
    final String id;
    private Map<String, Double> props;

    public ScoredVertex(ScoredVertex org){
        this.id = org.id;
        this.props = new HashMap<>(2);
        this.props.putAll(org.getProps());
    }

    public ScoredVertex(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Map<String, Double> getProps() {
        if(props == null){
            props = new HashMap<>(2);
        }
        return props;
    }

    public Double get(String key){
        return getProps().get(key);
    }

    public Double getOrDefault(String key, double defaultValue){
        return  getProps().getOrDefault(key, defaultValue);
    }

    public Double put(String key, Double val){
        return getProps().put(key, val);
    }

    public void add(ScoredVertex scoredVertex) {
        for (Map.Entry<String, Double> entry : scoredVertex.getProps().entrySet()) {
            String key = entry.getKey();
            getProps().put(key, entry.getValue() + getProps().getOrDefault(key, 0.0));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoredVertex)) return false;

        ScoredVertex that = (ScoredVertex) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ScoredVertex{" + "id='" + id + '\'' +
                ", props=" + props +
                '}';
    }
}
