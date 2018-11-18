package org.c4i.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Weighted edge that exposes source, target and weight.
 * @author Arvid Halma
 * @version 26-7-17
 */
public class WeightedEdge extends DefaultWeightedEdge {

    public WeightedEdge() {
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
}
