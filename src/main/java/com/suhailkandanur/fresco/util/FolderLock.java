package com.suhailkandanur.fresco.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by suhail on 2016-11-03.
 */
public class FolderLock implements Lock {
    private static final Logger logger = LoggerFactory.getLogger(FolderLock.class);

    private final String folderLocation;
    private final Path lockFilePath;

    private FileLock fileLock;
    private FileChannel fileChannel;

    private volatile boolean isLocked;

    public FolderLock(String folderLocation) {
        this.folderLocation = folderLocation;
        this.lockFilePath = Paths.get(folderLocation, ".lock");
    }

    @Override
    public boolean isLocked() {
        return this.isLocked;
    }

    @Override
    public synchronized void lock() throws Exception {
        try {
            Path folderPath = Paths.get(folderLocation);
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                throw new Exception(folderLocation + " is not a valid folder location");
            }
            if (Files.notExists(lockFilePath)) Files.createFile(lockFilePath);
            if (Files.notExists(lockFilePath)) {
                throw new Exception("unable to create lock file, path: " + lockFilePath.toString());
            }
            fileChannel = FileChannel.open(lockFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            if (fileChannel == null) throw new Exception("unable to open file channel");
            fileLock = fileChannel.lock();
            isLocked = true;
            logger.info("lock acquired on folder {}", folderLocation);
        } finally {
            if (!isLocked) release();
        }
    }

    @Override
    public synchronized boolean tryLock() {
        try {
            lock();
            return true;
        } catch (Exception e) {
            release();
            return false;
        }
    }

    @Override
    public synchronized void release() {
        try {
            if (fileLock != null) fileLock.release();
            if (fileChannel != null && fileChannel.isOpen()) fileChannel.close();
            if (Files.exists(lockFilePath)) Files.delete(lockFilePath);
            isLocked = false;
            fileChannel = null;
            fileLock = null;
            logger.info("lock released on folder {}", folderLocation);
        } catch (IOException e) {
            logger.error("unable to release lock, exception: {}", e.getMessage());
        }
    }
}

