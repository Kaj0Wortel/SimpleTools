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

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A {@link Logger} implementation for logging to an output stream.
 * <br>
 * This logger can be closed only once.
 * 
 * @author Kaj Wortel
 */
public class StreamLogger
        extends DefaultLogger {

    /* -------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    /** The stream used to output the log data. */
    private OutputStream stream;
    /** The used character set. */
    @Getter
    @Setter
    private Charset charset = StandardCharsets.UTF_8;


    /* -------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates a new stream logger which logs to the default {@code System.out}.
     */
    public StreamLogger() {
        this(System.out);
    }

    /**
     * Creates a new stream logger which logs to the given output stream.
     *
     * @param outputStream The output stream to log to. The default is {@code System.out}.
     */
    public StreamLogger(OutputStream outputStream) {
        stream = outputStream;
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    protected void writeText(String text)
            throws IOException {
        if (stream != null) {
            stream.write(text.getBytes(charset));
        }
    }
    
    @Override
    public void open() {
        lock();
        try {
            closed = (stream == null);
            
        } finally {
            unlock();
        }
    }

    @Override
    public void close() {
        lock();
        try {
            closed = true;
            if (stream != null) {
                stream.close();
                stream = null;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            
        } finally {
            unlock();
        }
    }

    @Override
    public void flush() {
        lock();
        try {
            stream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            
        } finally {
            unlock();
        }
    }
    
    
}
