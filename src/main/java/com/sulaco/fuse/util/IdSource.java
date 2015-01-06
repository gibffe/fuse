package com.sulaco.fuse.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IdSource {

    static final AtomicInteger intSource  = new AtomicInteger(0);
    static final AtomicLong    longSource = new AtomicLong(0);

    public static int getInt() {
        return intSource.incrementAndGet();
    }

    public static long getLong() {
        return longSource.incrementAndGet();
    }

    public static UUID getUUID() {
        return UUID.randomUUID();
    }

}
