package com.suhailkandanur.fresco.util;

/**
 * Created by suhail on 2016-12-02.
 */
public interface Lock {
    boolean isLocked();
    void lock() throws Exception;
    boolean tryLock();
    void release();
}

