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

import lombok.Setter;

import java.io.*;

/**
 * A {@link Logger} implementation for logging to a file.
 * <br>
 * This logger can be opened and closed multiple times.
 * 
 * @author Kaj Wortel
 */
public class FileLogger
        extends DefaultLogger {
    
    /* -------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    /** The default log file to write to. */
    private File logFile;
    /** The writer used to write the log data to the file. */
    private PrintWriter writer;
    /** Whether to append to the given file. */
    @Setter
    private boolean append;
    /** Whether to append to the given file initially. */
    @Setter
    private boolean clearPrev;
    
    
    /* -------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates a new file logger which logs to the given file.
     * By default, removes all data from the file at initialization, but
     * appends after closing an re-opening.
     *
     * @param fileName The name of the file to log to.
     */
    public FileLogger(String fileName) {
        this(new File(fileName));
    }

    /**
     * Creates a new file logger which logs to the given file.
     * By default, removes all data from the file at initialization, but
     * appends after closing and re-opening.
     *
     * @param file The file to log to.
     */
    public FileLogger(File file) {
        this(file, true, true);
    }

    /**
     * Creates a new file logger which logs to the given file.
     * It only appends the data for the first time if {@code appendBeginOnly == true},
     * and after being closed an re-opened it appends data only if {@code append == true}.
     *
     * @param fileName The Name of the file to log to.
     * @param append Whether to append to the log file after the stream has been closed
     *     and then re-opened.
     * @param clearPrev Whether to initially remove the original log file.
     */
    public FileLogger(String fileName, boolean append, boolean clearPrev) {
        this(new File(fileName), append, clearPrev);
    }

    /**
     * Creates a new file logger which logs to the given file.
     * It only appends the data for the first time if {@code appendBeginOnly == true},
     * and after being closed an re-opened it appends data only if {@code append == true}.
     *
     * @param file The file to log to.
     * @param append Whether to append to the log file after the stream has been closed
     *     and then re-opened.
     * @param clearPrev Whether to initially remove the original log file.
     */
    public FileLogger(File file, boolean append, boolean clearPrev) {
        this.append = append;
        this.clearPrev = clearPrev;
        setFile(file);
    }

    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates a new writer.
     *
     * @param append whether to append to the current log file or to
     *     overwrite the file.
     */
    protected void createWriter(boolean append)
            throws IOException {
        // Close any previously open writers.
        close();

        // Create the new writer.
        writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile, append)));

        // Add a header for clean files.
        if (!append) writeHeader();
    }

    @Override
    protected void writeText(String text) {
        if (isClosed()) return;
        writer.print(text);
    }

    /**
     * Sets the log file.
     *
     * @param fileName The name of the new log file.
     */
    public void setFile(String fileName) {
        setFile(new File(fileName));
    }
    
    /**
     * Sets the log file.
     *
     * @param file The name of the log file.
     */
    public void setFile(File file) {
        if (file == null)
            throw new NullPointerException();
        lock();
        try {
            logFile = file;
            if (logFile.getParentFile() != null) {
                logFile.getParentFile().mkdirs();
            }
            if (closed) {
                if (clearPrev) {
                    logFile.delete();
                }
                
            } else {
                close();
                if (clearPrev) {
                    logFile.delete();
                }
                open();
            }

        } finally {
            unlock();
        }
    }
    
    @Override
    public void open() {
        lock();
        try {
            if (!isClosed()) return;
            createWriter(append);
            closed = false;
            if (useHeader) {
                writeHeader();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            
        } finally {
            unlock();
        }
    }
    
    @Override
    public void close() {
        lock();
        try {
            if (isClosed()) return;
            if (writer != null) writer.close();
            writer = null;
            closed = true;
            
        } finally {
            unlock();
        }
    }
    
    @Override
    public void flush() {
        lock();
        try {
            if (writer != null) {
                writer.flush();
            }
            
        } finally {
            unlock();
        }
    }

    /**
     * Clears the log file.
     */
    @Override
    public void clear() {
        lock();
        try {
            if (closed) {
                logFile.delete();
            } else {
                // Close the previous log file.
                writer.close();
                writer = null;
                // Create a new writer.
                createWriter(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
            
        } finally {
            unlock();
        }
    }

    /**
     * @return Whether to append to the log file after the stream
     *     has been closed and then re-opened.
     */
    public boolean getAppend() {
        return append;
    }

    /**
     * @return Whether the logger initially removes the log file.
     */
    public boolean getClearPrev() {
        return clearPrev;
    }
    
    
}
