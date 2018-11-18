package org.c4i.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.util.function.ToDoubleFunction;

/**
 * A double value at a certain datetime.
 * @author Arvid Halma
 * @version 2-2-2017 - 17:10
 */
public class TimeValue implements Timestamped, ToDoubleFunction {
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonProperty
    private DateTime t;
    @JsonProperty
    private double value;

    public TimeValue(DateTime t, double value) {
        this.t = t;
        this.value = value;
    }

    @Override
    public DateTime getTimestamp() {
        return t;
    }

    public double getValue() {
        return value;
    }

    public TimeValue setValue(double value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeValue)) return false;

        TimeValue timeValue = (TimeValue) o;

        if (Double.compare(timeValue.value, value) != 0) return false;
        return t != null ? t.equals(timeValue.t) : timeValue.t == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = t != null ? t.hashCode() : 0;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimeValue{");
        sb.append("t=").append(t);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public double applyAsDouble(Object arg) {
        return value;
    }
}
