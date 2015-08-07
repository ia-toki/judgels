package org.iatoki.judgels.gabriel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class GabrielUtils {

    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static final Lock FETCH_CHECK_LOCK = new ReentrantLock();

    private GabrielUtils() {
        // prevent instantiation
    }

    public static Lock getGradingReadLock() {
        return READ_WRITE_LOCK.readLock();
    }

    public static Lock getGradingWriteLock() {
        return READ_WRITE_LOCK.writeLock();
    }

    public static Lock getGradingFetchCheckLock() {
        return FETCH_CHECK_LOCK;
    }
}
