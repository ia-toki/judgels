package org.iatoki.judgels.gabriel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class GabrielUtils {

    private static final Lock WRITE_LOCK = new ReentrantLock();
    private static final Lock FETCH_CHECK_LOCK = new ReentrantLock();

    private GabrielUtils() {
        // prevent instantiation
    }

    public static Lock getGradingWriteLock() {
        return WRITE_LOCK;
    }

    public static Lock getGradingFetchCheckLock() {
        return FETCH_CHECK_LOCK;
    }
}
