package org.ates.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private final AtomicLong atomicLong;

    public IdGenerator() {
        this.atomicLong = new AtomicLong(1);
    }

    public Long getNext() {
        return atomicLong.getAndIncrement();
    }


}
