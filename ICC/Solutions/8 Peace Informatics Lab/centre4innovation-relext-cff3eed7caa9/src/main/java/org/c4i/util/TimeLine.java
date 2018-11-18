package org.c4i.util;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * A sequence of events, that can be sampled and analyzed.
 * @version 30-1-17
 * @author Arvid Halma
 */
@JsonSerialize(using = TimeLineSerializer.class)
public class TimeLine<E extends Timestamped> implements Iterable<E>{
    private NavigableMap<DateTime, List<E>> events;

    public TimeLine() {
        this.events = new TreeMap<>();
    }

    public TimeLine(Collection<E> events) {
        this();
        for (E event : events) {
            add(event);
        }
    }

    protected TimeLine(NavigableMap<DateTime, List<E>> dateTimeESortedMap) {
        this.events = dateTimeESortedMap;
    }

    protected TimeLine(Map<DateTime, List<E>> dateTimeESortedMap) {
        this.events = new TreeMap<>(dateTimeESortedMap);
    }

    public List<E> get(DateTime t){
        return events.get(t);
    }

    public List<E> getFloor(DateTime t){
        Map.Entry<DateTime, List<E>> entry = events.floorEntry(t);
        return entry == null ? null : entry.getValue();
    }

    public List<E> getClosest(DateTime t){
        Map.Entry<DateTime, List<E>> floor = events.floorEntry(t);
        Map.Entry<DateTime, List<E>> ceil = events.ceilingEntry(t);
        if(floor == null){
            return ceil == null ? null : ceil.getValue();
        } else if (ceil == null){
            return floor.getValue();
        } else {
            long millis = t.getMillis();
            if(millis - floor.getKey().getMillis() < ceil.getKey().getMillis() - millis){
                return floor.getValue();
            } else {
                return ceil.getValue();
            }
        }
    }

    public double getValue(DateTime t, ToDoubleFunction<E> toDoubleFunction, DoubleBinaryOperator reduceOp, double defaultValue){
        return events.get(t).stream().mapToDouble(toDoubleFunction).reduce(reduceOp).orElse(defaultValue);
    }

    public double sampleValue(DateTime t, ToDoubleFunction<E> toDoubleFunction){
        return sampleValue(t, toDoubleFunction, StatReduce.MEAN);
    }

    public double sampleValue(DateTime t, ToDoubleFunction<E> toDoubleFunction, StatReduce reduce){
        Map.Entry<DateTime, List<E>> e0 = events.floorEntry(t);
        Map.Entry<DateTime, List<E>> e1 = events.ceilingEntry(t);

        if(e0 == null){
            // before first event
            return e1.getValue() == null ? 0 : reduce.apply(e1.getValue().stream().mapToDouble(toDoubleFunction));
        } else if (e1 == null){
            // after last event
            return e0.getValue() == null ? 0 : reduce.apply(e0.getValue().stream().mapToDouble(toDoubleFunction));
        } else {
            double y0 = reduce.apply(e0.getValue().stream().mapToDouble(toDoubleFunction));
            double y1 = reduce.apply(e1.getValue().stream().mapToDouble(toDoubleFunction));
            long tStart = e0.getKey().getMillis();
            double dt = e1.getKey().getMillis() - tStart;
            double w = t.getMillis() - tStart;
            return dt == 0.0 ?  0.5*(y0+y1) : y0 + (w/dt) * (y1-y0);
        }
    }

    public List<E> getStart(){
        return this.events.firstEntry().getValue();
    }

    public List<E> getEnd(){
        return this.events.lastEntry().getValue();
    }

    public DateTime getStartDateTime(){
        return this.events.firstKey();
    }

    public DateTime getEndDateTime(){
        return this.events.lastKey();
    }

    public synchronized TimeLine<E> add(E event){
        if(event == null)
            return this;

        DateTime t = event.getTimestamp();
        if(!events.containsKey(t)){
            events.put(t, new ArrayList<>(1));
        }
        events.get(t).add(event);
        return this;
    }

    public TimeLine<E> select(DateTime t0, DateTime t1){
        return new TimeLine<>(events.subMap(t0, t1));
    }

    @Override
    public Iterator<E> iterator() {
        return events.values().stream().flatMap(Collection::stream).iterator();
    }

