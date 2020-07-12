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

package com.github.simple_tools.data.array;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import com.github.simple_tools.MultiTool;
import com.github.simple_tools.iterators.GeneratorIterator;

/**
 * The {@code Array} class provides static methods to dynamically resolve
 * primitive typed arrays mixed with object arrays.
 * <br>
 * It re-implements the get, set and length functions for primitive types
 * of the {@link java.lang.reflect.Array} class, and the toDeepString function
 * of the {@link java.util.Arrays} class in pure java code.
 * Because of this, JIT can optimize the code better compared to the
 * native implementations of the reflect array class. <br>
 * <br>
 * The functions permit widening conversions to occur during a get or set
 * operation, but throws an {@code ClassCastException} if a narrowing
 * conversion would occur.
 * <br>
 * Additionally provides simple tool functions such as {@link #swap(Object, int, int)},
 * {@link #calcHashCode(Object)} and {@link #toList(Object)} for both object
 * and primitive typed arrays.
 * 
 * @author Kaj Wortel
 * 
 * @see java.lang.reflect.Array
 * @see java.util.Arrays
 */
@SuppressWarnings("unused")
public final class ArrayTools {
    
    /* ------------------------------------------------------------------------
     * Constants.
     * ------------------------------------------------------------------------
     */
    /** The message used for the index out of bounds exception. */
    private static final String OUT_OF_RANGE_MSG = "The given range [%d, %d] " +
            "does not fit in the range of the size of the array [%d, %d].";
    /** The begin hash code of the {@link #calcHashCode(Object)} functions. */
    private static final int HASH_RESULT_BEGIN = 41;
    /** The value to multiply with of the {@link #calcHashCode(Object)} functions. */
    private static final int HASH_RESULT_MUL = 37;
    
    
    /* ------------------------------------------------------------------------
     * Inner-classes.
     * ------------------------------------------------------------------------
     */
    /**
     * Processor used to iterate over (a part of) a array.
     */
    @FunctionalInterface
    public interface ArrayProcessor {
        
        /**
         * Returns the element to set at the given index in the array,
         * or return {@code null} to skip this index.
         * 
         * @param index The index to set.
         * 
         * @return The new value of the array at the given index, or {@code null}
         *     to not modify this index.
         */
        Object process(int index);
        
        
    }
    
    /**
     * Generator used to generate indices for an array.
     */
    @FunctionalInterface
    public interface IndexGenerator {
        
        /**
         * Generates the next index.
         * 
         * @param array The array to generate the next index for.
         * @param prevIndex The previously returned index, or {@code -1} if it
         *     is the first index.
         * 
         * @return The next index to process, or a negative number to stop iterating.
         */
        int nextIndex(Object array, int prevIndex);
        
        
    }
    
    private abstract static class ReadOnlyArrayList<V>
            extends AbstractList<V>
            implements List<V>, RandomAccess {
        
        @Override
        public abstract int size();

        @Override
        public abstract V get(int i);
        
        @Override
        public abstract V set(int i, V v);
    }
    
    
    /* ------------------------------------------------------------------------
     * Constructor.
     * ------------------------------------------------------------------------
     */
    /**
     * This is a static singleton class. No instances should be made.
     *
     * @deprecated No instances should be made.
     */
    @Deprecated
    private ArrayTools() { }
    
    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /* ------------------------------------------------------------------------
     * To string functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a deep string representation of the given multi-dimensional array. <br>
     * <br>
     * If the given object is not an array, then simply {@link Object#toString()}.
     * is used for the string representation. This is also used the values
     * inside the array. <br>
     * <br>
     * This function supports multi-dimensional arrays.
     * <h2>Format</h2>
     * The format of the returned string is as follows:
     * <table border='1'>
     *   <tr><th> #dimensions </th><th> #elements </th><th> Result </th></tr>
     *   <tr><td align='center'>1</td><td align='center'>0</td><td>{@code "[]"}</td></tr>
     *   <tr><td align='center'>1</td><td align='center'>1</td><td>{@code "[v1]"}</td></tr>
     *   <tr><td align='center'>1</td><td align='center'>3</td><td>{@code "[v1, v2, v3]"}</td></tr>
     *   <tr><td align='center'>2</td><td align='center'>0,0</td><td>{@code "[]"}</td></tr>
     *   <tr><td align='center'>2</td><td align='center'>1,0</td><td>{@code "[[]]"}</td></tr>
     *   <tr><td align='center'>2</td><td align='center'>2,0</td><td>{@code "[[], []]"}</td></tr>
     *   <tr><td align='center'>2</td><td align='center'>2,1</td><td>{@code "[[v1], [v2]]"}</td></tr>
     *   <tr><td align='center'>2</td><td align='center'>2,2</td><td>{@code "[[v1, v2], [v3, v4]]"}</td></tr>
     * </table>
     * Here, {@code v1, v2, ... , vn} are the string values are of the objects inside the array. <br>
     * Note that the separators of both the elements and the arrays are {@code ", "}, or a colon
     * followed by a space. <br>
     * If an element or array is {@code null}, then the entire contents of that element or array
     * is replaced by {@code "null"}.
     * 
     * @param array The array to print.
     * 
     * @return A string representation of the given array.
     */
    public static String toDeepString(Object array) {
        StringBuilder sb = new StringBuilder();
        toDeepString(sb, array);
        return sb.toString();
    }
    
