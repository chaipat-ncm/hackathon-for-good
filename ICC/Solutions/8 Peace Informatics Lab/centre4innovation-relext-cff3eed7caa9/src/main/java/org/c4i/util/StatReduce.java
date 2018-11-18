package org.c4i.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Statistical functions on {@link DoubleStream}s
 * @author Arvid Halma
 * @version 15-2-2017 - 20:45
 */
public interface StatReduce extends Function<DoubleStream, Double>{

    Double apply(DoubleStream xs);

    StatReduce COUNT = xs -> xs.average().orElse(Double.NaN);

    StatReduce SUM = DoubleStream::sum;

    StatReduce MAX = xs -> xs.max().orElse(Double.NaN);

    StatReduce MIN = xs -> xs.min().orElse(Double.NaN);

    StatReduce MEAN = xs -> xs.average().orElse(Double.NaN);

    StatReduce VAR = xs -> {
        List<Double> list = xs.boxed().collect(Collectors.toList());
        double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        return list.stream().mapToDouble(Double::doubleValue).map(x -> (x - avg) * (x - avg)).sum() / list.size();
    };

    StatReduce STD = xs -> Math.sqrt(VAR.apply(xs));

    StatReduce MEDIAN = xs -> {
        List<Double> list = xs.boxed().collect(Collectors.toList());
        int n = list.size();
        if(n == 0){
            return Double.NaN;
        }
        list.sort(Double::compareTo);
        if(n % 2 == 0){
            return 0.5 * (list.get(n/2) + list.get(1+n/2));
        } else {
            return list.get(1+n/2);
        }
    };

    class PERCENTILE implements StatReduce {
        double p;

        public PERCENTILE(double p) {
            this.p = p;
        }

        @Override
        public Double apply(DoubleStream xs) {
            List<Double> list = xs.boxed().collect(Collectors.toList());
            Collections.sort(list);
            int n = list.size();
            double sum = list.stream().mapToDouble(Double::doubleValue).sum();
            double s = 0;
            for (Double x : list) {
                s += x;
                if (s/sum > p) {
                    return x;
                }
            }
            return Double.NaN;

        }
    };

    class MEAN_PLUS_VAR implements StatReduce {
        double varScalar = 1.0;

        public MEAN_PLUS_VAR(double varScalar) {
            this.varScalar = varScalar;
        }

        @Override
        public Double apply(DoubleStream xs) {
            List<Double> list = xs.boxed().collect(Collectors.toList());
            double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
            return avg + varScalar * list.stream().mapToDouble(Double::doubleValue).map(x -> (x - avg) * (x - avg)).sum() / list.size();
        }
    };

    class MEAN_PLUS_STD implements StatReduce {
        double varScalar = 1.0;

        public MEAN_PLUS_STD(double varScalar) {
            this.varScalar = varScalar;
        }

        @Override
        public Double apply(DoubleStream xs) {
            List<Double> list = xs.boxed().collect(Collectors.toList());
            double avg = list.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
            return avg + varScalar * Math.sqrt(list.stream().mapToDouble(Double::doubleValue).map(x -> (x - avg) * (x - avg)).sum() / list.size());
        }
    };
}
