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

import java.util.Objects;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * This is an abstract testing class which should be extended by all testing classes.
 * 
 * @author Kaj Wortel
 */
@SuppressWarnings("unused")
public abstract class AbstractTest {

    /* ------------------------------------------------------------------------
     * Constants.
     * ------------------------------------------------------------------------
     */
    /** The fail message for initial values. */
    private static final String INIT_VALUE_MSG = "Invalid initial value!";
    
    /** The maximum number of threads which should be used. */
    public static final int MAX_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    /** The executor used for scheduling tasks. */
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(MAX_THREADS);

    
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
            if (!c.isInstance(e)) {
                e.printStackTrace();
                fail("Expected a " + c.getName() + " to be thrown, but a "
                        + e.getClass().getName() + "was thrown instead:\n");
            }
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
     * @param millis The timeout in milliseconds for each run.
     */
    public static void runAndWait(ExceptionRunner er, int amt, long millis) {
        Future<?>[] futures = new Future[amt];
        for (int i = 0; i < amt; i++) {
            futures[i] = EXECUTOR.submit(wrap(er));
        }
        
        for (Future<?> future : futures) {
            try {
                future.get(millis, TimeUnit.MILLISECONDS);
                
            } catch (InterruptedException e) {
                future.cancel(true);
                e.printStackTrace();
                fail("One of the runs was interrupted!");
                
            } catch (ExecutionException e) {
                e.printStackTrace();
                fail("One of the runs has failed!");
                
            } catch (TimeoutException e) {
                future.cancel(true);
                e.printStackTrace();
                fail("One of the runs timed out!");
            }
        }
    }

    /**
     * Checks whether the given value is set to the initial value of that type.
     * 
     * @param val The value to check.
     */
    @SuppressWarnings("SimplifiableJUnitAssertion")
    protected static void checkInitialValue(Object val) {
        if (val instanceof Boolean)
            assertTrue(INIT_VALUE_MSG, !((boolean) val));
        else if (val instanceof Byte)
            assertTrue(INIT_VALUE_MSG, (byte) val == 0);
        else if (val instanceof Character)
            assertTrue(INIT_VALUE_MSG, (char) val == 0);
        else if (val instanceof Short)
            assertTrue(INIT_VALUE_MSG, (short) val == 0);
        else if (val instanceof Integer)
            assertTrue(INIT_VALUE_MSG, (int) val == 0);
        else if (val instanceof Long)
            assertTrue(INIT_VALUE_MSG, (long) val == 0L);
        else if (val instanceof Float)
            assertTrue(INIT_VALUE_MSG, (float) val == 0.0f);
        else if (val instanceof Double)
            assertTrue(INIT_VALUE_MSG, (double) val == 0.0);
        else if (val != null)
            fail(INIT_VALUE_MSG);
    }

    /**
     * Checks whether the given two objects are the same.
     * Either the objects have the same pointer, or, if the two
     * objects have a primitive counterpart, have the same value.
     * 
     * @param o1 The first object to compare.
     * @param o2 The second object to compare.
     */
    protected static void checkSame(Object o1, Object o2) {
        if (o1 == o2) return;
        assertNotNull(o1);
        assertNotNull(o2);
        assertEquals(o1.getClass(), o2.getClass());
        assertTrue(o1 instanceof Boolean ||
                o1 instanceof Byte ||
                o1 instanceof Character ||
                o1 instanceof Short ||
                o1 instanceof Integer ||
                o1 instanceof Long ||
                o1 instanceof Float ||
                o1 instanceof Double);
        assertEquals(o1, o2);
    }

    /**
     * Switches a primitive typed class to its Object counterpart and vice versa.
     * Other classes remain unchanged.
     * 
     * @param c The class to switch.
     * 
     * @return The Object counter part of a primitive class, or vice versa. If no
     *         primitive part exists, then the same as the input.
     */
    protected static Class<?> switchPrimObjClass(Class<?> c) {
        if (c == null) {
            return null;
            
        } else if (c.isPrimitive()) {
            if (Objects.equals(c, Boolean.TYPE))
                return Boolean.class;
            else if (Objects.equals(c, Byte.TYPE))
                return Byte.class;
            else if (Objects.equals(c, Character.TYPE))
                return Character.class;
            else if (Objects.equals(c, Short.TYPE))
                return Short.class;
            else if (Objects.equals(c, Integer.TYPE))
                return Integer.class;
            else if (Objects.equals(c, Long.TYPE))
                return Long.class;
            else if (Objects.equals(c, Float.TYPE))
                return Float.class;
            else if (Objects.equals(c, Double.TYPE))
                return Double.class;
            else if (Objects.equals(c, Void.TYPE))
                return Void.class;
            throw new IllegalStateException();
            
        } else {
            if (Objects.equals(c, Boolean.class))
                return Boolean.TYPE;
            else if (Objects.equals(c, Byte.class))
                return Byte.TYPE;
            else if (Objects.equals(c, Character.class))
                return Character.TYPE;
            else if (Objects.equals(c, Short.class))
                return Short.TYPE;
            else if (Objects.equals(c, Integer.class))
                return Integer.TYPE;
            else if (Objects.equals(c, Long.class))
                return Long.TYPE;
            else if (Objects.equals(c, Float.class))
                return Float.TYPE;
            else if (Objects.equals(c, Double.class))
                return Double.TYPE;
            else if (Objects.equals(c, Void.class))
                return Void.TYPE;
            return c;
        }
    }

    /**
     * Checks whether the two classes are the same, where each primitive type
     * and its Object counterpart is regarded as the same. <br>
     * For example:
     * <table border=1'>
     *   <tr><th> Class 1 </th><th> Class 2 </th><th> Result </th></tr>
     *   <tr><td>{@code Byte}</td><td>{@code Byte}</td><td>{@code OK}</td></tr>
     *   <tr><td>{@code byte}</td><td>{@code byte}</td><td>{@code OK}</td></tr>
     *   <tr><td>{@code Byte}</td><td>{@code byte}</td><td>{@code OK}</td></tr>
     *   <tr><td>{@code byte}</td><td>{@code Byte}</td><td>{@code OK}</td></tr>
     *   <tr><td>{@code byte[]}</td><td>{@code Byte[]}</td><td>{@code OK}</td></tr>
     *   <tr><td>{@code byte[]}</td><td>{@code Byte[][]}</td><td>{@code Not OK}</td></tr>
     *   <tr><td>{@code byte[]}</td><td>{@code int[]}</td><td>{@code Not OK}</td></tr>
     *   <tr><td>{@code String}</td><td>{@code String}</td><td>{@code OK}</td></tr>
     * </table>
     * 
     * @param c1 The first class to compare.
     * @param c2 The second class to compare
     */
    protected static void checkSameClassPrim(Class<?> c1, Class<?> c2) {
        if (!Objects.equals(c1, c2)) {
            assertNotNull(c1);
            assertNotNull(c2);
            while (c1.isArray() && c2.isArray()) {
                c1 = c1.getComponentType();
                c2 = c2.getComponentType();
            }
            
            assertEquals(c1, switchPrimObjClass(c2));
        }
    }
    

}
