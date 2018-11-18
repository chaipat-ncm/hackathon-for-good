package org.c4i.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Iterates backward over a list
 * @author Arvid Halma
 * @version 14-7-2017 - 16:11
 */
public class ReverseIterator<T> implements Iterator<T>, Iterable<T> {

    private final ListIterator<T> backward;

    public ReverseIterator(List<T> list) {
        this.backward = list.listIterator(list.size());
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return backward.hasPrevious();
    }

    @Override
    public T next() {
        return backward.previous();
    }

    @Override
    public void remove() {
        backward.remove();
    }

}