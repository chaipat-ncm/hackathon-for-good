package org.c4i.graph;


import dk.aaue.sna.alg.centrality.BrandesBetweennessCentrality;
import dk.aaue.sna.alg.centrality.CentralityResult;
import dk.aaue.sna.alg.centrality.OrtizArroyoEntropyCentrality;
import dk.aaue.sna.generate.ErdosRenyiGraphGenerator;
import dk.aaue.sna.util.StringContinousFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Arvid Halma
 * @version 16-8-2017 - 12:23
 */
public class SNATest {

    public static void main(String[] args) {
        /*Graph<String, Defaultedge> G = buildGraph();
        CentralityMeasure<String> cm = new dk.aaue.sna.alg.centrality.OrtizArroyoEntropyCentrality(G);
        CentralityResult<String> cr = cm.calculateCentrality();

// assuming your graph has a node "me"
        System.out.println("centrality of 'me' = " + cr.get("me"));
        System.out.println("rank of 'me'       = " + cr.getSortedNodes().indexOf("me"));*/
        WeightedGraph<String, DefaultWeightedEdge> G = generate(new ErdosRenyiGraphGenerator(50, 0.10, null));

        CentralityResult c = new OrtizArroyoEntropyCentrality(G).calculate();
        List<String> sorted = c.getSortedNodes();
        System.out.println("Entropy: c = " + c + ", sorted = " + sorted);

        c = new BrandesBetweennessCentrality(G).calculate();
        sorted = c.getSortedNodes();
        System.out.println("Entropy: c = " + c + ", sorted = " + sorted);
    }

    public static WeightedGraph<String, DefaultWeightedEdge>  getGraph(){
        return generate(new ErdosRenyiGraphGenerator(50, 0.10, null));
    }

    public static WeightedGraph<String, DefaultWeightedEdge> emptyWeighted() {
        return new ListenableUndirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    }

    public static StringContinousFactory stringFactory(String... optionalPrefix) {
        if (optionalPrefix != null && optionalPrefix.length > 0)
            return new StringContinousFactory(optionalPrefix[0], 1);
        else
            return new StringContinousFactory();
    }

    public static WeightedGraph<String, DefaultWeightedEdge> generate(GraphGenerator generator) {
        WeightedGraph<String, DefaultWeightedEdge> g = emptyWeighted();
        Map<String, String> m = new HashMap();
        generator.generateGraph(g, stringFactory(), m);
        return g;
    }
}
