/*
 * Copyright 2020 Kaj Wortel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.simple_tools.log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract logger implementation which can be used as basis for a {@link Logger}.
 * 
 * @author Kaj Wortel
 */
public abstract class DefaultLogger
        extends Logger {
    
    /* -------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    /** The lock used for locking the logger. */
    protected Lock lock;
    

    /* -------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Default constructor which creates a new fair lock for concurrent operations.
     */
    public DefaultLogger() {
        lock = new ReentrantLock(true);
    }

    /**
     * Constructor which initializes the lock with the given lock.
     *
     * @param lock The lock to use for this class.
     */
    public DefaultLogger(Lock lock) {
        this.lock = lock;
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Writes the given text to the log.
     * <br>
     * Ensures that the logger is locked at least once using {@link #lock()},
     * and an equal amount of {@link #unlock()} calls afterwards.
     * Additionally ensures that {@link #isClosed()} {@code == false}
     *
     * @param text The text to be write to the log.
     *
     * @implSpec
     * The default exception handling is to print exceptions to the {@code System.err} stream.
     *
     * @throws IOException If some IO error occurred.
     */
    protected abstract void writeText(String text)
            throws IOException;

    @Override
    public void writeE(Exception e, Level level, Date timeStamp) {
        if (useFull) {
            String[] text = Arrays.toString(e.getStackTrace()).split(", ");
            String message = e.getClass().getName() + ": " + e.getMessage();

            lock();
            try {
                if (isClosed()) return;
                processText(message, level, timeStamp, useTimeStamp);
                for (String s : text) {
                    processText(s, Level.NONE, timeStamp, false);
                }
                flush();

            } finally {
                unlock();
            }

        } else {
            String message = e.getClass().getName() + ": " + e.getMessage();
            processText(message, Level.ERROR, timeStamp, useTimeStamp);
            flush();
        }
    }

    @Override
    public void writeO(Object obj, Level level, Date timeStamp) {
        if (obj == null) obj = "null";
        String[] strArr = obj.toString().split(System.lineSeparator());
        lock();
        try {
            if (isClosed()) {
                return;
            }
            for (String str : strArr) {
                processText(str, level, timeStamp, useTimeStamp);
            }
            flush();

        } finally {
            unlock();
        }
    }

    /**
     * Processes the text with the given attributes to a single String.
     *
     * @param text The text to be processed.
     * @param level The level of logging.
     * @param timeStamp The time stamp of the logging.
     * @param useDate Whether the time should be used in this logging.
     */
    protected void processText(String text, Level level, Date timeStamp, boolean useDate) {
        try {
            StringBuilder sb = new StringBuilder();

            // Determine the date line
            String dateLine = dateFormat.format(timeStamp) + " ";
            if (useDate) {
                sb.append(dateLine);
            } else {
                sb.append(" ".repeat(dateLine.length()));
            }

            // Determine the info line
            if (level == Level.NONE) {
                sb.append(" ".repeat(10));

            } else {
                sb.append("[")
                        .append(level.toString())
                        .append("]")
                        .append(" ".repeat(8 - level.toString().length()));
            }

            sb.append(text)
                    .append(System.lineSeparator());
            
            String writeText = sb.toString();
            lock();
            try {
                if (isClosed()) return;
                writeText(writeText);
            } finally {
                unlock();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the header of the log file if it was not yet written.
     */
    protected void writeHeader() {
        lock();
        try {
            if (isClosed() || header == null)
                return;
            writeText(header.replaceAll("&date&", formatDate(new Date())) + System.lineSeparator());

        } catch (IOException e) {
            e.printStackTrace();
            
        } finally {
            unlock();
        }
    }

    @Override
    public void lock() {
        if (lock != null) lock.lock();
    }

    @Override
    public void unlock() {
        if (lock != null) lock.unlock();
    }
    
    @Override
    public void open() {
        closed = false;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public void flush() {
    }
    
    
}
