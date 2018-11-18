package org.c4i.util;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Turns <code>Iterator A</code> into <code>Iterator B</code>
 * @author Arvid Halma
 * @version 5-9-2017 - 20:40
 */
public class MappingIterator<A,B> implements Iterator<B> {
    private Iterator<A> as;
    private Function<A, B> mapping;


    public MappingIterator(Iterator<A> as, Function<A, B> mapping) {
        this.as = as;
        this.mapping = mapping;
    }

    @Override
    public boolean hasNext() {
        return as.hasNext();
    }

    @Override
    public B next() {
        return mapping.apply(as.next());
    }
}