    /**
     * Creates a deep string representation of the given array.
     * 
     * @param sb The builder to output the generated string to.
     * @param array The array to create the representation of.
     */
    private static void toDeepString(StringBuilder sb, Object array) {
        if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            sb.append("[");
            boolean first = true;
            for (Object obj : arr) {
                if (first) first = false;
                else sb.append(", ");
                toDeepString(sb, obj);
            }
            sb.append("]");
            
        } else if (array instanceof boolean[]) {
            boolean[] arr = (boolean[]) array;
            sb.append("[");
            boolean first = true;
            for (boolean b : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(b);
            }
            sb.append("]");
            
        } else if (array instanceof byte[]) {
            byte[] arr = (byte[]) array;
            sb.append("[");
            boolean first = true;
            for (byte b : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(b);
            }
            sb.append("]");
            
        } else if (array instanceof char[]) {
            char[] arr = (char[]) array;
            sb.append("[");
            boolean first = true;
            for (char c : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(c);
            }
            sb.append("]");
            
        } else if (array instanceof short[]) {
            short[] arr = (short[]) array;
            sb.append("[");
            boolean first = true;
            for (short s : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(s);
            }
            sb.append("]");
            
        } else if (array instanceof int[]) {
            int[] arr = (int[]) array;
            sb.append("[");
            boolean first = true;
            for (int i : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(i);
            }
            sb.append("]");
            
        } else if (array instanceof long[]) {
            long[] arr = (long[]) array;
            sb.append("[");
            boolean first = true;
            for (long l : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(l);
            }
            sb.append("]");
            
        } else if (array instanceof float[]) {
            float[] arr = (float[]) array;
            sb.append("[");
            boolean first = true;
            for (float v : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(v);
            }
            sb.append("]");
            
        } else if (array instanceof double[]) {
            double[] arr = (double[]) array;
            sb.append("[");
            boolean first = true;
            for (double v : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(v);
            }
            sb.append("]");
            
        } else if (array == null) {
            sb.append("null");
            
        } else {
            sb.append(array.toString());
        }
    }
    
    /**
     * Converts the given integer typed number array to a string representation
     * in base {@code radix}. <br>
     * This means that the types {@code byte}, {@code short}, {@code char},
     * {@code int}, {@code long}, their class equivalents, and custom types
     * extending {@link Number} are allowed. <br>
     * Floating point numbers ({@code float} and {@code double} are INVALID,
     * as well as classes not extending {@link Number}.
     * 
     * @param array The integer typed array to print.
     * @param radix The radix of the values.
     * 
     * @return The String representation as specified by {@link Arrays#toString(Object[])}.
     * 
     * @throws IllegalArgumentException If the given object has the wrong type.
     * 
     * @see Integer#toString(int, int)
     * @see Long#toString(long, int)
     */
    public static String toDeepString(Object array, int radix)
            throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();
        toDeepString(sb, array, radix);
        return sb.toString();
    }
    
    /**
     * Converts a integer typed multi-dimensional array to a string representation.
     * 
     * @param sb The string builder to store the result in.
     * @param array The array to process.
     * @param radix The radix used.
     * 
     * @throws IllegalArgumentException If the given object has the wrong type.
     * 
     * @see #toDeepString(Object, int)
     */
    public static void toDeepString(StringBuilder sb, Object array, int radix)
            throws IllegalArgumentException {
        if (array instanceof Object[]) {
            if (!(array instanceof Number[])) {
                throw new IllegalArgumentException("Argument is not a number array!");
                
            } else if (array instanceof Float[]) {
                throw new IllegalArgumentException(
                        "Expected an integer typed number array, "
                                + "but found a floating point array (Float[]).");
                
            } else if (array instanceof Double[]) {
                throw new IllegalArgumentException(
                        "Expected an integer typed number array, "
                                + "but found a floating point array (Double[]).");
                
            } else {
                sb.append("[");
                Number[] arr = (Number[]) array;
                boolean first = true;
                for (Number num : arr) {
                    if (first) first = false;
                    else sb.append(", ");
                    toDeepString(sb, num, radix);
                }
                sb.append("]");
            }
            
        } else if (array instanceof byte[]) {
            sb.append("[");
            byte[] arr = (byte[]) array;
            boolean first = true;
            for (byte b : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(MultiTool.toFilledString(b, radix));
            }
            sb.append("]");
            
        } else if (array instanceof char[]) {
            sb.append("[");
            char[] arr = (char[]) array;
            boolean first = true;
            for (char c : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(MultiTool.toFilledString(c, radix));
            }
            sb.append("]");
            
        } else if (array instanceof short[]) {
            sb.append("[");
            short[] arr = (short[]) array;
            boolean first = true;
            for (short s : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(MultiTool.toFilledString(s, radix));
            }
            sb.append("]");
            
        } else if (array instanceof int[]) {
            sb.append("[");
            int[] arr = (int[]) array;
            boolean first = true;
            for (int i : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(MultiTool.toFilledString(i, radix));
            }
            sb.append("]");
            
        } else if (array instanceof long[]) {
            sb.append("[");
            long[] arr = (long[]) array;
            boolean first = true;
            for (long l : arr) {
                if (first) first = false;
                else sb.append(", ");
                sb.append(MultiTool.toFilledString(l, radix));
            }
            sb.append("]");
            
        } else if (array == null) {
            sb.append("null");
            
        } else if (array instanceof boolean[]) {
            throw new IllegalArgumentException(
                    "Expected an integer typed number array, "
                            + "but found a boolean array (boolean[]).");
            
        } else if (array instanceof float[]) {
            throw new IllegalArgumentException(
                    "Expected an integer typed number array, "
                            + "but found a floating point array (float[]).");
            
        } else if (array instanceof double[]) {
            throw new IllegalArgumentException(
                    "Expected an integer typed number array, "
                            + "but found a floating point array (double[]).");
            
        } else { // {@code array} is not an array.
            if (array instanceof Byte) {
                sb.append(MultiTool.toFilledString((byte) array, radix));
                
            } else if (array instanceof Short) {
                sb.append(MultiTool.toFilledString((short) array, radix));
                
            } else if (array instanceof Integer) {
                sb.append(MultiTool.toFilledString((int) array, radix));
                
            } else if (array instanceof Long) {
                sb.append(MultiTool.toFilledString((long) array, radix));
                
            } else {
                throw new IllegalArgumentException(
                        "Expected an integer typed number, but found: " + array);
            }
        }
    }
    
    /**
     * Creates a deep string representation of the given array.
     * 
     * @param array The array to convert.
     * 
     * @return A deep string representation of {@code array}.
     */
    public static String deepToString(Object array) {
        if (array instanceof Object[]) return Arrays.deepToString((Object[]) array);
        return toDeepString(array);
    }
    
    /**
     * Creates a deep string representation of the given array containing numbers,
     * and converts all numbers with the given radix.
     * 
     * @param array The array to convert.
     * @param radix The radix of the values.
     * 
     * @return A deep string representation of {@code array} using the given radix.
     * 
     * @see Arrays#deepToString(Object[])
     */
    public static String deepToString(Object array, int radix) {
        StringBuilder sb = new StringBuilder();
        deepToString(array, radix, sb);
        return sb.toString();
    }
    
    /**
     * Creates a deep string representation of the given array containing numbers,
     * and converts all numbers with the given radix. <br>
     * This function is invoked recursively to determine the end result.
     * 
     * @param array The array to convert.
     * @param radix The radix of the values.
     * @param sb    The string builder used to store the result.
     */
    private static void deepToString(Object array, int radix, StringBuilder sb) {
        if (array == null) {
            sb.append("null");
            return;
        }
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Argument is not an array!");
        }
        if (!array.getClass().getComponentType().isArray()) {
            sb.append(toDeepString(array, radix));
            return;
        }
        
        sb.append("[");
        Object[] arr = (Object[]) array;
        boolean first = true;
        for (Object o : arr) {
            if (first) first = false;
            else sb.append(", ");
            deepToString(o, radix, sb);
        }
        sb.append("]");
    }
    
    
    /* ------------------------------------------------------------------------
     * Get length functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Returns the length of the specified array object, as an {@code int}.
     *
     * @param array The array
     * 
     * @return The length of the array
     * 
     * @throws IllegalArgumentException If the object argument is not an array
     * 
     * @see java.lang.reflect.Array#getLength(Object) &nbsp reflect entry point.
     */
    public static int length(Object array)
            throws IllegalArgumentException {
        if (array instanceof Object[]) return ((Object[]) array).length;
        else if (array instanceof boolean[]) return ((boolean[]) array).length;
        else if (array instanceof byte[]) return ((byte[]) array).length;
        else if (array instanceof char[]) return ((char[]) array).length;
        else if (array instanceof short[]) return ((short[]) array).length;
        else if (array instanceof int[]) return ((int[]) array).length;
        else if (array instanceof long[]) return ((long[]) array).length;
        else if (array instanceof float[]) return ((float[]) array).length;
        else if (array instanceof double[]) return ((double[]) array).length;
        else if (array == null)
            throw new NullPointerException("Array is null!");
        else if (!array.getClass().isArray())
            throw new IllegalArgumentException("Argument is not an array!");
        else
            throw new IllegalStateException();
    }
    
    
    /* ------------------------------------------------------------------------
     * Get functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Returns the value of the indexed component in the specified
     * array object. The value is automatically wrapped in an object
     * if it has a primitive type.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The (possibly wrapped) value of the indexed component in the specified array.
     * 
     * @throws NullPointerException If the specified object is null.
     * @throws IllegalArgumentException If the specified object is not an array
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array.
     * 
     * @see java.lang.reflect.Array#get(Object, int) &nbsp reflect entry point.
     * @see #getBoolean(Object, int)
     * @see #getByte(Object, int)
     * @see #getShort(Object, int)
     * @see #getInt(Object, int)
     * @see #getLong(Object, int)
     * @see #getFloat(Object, int)
     * @see #getDouble(Object, int)
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object array, int index)
            throws IllegalArgumentException {
        if (array instanceof Object[]) return (T) ((Object[]) array)[index];
        else if (array instanceof boolean[]) return (T) (Boolean) ((boolean[]) array)[index];
        else if (array instanceof byte[]) return (T) (Byte) ((byte[]) array)[index];
        else if (array instanceof short[]) return (T) (Short) ((short[]) array)[index];
        else if (array instanceof char[]) return (T) (Character) ((char[]) array)[index];
        else if (array instanceof int[]) return (T) (Integer) ((int[]) array)[index];
        else if (array instanceof long[]) return (T) (Long) ((long[]) array)[index];
        else if (array instanceof float[]) return (T) (Float) ((float[]) array)[index];
        else if (array instanceof double[]) return (T) (Double) ((double[]) array)[index];
        else if (array == null) {
            throw new NullPointerException("Array is null!");    
        } else if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Argument is not an array!");
        }
        throw new IllegalStateException();
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code boolean}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getBoolean(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static boolean getBoolean(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((boolean[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code byte}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getByte(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static byte getByte(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((byte[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code boolean}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getShort(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static short getShort(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((short[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code char}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getChar(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static char getChar(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((char[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code short}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getInt(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static int getInt(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((int[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code long}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getLong(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static long getLong(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((long[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code float}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getFloat(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static float getFloat(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((float[]) array)[index];
    }
    
    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code double}.
     *
     * @param array The array.
     * @param index The index.
     * 
     * @return The value of the indexed component in the specified array.
     * 
     * @throws ClassCastException If the specified object is not
     *     an array, or if the indexed element cannot be converted to the
     *     return type by an identity or widening conversion
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to the
     *     length of the specified array
     * 
     * @see java.lang.reflect.Array#getDouble(Object, int) &nbsp reflect entry point.
     * @see #get(Object, int)
     */
    public static double getDouble(Object array, int index)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        return ((double[]) array)[index];
    }
    
    
    /* -------------------------------------------------------------------------
     * Set functions.
     * -------------------------------------------------------------------------
     */
    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified new value. The new value is first
     * automatically unwrapped if the array has a primitive component type.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param value The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the specified object argument
     *     is not an array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array
     * @throws ClassCastException If {@code value} is not the same type
     *     as {@code array}.
     * 
     * @see java.lang.reflect.Array#set(Object, int, Object) &nbsp reflect entry point
     * @see #setBoolean(Object, int, boolean)
     * @see #setByte(Object, int, byte)
     * @see #setShort(Object, int, short)
     * @see #setInt(Object, int, int)
     * @see #setLong(Object, int, long)
     * @see #setFloat(Object, int, float)
     * @see #setDouble(Object, int, double)
     */
    public static void set(Object array, int index, Object value)
            throws IllegalArgumentException, ArrayIndexOutOfBoundsException,
            ClassCastException {
        if (array instanceof Object[]) ((Object[]) array)[index] = value;
        else if (array instanceof boolean[]) ((boolean[]) array)[index] = (boolean) value;
        else if (array instanceof byte[]) ((byte[]) array)[index] = (byte) value;
        else if (array instanceof short[]) ((short[]) array)[index] = (short) value;
        else if (array instanceof char[]) ((char[]) array)[index] = (char) value;
        else if (array instanceof int[]) ((int[]) array)[index] = (int) value;
        else if (array instanceof long[]) ((long[]) array)[index] = (long) value;
        else if (array instanceof float[]) ((float[]) array)[index] = (float) value;
        else if (array instanceof double[]) ((double[]) array)[index] = (double) value;
        else if (array == null)
            throw new NullPointerException("Array is null!");    
        else if (!array.getClass().isArray())
            throw new IllegalArgumentException("Argument is not an array!");
        else
            throw new IllegalStateException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code boolean} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param b The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a boolean array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setBoolean(Object, int, boolean) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setBoolean(Object array, int index, boolean b)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof boolean[]) ((boolean[]) array)[index] = b;
        else if (array instanceof Boolean[]) ((Boolean[]) array)[index] = b;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code byte} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param b The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a byte array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setByte(Object, int, byte) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setByte(Object array, int index, byte b)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof byte[]) ((byte[]) array)[index] = b;
        else if (array instanceof Byte[]) ((Byte[]) array)[index] = b;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code short} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param s The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a short array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setShort(Object, int, short) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setShort(Object array, int index, short s)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof short[]) ((short[]) array)[index] = s;
        else if (array instanceof Short[]) ((Short[]) array)[index] = s;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code char} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param c The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a char array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setChar(Object, int, char) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setChar(Object array, int index, char c)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof char[]) ((char[]) array)[index] = c;
        else if (array instanceof Character[]) ((Character[]) array)[index] = c;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code int} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param i The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a int array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setInt(Object, int, int) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setInt(Object array, int index, int i)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof int[]) ((int[]) array)[index] = i;
        else if (array instanceof Integer[]) ((Integer[]) array)[index] = i;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code long} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param l The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a long array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setLong(Object, int, long) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setLong(Object array, int index, long l)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof long[]) ((long[]) array)[index] = l;
        else if (array instanceof Long[]) ((Long[]) array)[index] = l;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code boolean} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param f The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a float array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setFloat(Object, int, float) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setFloat(Object array, int index, float f)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof float[]) ((float[]) array)[index] = f;
        else if (array instanceof Float[]) ((Float[]) array)[index] = f;
        else throw new IllegalArgumentException();
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code double} value.
     * 
     * @param array The array.
     * @param index The index into the array.
     * @param d The new value of the indexed component.
     * 
     * @throws IllegalArgumentException If the given object {@code array} is not a double array.
     * @throws ArrayIndexOutOfBoundsException If the specified {@code index}
     *     argument is negative, or if it is greater than or equal to
     *     the length of the specified array.
     * 
     * @see java.lang.reflect.Array#setDouble(Object, int, double) &nbsp reflect entry point.
     * @see #set(Object, int, Object)
     */
    public static void setDouble(Object array, int index, double d)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (array instanceof double[]) ((double[]) array)[index] = d;
        else if (array instanceof Double[]) ((Double[]) array)[index] = d;
        else throw new IllegalArgumentException();
    }
    
    
    /* ------------------------------------------------------------------------
     * List functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Wraps an array in a list.
     * Also wraps primitive data types in their class counter part.
     * 
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * 
     * @param array The array to convert.
     * @param <V>   The type of list to create.
     * 
     * @return A write-through list representation of the provided array.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> asList(Object array) {
        if (array instanceof Object[]) return asList((V[]) array);
        if (array instanceof boolean[]) return (List<V>) asList((boolean[]) array);
        if (array instanceof byte[]) return (List<V>) asList((byte[]) array);
        if (array instanceof char[]) return (List<V>) asList((char[]) array);
        if (array instanceof short[]) return (List<V>) asList((short[]) array);
        if (array instanceof int[]) return (List<V>) asList((int[]) array);
        if (array instanceof long[]) return (List<V>) asList((long[]) array);
        if (array instanceof float[]) return (List<V>) asList((float[]) array);
        if (array instanceof double[]) return (List<V>) asList((double[]) array);
        if (array == null)
            throw new NullPointerException("Array is null!");
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("Argument is not an array!");
        else
            throw new IllegalStateException();
    }

    /**
     * Wraps an Object array in a list.
     * Equivalent to {@link Arrays#asList(Object...)}.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     *
     * @param arr The array to convert.
     * @param <V>   The type of list to create.
     *
     * @return A write-through list representation of the provided array.
     */
    public static <V> List<V> asList(V[] arr) {
        return Arrays.asList(arr);
    }

    /**
     * Wraps a {@code boolean[]} array in a {@link List<Boolean>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Boolean> asList(final boolean[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Boolean get(int i) {
                return arr[i];
            }

            @Override
            public Boolean set(int i, Boolean b) {
                final boolean old = arr[i];
                arr[i] = b;
                return old;
            }
        };
    }
    
    /**
     * Wraps a {@code byte[]} array in a {@link List<Byte>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Byte> asList(final byte[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Byte get(int i) {
                return arr[i];
            }

            @Override
            public Byte set(int i, Byte b) {
                final byte old = arr[i];
                arr[i] = b;
                return old;
            }
        };
    }

    /**
     * Wraps a {@code char[]} array in a {@link List<Character>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Character> asList(final char[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Character get(int i) {
                return arr[i];
            }

            @Override
            public Character set(int i, Character c) {
                final char old = arr[i];
                arr[i] = c;
                return old;
            }
        };
    }
    
    /**
     * Wraps a {@code short[]} array in a {@link List<Short>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Short> asList(final short[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Short get(int i) {
                return arr[i];
            }

            @Override
            public Short set(int i, Short s) {
                final short old = arr[i];
                arr[i] = s;
                return old;
            }
        };
    }
    
    /**
     * Wraps a {@code int[]} array in a {@link List<Integer>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Integer> asList(final int[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Integer get(int i) {
                return arr[i];
            }

            @Override
            public Integer set(int i, Integer b) {
                final int old = arr[i];
                arr[i] = b;
                return old;
            }
        };
    }

    /**
     * Wraps a {@code long[]} array in a {@link List<Long>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Long> asList(final long[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Long get(int i) {
                return arr[i];
            }

            @Override
            public Long set(int i, Long b) {
                final long old = arr[i];
                arr[i] = b;
                return old;
            }
        };
    }

    /**
     * Wraps a {@code float[]} array in a {@link List<Float>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Float> asList(final float[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Float get(int i) {
                return arr[i];
            }

            @Override
            public Float set(int i, Float b) {
                final float old = arr[i];
                arr[i] = b;
                return old;
            }
        };
    }

    /**
     * Wraps a {@code double[]} array in a {@link List<Double>} list.
     *
     * @apiNote
     * Runs in {@code O(1)}, but all addition and removal operations of the
     * returned list will throw a {@link UnsupportedOperationException}.
     * Setting values will also alter the value in the original array,
     * but {@code null} values will throw a {@link NullPointerException}.
     *
     * @param arr The array to convert.
     *
     * @return A write-through list representation of the provided array.
     */
    public static List<Double> asList(final double[] arr) {
        return new ReadOnlyArrayList<>() {
            @Override
            public int size() {
                return arr.length;
            }

            @Override
            public Double get(int i) {
                return arr[i];
            }

            @Override
            public Double set(int i, Double b) {
                final double old = arr[i];
                arr[i] = b;
                return old;
            }
        };
    }
    
    /**
     * Converts the given array of any type to a list of the same type.
     * 
     * @apiNote
     * This operation takes {@code O(n)} time.
     * 
     * @param array The array to convert.
     * 
     * @return An array of the given list.
     * 
     * @see Arrays#asList(Object...)
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> toList(Object array) {
        if (array instanceof Object[]) return toList((V[]) array);
        if (array instanceof boolean[]) return (List<V>) toList((boolean[]) array);
        if (array instanceof byte[]) return (List<V>) toList((byte[]) array);
        if (array instanceof char[]) return (List<V>) toList((char[]) array);
        if (array instanceof short[]) return (List<V>) toList((short[]) array);
        if (array instanceof int[]) return (List<V>) toList((int[]) array);
        if (array instanceof long[]) return (List<V>) toList((long[]) array);
        if (array instanceof float[]) return (List<V>) toList((float[]) array);
        if (array instanceof double[]) return (List<V>) toList((double[]) array);
        if (array == null)
            throw new NullPointerException("Array is null!");
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("Argument is not an array!");
        else
            throw new IllegalStateException();
    }
    
    /**
     * Converts the given object array to a list of the same type.
     * 
     * @param <V> The type of the given array.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     * 
     * @see Arrays#asList(Object...)
     */
    @SafeVarargs
    public static <V> List<V> toList(V... arr) {
        List<V> list = new ArrayList<>(arr.length);
        Collections.addAll(list, arr);
        return list;
    }
    
    /**
     * Converts the given boolean array to a Boolean list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Boolean> toList(boolean... arr) {
        List<Boolean> list = new ArrayList<>(arr.length);
        for (boolean b : arr) {
            list.add(b);
        }
        return list;
    }
    
    /**
     * Converts the given byte array to a Byte list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Byte> toList(byte... arr) {
        List<Byte> list = new ArrayList<>(arr.length);
        for (byte b : arr) {
            list.add(b);
        }
        return list;
    }
    
    /**
     * Converts the given short array to a Short list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Short> toList(short... arr) {
        List<Short> list = new ArrayList<>(arr.length);
        for (short s : arr) {
            list.add(s);
        }
        return list;
    }
    
    /**
     * Converts the given char array to a Character list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Character> toList(char... arr) {
        List<Character> list = new ArrayList<>(arr.length);
        for (char c : arr) {
            list.add(c);
        }
        return list;
    }
    
    /**
     * Converts the given int array to a Integer list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Integer> toList(int... arr) {
        List<Integer> list = new ArrayList<>(arr.length);
        for (int i : arr) {
            list.add(i);
        }
        return list;
    }
    
    /**
     * Converts the given long array to a Long list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Long> toList(long... arr) {
        List<Long> list = new ArrayList<>(arr.length);
        for (long l : arr) {
            list.add(l);
        }
        return list;
    }
    
    /**
     * Converts the given float array to a Float list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Float> toList(float... arr) {
        List<Float> list = new ArrayList<>(arr.length);
        for (float f : arr) {
            list.add(f);
        }
        return list;
    }
    
    /**
     * Converts the given double array to a Double list.
     * 
     * @param arr The array to convert.
     * 
     * @return An array of the given list.
     */
    public static List<Double> toList(double... arr) {
        List<Double> list = new ArrayList<>(arr.length);
        for (double d : arr) {
            list.add(d);
        }
        return list;
    }
    
    
    /* ------------------------------------------------------------------------
     * Copy functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Copies any typed source array {@code src} to the destination array {@code dst}. <br>
     * The number of elements copied is equal to the minimum of the length of both arrays. <br>
     * If the destination array is longer, then all elements are copied. The last elements
     * will remain unchanged. <br>
     * If the source array is longer, then the first elements which fit are copied to the
     * destination array.
     * 
     * @param src The source array.
     * @param dst The destination array.
     * 
     * @return The elements from {@code src} copied to {@code dst}.
     * 
     * @see #copyOf(Object, int, Object, int)
     * @see #copyOf(Object, int, Object, int, int)
     */
    public static <V> V copyOf(Object src, V dst) {
        return copyOf(src, 0, dst, 0, Math.min(length(src), length(dst)));
    }
    
    /**
     * Copies any typed source array {@code src} to the destination array {@code dst}. <br>
     * There will be {@code len} elements copied from {@code src} to {@code dst}, starting
     * at the offset {@code offSrc} from the source array.
     * 
     * @param src The source array.
     * @param dst The destination array.
     * @param offSrc The offset to start copying elements from the source array.
     * @param len The number of elements to copy.
     * 
     * @return The elements from {@code src} copied to {@code dst}.
     * 
     * @see #copyOf(Object, int, Object, int, int)
     */
    public static <V> V copyOf(Object src, int offSrc, V dst, int len) {
        return copyOf(src, 0, dst, offSrc, len);
    }

    /**
     * Copies any typed source array {@code src} to the destination array {@code dst}. <br>
     * There will be {@code len} elements copied from {@code src} to {@code dst}, starting
     * at the offset {@code offSrc} from the source array and the offset {@code offDst} from
     * the destination array.
     * 
     * @param src The source array.
     * @param dst The destination array.
     * @param offSrc The offset to start copying elements from the source array.
     * @param offDst The offset to start writing elements to the destination array.
     * @param len The number of elements to copy.
     * 
     * @return The elements from {@code src} copied to {@code dst}.
     */
    @SuppressWarnings("MismatchedReadAndWriteOfArray") // Indirect read/writes due to array casting.
    public static <V> V copyOf(Object src, int offSrc, V dst, int offDst, int len) {
        if (src == null || dst == null) throw new NullPointerException();
        if (!src.getClass().isArray() || !dst.getClass().isArray())
            throw new IllegalArgumentException("Argument is not an array!");
        if (len < 0)
            throw new IllegalArgumentException();
        
        final Class<?> compTypeSrc = src.getClass().getComponentType();
        final Class<?> compTypeDst = dst.getClass().getComponentType();
        
        if (compTypeSrc.isAssignableFrom(compTypeDst)) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(src, offSrc, dst, offDst, len);
            
        } else {
            final boolean isSrcPrim = compTypeSrc.isPrimitive();
            final boolean isDstPrim = compTypeDst.isPrimitive();
            if (isSrcPrim && !isDstPrim) {
                if (src instanceof boolean[]) {
                    boolean[] s = (boolean[]) src;
                    Boolean[] d = (Boolean[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof byte[]) {
                    byte[] s = (byte[]) src;
                    Byte[] d = (Byte[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof char[]) {
                    char[] s = (char[]) src;
                    Character[] d = (Character[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof short[]) {
                    short[] s = (short[]) src;
                    Short[] d = (Short[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof int[]) {
                    int[] s = (int[]) src;
                    Integer[] d = (Integer[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof long[]) {
                    long[] s = (long[]) src;
                    Long[] d = (Long[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof float[]) {
                    float[] s = (float[]) src;
                    Float[] d = (Float[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (src instanceof double[]) {
                    double[] s = (double[]) src;
                    Double[] d = (Double[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else {
                    throw new IllegalStateException();
                }

            } else if (!isSrcPrim && isDstPrim) {
                if (dst instanceof boolean[]) {
                    Boolean[] s = (Boolean[]) src;
                    boolean[] d = (boolean[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof byte[]) {
                    Byte[] s = (Byte[]) src;
                    byte[] d = (byte[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof char[]) {
                    Character[] s = (Character[]) src;
                    char[] d = (char[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof short[]) {
                    Short[] s = (Short[]) src;
                    short[] d = (short[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof int[]) {
                    Integer[] s = (Integer[]) src;
                    int[] d = (int[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof long[]) {
                    Long[] s = (Long[]) src;
                    long[] d = (long[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof float[]) {
                    Float[] s = (Float[]) src;
                    float[] d = (float[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else if (dst instanceof double[]) {
                    Double[] s = (Double[]) src;
                    double[] d = (double[]) dst;
                    for (int i = 0; i < len; i++) {
                        d[offDst + i] = s[offSrc + i];
                    }

                } else {
                    throw new IllegalStateException();
                }

            } else {
                throw new ClassCastException("Cannot perform copy: " +
                        src.getClass().getComponentType() + " --> " +
                        dst.getClass().getComponentType() + ".");
            }
        }
        return dst;
    }
    
    
    /* ------------------------------------------------------------------------
     * Swap elements functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Performs a swap between two elements of an array of any type.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object[], int, int)
     * @see #swap(boolean[], int, int)
     * @see #swap(byte[], int, int)
     * @see #swap(short[], int, int)
     * @see #swap(char[], int, int)
     * @see #swap(int[], int, int)
     * @see #swap(long[], int, int)
     * @see #swap(float[], int, int)
     * @see #swap(double[], int, int)
     */
    @SuppressWarnings("unchecked")
    public static <A> A swap(A arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        if (arr instanceof Object[]) return (A) swap((Object[]) arr, i, j);
        if (arr instanceof boolean[]) return (A) swap((boolean[]) arr, i, j);
        if (arr instanceof byte[]) return (A) swap((byte[]) arr, i, j);
        if (arr instanceof short[]) return (A) swap((short[]) arr, i, j);
        if (arr instanceof char[]) return (A) swap((char[]) arr, i, j);
        if (arr instanceof int[]) return (A) swap((int[]) arr, i, j);
        if (arr instanceof long[]) return (A) swap((long[]) arr, i, j);
        if (arr instanceof float[]) return (A) swap((float[]) arr, i, j);
        if (arr instanceof double[]) return (A) swap((double[]) arr, i, j);
        if (arr == null) throw new NullPointerException();
        if (!arr.getClass().isArray()) throw new IllegalArgumentException("Object is not an array!");
        throw new IllegalStateException();
    }
    
    /**
     * Performs a swap between two elements of an object array.
     * 
     * @param <V> The type of the array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static <V> V[] swap(V[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        V tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a boolean array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static boolean[] swap(boolean[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        boolean tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a byte array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static byte[] swap(byte[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        byte tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a short array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static short[] swap(short[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        short tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a char array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static char[] swap(char[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        char tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of an int array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static int[] swap(int[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a long array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static long[] swap(long[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        long tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a float array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static float[] swap(float[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        float tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    /**
     * Performs a swap between two elements of a double array.
     * 
     * @param arr The array where the swap occurs.
     * @param i The first element of the swap.
     * @param j The second element of the swap.
     * 
     * @return {@code arr}, but then with the elements {@code i} and {@code j} swapped.
     * 
     * @throws ArrayIndexOutOfBoundsException If {@code i} or {@code j}
     *     are invalid indices of {@code arr}.
     * 
     * @see #swap(Object, int, int)
     */
    public static double[] swap(double[] arr, int i, int j)
            throws ArrayIndexOutOfBoundsException {
        double tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
        return arr;
    }
    
    
    /* ------------------------------------------------------------------------
     * Shuffle array functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Randomly shuffles an array of anny type.
     * This is an in-place algorithm.
     * 
     * @apiNote
     * The arrays is shuffled by relocating every element to a random
     * position in the array, starting at the front of the array towards
     * the back. Therefore, every element is relocated at least once,
     * and twice on average.
     * 
     * @implSpec
     * This function runs in {@code O(n)}.
     *
     * @param <A> The type of the array.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static <A> A shuffle(A arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles an array of any type.
     * This is an in-place algorithm.
     * 
     * @apiNote
     * The arrays is shuffled by relocating every element to a random
     * position in the array, starting at the front of the array towards
     * the back. Therefore, every element is relocated at least once,
     * and twice on average.
     * 
     * @implSpec
     * This function runs in {@code O(n)}.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object[], Random)
     * @see #shuffle(boolean[], Random)
     * @see #shuffle(byte[], Random)
     * @see #shuffle(short[], Random)
     * @see #shuffle(char[], Random)
     * @see #shuffle(int[], Random)
     * @see #shuffle(long[], Random)
     * @see #shuffle(float[], Random)
     * @see #shuffle(double[], Random)
     */
    @SuppressWarnings("unchecked")
    public static <A> A shuffle(A arr, Random ran) {
        if (arr instanceof Object[]) return (A) shuffle((Object[]) arr, ran);
        if (arr instanceof boolean[]) return (A) shuffle((boolean[]) arr, ran);
        if (arr instanceof byte[]) return (A) shuffle((byte[]) arr, ran);
        if (arr instanceof short[]) return (A) shuffle((short[]) arr, ran);
        if (arr instanceof char[]) return (A) shuffle((char[]) arr, ran);
        if (arr instanceof int[]) return (A) shuffle((int[]) arr, ran);
        if (arr instanceof long[]) return (A) shuffle((long[]) arr, ran);
        if (arr instanceof float[]) return (A) shuffle((float[]) arr, ran);
        if (arr instanceof double[]) return (A) shuffle((double[]) arr, ran);
        if (arr == null) throw new NullPointerException();
        if (!arr.getClass().isArray()) throw new IllegalArgumentException("Object is not an array!");
        throw new IllegalStateException();
    }
    
    /**
     * Randomly shuffles an object array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(Object)
     */
    public static <V> V[] shuffle(V[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles an object array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static <V> V[] shuffle(V[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a boolean array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(boolean[], Random)
     */
    public static boolean[] shuffle(boolean[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a boolean array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static boolean[] shuffle(boolean[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a byte array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(byte[], Random)
     */
    public static byte[] shuffle(byte[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a byte array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static byte[] shuffle(byte[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a short array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(short[], Random)
     */
    public static short[] shuffle(short[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a short array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static short[] shuffle(short[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a char array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(char[], Random)
     */
    public static char[] shuffle(char[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a char array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static char[] shuffle(char[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles an int array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(int[], Random)
     */
    public static int[] shuffle(int[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a int array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static int[] shuffle(int[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a long array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(long[], Random)
     */
    public static long[] shuffle(long[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a long array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static long[] shuffle(long[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a float array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(float[], Random)
     */
    public static float[] shuffle(float[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a float array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static float[] shuffle(float[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    /**
     * Randomly shuffles a double array.
     * This is an in-place algorithm.
     * 
     * @param arr The input array.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     * @see #shuffle(double[], Random)
     */
    public static double[] shuffle(double[] arr) {
        return shuffle(arr, ThreadLocalRandom.current());
    }
    
    /**
     * Randomly shuffles a double array.
     * This is an in-place algorithm.
     *
     * @param arr The input array.
     * @param ran The used random generator used for swapping elements.
     * 
     * @return The input array, but then all elements are randomly shuffled.
     * 
     * @see #shuffle(Object, Random)
     */
    public static double[] shuffle(double[] arr, Random ran) {
        for (int i = arr.length; i > 1; i--) {
            swap(arr, i - 1, ran.nextInt(i));
        }
        return arr;
    }
    
    
    /* ------------------------------------------------------------------------
     * List to array functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Converts a collection of {@code Boolean}s to an array of {@code boolean}s
     *
     * @param col The collection to convert.
     *
     * @return An array of {@code boolean}s from the original collection.
     */
    public static boolean[] toBooleanArray(List<Boolean> col) {
        boolean[] arr = new boolean[col.size()];
        int i = 0;
        for (boolean b : col) {
            arr[i++] = b;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code byte}s to an array of {@code byte}s
     *
     * @param col The collection to convert.
     *
     * @return An array of {@code byte}s from the original collection.
     */
    public static byte[] toByteArray(Collection<Byte> col) {
        byte[] arr = new byte[col.size()];
        int i = 0;
        for (byte b : col) {
            arr[i++] = b;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code Short}s to an array of {@code short}s
     *
     * @param col The collection to convert.
     *
     * @return An array of {@code short}s from the original collection.
     */
    public static short[] toShortArray(Collection<Short> col) {
        short[] arr = new short[col.size()];
        int i = 0;
        for (short s : col) {
            arr[i++] = s;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code Character}s to an array of {@code char}s
     *
     * @param col The collection to convert.
     *
     * @return An array of {@code char}s from the original collection.
     */
    public static char[] toCharArray(Collection<Character> col) {
        char[] arr = new char[col.size()];
        int i = 0;
        for (char c : col) {
            arr[i++] = c;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code Integer}s to an array of {@code int}s
     *
     * @param col The collection to convert.
     *
     * @return An array of {@code int}s from the original collection.
     */
    public static int[] toIntArray(Collection<Integer> col) {
        int[] arr = new int[col.size()];
        int i = 0;
        for (int value : col) {
            arr[i++] = value;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code Long}s to an array of {@code long}s
     *
     * @param col The collection to convert.
     *
     * @return An array of {@code long}s from the original collection.
     */
    public static long[] toLongArray(Collection<Long> col) {
        long[] arr = new long[col.size()];
        int i = 0;
        for (long l : col) {
            arr[i++] = l;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code Float}s to an array of {@code float}s
     * 
     * @param col The collection to convert.
     * 
     * @return An array of {@code float}s from the original collection.
     */
    public static float[] toFloatArray(Collection<Float> col) {
        float[] arr = new float[col.size()];
        int i = 0;
        for (float f : col) {
            arr[i++] = f;
        }

        return arr;
    }
    
    /**
     * Converts a collection of {@code Double}s to an array of {@code double}s
     * 
     * @param col The collection to convert.
     * 
     * @return An array of {@code double}s from the original collection.
     */
    public static double[] toDoubleArray(Collection<Double> col) {
        double[] arr = new double[col.size()];
        int i = 0;
        for (double d : col) {
            arr[i++] = d;
        }
            
        return arr;
    }
    
    
    /* ------------------------------------------------------------------------
     * Array depth calculation functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Calculates the dimensions of a balanced the array. <br>
     * <br>
     * Example:<br>
     * {@code calcDimBalanced(new int[4][3][2]) --> new int[] {4, 3, 2}}.
     * 
     * @implNote
     * It is assumed that the provided array is balanced (each sub-array has equal size).
     * If an unbalanced array is given, then the values will match only for the first
     * element each time: <br>
     * {@code calcDimBalancedArray(new int[][] {new int[1], new int[2]}) == new int[] {2, 1}} <br>
     * {@code calcDimBalancedArray(new int[][] {new int[2], new int[1]}) == new int[] {2, 2}} <br>
     * <br>
     * If an array of length 0 or {@code null} is encountered as first element at some point,
     * then the remaining array dimensions are set to 0: <br>
     * {@code calcDimBalancedArray(new int[1][0][1]) == new int[] {1, 0, 0}} <br>
     * {@code calcDimBalancedArray(new int[1][][]) == new int[] {1, 0, 0}}
     *
     * @param obj The Array to determine the dimensions of.
     * 
     * @return An array containing the dimensions of each of sub-level of the array.
     *     The length of the array is equal to the depth of the array. 
     */
    public static int[] calcDimBalanced(Object obj) {
        int[] dims = new int[calcDepth(obj)];
        Object arr = obj;
        int i = 0;
        while (arr != null && arr.getClass().isArray()) {
            int l = length(arr);
            if (l == 0) break;
            dims[i++] = l;
            arr = get(arr, 0);
        }
        return dims;
    }
    
    /**
     * Calculates the depth of the given array.
     *
     * @param obj The array to calculate the depth of.
     * 
     * @return The depth of the given array, or {@code 0} if the given object is not an array
     * 
     * @throws NullPointerException If the given array is {@code null}.
     */
    public static int calcDepth(Object obj)
            throws NullPointerException {
        String name = obj.getClass().getName();
        int depth = 0;
        while (depth < name.length() && name.charAt(depth) == '[') {
            depth++;
        }
        return depth;
    }
    
    
    /* ------------------------------------------------------------------------
     * For each functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Executes the given action for each element of an array of any type.
     * 
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     * 
     * @see #forEach(Object[], Consumer)
     * @see #forEach(boolean[], Consumer)
     * @see #forEach(byte[], Consumer)
     * @see #forEach(short[], Consumer)
     * @see #forEach(char[], Consumer)
     * @see #forEach(int[], Consumer)
     * @see #forEach(long[], Consumer)
     * @see #forEach(float[], Consumer)
     * @see #forEach(double[], Consumer)
     */
    @SuppressWarnings("unchecked")
    public static <V> void forEach(Object arr, Consumer<?> action) {
        if (arr instanceof Object[]) forEach((V[]) arr, (Consumer<? super V>) action);
        else if (arr instanceof boolean[]) forEach((boolean[]) arr, (Consumer<Boolean>) action);
        else if (arr instanceof byte[]) forEach((byte[]) arr, (Consumer<Byte>) action);
        else if (arr instanceof short[]) forEach((short[]) arr, (Consumer<Short>) action);
        else if (arr instanceof char[]) forEach((char[]) arr, (Consumer<Character>) action);
        else if (arr instanceof int[]) forEach((int[]) arr, (Consumer<Integer>) action);
        else if (arr instanceof long[]) forEach((long[]) arr,(Consumer<Long>)  action);
        else if (arr instanceof float[]) forEach((float[]) arr, (Consumer<Float>) action);
        else if (arr instanceof double[]) forEach((double[]) arr, (Consumer<Double>) action);
        else if (arr == null)
            throw new NullPointerException();
        else if (!arr.getClass().isArray())
            throw new IllegalArgumentException("Object is not an array!");
        else
            throw new IllegalStateException();
    }
    
    /**
     * Executes the given action for each element of the object array.
     *
     * @param <V> The type of elements in the array.
     * 
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     * 
     * @see #forEach(Object, Consumer)
     */
    public static <V> void forEach(V[] arr, Consumer<? super V> action) {
        for (V v : arr) {
            action.accept(v);
        }
    }
    
    /**
     * Executes the given action for each element of the boolean array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(boolean[] arr, Consumer<Boolean> action) {
        for (boolean b : arr) {
            action.accept(b);
        }
    }
        
    /**
     * Executes the given action for each element of the byte array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(byte[] arr, Consumer<Byte> action) {
        for (byte b : arr) {
            action.accept(b);
        }
    }
    
    /**
     * Executes the given action for each element of the short array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(short[] arr, Consumer<Short> action) {
        for (short s : arr) {
            action.accept(s);
        }
    }
    
    /**
     * Executes the given action for each element of the given array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(char[] arr, Consumer<Character> action) {
        for (char c : arr) {
            action.accept(c);
        }
    }
    
    /**
     * Executes the given action for each element of the int array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(int[] arr, Consumer<Integer> action) {
        for (int i : arr) {
            action.accept(i);
        }
    }
    
    /**
     * Executes the given action for each element of the long array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(long[] arr, Consumer<Long> action) {
        for (long l : arr) {
            action.accept(l);
        }
    }
    
    /**
     * Executes the given action for each element of the float array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(float[] arr, Consumer<Float> action) {
        for (float f : arr) {
            action.accept(f);
        }
    }
    
    /**
     * Executes the given action for each element of the double array.
     *
     * @param arr The array the for each statement will be applied.
     * @param action The action that is executed for each element.
     *
     * @see #forEach(Object, Consumer)
     */
    public static void forEach(double[] arr, Consumer<Double> action) {
        for (double d : arr) {
            action.accept(d);
        }
    }
    
    /* ------------------------------------------------------------------------
     * Deep equals function.
     * ------------------------------------------------------------------------
     */
    /**
     * Compares two N-dimensional arrays and their contents recursively. It ignores
     * the type of the arrays, so primitive and non-primitive arrays can be mixed.
     * <h2>Examples</h2>
     * <pre>{@code
     * deepEquals(1, 1) == true;
     * deepEquals(0, 1) == false;
     * deepEquals("text", "text") == true;
     * deepEquals(new int[] {1, 2}, new int[] {1, 2}) == true;
     * deepEquals(new int[] {1, 2}, new int[] {2, 1}) == false;
     * deepEquals(new int[] {1, 2}, new Integer[] {1, 2}) == true;
     * deepEquals(new Integer[][] {new Integer[] {1, 2}, new Integer[] {3, 4}},
     *         new int[][] {new int[] {1, 2}, new int[] {3, 4}}) == true;
     * deepEquals(new int[][] {new int[] {1, 2}, new int[] {3, 4}},
     *         new int[][] {new int[] {9, 2}, new int[] {3, 4}}) == false;
     * }</pre>
     * 
     * @apiNote
     * <ul>
     *   <li> The result of this function is undefined if the arrays contain recursive references. </li>
     *   <li> If objects are compared, then the {@link Object#equals(Object)} function of the of the
     *        objects in the first array is used. </li>
     * </ul>
     * 
     * @param obj1 The first array to be compared.
     * @param obj2 The second array to be compared.
     * 
     * @return {@code true} if both arrays and all their values are equal.
     *     {@code false} otherwise.
     */
    public static boolean deepEquals(Object obj1, Object obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;
        
        if (obj1.getClass().isArray()) {
            if (!obj2.getClass().isArray()) return false;
            // obj1 and obj2 are both arrays.
            if (obj1 instanceof Object[] && obj2 instanceof Object[]) {
                // obj1 and obj2 are both object arrays.
                Object[] arr1 = (Object[]) obj1;
                Object[] arr2 = (Object[]) obj2;
                if (arr1.length != arr2.length) return false;
                
                for (int i = 0; i < arr1.length; i++) {
                    if (!deepEquals(arr1[i], arr2[i])) return false;
                }
                
            } else {
                // obj1 and/or obj2 is a primitive array.
                int length = length(obj1);
                if (length != length(obj2)) return false;
                for (int i = 0; i < length; i++) {
                    if (!deepEquals(get(obj1, i), get(obj2, i))) return false;
                }
            }
            
        } else {
            if (obj2.getClass().isArray()) return false;
            // obj1 and obj2 are not arrays.
            return obj1.equals(obj2);
        }

        return true;
    }
    
    /* ------------------------------------------------------------------------
     * Set/get range function.
     * ------------------------------------------------------------------------
     */
    /**
     * Sets the values of the array generated by the array processor.
     * This function is faster values compared to {@link #set(Object, int, Object)}
     * when setting multiple elements.
     * 
     * @apiNote
     * The indices are set in increasing order from {@code off} to {@code off + len}.
     * 
     * @param array The array set the data for.
     * @param off The offset to start setting values.
     * @param len The number of values to set.
     * @param proc The array processor used to generate the values.
     */
    public static void setRange(Object array, int off, int len, ArrayProcessor proc) {
        final int end = off + len;
        if (off < 0 || length(array) < end) {
            throw new IndexOutOfBoundsException("The range [" + off + ", " + end
                    + "] lies outside the bounds of the array (length=" + length(array));
        }
        
        if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            for (int i = 0; i < end; i++) {
                arr[i] = proc.process(i);
            }
            
        } else if (array instanceof boolean[]) {
            boolean[] arr = (boolean[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (boolean) proc.process(i);
            }
            
        } else if (array instanceof byte[]) {
            byte[] arr = (byte[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (byte) proc.process(i);
            }
            
        } else if (array instanceof short[]) {
            short[] arr = (short[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (short) proc.process(i);
            }
            
        } else if (array instanceof char[]) {
            char[] arr = (char[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (char) proc.process(i);
            }
            
        } else if (array instanceof int[]) {
            int[] arr = (int[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (int) proc.process(i);
            }
            
        } else if (array instanceof long[]) {
            long[] arr = (long[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (long) proc.process(i);
            }
            
        } else if (array instanceof float[]) {
            float[] arr = (float[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (float) proc.process(i);
            }
            
        } else if (array instanceof double[]) {
            double[] arr = (double[]) array;
            for (int i = off; i < end; i++) {
                arr[i] = (double) proc.process(i);
            }
            
        } else if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("The given object is not an array!");
            
        } else {
            throw new IllegalStateException();
        }
    }
    
    /**
     * Sets the values of the array generated by the array processor.
     * This function is faster values compared to {@link #set(Object, int, Object)}
     * when setting multiple.
     * 
     * @apiNote
     * The indices are set in increasing order from {@code off} to {@code off + len}.
     * 
     * @param array The array set the data for.
     * @param gen The generator used to generate the indices.
     * @param proc The array processor used to generate the values.
     */
    public static void setRange(Object array, IndexGenerator gen, ArrayProcessor proc) {
        if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = obj;
            }
            
        } else if (array instanceof boolean[]) {
            boolean[] arr = (boolean[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (boolean) obj;
            }
            
        } else if (array instanceof byte[]) {
            byte[] arr = (byte[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (byte) obj;
            }
            
        } else if (array instanceof short[]) {
            short[] arr = (short[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (short) obj;
            }
            
        } else if (array instanceof char[]) {
            char[] arr = (char[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (char) obj;
            }
            
        } else if (array instanceof int[]) {
            int[] arr = (int[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (int) obj;
            }
            
        } else if (array instanceof long[]) {
            long[] arr = (long[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (long) obj;
            }
            
        } else if (array instanceof float[]) {
            float[] arr = (float[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (float) obj;
            }
            
        } else if (array instanceof double[]) {
            double[] arr = (double[]) array;
            int i = -1;
            while ((i = gen.nextIndex(arr, i)) > 0) {
                Object obj = proc.process(i);
                if (obj != null) arr[i] = (double) obj;
            }
            
        } else if (array == null) {
            throw new NullPointerException();
            
        } else if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("The given object is not an array!");
            
        } else {
            throw new IllegalStateException();
        }
    }
    
    /**
     * Generates an iterator over the given array.
     * 
     * @apiNote
     * The iterator will throw an {@link ClassCastException} <u>when iterating</u> if the
     * provided type is wrong.
     * 
     * @param <V> The type of the return value.
     * 
     * @param array The array to get the elements from.
     * 
     * @return An iterator over all the elements of the array from low to high index.
     * 
     * @throws IllegalArgumentException If the given object is not an array.
     */
    public static <V> Iterator<V> getRangeIterator(Object array)
            throws IllegalArgumentException {
        return getRangeIterator(array, 0, ArrayTools.length(array));
    }
    
    /**
     * Generates an iterator over the given array.
     * It will iterate from {@code off} to {@code off + len}.
     * 
     * @apiNote
     * The iterator will throw an {@link ClassCastException} <u>when iterating</u> if the
     * provided type is wrong.
     * 
     * @param <V> The type of the return value.
     * 
     * @param array The array to get the elements from.
     * @param off The offset to start reading from the array.
     * @param len The number of elements to read. A negative number will result in reading
     *     the array backwards
     * 
     * @return An iterator over all the elements of the array from low to high index.
     * 
     * @throws IllegalArgumentException If the given object is not an array.
     */
    @SuppressWarnings("unchecked")
    public static <V> Iterator<V> getRangeIterator(Object array, int off, int len)
            throws IllegalArgumentException {
        if (off < 0) throw new IndexOutOfBoundsException("Expected a positive offset, but found: " + off);
        final int end = off + len;
        if (end < 0 || length(array) < end) {
            throw new IndexOutOfBoundsException(String.format(OUT_OF_RANGE_MSG,
                    off, off + len, 0, length(array)));
        }
        final boolean incr = (len > 0);
        
        if (array instanceof Object[]) {
            V[] arr = (V[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof boolean[]) {
            boolean[] arr = (boolean[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Boolean) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof byte[]) {
            byte[] arr = (byte[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Byte) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof short[]) {
            short[] arr = (short[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Short) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof char[]) {
            char[] arr = (char[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Character) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof int[]) {
            int[] arr = (int[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Integer) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof long[]) {
            long[] arr = (long[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Long) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof float[]) {
            float[] arr = (float[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Float) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (array instanceof double[]) {
            double[] arr = (double[]) array;
            return new GeneratorIterator<>() {
                private int i = off;
                @Override
                protected V generateNext() {
                    if ((incr && i < end) || (!incr && i > end)) {
                        return (V) (Double) arr[(incr ? i++ : i--)];
                        
                    } else {
                        done();
                        return null;
                    }
                }
            };
            
        } else if (!array.getClass().isArray())
            throw new IllegalArgumentException("The given object is not an array!");
        else
            throw new IllegalStateException();
    }


    /* ------------------------------------------------------------------------
     * Hash code functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Calculates a hash code for the given object. <br>
     * This function will always return the same value in the following case:
     * <ul>
     *   <li> If the provided objects are not arrays, and the provided objects
     *        are equal according to the {@link Object#equals(Object)} contract. </li>
     *   <li> If the provided objects are arrays, then these (recursively) rules also hold
     *        for each element in the array with the same depth/index. </li>
     * </ul>
     * 
     * @param obj The object to determine the hash code of.
     * 
     * @return A hash code for the given object.
     * 
     * @see #calcHashCode(boolean)
     * @see #calcHashCode(byte)
     * @see #calcHashCode(short)
     * @see #calcHashCode(char)
     * @see #calcHashCode(int)
     * @see #calcHashCode(long)
     * @see #calcHashCode(float)
     * @see #calcHashCode(double)
     */
    public static int calcHashCode(Object obj) {
        if (obj == null) { // {@code obj} is null.
            return 0;
            
        } else if (obj.getClass().isArray()) { // {@code obj} is an array.
            int result = HASH_RESULT_BEGIN;
            if (obj.getClass().getComponentType().isArray()) { // {@code obj} is a 2+D array.
                for (int i = 0; i < length(obj); i++) {
                    result = HASH_RESULT_MUL * result + calcHashCode(get(obj, i));
                }
                
            } else { // {@code obj} is a 1D array.
                if (obj.getClass().getComponentType().isPrimitive()) { // {@code obj} is a primitive 1D array.
                    if (obj instanceof boolean[]) {
                        for (boolean b : (boolean[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(b);
                        }
                        
                    } else if (obj instanceof byte[]) {
                        for (byte b : (byte[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(b);
                        }
                        
                    } else if (obj instanceof short[]) {
                        for (short s : (short[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(s);
                        }

                    } else if (obj instanceof char[]) {
                        for (char c : (char[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(c);
                        }

                    } else if (obj instanceof int[]) {
                        for (int i : (int[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(i);
                        }

                    } else if (obj instanceof long[]) {
                        for (long l : (long[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(l);
                        }

                    } else if (obj instanceof float[]) {
                        for (float f : (float[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(f);
                        }

                    } else if (obj instanceof double[]) {
                        for (double d : (double[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(d);
                        }
                    }
                    
                } else { // {@code obj} is a non-primitive 1D array.
                    if (obj instanceof Boolean[]) {
                        for (boolean b : (Boolean[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(b);
                        }

                    } else if (obj instanceof Byte[]) {
                        for (byte b : (Byte[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(b);
                        }

                    } else if (obj instanceof Short[]) {
                        for (short s : (Short[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(s);
                        }

                    } else if (obj instanceof Character[]) {
                        for (char c : (Character[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(c);
                        }

                    } else if (obj instanceof Integer[]) {
                        for (int i : (Integer[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(i);
                        }

                    } else if (obj instanceof Long[]) {
                        for (long l : (Long[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(l);
                        }

                    } else if (obj instanceof Float[]) {
                        for (float f : (Float[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(f);
                        }

                    } else if (obj instanceof Double[]) {
                        for (double d : (Double[]) obj) {
                            result = HASH_RESULT_MUL * result + calcHashCode(d);
                        }
                        
                    } else {
                        for (Object o : (Object[]) obj) {
                            result = HASH_RESULT_MUL * result + Objects.hashCode(o);
                        }
                    }
                }
            }
            
            return result;

        } else { // {@code obj} is a single object.
            if (obj instanceof Boolean) return calcHashCode((boolean) obj);
            else if (obj instanceof Byte) return calcHashCode((byte) obj);
            else if (obj instanceof Short) return calcHashCode((short) obj);
            else if (obj instanceof Character) return calcHashCode((char) obj);
            else if (obj instanceof Integer) return calcHashCode((int) obj);
            else if (obj instanceof Long) return calcHashCode((long) obj);
            else if (obj instanceof Float) return calcHashCode((float) obj);
            else if (obj instanceof Double) return calcHashCode((double) obj);
            else return obj.hashCode();
        }
    }

    /**
     * Calculates the hash code of a boolean. <br>
     * It differs from {@link Boolean#hashCode(boolean)} by increasing the
     * distance between possible hash values and evades the value 0. <br>
     * The run times of these two functions are equal.
     *
     * @param b The boolean to calculates the hash code of.
     *
     * @return The hash code of the provided value.
     */
    public static int calcHashCode(boolean b) {
        return (b ? 0x70FFFFFF : 0x000000FF);
    }

    /**
     * Calculates the hash code of a byte. <br>
     * It differs from {@link Byte#hashCode(byte)} by increasing the
     * distance between possible hash values. <br>
     *
     * @param b The byte to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     */
    public static int calcHashCode(byte b) {
        return (b << 8) ^ (b << 4) ^ b;
    }

    /**
     * Calculates the hash code of a short. <br>
     * Differs from {@link Short#hashCode(short)} by increasing the
     * distance between possible hash values. <br>
     * 
     * @param s The short to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     */
    public static int calcHashCode(short s) {
        return (s << 16) ^ (s << 8) ^ s;
    }

    /**
     * Calculates the hash code of a char. <br>
     * Differs from {@link Character#hashCode(char)} by increasing the
     * distance between possible hash values. <br>
     *
     * @param c The char to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     * 
     * @see Character#hashCode(char)
     */
    public static int calcHashCode(char c) {
        return (c << 16) ^ (c << 8) ^ c;
    }

    /**
     * Calculates the hash code of a int. <br>
     * Differs from {@link Integer#hashCode(int)} by increasing the
     * distance between close values. <br>
     *
     * @param i The int to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     */
    public static int calcHashCode(int i) {
        return (i << 16) | (i >>> 16);
    }

    /**
     * Calculates the hash code of a long. <br>
     * Differs from {@link Long#hashCode(long)} by increasing the
     * distance between close values. <br>
     *
     * @param l The long to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     */
    public static int calcHashCode(long l) {
        return calcHashCode((int) (l ^ l >>> 32));
    }

    /**
     * Calculates the hash code of a float. <br>
     * Differs from {@link Float#hashCode(float)} by increasing the
     * distance between possible hash values and evades the value 0. <br>
     * This function should only be used when the hash code of an array
     * must be determined, as it is slower but reduces collisions.
     *
     * @implNote
     * This function is (slightly) slower compared to the java
     * {@link Float#hashCode(float)} function as it additionally uses
     * {@link #calcHashCode(int)}.
     *
     * @param f The float to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     *
     * @see #calcHashCode(int)
     */
    public static int calcHashCode(float f) {
        return calcHashCode(Float.floatToIntBits(f));
    }

    /**
     * Calculates the hash code of a double. <br>
     * Differs from {@link Double#hashCode(double)} by increasing the
     * distance between possible hash values and evades the value 0. <br>
     * This function should only be used when the hash code of an array
     * must be determined, as it is slower but reduces collisions.
     *
     * @implNote
     * <ul>
     *   <li> This function is (slightly) slower compared to the java
     *        {@link Double#hashCode(double)} function as it additionally
     *        uses {@link #calcHashCode(long)}. </li>
     * </ul>
     *
     * @param d The double to calculate the hash code of.
     *
     * @return The hash code of the provided value.
     *
     * @see #calcHashCode(long)
     */
    public static int calcHashCode(double d) {
        return calcHashCode(Double.doubleToLongBits(d));
    }
    
    
    /* ------------------------------------------------------------------------
     * Binary search functions.
     * ------------------------------------------------------------------------
     */
    
}
