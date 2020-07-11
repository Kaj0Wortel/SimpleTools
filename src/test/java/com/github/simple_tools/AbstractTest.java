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

import com.github.simple_tools.data.array.ArrayTools;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This is an abstract testing class which should be extended by all testing classes.
 * 
 * @author Kaj Wortel
 */
@SuppressWarnings("unused")
public abstract class AbstractTest {

    /* ------------------------------------------------------------------------
     * Inner-classes.
     * ------------------------------------------------------------------------
     */
    /**
     * Same interface as {@link Runnable}, but now the run function is allowed
     * to throw an exception.
     */
    @FunctionalInterface
    protected interface ExceptionRunner {

        /**
         * The function to be invoked by the runner.
         *
         * @throws Exception The exception to be thrown.
         */
        void run()
                throws Exception;


    }


    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Runs the given code and tests whether the provided exception is thrown.
     *
     * @param c The class of the expected exception.
     * @param er The exception runner to execute.
     */
    protected static void expEx(Class<? extends Exception> c, ExceptionRunner er) {
        try {
            er.run();
            fail("Expected a " + c.getName() + " to be thrown, but it terminated normally.");

        } catch (Exception e) {
            assertTrue("Expected a " + c.getName() + " to be thrown, but a "
                    + e.getClass().getName() + "was thrown instead.", c.isInstance(e));
        }
    }

    /**
     * Wraps an {@link ExceptionRunner} in a {@link Runnable} and
     * re-throws any exception as a {@link RuntimeException}
     * 
     * @param er The {@link ExceptionRunner} to convert.
     * 
     * @return A {@link Runnable} which runs the {@link ExceptionRunner}.
     */
    protected static Runnable wrap(ExceptionRunner er) {
        return () -> {
            try {
                er.run();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Generates an array of strings containing the number {@code 0} up to {@code len}.
     *
     * @param len The length of the array to return.
     *
     * @return A deterministic array of strings.
     */
    protected static String[] genStrArr(int len) {
        String[] strings = new String[len];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Integer.toString(i);
        }
        return strings;
    }

    /**
     * Generates an array of integers containing the number {@code 0} up to {@code len}.
     *
     * @param len The length of the array to return.
     *
     * @return A deterministic array of integers.
     */
    protected static int[] genIntArr(int len) {
        int[] ints = new int[len];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }
        return ints;
    }

    /**
     * Generates an index to be used as message from the given indices.
     *
     * @param indices The indices used to generate the message from.
     *
     * @return A message from the provided indices.
     */
    protected static String genIndex(Object... indices) {
        if (indices == null) return "";
        StringBuilder sb = new StringBuilder("@(");
        boolean first = true;
        for (Object index : indices) {
            if (first) first = false;
            else sb.append(",");
            sb.append((index == null ? "null" : index.toString()));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Fills the given 2D array with the given value.
     *
     * @param arr The array to fill.
     * @param val The value to fill the array with.
     */
    protected static <T> T[] fill(T[] arr, Object val) {
        for (T t : arr) {
            for (int j = 0; j < ArrayTools.length(t); j++) {
                ArrayTools.set(t, j, val);
            }
        }
        return arr;
    }

    /**
     * Fills the given 2D array with values from {@code init} to
     * {@code init + arr.getWidth() * arr.getHeight()}. The rows are filled incrementally.
     *
     * @param arr The array to fill.
     * @param init The initial number in the array.
     */
    public static <T> T[] incrFill(T[] arr, int init) {
        if (arr.length == 0) return arr;
        int val = init;
        for (int j = 0; j < ArrayTools.length(arr[0]); j++) {
            for (T t : arr) {
                ArrayTools.set(t, j, val++);
            }
        }
        return arr;
    }

    /**
     * Creates and runs {@code amt} threads and waits until they are all done.
     *
     * @param er     The runnable to run for each thread.
     * @param amt    The amount of threads to spawn.
     * @param millis The timeout in milliseconds.
     */
    public static void runAndWait(ExceptionRunner er, int amt, long millis) {
        Thread ct = Thread.currentThread();
        AtomicBoolean exceptionOccurred = new AtomicBoolean(false);
        ThreadGroup tg = new ThreadGroup("runners") {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println(t.getName()+ ": " + System.lineSeparator() +
                        "  " + Arrays.toString(e.getStackTrace())
                        .replaceAll(", ", "," + System.lineSeparator() + "  "));
                ct.interrupt();
                exceptionOccurred.set(true);
            }
        };
        Thread[] threads = new Thread[amt];
        for (int i = 0; i < amt; i++) {
            threads[i] = new Thread(tg, wrap(er), "runner " + i);
        }
        for (int i = 0; i < amt; i++) {
            threads[i].start();
        }
        long start = System.currentTimeMillis();
        try {
        for (int i = 0; i < amt; i++) {
                long timeout = start - System.currentTimeMillis() + millis;
                if (timeout <= 0) fail("Timed out!");
                threads[i].join(timeout);
            }
        } catch (Throwable e) {
            fail("One of the runs has failed!");
        }
        if (exceptionOccurred.get()) {
            fail("One of the runs has thrown an exception!");
        }
    }


}
