package org.c4i.util;

import org.joda.time.DateTime;

/**
 * An object with a DateTime label
 * @version 30-1-17
 * @author Arvid Halma
 */
public interface Timestamped {
    DateTime getTimestamp();
}
