package org.c4i.graph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Vertex with dynamic (lazily initialized) map with named scores.
 * @author Arvid Halma
 * @version 17-9-2017 - 20:04
 */
public class PropVertex {
    final String id;
    private Map<String, Object> props;

    public PropVertex(PropVertex org){
        this.id = org.id;
        this.props = new LinkedHashMap<>(2);
        this.props.putAll(org.getProps());
    }

    public PropVertex(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getProps() {
        if(props == null){
            props = new HashMap<>(2);
        }
        return props;
    }

    public Object get(String key){
        return getProps().get(key);
    }

    public Object getOrDefault(String key, double defaultValue){
        return  getProps().getOrDefault(key, defaultValue);
    }

    public Object put(String key, Double val){
        return getProps().put(key, val);
    }

    public void add(PropVertex scoredVertex) {
        for (Map.Entry<String, Object> entry : scoredVertex.getProps().entrySet()) {
            String key = entry.getKey();
            getProps().put(key, getProps().get(key));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropVertex)) return false;

        PropVertex that = (PropVertex) o;

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
