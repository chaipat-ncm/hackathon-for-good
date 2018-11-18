package org.c4i.util;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Some more additional Array utilities.
 * Mostly for insertion deletion of elements.
 *
 * @see Arrays
 * @author Arvid Halma
 *         Date: 1-sep-2008
 *         Time: 14:08:06
 */
public class ArrayUtil {

    /**
     * Get first element
     * @param xs array
     * @param <T> element type
     * @return the element or null
     */
    public static <T> T first(T[] xs){
      return xs == null || xs.length == 0 ? null : xs[0];
    }

    /**
     * Get last element
     * @param xs array
     * @param <T> element type
     * @return the element or null
     */
    public static <T> T last(T[] xs){
      return xs == null || xs.length == 0 ? null : xs[xs.length-1];
    }

    public static <T> T[] unique(T[] xs){
        LinkedHashSet<T> set = new LinkedHashSet<>(Arrays.asList(xs));
        return (T[]) set.toArray();
    }

    public static double[][] unique(double[][] xs){
        List<double[]> doubles = Arrays.asList(xs);
        Set<double[]> set = new LinkedHashSet<>(doubles);
        double[][] result = new double[set.size()][];
        int i = 0;
        for (double[] x : set) {
            result[i++] = x;
        }
        return result;
    }

    /**
     * Sort two arrays in parralel, by using the order of the first one
     * @param xs array main array
     * @param index array auxiliary array
     */
    public static void quicksort(float[] xs, int[] index) {
        quicksort(xs, index, 0, index.length - 1);
    }

    // quicksort a[left] to a[right]
    public static void quicksort(float[] a, int[] index, int left, int right) {
        if (right <= left) return;
        int i = partition(a, index, left, right);
        quicksort(a, index, left, i-1);
        quicksort(a, index, i+1, right);
    }

