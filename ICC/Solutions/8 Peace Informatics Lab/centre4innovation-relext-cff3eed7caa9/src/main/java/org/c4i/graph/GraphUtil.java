package org.c4i.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convenience functions for graphs
 * @author Arvid Halma
 */
public class GraphUtil {

    public static void removeReflexiveEdges(Graph<?, WeightedEdge> g){
        List<WeightedEdge> reflexive = g.edgeSet().stream().filter(e -> e.getSource().equals(e.getTarget())).collect(Collectors.toList());
        g.removeAllEdges(reflexive);
    }

    public static <V,E> E addEdge(Graph<V,E> graph, V a, V b){
        if(!graph.vertexSet().contains(a)){
            graph.addVertex(a);
        }

        if(!graph.vertexSet().contains(b)){
            graph.addVertex(b);
        }

        return graph.addEdge(a, b);
    }

    public static <V,E> E ensureEdge(Graph<V,E> graph, V a, V b){
        E edge = graph.getEdge(a, b);

        if(edge != null){
            return edge;
        } else {
            return addEdge(graph, a, b);
        }
    }

    public static <V,E extends WeightedEdge> E addEdge(WeightedGraph<V,E> graph, V a, V b, double w){
        E edge = addEdge(graph, a, b);
        graph.setEdgeWeight(edge, w);
        return edge;
    }

    public static <V,E extends WeightedEdge> E updateEdge(WeightedGraph<V,E> graph, V a, V b, double w){
        E edge = ensureEdge(graph, a, b);
        graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + w);
        return edge;
    }

    public static <V,E extends WeightedEdge> E updateEdge(WeightedGraph<V,E> graph, V a, V b){
        return updateEdge(graph, a, b, 1.0);
    }

    public static <V,E> void merge(Graph<V,E> g, Graph<V,E> h){
        for (E e : h.edgeSet()) {
            ensureEdge(g, g.getEdgeSource(e), g.getEdgeTarget(e));
        }
    }

    public static void removeUnconnectedVertices(DirectedGraph<ScoredVertex, WeightedEdge> g){
        List<ScoredVertex> solitair = g.vertexSet().stream().filter(v -> g.inDegreeOf(v) + g.outDegreeOf(v) == 0).collect(Collectors.toList());
        g.removeAllVertices(solitair);
    }

    public static void removeUnconnectedVertices(WeightedPseudograph<ScoredVertex, WeightedEdge> g){
        List<ScoredVertex> solitair = g.vertexSet().stream().filter(v -> g.edgesOf(v).size() == 0).collect(Collectors.toList());
        g.removeAllVertices(solitair);
    }

    public static int heightOutgoing(DirectedGraph<ScoredVertex, WeightedEdge> g, ScoredVertex v, Set<ScoredVertex> visited, int init){
        Set<WeightedEdge> childSet = g.outgoingEdgesOf(v);
        if (childSet == null || childSet.isEmpty()) {
            return init;
        } else {
            return childSet.stream().mapToInt(e -> {
                ScoredVertex target = g.getEdgeTarget(e);
                if(visited.contains(target)){
                    return init;
                }
                visited.add(target);
                return heightOutgoing(g, target, visited, init + 1);
            }).max().orElse(0);
        }
    }

    public static int heightIncoming(DirectedGraph<ScoredVertex, WeightedEdge> g, ScoredVertex v, int init){
        Set<WeightedEdge> childSet = g.incomingEdgesOf(v);
        if (childSet == null || childSet.isEmpty()) {
            return init;
        } else {
            return childSet.stream().mapToInt(e -> heightIncoming(g, g.getEdgeSource(e), init + 1)).max().orElse(0);
        }
    }

}
