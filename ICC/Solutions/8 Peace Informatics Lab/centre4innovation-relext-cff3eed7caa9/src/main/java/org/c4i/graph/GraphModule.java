package org.c4i.graph;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jgrapht.Graph;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * JSON Serializer for JGraphT graphs.
 * @author Arvid Halma
 * @version 26-7-17
 */
public class GraphModule extends SimpleModule {
    private static final String NAME = "GraphModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil() {};

    public GraphModule() {
        super(NAME, VERSION_UTIL.version());
        //noinspection unchecked
//        addSerializer(ScoredVertex.class, new ScoredVertexSerializer());
        addSerializer(PropVertex.class, new PropVertexSerializer());
//        addSerializer(WeightedEdge.class, new WeightedEdgeSerializer());
        addSerializer(PropWeightedEdge.class, new PropWeightedEdgeSerializer());
//        addSerializer((Class<Graph<ScoredVertex, WeightedEdge>>)(Class<?>)Graph.class, new GraphSerializer());
        addSerializer((Class<Graph<PropVertex, PropWeightedEdge>>)(Class<?>)Graph.class, new PropGraphSerializer());
    }

    /**
     * Graph to JSON
     */
    public static class ScoredVertexSerializer extends JsonSerializer<ScoredVertex> {
        @Override
        public void serialize(ScoredVertex vertex, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeStringField("id", vertex.id);
            for (Map.Entry<String, Double> entry : vertex.getProps().entrySet()) {
                jgen.writeNumberField(entry.getKey(), entry.getValue());
            }
            jgen.writeEndObject();
        }
    }
    /**
     * Graph to JSON
     */
    public static class WeightedEdgeSerializer extends JsonSerializer<WeightedEdge> {
        @Override
        public void serialize(WeightedEdge edge, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeStringField("a", ((ScoredVertex) edge.getSource()).getId());
            jgen.writeStringField("b", ((ScoredVertex) edge.getTarget()).getId());
            jgen.writeNumberField("w", edge.getWeight());
            jgen.writeEndObject();
        }
    }

    /**
     * Graph to JSON
     */
    public static class PropVertexSerializer extends JsonSerializer<PropVertex> {
        @Override
        public void serialize(PropVertex vertex, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeStringField("id", vertex.id);
            for (Map.Entry<String, Object> entry : vertex.getProps().entrySet()) {
                jgen.writeStringField(entry.getKey(), entry.getValue().toString());
            }
            jgen.writeEndObject();
        }
    }
    /**
     * Graph to JSON
     */
    public static class PropWeightedEdgeSerializer extends JsonSerializer<PropWeightedEdge> {
        @Override
        public void serialize(PropWeightedEdge edge, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();
            jgen.writeStringField("a", ((PropVertex) edge.getSource()).getId());
            jgen.writeStringField("b", ((PropVertex) edge.getTarget()).getId());
            jgen.writeNumberField("w", edge.getWeight());
            for (Map.Entry<String, Object> entry : edge.getProps().entrySet()) {
                jgen.writeStringField(entry.getKey(), entry.getValue().toString());
            }

            jgen.writeEndObject();
        }
    }

    /**
     * Graph vertex to JSON
     */
    public static class GraphSerializer extends JsonSerializer<Graph<ScoredVertex, WeightedEdge>> {
        @Override
        public void serialize(Graph<ScoredVertex, WeightedEdge> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

            jgen.writeStartObject();
            jgen.writeArrayFieldStart("nodes");
            for (ScoredVertex vertex : value.vertexSet()) {
                jgen.writeStartObject();
                jgen.writeStringField("id", vertex.id);
                for (Map.Entry<String, Double> entry : vertex.getProps().entrySet()) {
                    jgen.writeNumberField(entry.getKey(), entry.getValue());
                }
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
            jgen.writeArrayFieldStart("edges");
            for (WeightedEdge edge : value.edgeSet()) {
                jgen.writeStartObject();
                jgen.writeStringField("a", ((ScoredVertex) edge.getSource()).getId());
                jgen.writeStringField("b", ((ScoredVertex) edge.getTarget()).getId());
                jgen.writeNumberField("w", edge.getWeight());
                jgen.writeEndObject();

            }
            jgen.writeEndArray();
            jgen.writeEndObject();
        }
    }

    /**
     * Graph vertex to JSON
     */
    public static class PropGraphSerializer extends JsonSerializer<Graph<PropVertex, PropWeightedEdge>> {
        @Override
        public void serialize(Graph<PropVertex, PropWeightedEdge> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

            jgen.writeStartObject();
            jgen.writeArrayFieldStart("nodes");
            for (PropVertex vertex : value.vertexSet()) {
                jgen.writeStartObject();
                jgen.writeStringField("id", vertex.id);
                for (Map.Entry<String, Object> entry : vertex.getProps().entrySet()) {
                    jgen.writeStringField(entry.getKey(), Objects.toString(entry.getValue()));
                }
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
            jgen.writeArrayFieldStart("edges");
            for (PropWeightedEdge edge : value.edgeSet()) {
                jgen.writeStartObject();
                jgen.writeStringField("a", ((PropVertex) edge.getSource()).getId());
                jgen.writeStringField("b", ((PropVertex) edge.getTarget()).getId());
                jgen.writeNumberField("w", edge.getWeight());
                for (Map.Entry<String, Object> entry : edge.getProps().entrySet()) {
                    jgen.writeStringField(entry.getKey(), Objects.toString(entry.getValue()));
                }
                jgen.writeEndObject();

            }
            jgen.writeEndArray();
            jgen.writeEndObject();
        }
    }
}