    // partition a[left] to a[right], assumes left < right
    private static int partition(float[] a, int[] index,
    int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (less(a[++i], a[right]))      // find item on left to swap
                ;                               // a[right] acts as sentinel
            while (less(a[right], a[--j]))      // find item on right to swap
                if (j == left) break;           // don't go out-of-bounds
            if (i >= j) break;                  // check if pointers cross
            exch(a, index, i, j);               // swap two elements into place
        }
        exch(a, index, i, right);               // swap with partition element
        return i;
    }

    // is x < y ?
    private static boolean less(float x, float y) {
        return (x < y);
    }

    // exchange a[i] and a[j]
    private static void exch(float[] a, int[] index, int i, int j) {
        float swap = a[i];
        a[i] = a[j];
        a[j] = swap;
        int b = index[i];
        index[i] = index[j];
        index[j] = b;
    }

    
    /**
     * Proper way to make a new instance of a generic array with the same
     * type as 'prototype'.
     * @param <T> element type
     * @param prototype the example type
     * @param length the length of the newly created array
     * @return a new array
     */
    public static <T> T[] newInstance(T[] prototype, int length){
      return (T[])Array.newInstance(prototype.getClass().getComponentType(), length);
    }

    /**
     * Add b to the end of the array.
     * @param as original array.
     * @param b element to add at the end
     * @return new array with length: as.length+1
     */
    public static int[] append(int[] as, int b){
        int[] copy = new int[as.length+1];
        System.arraycopy(as, 0, copy, 0, as.length);
        copy[as.length] = b;
        return copy;
    }

    /**
     * Add a to the beginning of the array.
     * @param bs original array.
     * @param a element to add at the beginning.
     * @return new array with length: bs.length+1
     */
    public static int[] prepend(int[] bs, int a){
        int[] copy = new int[bs.length+1];
        System.arraycopy(bs, 0, copy, 1, bs.length);
        copy[0] = a;
        return copy;
    }

    /**
     * Add b at location ix of the array.
     * @param as original array.
     * @param ix location
     * @param b element to add at as[ix].
     * @return new array with length: as.length+1
     */
    public static int[] insert(int[] as, int ix, int b){
        int[] copy = new int[as.length+1];
        System.arraycopy(as, 0, copy, 0, ix);
        System.arraycopy(as, ix, copy, ix+1, as.length-ix);
        copy[ix] = b;
        return copy;
    }

    /**
     * Add b in a already sorted array at its proper location.
     * @param as original sorted array.
     * @param b element to add.
     * @return new array with length: as.length+1
     */
    public static int[] insertSorted(int[] as, int b){
        int[] copy = new int[as.length+1];
        int ix = Arrays.binarySearch(as, b);
        ix = ix < 0 ? -ix-1 : ix;
        System.arraycopy(as, 0, copy, 0, ix);
        System.arraycopy(as, ix, copy, ix+1, as.length-ix);
        copy[ix] = b;
        return copy;
    }

    /**
     * Remove the element from the array at location i.
     * @param as original array.
     * @param i index indicating the element to be removed.
     * @return new array with length (as.length-1) if a is contained in as, or else a copy of the original array.
     */
    public static int[] remove(int[] as, int i){
        int n = as.length;
        if(i < 0 || i >= n) {
            return Arrays.copyOf(as, n);
        }
        int[] copy = new int[n-1];
        System.arraycopy(as,0,copy,0,i);
        System.arraycopy(as,i+1,copy,i,n-1-i);
        return copy;
    }

    /**
     * Remove the first occurrence of element a from the array.
     * @param as original array.
     * @param a the element to be removed.
     * @return new array with length (as.length-1) if a is contained in as, or else a copy of the original array.
     */
    public static int[] removeFirst(int[] as, int a){
        return remove(as, indexOf(as, a));
    }

    /**
     * Add b to the end of the array.
     * @param as original array.
     * @param b element to add at the end
     * @return new array with length: as.length+1
     */
    public static double[] append(double[] as, double b){
        double[] copy = new double[as.length+1];
        System.arraycopy(as, 0, copy, 0, as.length);
        copy[as.length] = b;
        return copy;
    }

    /**
     * Add a to the beginning of the array.
     * @param bs original array.
     * @param a element to add at the beginning.
     * @return new array with length: bs.length+1
     */
    public static double[] prepend(double[] bs, double a){
        double[] copy = new double[bs.length+1];
        System.arraycopy(bs, 0, copy, 1, bs.length);
        copy[0] = a;
        return copy;
    }

    public static <T> T[] subArray(T[] org, int from, int to) {
        T[] result = Arrays.copyOf(org, to - from);
        System.arraycopy(org, from, result, 0, result.length);
        return result;
    }

    /**
     * Add a at location ix of the array.
     * @param as original array.
     * @param ix location
     * @param b element to add at as[ix].
     * @return new array with length: as.length+1
     */
    public static double[] insert(double[] as, int ix, double b){
        double[] copy = new double[as.length+1];
        System.arraycopy(as, 0, copy, 0, ix);
        System.arraycopy(as, ix, copy, ix+1, as.length-ix);
        copy[ix] = b;
        return copy;
    }

    /**
     * Add b in a already sorted array at its proper location.
     * @param as original sorted array.
     * @param b element to add.
     * @return new array with length: as.length+1
     */
    public static double[] insertSorted(double[] as, double b){
        double[] copy = new double[as.length+1];
        int ix = Arrays.binarySearch(as, b);
        ix = ix < 0 ? -ix-1 : ix;
        System.arraycopy(as, 0, copy, 0, ix);
        System.arraycopy(as, ix, copy, ix+1, as.length-ix);
        copy[ix] = b;
        return copy;
    }

    /**
     * Remove the element from the array at location i.
     * @param as original array.
     * @param i index indicating the element to be removed.
     * @return new array with length (as.length-1) if a is contained in as, or else a copy of the original array.
     */
    public static double[] remove(double[] as, int i){
        int n = as.length;
        if(i < 0 || i >= n) {
            return Arrays.copyOf(as, n);
        }
        double[] copy = new double[n-1];
        System.arraycopy(as,0,copy,0,i);
        System.arraycopy(as,i+1,copy,i,n-1-i);
        return copy;
    }

    public static double[] removeFirst(double[] as, double a){
        return remove(as, indexOf(as, a));
    }

    /**
     * Add b to the end of the array.
     * @param <T> element type
     * @param as original array.
     * @param b element to add at the end
     * @return new array with length: as.length+1
     */
    public static <T> T[] append(T[] as, T b){
        T[] copy = Arrays.copyOf(as, as.length + 1);
        copy[as.length] = b;
        return copy;
    }

    /**
     * Add a to the beginning of the array.
     * @param <T> element type
     * @param bs original array.
     * @param a element to add at the beginning.
     * @return new array with length: bs.length+1
     */
    public static <T> T[] prepend(T[] bs, T a){        
        T[] copy = newInstance(bs, bs.length + 1);
        System.arraycopy(bs, 0, copy, 1, bs.length);
        copy[0] = a;
        return copy;
    }

    /**
     * Add a at location ix of the array.
     * @param <T> element type
     * @param as original array.
     * @param ix location
     * @param b element to add at as[ix].
     * @return new array with length: as.length+1
     */
    public static <T> T[] insert(T[] as, int ix, T b){
        T[] copy = newInstance(as, as.length + 1);
        System.arraycopy(as, 0, copy, 0, ix);
        System.arraycopy(as, ix, copy, ix+1, as.length-ix);
        copy[ix] = b;
        return copy;
    }

    /**
     * Add b in a already sorted array at its proper location.
     * @param <T> element type
     * @param as original sorted array.
     * @param b element to add.
     * @return new array with length: as.length+1
     */
    public static <T> T[] insertSorted(T[] as, T b){
        T[] copy = newInstance(as, as.length + 1);
        int ix = Arrays.binarySearch(as, b);
        ix = ix < 0 ? -ix-1 : ix;
        System.arraycopy(as, 0, copy, 0, ix);
        System.arraycopy(as, ix, copy, ix+1, as.length-ix);
        copy[ix] = b;
        return copy;
    }

    /**
     * Remove the element from the array at location i.
     * @param <T> element type
     * @param as original array.
     * @param i index indicating the element to be removed.
     * @return new array with length (as.length-1) if a is contained in as, or else a copy of the original array.
     */
    public static <T> T[] remove(T[] as, int i){
        int n = as.length;
        if(i < 0 || i >= n) {
            return Arrays.copyOf(as, n);
        }
        T[] copy = newInstance(as, as.length - 1);
        System.arraycopy(as,0,copy,0,i);
        System.arraycopy(as,i+1,copy,i,n-1-i);
        return copy;
    }

    /**
     * Remove the first occurrence of element a from the array.
     * @param <T> element type
     * @param as original array.
     * @param a the element to be removed.
     * @return new array with length (as.length-1) if a is contained in as, or else a copy of the original array.
     */
    public static <T> T[] removeFirst(T[] as, T a){
        return remove(as, indexOf(as, a));
    }
    
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }



    public static <T> T[] concatAll(Class<T> c, T[][] xss) {
        int totalLength = 0;
        for (T[] array : xss) {
            totalLength += array.length;
        }
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(c, totalLength);
        int offset = 0;
        for (T[] array : xss) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static <T> T[] concatAll(Class<T> c, T[][] xss, int from, int to) {
        int totalLength = 0;
        from = Math.max(0, from);
        to = Math.min(xss.length, to);
        for (int i = from; i < to; i++) {
            T[] array = xss[i];
            totalLength += array.length;
        }
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(c, totalLength);
        int offset = 0;
        for (int i = from; i < to; i++) {
            T[] array = xss[i];
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * In place reverse of an array
     * @param b initial array
     */
    public static void reverse(int[] b) {
       int left  = 0;          // index of leftmost element
       int right = b.length-1; // index of rightmost element

       while (left < right) {
          // exchange the left and right elements
          int temp = b[left]; 
          b[left]  = b[right]; 
          b[right] = temp;

          // move the bounds toward the center
          left++;
          right--;
       }
    }
    
    /**
     * In place reverse of an array
     * @param b initial array
     */
    public static void reverse(double[] b) {
       int left  = 0;          // index of leftmost element
       int right = b.length-1; // index of rightmost element

       while (left < right) {
          // exchange the left and right elements
          double temp = b[left]; 
          b[left]  = b[right]; 
          b[right] = temp;

          // move the bounds toward the center
          left++;
          right--;
       }
    }
    
    /**
     * In place reverse of an array
     * @param <T> element type
     * @param b initial array
     */
    public static <T> void reverse(T[] b) {
       int left  = 0;          // index of leftmost element
       int right = b.length-1; // index of rightmost element

       while (left < right) {
          // exchange the left and right elements
          T temp = b[left]; 
          b[left]  = b[right]; 
          b[right] = temp;

          // move the bounds toward the center
          left++;
          right--;
       }
    }


    /**
     * Check whether or not xs contains the value x.
     * @param xs array to be queried.
     * @param x the query value.
     * @param <T> array type
     * @return true if xs contains the value, else false
     */
    public static <T> boolean contains(T[] xs, T x){
        for (T t : xs) {
            if((t == null && x == null) || (t != null && t.equals(x))){
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether or not xs contains the value x.
     * @param xs array to be queried.
     * @param x the query value.
     * @return true if xs contains the value, else false
     */
    public static boolean contains(double[] xs, double x){
        for (double d : xs) {
            if(d == x){
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether or not xs contains the value x.
     * @param xs array to be queried.
     * @param x the query value.
     * @return true if xs contains the value, else false
     */
    public static boolean contains(int[] xs, int x){
        for (int i : xs) {
            if(i == x){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether all values in collection c are contained in the array xs.
     * @param xs array to be queried.
     * @param c the query values.
     * @return true if xs contains all the values, else false
     */
    public static boolean containsAll(Object[] xs, Collection c){
        //for (Object t : xs) {
        //    if(!c.contains(t))
        //        return false;
        //}
        for (Object o : c) {
            if(!contains(xs, o))
                return false;
        }
        return true;
    }

    /**
     * Checks whether all values in collection c are contained in the array xs.
     * @param xs array to be queried.
     * @param c the query values.
     * @return true if xs contains all the values, else false
     */
    public static boolean containsAll(int[] xs, Collection c){
        //for (Object t : xs) {
        //    if(!c.contains(t))
        //        return false;
        //}
        for (Object o : c) {
            if(!(o instanceof Integer) || !contains(xs, (Integer)o))
                return false;
        }
        return true;
    }

    /**
     * The index of element a in array as, if it is contained.
     * @param as array to be queried.
     * @param a the query value.
     * @return the index if as is contained, else -1
     */
    public static int indexOf(int[] as, int a){
        for (int i = 0; i < as.length; i++) {
            if (as[i] == a)
                return i;
        }
        return -1;
    }

    /**
     * The index of element a in array as, if it is contained.
     * @param as array to be queried.
     * @param a the query value.
     * @return the index if as is contained, else -1
     */
    public static int indexOf(double[] as, double a){
        for (int i = 0; i < as.length; i++) {
            if (as[i] == a)
                return i;
        }
        return -1;
    }

    /**
     * The index of element a in array as, if it is contained.
     * @param <T> element type
     * @param objects array to be queried.
     * @param target the query value.
     * @return the index if as is contained, else -1
     */
    public static <T> int indexOf(T[] objects, T target){
        for (int i = 0; i < objects.length; i++) {
            if ((target == null && objects[i] == null) || (objects[i].equals(target)))
                return i;
        }
        return -1;
    }

    /**
     * The index of element a in array as, if it is contained.
     * @param rows array to be queried.
     * @param target the query value.
     * @return the index if as is contained, else -1
     */
    public static int indexOf(double[][] rows, double[] target){
        for (int i = 0; i < rows.length; i++) {
            if ((target == null && rows[i] == null) || (Arrays.equals(rows[i], target)))
                return i;
        }
        return -1;
    }

    /**
     * The indices of target in array objects, if it is contained.
     * @param objects array to be queried.
     * @param target the query value.
     * @return the indices if target is contained, else the empty list
     */
    public static LinkedList<Integer> indicesOf(Object[] objects, Object target){
        return indicesOf(objects, target, 0);
    }

    /**
     * The indices of target in array objects, if it is contained.
     * @param objects array to be queried.
     * @param target the query value.
     * @param offset starting index
     * @return the indices if target is contained, else the empty list
     */
    public static LinkedList<Integer> indicesOf(Object[] objects, Object target, int offset){
        LinkedList<Integer> ixs = new LinkedList<Integer>();
        for (int i = offset; i < objects.length; i++) {
            if (target.equals(objects[i]))
                ixs.add(i);
        }
        return ixs;
    }

    public static void setAll(Iterable<Integer> indices, Object[] objects, Object target){
        for (Integer ix : indices) {
            objects[ix] = target;
        }
    }

    public static void quicksort(double[] main, int[] index) {
        quicksort(main, index, 0, index.length - 1);
    }

    // quicksort a[left] to a[right]
    public static void quicksort(double[] a, int[] index, int left, int right) {
        if (right <= left) return;
        int i = partition(a, index, left, right);
        quicksort(a, index, left, i-1);
        quicksort(a, index, i+1, right);
    }

    // partition a[left] to a[right], assumes left < right
    private static int partition(double[] a, int[] index,
    int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (a[++i] < a[right])      // find item on left to swap
            ;                              // a[right] acts as sentinel
            while (a[right] < a[--j])      // find item on right to swap
                if (j == left) break;      // don't go out-of-bounds
            if (i >= j) break;             // check if pointers cross
            exchange(a, index, i, j);      // swap two elements into place
        }
        exchange(a, index, i, right);      // swap with partition element
        return i;
    }

    // exchange a[i] and a[j]
    private static void exchange(double[] a, int[] index, int i, int j) {
        double swap = a[i];
        a[i] = a[j];
        a[j] = swap;
        int b = index[i];
        index[i] = index[j];
        index[j] = b;
    }

    public static <T> boolean all(T[] array, T value){
        if(value == null)
            return false;
        for (T t : array) {
            if(!value.equals(t))
                return false;
        }
        return true;
    }

    public static <T> boolean any(T[] array, T value){
        if(value == null)
            return false;
        for (T t : array) {
            if(value.equals(t))
                return true;
        }
        return false;
    }

    public static <T> Iterator<T> iterator(final T[] array){
        return new Iterator<T>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return (position < array.length);
            }
            
            @Override
            public T next() {
                if (hasNext()) {
                    return array[position++];
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
              throw new UnsupportedOperationException();
            }
        };
    }

    public static Iterator<Integer> iterator(final int[] array){
        return new Iterator<Integer>() {
            private int position = 0;

            @Override
            public boolean hasNext() {
                return (position < array.length);
            }

            @Override
            public Integer next() {
                if (hasNext()) {
                    return array[position++];
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
              throw new UnsupportedOperationException();
            }
        };
    }
    
    /**
     * Finds all first occurances of the objects in an array, using equals();
     * @param as array, possibly containing copies of elements.
     * @return an array as long as the input with corresponding mark 
     * if it is a first occurance or not.
     */
    public static boolean[] markFirstOccurances(Object[] as){
        //only actively read first occurances of input streams
        final int n = as.length;
        boolean[] result = new boolean[n];
        Arrays.fill(result, true);
        for (int i = 0; i < n; i++) {            
            if(!result[i])
                continue; // shortcut: already marked as duplicate;
            for (int j = i+1; j < n; j++) {
                // mark as first when all others agree
                result[j] &= !(as[i].equals(as[j]));
            }            
        }
        return result;
    }

    /**
     * Parse array of strings. Strings may not contain '[' and ']',
     * except from the outer brackets.
     * @param s format "[b, c, d]"
     * @return array of elements as (trimmed) strings
     */
    public static String[] parseArray(String s){
        s = s.replaceAll("\\s*[\\[\\]]\\s*",""); // remove brackets
        return s.split("\\s*,\\s*");
    }
    
    /**
     * Parse array of doubles. 
     * @param s format "[2, -3.14, 1e4]"
     * @return array of double values
     */
    public static double[] parseDoubles(String s){
        s = s.replaceAll("\\s*[\\[\\]]\\s*",""); // remove brackets
        String[] ss = s.split("\\s*,\\s*");
        int n = ss.length;
        double[] result = new double[n];
        for(int i = 0; i < n; i++){
            try{
                result[i] = Double.valueOf(ss[i]);
            }
            catch(NumberFormatException ex){
                result[i] = Double.NaN;
            }
        }
        return result;
    }
    
    /**
     * Parse array of ints. 
     * @param s format "[2, -314, 0]"
     * @return array of int values
     */
    public static int[] parseInts(String s){
        s = s.replaceAll("\\s*[\\[\\]]\\s*",""); // remove brackets
        String[] ss = s.split("\\s*,\\s*");
        int n = ss.length;
        int[] result = new int[n];
        for(int i = 0; i < n; i++){
            try{
                result[i] = Integer.valueOf(ss[i]);
            }
            catch(NumberFormatException ex){
                result[i] = 0;
            }
        }
        return result;
    }

    /**
     * In object array set all indices from indices array to target.
     *
     * @param indices 1
     * @param objects 2
     * @param target 3
     */
    public static void setAll(int[] indices, Object[] objects, Object target){
        for (Integer ix : indices) {
            objects[ix] = target;
        }
    }

}