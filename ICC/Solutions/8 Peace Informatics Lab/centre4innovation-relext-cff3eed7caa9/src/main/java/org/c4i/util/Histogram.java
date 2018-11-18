package org.c4i.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sparse histogram. A map from value to double.
 * @author Arvid Halma
 * @version 6/16/11
 *          Time: 2:53 PM
 */
public class Histogram<V> implements Collection<V>{
    public Map<V, Double> freqs;

    public Histogram() {
        this.freqs = new HashMap<>();
    }

    public Histogram(V[] vs) {
        this();
        addAll(vs);
    }

    public Histogram(Collection<? extends V> vs) {
        this.freqs = new HashMap<>();
        addAll(vs);
    }

    public Histogram(Stream<? extends V> vs) {
        this.freqs = new HashMap<>();
        vs.forEach(this::add);
    }

    public Histogram(Histogram<V> h) {
        synchronized (h) {
            this.freqs = new HashMap<>(h.asMap());
        }
    }

    public synchronized double get(V v){
        Double f = freqs.get(v);
        return f == null ? 0 : f;
    }

    public synchronized boolean add(V v, double n) {
        return freqs.put(v, get(v) + n) == null;
    }

    public synchronized boolean add(V v){
        return add(v, 1);
    }

    @Override
    public synchronized boolean contains(Object o) {
        return get((V) o) == 0;
    }

    @Override
    public synchronized Iterator<V> iterator() {
        return freqs.keySet().iterator();
    }

    @Override
    public synchronized Object[] toArray() {
        return freqs.keySet().toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] a) {
        return freqs.keySet().toArray(a);
    }

    @Override
    public synchronized boolean remove(Object obj) {
        synchronized (freqs) {
            if (freqs.containsKey(obj)) {
                Double oldFreq = freqs.get(obj);
                if (oldFreq <= 1) {
                    freqs.remove(obj);
                }
                freqs.put((V) obj, oldFreq - 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        boolean result = true;
        for (Object o : c) {
            result &= contains(o);
        }

        return result;
    }

    @Override
    public synchronized boolean addAll(Collection<? extends V> c) {
        boolean result = false;
        for (V v : c) {
            result |= add(v);
        }
        return result;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object o : c) {
            result |= remove(o);
        }
        return result;
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll is not supported for Histograms.");
    }

    @Override
    public synchronized void clear() {
        freqs.clear();
    }

    @Override
    public synchronized int size(){
        return freqs.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return freqs.isEmpty();
    }

    public synchronized void addAll(V[] vs){
        for (V v : vs) {
            add(v);
        }
    }

    public synchronized List<V> getEntries(){
        return new ArrayList<>(freqs.keySet());
    }

    public synchronized Set<V> keySet(){
        return freqs.keySet();
    }

    public synchronized List<Double> getFrequencies(){
        return new ArrayList<>(freqs.values());
    }

    public synchronized List<Integer> getIntFrequencies(){
        return freqs.values().stream().map(Double::intValue).collect(Collectors.toList());
    }

    public synchronized double totalFrequency(){
        return freqs.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public synchronized double meanFrequency(){
        return totalFrequency()/freqs.size();
    }

    public synchronized void normalize(){
        double total = totalFrequency();
        freqs.entrySet().forEach(e -> freqs.put(e.getKey(), e.getValue()/total));
    }

    public synchronized Map<V, Double> asMap(){
        return freqs;
    }

    public synchronized HashMap<V, Double> asSortedMap(){
        return asSortedMap(freqs.size());
    }

    public synchronized HashMap<V, Double> asSortedMap(int maxEntries){
        List<Map.Entry<V, Double>> entries = new ArrayList<>(freqs.entrySet());
        entries.sort((o1, o2) -> {
            return o2.getValue().compareTo(o1.getValue()); // reverse
        });
        entries = entries.subList(0, Math.min(maxEntries, entries.size()));
        LinkedHashMap<V, Double> sortedMap = new LinkedHashMap<>(entries.size());
        for (Map.Entry<V, Double> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public synchronized void join(Histogram<V> h){
        Set<V> allKeys = new HashSet<>(h.freqs.keySet());
        allKeys.addAll(this.freqs.keySet());
        for (V v : allKeys) {
            freqs.put(v, h.get(v) + this.get(v));
        }
    }


    public synchronized void intersect(Histogram<V> h){
        Set<V> allKeys = new HashSet<>(h.freqs.keySet());
        allKeys.addAll(this.freqs.keySet());
        HashMap<V, Double> newFreqs = new HashMap<>();
        for (V v : allKeys) {
            double f = Math.min(h.get(v), this.get(v));
            if(f > 0) {
                newFreqs.put(v, f);
            }
        }
        this.freqs = newFreqs;
    }

    public synchronized void removeLowOccurences(double lowerbound){
        freqs.entrySet().removeIf(entry -> entry.getValue() < lowerbound);
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Histogram)) return false;

        Histogram histogram = (Histogram) o;

        return !(freqs != null ? !freqs.equals(histogram.freqs) : histogram.freqs != null);
    }
    
    

    @Override
    public int hashCode() {
        return freqs != null ? freqs.hashCode() : 0;
    }

    @Override
    public String toString() {
        return freqs.toString();
    }



}