    public List<E> asList(){
        return events.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public <R> List<R> asList(Function<E,R> mapping ){
        return events.values().stream().flatMap(Collection::stream).map(mapping).collect(Collectors.toList());
    }

    public DoubleStream asDoubleStream(ToDoubleFunction<E> toDouble) {
        return events.values().parallelStream().flatMapToDouble((es) -> es.stream().mapToDouble(toDouble));
    }

    public DoubleStream asDoubleStream(DateTime t0, DateTime t1, ToDoubleFunction<E> toDouble) {
        return events.subMap(t0, t1).values().parallelStream().flatMapToDouble((es) -> es.stream().mapToDouble(toDouble));
    }

    public double reduce(StatReduce reduce, ToDoubleFunction<E> toDouble){
        return reduce.apply(asDoubleStream(toDouble));
    }

    public TimeLine<E> filter(Predicate<E> p){
        TimeLine<E> result = new TimeLine<>();
        events.values().parallelStream().flatMap(Collection::stream).filter(p).forEach(result::add);
        return result;
    }

    public int count(){
        return events.values().parallelStream().mapToInt(List::size).sum();
    }

    public int count(DateTime t0, DateTime t1){
        return events.subMap(t0, t1).values().parallelStream().mapToInt(List::size).sum();
    }

    public int count(DateTime t0, DateTime t1, Predicate<E> p){
        return (int) events.subMap(t0, t1).values().parallelStream().flatMap(Collection::parallelStream).filter(p).count();
    }

    public double sum(ToDoubleFunction<E> toDouble){
        return (int) asDoubleStream(toDouble).sum();
    }

    public double sum(DateTime t0, DateTime t1, ToDoubleFunction<E> toDouble){
        return (int) asDoubleStream(t0, t1, toDouble).sum();
    }

    public TimeLine<TimeValue> resample(DateTime t0, DateTime t1, Period step, ToDoubleFunction<E> toDouble, StatReduce reduce){
        TimeLine<TimeValue> result = new TimeLine<>();

        DateTime tEnd = t1.minus(step);
        for (DateTime t = t0; t.isBefore(tEnd); t = t.plus(step)){

//            DateTime tw = t.plus(step);
//            double value = sampleValue(t, toDouble, reduce);
            double value = select(t, t.plus(step)).reduce(reduce, toDouble);
            result.add(new TimeValue(t, value));
        }

        return result;
    }

    public TimeLine<TimeValue> countPerDay(DateTime t0, DateTime t1){
        return resample(t0.withTime(0,0,0,0), t1, Period.days(1) , e -> 1.0  , StatReduce.SUM);
//        return resample(t0, t1, Period.days(1) , e -> 1.0 , StatReduce.SUM);
    }

    public TimeLine<TimeValue> slidingWindow(DateTime t0, DateTime t1, Period window, Period step, ToDoubleFunction<E> toDouble, StatReduce reduce){
        TimeLine<TimeValue> result = new TimeLine<>();

        DateTime tEnd = t1.minus(window);
        for (DateTime t = t0; t.isBefore(tEnd); t = t.plus(step)){

            DateTime tw = t.plus(window);
            double value = reduce.apply(events.subMap(t, tw).values().parallelStream().flatMapToDouble(list -> list.parallelStream().mapToDouble(toDouble)));
            result.add(new TimeValue(tw, value));
        }

        return result;
    }

    public TimeLine<TimeValue> slidingWindow(DateTime t0, DateTime t1, Period window1, Period window2, Period step, ToDoubleFunction<E> toDouble, StatReduce reduce1, StatReduce reduce2, BinaryOperator<Double> windowValueCombiner){
        TimeLine<TimeValue> result = new TimeLine<>();

        DateTime tEnd = t1.minus(window1).minus(window2);
        for (DateTime t = t0; t.isBefore(tEnd); t = t.plus(step)){

            DateTime tw1 = t.plus(window1);
            DateTime tw2 = tw1.plus(window2);
            double value1 = reduce1.apply(events.subMap(t, tw1).values().parallelStream().flatMapToDouble(list -> list.parallelStream().mapToDouble(toDouble)));
            double value2 = reduce2.apply(events.subMap(tw1, tw2).values().parallelStream().flatMapToDouble(list -> list.parallelStream().mapToDouble(toDouble)));
            result.add(new TimeValue(tw1, windowValueCombiner.apply(value1, value2)));
        }

        return result;
    }


    public TimeLine<TimeValue> derivative(ToDoubleFunction<E> toDouble, Period dt){
        DateTime t0 = getStartDateTime();
        DateTime t1 = getEndDateTime();
        return resample(t0, t1, dt, toDouble, StatReduce.SUM).slidingWindow(t0, t1, dt, dt, dt, TimeValue::getValue, StatReduce.SUM, StatReduce.SUM, (ti, tj) -> tj - ti);
    }

    public void print(){
        System.out.println("Timeline");
        for (Map.Entry<DateTime, List<E>> entry : events.entrySet()) {
            System.out.println(" * " + entry.getKey() + " : \t" + entry.getValue());
        }
    }

    @Override
    public String toString() {
        return "TimeLine{" + "events=" + events +
                '}';
    }
}
