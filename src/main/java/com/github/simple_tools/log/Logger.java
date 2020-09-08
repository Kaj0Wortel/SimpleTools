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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base framework class for logging.
 * It supports the log levels {@code NONE}, {@code DEBUG}, {@code INFO},
 * {@code WARNING} and {@code ERROR}.
 * It also allows customization in the date, log level and contents printing.
 * Additionally, it keeps track of a global logger and provides shorthand
 * methods for using it.
 * 
 * @author Kaj Wortel
 */
@Setter
@Getter
public abstract class Logger
        implements AutoCloseable {

    /* -------------------------------------------------------------------------
     * Constants.
     * -------------------------------------------------------------------------
     */
    /** The default date format. */
    protected static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    
    
    /* -------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    /** The global logger used for the static functions in this class. */
    @Getter
    @Setter
    private static Logger globalLogger = null;
    
    /** Whether to use a time stamp by default or not. */
    @Getter(AccessLevel.NONE)
    protected boolean useTimeStamp = true;
    /** The date format used for logging a time stamp. */
    protected DateFormat dateFormat = DEFAULT_DATE_FORMAT;
    
    /** Whether to write the header. */
    @Getter(AccessLevel.NONE)
    protected boolean useHeader = false;
    /** The header of the logger. */
    protected String header = "";
    
    /** Whether to use the full exception notation as default or not. */
    @Getter(AccessLevel.NONE)
    protected boolean useFull = true;
    
    /** Whether the logger is closed. */
    protected boolean closed = true;

    
    /* -------------------------------------------------------------------------
     * Alternate get functions.
     * -------------------------------------------------------------------------
     */
    /**
     * @return Whether the logger uses a time stamp.
     */
    public boolean useTimeStamp() {
        return useTimeStamp;
    }
    /**
     * @return Whether the logger uses a header.
     */
    public boolean useHeader() {
        return useHeader;
    }

    /**
     * @return Whether the logger uses full notation.
     */
    public boolean useFull() {
        return useFull;
    }
    
    
    /* -------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    /**
     * Enum class denoting the severity level of a log action.
     */
    public enum Level {

        /**
         * This level is used when the level field should be blank.
         */
        NONE,

        /**
         * This level is used to notify the reader of the log that the text
         * contains plain information.
         */
        INFO,

        /**
         * This level is used to notify the reader of the log that something
         * might have gone wrong, but it was not fatal.
         */
        WARNING,

        /**
         * This level is used to notify the reader of the log that something
         * went wrong and, most likely, the application could not recover
         * from the error.
         */
        ERROR,

        /**
         * This level is used to notify the reader of the log that this text
         * is only temporary and should be removed when the component is finished.
         */
        DEBUG;
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Formats the date using the date format
     *
     * @param timeStamp the time stamp to be converted.
     *
     * @return A string representing the date.
     */
    protected String formatDate(Date timeStamp) {
        return dateFormat.format(timeStamp);
    }
    
    /**
     * Checks the given level on null values.
     *
     * @param level The level to be checked. May be null.
     *
     * Here, it must be ensured that {@code level != null}.
     */
    protected static Level checkLevel(Level level) {
        return (level != null ? level : Level.NONE);
    }


    /**
     * Writes the exception to a log file. <br>
     * Here, it must be ensured that {@code timeStamp != null}.
     *
     * @param e The exception to be written.
     * @param timeStamp The time stamp in milliseconds precision.
     */
    public void writeE(Exception e, Date timeStamp) {
        writeE(e, Level.ERROR, timeStamp);
    }

    /**
     * Writes the exception to a log file. <br>
     * Here, it must be ensured that {@code level != null} and {@code timeStamp != null}.
     *
     * @param e The exception to be written.
     * @param level The severity level. The default is {@link Level#ERROR}.
     * @param timeStamp The time stamp in milliseconds precision.
     */
    public abstract void writeE(Exception e, Level level, Date timeStamp);

    /**
     * Writes the object to a log file. <br>
     * Here, it must be ensured that {@code level != null} and {@code timeStamp != null}.
     *
     * @param obj The object to be written.
     * @param timeStamp The time stamp in milliseconds precision.
     */
    public void writeO(Object obj, Date timeStamp) {
        writeO(obj, Level.DEBUG, timeStamp);
    }

    /**
     * Writes the object to a log file. <br>
     * Here, it must be ensured that {@code level != null} and {@code timeStamp != null}.
     *
     * @param obj The object to be written.
     * @param level The severity level.
     * @param timeStamp The time stamp in milliseconds precision.
     */
    public abstract void writeO(Object obj, Level level, Date timeStamp);

    /**
     * Writes an object array to a log file.
     * Ensures that all data is logged consecutively. <br>
     * Here, it must be ensured that {@code timeStamp != null}.
     *
     * @param objArr The object array to be written.
     * @param timeStamp The time stamp in milliseconds precision.
     */
    public void writeOA(Object[] objArr, Date timeStamp) {
        writeOA(objArr, Level.DEBUG, timeStamp);
    }

    /**
     * Writes an object array to a log file.
     * Ensures that all data is logged consecutively. <br>
     * Here, it must be ensured that {@code level != null} and {@code timeStamp != null}.
     *
     * @param objArr The object array to be written.
     * @param level The severity level.
     * @param timeStamp The time stamp in milliseconds precision.
     */
    public void writeOA(Object[] objArr, Level level, Date timeStamp) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.lock();
        try {
            if (objArr == null) {
                writeO("null", timeStamp);

            } else {
                for (Object obj : objArr) {
                    if (obj == null) {
                        writeO("null", level, timeStamp);

                    } else if (obj instanceof Exception) {
                        logger.writeE((Exception) obj, level, timeStamp);

                    } else if (obj.getClass().isArray()) {
                        writeOA((Object[]) obj, level, timeStamp);

                    } else {
                        logger.writeO(obj, level, timeStamp);
                    }
                }
            }
        } finally {
            logger.unlock();
        }
    }

    /**
     * Opens the logger.
     * Opening a logger which is already opened will have no effect.
     * <br>
     * The default state of a logger is closed, and it must be opened first
     * before it can be used using this method.
     * Every logger can be opened at least once, but not all loggers can
     * be re-opened being closed.
     */
    public abstract void open();
    
    /**
     * Closes the logger and releases system resources, if any.
     * No data can be written to a closed logger.
     * Attempting to do so will simply cause the data to be ignored. 
     * <br>
     * The default state of a logger is closed, and it must be opened first
     * before it can be used using {@link #open()}.
     * <br>
     * Closing a closed logger will have no effect.
     */
    public abstract void close();

    /**
     * Flushes the data in the logger.
     * <br>
     * Flushing a closed logger will have no effect.
     */
    public abstract void flush();

    /**
     * Clears all data in the log.
     * <br>
     * Clearing a log will have no effect on the open/close state of a log.
     * <br>
     * The implementation of this function is optional.
     */
    public void clear() {
    }

    /**
     * Locks the logger.
     * <br>
     * Similar to {@link java.util.concurrent.locks.Lock#lock()}
     */
    public abstract void lock();

    /**
     * Unlocks the logger.
     * <br>
     * Similar to {@link java.util.concurrent.locks.Lock#unlock()}
     */
    public abstract void unlock();
    
    
    /* -------------------------------------------------------------------------
     * Static delegate functions
     * -------------------------------------------------------------------------
     */
    /**
     * Delegates the static write action to the default logger instance.
     *
     * @see #writeE(Exception, Date)
     */
    public static void write(Exception e) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.writeE(e, new Date());
    }

    /**
     * Delegates the static write action to the default logger instance.
     * 
     * @see #writeE(Exception, Level, Date)
     */
    public static void write(Exception e, Level level) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.writeE(e, checkLevel(level), new Date());
    }

    /**
     * Delegates the static write action to the default logger instance.
     *
     * @see #writeO(Object, Date)
     */
    public static void write(Object obj) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.writeO(obj, new Date());
    }

    /**
     * Delegates the static write action to the default logger instance.
     *
     * @see #writeO(Object, Level, Date)
     */
    public static void write(Object obj, Level level) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.writeO(obj, checkLevel(level), new Date());
    }

    /**
     * Delegates the static write action to the default logger instance. <br>
     * Ensures that all data is logged consecutively and with the same time stamp.
     *
     * @param objArr the object array to be logged.
     *
     * @see #writeOA(Object[], Date)
     */
    public static void write(Object[] objArr) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.writeOA(objArr, new Date());
    }

    /**
     * Delegates the static write action to the default logger instance. <br>
     * Ensures that all data is logged consecutively.
     *
     * @param objArr the object array to be logged.
     *
     * @see #writeOA(Object[], Level, Date)
     */
    public static void write(Object[] objArr, Level level) {
        Logger logger = globalLogger;
        if (logger == null) return;
        logger.writeOA(objArr, checkLevel(level), new Date());
    }


}
