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

package com.github.simple_tools;

import java.util.concurrent.locks.Lock;

public final class MultiTool {

    /* ------------------------------------------------------------------------
     * Constructor.
     * ------------------------------------------------------------------------
     */
    @Deprecated
    private MultiTool() { }

    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    public static String toFilledString(byte b, int radix) {
        if (radix < 2 || radix > 36) throw new IllegalArgumentException();
        String text = Integer.toString(b & 0xFF, radix);
        int length;
        if (radix <= 2) length = 8;
        else if (radix <= 3) length = 6;
        else if (radix <= 6) length = 4;
        else if (radix <= 15) length = 3;
        else length = 2;
        return "0".repeat(length - text.length()) + text;
    }

    public static String toFilledString(short s, int radix) {
        if (radix < 2 || radix > 36) throw new IllegalArgumentException();
        String text = Integer.toString(s & 0xFFFF, radix);
        int length;
        if (radix <= 2) length = 16;
        else if (radix <= 3) length = 11;
        else if (radix <= 4) length = 8;
        else if (radix <= 6) length = 7;
        else if (radix <= 9) length = 6;
        else if (radix <= 15) length = 5;
        else length = 4;
        return "0".repeat(length - text.length()) + text;
    }
    
    public static String toFilledString(char c, int radix) {
        return toFilledString((short) c, radix);
    }

    public static String toFilledString(int i, int radix) {
        if (radix < 2 || radix > 36) throw new IllegalArgumentException();
        String text = Integer.toString(i, radix);
        int length;
        if (radix <= 2) length = 32;
        else if (radix <= 3) length = 21;
        else if (radix <= 4) length = 16;
        else if (radix <= 5) length = 14;
        else if (radix <= 6) length = 13;
        else if (radix <= 7) length = 12;
        else if (radix <= 9) length = 11;
        else if (radix <= 11) length = 10;
        else if (radix <= 15) length = 9;
        else if (radix <= 23) length = 8;
        else length = 7;
        
        if (text.startsWith("-"))
            return "-" + "0".repeat(length - text.length() + 1) + text.substring(1);
        else
            return " " + "0".repeat(length - text.length()) + text;
    }

    public static String toFilledString(long l, int radix) {
        if (radix < 2 || radix > 36) throw new IllegalArgumentException();
        String text = Long.toString(l, radix);
        int length;
        if (radix <= 2) length = 64;
        else if (radix <= 3) length = 41;
        else if (radix <= 4) length = 32;
        else if (radix <= 5) length = 28;
        else if (radix <= 6) length = 25;
        else if (radix <= 7) length = 23;
        else if (radix <= 8) length = 22;
        else if (radix <= 9) length = 21;
        else if (radix <= 10) length = 20;
        else if (radix <= 11) length = 19;
        else if (radix <= 13) length = 18;
        else if (radix <= 15) length = 17;
        else if (radix <= 19) length = 16;
        else if (radix <= 23) length = 15;
        else if (radix <= 30) length = 14;
        else length = 13;
        
        if (text.startsWith("-"))
            return "-" + "0".repeat(length - text.length() + 1) + text.substring(1);
        else
            return " " + "0".repeat(length - text.length()) + text;
    }
    
    public static void runLock(Lock lock, Runnable r) {
        lock.lock();
        try {
            r.run();
        } finally {
            lock.unlock();
        }
    }
    
}
