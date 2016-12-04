package com.suhailkandanur.fresco.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Created by suhail on 2016-12-02.
 */
public abstract class AbstractLockableResource implements LockableResource {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLockableResource.class);

    public <T> T withLockOn(Supplier<T> codeToExecute) throws Exception {
        return withLockOn(codeToExecute, null);
    }

    public <T> T withLockOn(Supplier<T> codeToExecute, T defaultReturnObject) throws Exception {
        Lock lock = this.getLock();
        try {
            if (lock != null) {
                lock.lock();
                logger.info("lock acquired successfully");
                return codeToExecute.get();
            }
        } catch (Exception ex) {
            logger.error("unable to lock resource and execute function, error: {}", ex.getMessage());
            throw ex;
        } finally {
            if (lock != null && lock.isLocked()) {
                lock.release();
                logger.info("lock released successfully");
            }
        }
        return defaultReturnObject;
    }
}
