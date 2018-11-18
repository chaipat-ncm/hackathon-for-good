package org.c4i.chitchat;

import com.google.common.collect.ImmutableList;
import org.c4i.util.ReverseIterator;
import org.junit.Test;

/**
 * @author Arvid Halma
 * @version 15-7-2017 - 21:45
 */
public class ReverseIteratorTest {

    @Test
    public void testRevere(){
        for (Integer integer : new ReverseIterator<>(ImmutableList.of(1, 2, 3))) {
            System.out.println("integer = " + integer);
        }
    }

    @Test
    public void testEmpty(){
        for (Object integer : new ReverseIterator<>(ImmutableList.of())) {
            System.out.println("integer = " + integer);
        }
    }
}
