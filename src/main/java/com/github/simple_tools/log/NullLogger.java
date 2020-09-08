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

import java.util.Date;

/**
 * A {@link Logger} implementation which ignores all logged data.
 * 
 * @author Kaj Wortel
 */
public class NullLogger
        extends Logger {
    @Override
    public void writeE(Exception e, Level level, Date timeStamp) {
    }

    @Override
    public void writeO(Object obj, Level level, Date timeStamp) {
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void clear() {
    }

    @Override
    public void lock() {
    }

    @Override
    public void unlock() {
    }


}
