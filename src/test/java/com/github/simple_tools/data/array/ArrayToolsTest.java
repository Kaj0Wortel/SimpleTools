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

import com.github.simple_tools.AbstractTest;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.*;

public class ArrayToolsTest
        extends AbstractTest {
    /* ------------------------------------------------------------------------
     * Constants.
     * ------------------------------------------------------------------------
     */
    /** The minimum allowed radix. */
    private static final int MIN_RADIX = 2;
    /** The maximum allowed radix (0-9,A-Z). */
    private static final int MAX_RADIX = 36;
    
    /* Default arrays containing edge cases for each primitive type (except void). */
    private static final boolean[] BOOLEAN_TYPE_DEFAULT_ARR =
            new boolean[] { true, false };
    private static final byte[] BYTE_TYPE_DEFAULT_ARR =
            new byte[] { Byte.MIN_VALUE, Byte.MAX_VALUE, -1, 0, 1, 10 };
    private static final char[] CHAR_TYPE_DEFAULT_ARR =
            new char[] { Character.MIN_VALUE, Character.MAX_VALUE, 0, 1, 10 };
    private static final short[] SHORT_TYPE_DEFAULT_ARR =
            new short[] { Short.MIN_VALUE, Short.MAX_VALUE, -1, 0, 1, 10 };
    private static final int[] INT_TYPE_DEFAULT_ARR =
            new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE, -1, 0, 1, 10 };
    private static final long[] LONG_TYPE_DEFAULT_ARR =
            new long[] { Long.MIN_VALUE, Long.MAX_VALUE, -1L, 0L, 1L, 10L };
    private static final float[] FLOAT_TYPE_DEFAULT_ARR =
            new float[] { Float.MIN_VALUE, Float.MAX_VALUE, -Float.MIN_VALUE, -Float.MAX_VALUE,
                    Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN, -1.0f, 0.0f, 1.0f, 10.5f };
    private static final double[] DOUBLE_TYPE_DEFAULT_ARR =
            new double[] { Double.MIN_VALUE, Double.MAX_VALUE, -Double.MIN_VALUE, -Double.MAX_VALUE,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN, -1.0, 0.0, 1.0, 10.5 };

    /* Default arrays containing edge cases for each counterpart of the primitive types (except Void). */
    private final Boolean[] BOOLEAN_CLASS_DEFAULT_ARR =
            new Boolean[] { true, false };
    private static final Byte[] BYTE_CLASS_DEFAULT_ARR =
            new Byte[] { Byte.MIN_VALUE, Byte.MAX_VALUE, -1, 0, 1, 10 };
    private static final Character[] CHAR_CLASS_DEFAULT_ARR =
            new Character[] { Character.MIN_VALUE, Character.MAX_VALUE, 0, 1, 10 };
    private static final Short[] SHORT_CLASS_DEFAULT_ARR =
            new Short[] { Short.MIN_VALUE, Short.MAX_VALUE, -1, 0, 1, 10 };
    private static final Integer[] INT_CLASS_DEFAULT_ARR =
            new Integer[] { Integer.MIN_VALUE, Integer.MAX_VALUE, -1, 0, 1, 10 };
    private static final Long[] LONG_CLASS_DEFAULT_ARR =
            new Long[] { Long.MIN_VALUE, Long.MAX_VALUE, -1L, 0L, 1L, 10L };
    private static final Float[] FLOAT_CLASS_DEFAULT_ARR =
            new Float[] { Float.MIN_VALUE, Float.MAX_VALUE, -Float.MIN_VALUE, -Float.MAX_VALUE,
                    Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN, -1.0f, 0.0f, 1.0f, 10.5f };
    private static final Double[] DOUBLE_CLASS_DEFAULT_ARR =
            new Double[] { Double.MIN_VALUE, Double.MAX_VALUE, -Double.MIN_VALUE, -Double.MAX_VALUE,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN, -1.0, 0.0, 1.0, 10.5 };


    /* ------------------------------------------------------------------------
     * deepClone functions.
     * ------------------------------------------------------------------------
     */
    private <A, T> void deepCloneMod(A arr, Function<T, T> f) {
        for (int i = 0; i < ArrayTools.length(arr); i++) {
            if (ArrayTools.get(arr, i) == null) {
                ArrayTools.set(arr, i, f.apply(null));
            } else if (ArrayTools.get(arr, i).getClass().isArray()) {
                deepCloneMod(ArrayTools.get(arr, i), f);
            } else {
                ArrayTools.set(arr, i, f.apply(ArrayTools.get(arr, i)));
            }
        }
    }
    
    private <A, T> void deepCloneShallowModTest(A arr, Function<T, T> f) {
        A arr2 = ArrayTools.copyOf(arr);
        A clone = ArrayTools.deepClone(arr);
        deepCloneMod(arr, f);
        assertTrue(ArrayTools.deepEquals(clone, arr2));
    }
    
    private <A, T> void deepCloneDeepModTest(A arr, A res, Function<T, T> f) {
        A clone = ArrayTools.deepClone(arr);
        deepCloneMod(arr, f);
        assertTrue(ArrayTools.deepEquals(clone, res));
    }
    
    @Test
    public void deepCloneBoolean() {
        {
            boolean[] arr = new boolean[] { true, false };
            deepCloneShallowModTest(arr, (Boolean b) -> !b);
            
            boolean[][] arr2 = new boolean[][] { { true }, { false } };
            boolean[][] arr2Res = new boolean[][] { { true }, { false } };
            deepCloneDeepModTest(arr2, arr2Res, (Boolean b) -> !b);
            
        }
        {
            Boolean[] arr = new Boolean[] { true, false };
            deepCloneShallowModTest(arr, (Boolean b) -> !b);

            Boolean[][] arr2 = new Boolean[][] { { true }, { false } };
            Boolean[][] arr2Res = new Boolean[][] { { true }, { false } };
            deepCloneDeepModTest(arr2, arr2Res, (Boolean b) -> !b);
        }
    }

    @Test
    public void deepCloneByte() {
        {
            byte[] arr = new byte[] { 0, 1 };
            deepCloneShallowModTest(arr, (Byte b) -> (byte) ~b);

            byte[][] arr2 = new byte[][] { { 0 }, { 1 } };
            byte[][] arr2Res = new byte[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Byte b) -> (byte) ~b);
        }
        {
            Byte[] arr = new Byte[] { 0, 1 };
            deepCloneShallowModTest(arr, (Byte b) -> (byte) ~b);

            Byte[][] arr2 = new Byte[][] { { 0 }, { 1 } };
            Byte[][] arr2Res = new Byte[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Byte b) -> (byte) ~b);
        }
    }

    @Test
    public void deepCloneShort() {
        {
            short[] arr = new short[] { 0, 1 };
            deepCloneShallowModTest(arr, (Short b) -> (short) ~b);

            short[][] arr2 = new short[][] { { 0 }, { 1 } };
            short[][] arr2Res = new short[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Short b) -> (short) ~b);
        }
        {
            Short[] arr = new Short[] { 0, 1 };
            deepCloneShallowModTest(arr, (Short b) -> (short) ~b);

            Short[][] arr2 = new Short[][] { { 0 }, { 1 } };
            Short[][] arr2Res = new Short[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Short b) -> (short) ~b);
        }
    }

    @Test
    public void deepCloneCharacter() {
        {
            char[] arr = new char[] { 0, 1 };
            deepCloneShallowModTest(arr, (Character b) -> (char) ~b);

            char[][] arr2 = new char[][] { { 0 }, { 1 } };
            char[][] arr2Res = new char[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Character b) -> (char) ~b);
        }
        {
            Character[] arr = new Character[] { 0, 1 };
            deepCloneShallowModTest(arr, (Character b) -> (char) ~b);

            Character[][] arr2 = new Character[][] { { 0 }, { 1 } };
            Character[][] arr2Res = new Character[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Character b) -> (char) ~b);
        }
    }

    @Test
    public void deepCloneInteger() {
        {
            int[] arr = new int[] { 0, 1 };
            deepCloneShallowModTest(arr, (Integer b) -> ~b);

            int[][] arr2 = new int[][] { { 0 }, { 1 } };
            int[][] arr2Res = new int[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Integer b) -> ~b);
        }
        {
            Integer[] arr = new Integer[] { 0, 1 };
            deepCloneShallowModTest(arr, (Integer b) -> ~b);

            Integer[][] arr2 = new Integer[][] { { 0 }, { 1 } };
            Integer[][] arr2Res = new Integer[][] { { 0 }, { 1 } };
            deepCloneDeepModTest(arr2, arr2Res, (Integer b) -> ~b);
        }
    }

    @Test
    public void deepCloneLong() {
        {
            long[] arr = new long[] { 0L, 1L };
            deepCloneShallowModTest(arr, (Long b) -> ~b);

            long[][] arr2 = new long[][] { { 0L }, { 1L } };
            long[][] arr2Res = new long[][] { { 0L }, { 1L } };
            deepCloneDeepModTest(arr2, arr2Res, (Long b) -> ~b);
        }
        {
            Long[] arr = new Long[] { 0L, 1L };
            deepCloneShallowModTest(arr, (Long b) -> ~b);

            Long[][] arr2 = new Long[][] { { 0L }, { 1L } };
            Long[][] arr2Res = new Long[][] { { 0L }, { 1L } };
            deepCloneDeepModTest(arr2, arr2Res, (Long b) -> ~b);
        }
    }

    @Test
    public void deepCloneFloat() {
        {
            float[] arr = new float[] { 0f, 1f };
            deepCloneShallowModTest(arr, (Float b) -> -b);

            float[][] arr2 = new float[][] { { 0f }, { 1f } };
            float[][] arr2Res = new float[][] { { 0f }, { 1f } };
            deepCloneDeepModTest(arr2, arr2Res, (Float b) -> -b);
        }
        {
            Float[] arr = new Float[] { 0f, 1f };
            deepCloneShallowModTest(arr, (Float b) -> -b);

            Float[][] arr2 = new Float[][] { { 0f }, { 1f } };
            Float[][] arr2Res = new Float[][] { { 0f }, { 1f } };
            deepCloneDeepModTest(arr2, arr2Res, (Float b) -> -b);
        }
    }

    @Test
    public void deepCloneDouble() {
        {
            double[] arr = new double[] { 0d, 1d };
            deepCloneShallowModTest(arr, (Double b) -> -b);

            double[][] arr2 = new double[][] { { 0d }, { 1d } };
            double[][] arr2Res = new double[][] { { 0d }, { 1d } };
            deepCloneDeepModTest(arr2, arr2Res, (Double b) -> -b);
        }
        {
            Double[] arr = new Double[] { 0d, 1d };
            deepCloneShallowModTest(arr, (Double b) -> -b);

            Double[][] arr2 = new Double[][] { { 0d }, { 1d } };
            Double[][] arr2Res = new Double[][] { { 0d }, { 1d } };
            deepCloneDeepModTest(arr2, arr2Res, (Double b) -> -b);
        }
    }
    
    
    /* ------------------------------------------------------------------------
     * toDeepString functions.
     * ------------------------------------------------------------------------
     */
    @Test
    public void deepStringObj() {
        final Object[] arr = new Object[] {"1", null, 5, null, 2.0};
        final String exp = "[1, null, 5, null, 2.0]"; 
        assertEquals(exp, ArrayTools.deepToString(arr));
    }
    
    @Test
    public void deepStringBoolean() {
        final boolean[] arr = new boolean[] {true, true, false, true, false};
        final String exp = "[true, true, false, true, false]";
        assertEquals(exp, ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringByte() {
        final byte[] arr = new byte[] {127, -128, 0, 12, -1};
        final String exp = "[127, -128, 0, 12, -1]";
        assertEquals(exp, ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringCharacter() {
        final char[] arr = new char[] {'A', 'a', 'Z', 'z', '0', '9', '?', '!'};
        final String exp = "[A, a, Z, z, 0, 9, ?, !]";
        assertEquals(exp, ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringShort() {
        final short[] arr = new short[] {32_767, -32_768, 0, -1, 12, 1234};
        final String exp = "[32767, -32768, 0, -1, 12, 1234]";
        assertEquals(exp, ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringInteger() {
        final int[] arr = new int[] {2_147_483_647, -2_147_483_648, 0, -1, 12, 1234};
        final String exp = "[2147483647, -2147483648, 0, -1, 12, 1234]";
        assertEquals(exp, ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringLong() {
        final long[] arr = new long[] {9_223_372_036_854_775_807L, -9_223_372_036_854_775_808L, 0, -1, 12, 1234};
        final String exp = "[9223372036854775807, -9223372036854775808, 0, -1, 12, 1234]";
        assertEquals(exp, ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringFloat() {
        final float[] arr = new float[] {
                Float.MAX_VALUE, Float.MIN_VALUE, Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY, Float.NaN, 0.0f, -0.5f, 12.4f
        };
        StringBuilder exp = new StringBuilder("[");
        boolean first = true;
        for (float d : arr) {
            if (first) first = false;
            else exp.append(", ");
            exp.append(d);
        }
        exp.append("]");
        assertEquals(exp.toString(), ArrayTools.deepToString(arr));
    }

    @Test
    public void deepStringDouble() {
        final double[] arr = new double[] {
                Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.NaN, 0.0, -0.5, 12.4
        };
        StringBuilder exp = new StringBuilder("[");
        boolean first = true;
        for (double d : arr) {
            if (first) first = false;
            else exp.append(", ");
            exp.append(d);
        }
        exp.append("]");
        assertEquals(exp.toString(), ArrayTools.deepToString(arr));
    }
    
    @Test public void deepStringMisc() {
        assertEquals("null", ArrayTools.deepToString(null));
        {
            final Object[][][][] multiArr = new Object[2][2][2][2];
            final String exp = "[" +
                    "[[[null, null], [null, null]], [[null, null], [null, null]]], " +
                    "[[[null, null], [null, null]], [[null, null], [null, null]]]" +
                    "]";
            assertEquals(exp, ArrayTools.deepToString(multiArr));
        }
        {
            final boolean[][][][] multiArr = new boolean[2][2][2][2];
            final String exp = "[" +
                    "[[[false, false], [false, false]], [[false, false], [false, false]]], " +
                    "[[[false, false], [false, false]], [[false, false], [false, false]]]" +
                    "]";
            assertEquals(exp, ArrayTools.deepToString(multiArr));
        }
        {
            final String str = "Test String";
            assertEquals(str, ArrayTools.deepToString(str));
        }
        {
            final boolean bool = true;
            assertEquals("true", ArrayTools.deepToString(bool));
        }
    }
    
    private static <V> String genString(Object arr, int length, Function<V, String> function, boolean allowNeg) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (int i = 0; i < ArrayTools.length(arr); i++) {
            if (first) first = false;
            else sb.append(", ");
            String str = function.apply(ArrayTools.get(arr, i));
            sb.append(fillNumber(str, length, allowNeg));
        }
        sb.append("]");
        return sb.toString();
    }
    
    private static String fillNumber(String strNum, int length, boolean allowNeg) {
        StringBuilder sb = new StringBuilder();
        if (allowNeg) {
            if (strNum.startsWith("-")) {
                sb.append("-");
                sb.append("0".repeat(length - strNum.length() + 1));
                sb.append(strNum.substring(1));
            } else {
                sb.append(" ");
                sb.append("0".repeat(length - strNum.length()));
                sb.append(strNum);
            }
        } else {
            sb.append("0".repeat(length - strNum.length()));
            sb.append(strNum);
        }
        return sb.toString();
    }
    
    @Test
    public void deepStringRadixByte() {
        final byte[] arr0 = new byte[] {
                Byte.MAX_VALUE, Byte.MIN_VALUE, 0, 12, -1
        };
        final Byte[] arr1 = new Byte[] {
                Byte.MAX_VALUE, Byte.MIN_VALUE, 0, 12, -1
        };
        final double lengthLog10 = Math.log10(Math.pow(2, Byte.SIZE) - 1);
        for (int r = MIN_RADIX; r <= MAX_RADIX; r++) {
            final int radix = r;
            final String exp = genString(
                    arr0,
                    (int) Math.ceil(lengthLog10 / Math.log10(r)),
                    (Byte b) -> Integer.toString(b & 0xFF, radix),
                    false
            );
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr0, r));
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr1, r));
        }
    }

    @Test
    public void deepStringRadixCharacter() {
        final char[] arr0 = new char[] {
                Character.MAX_VALUE, Character.MIN_VALUE, 'A', 'a', 'Z', 'z', '0', '9', '?', '!'
        };
        final Character[] arr1 = new Character[] {
                Character.MAX_VALUE, Character.MIN_VALUE, 'A', 'a', 'Z', 'z', '0', '9', '?', '!'
        };
        final double lengthLog10 = Math.log10(Math.pow(2, Character.SIZE) - 1);
        for (int r = MIN_RADIX; r <= MAX_RADIX; r++) {
            final int radix = r;
            final String exp = genString(
                    arr0,
                    (int) Math.ceil(lengthLog10 / Math.log10(r)),
                    (Character b) -> Integer.toString(b & 0xFFFF, radix),
                    false
            );
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr0, r));
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr1, r));
        }
    }

    @Test
    public void deepStringRadixShort() {
        final short[] arr0 = new short[] {
                Short.MAX_VALUE, Short.MIN_VALUE, 0, -1, 12, 1234
        };
        final Short[] arr1 = new Short[] {
                Short.MAX_VALUE, Short.MIN_VALUE, 0, -1, 12, 1234
        };
        final double lengthLog10 = Math.log10(Math.pow(2, Short.SIZE) - 1);
        for (int r = MIN_RADIX; r <= MAX_RADIX; r++) {
            final int radix = r;
            final String exp = genString(
                    arr0,
                    (int) Math.ceil(lengthLog10 / Math.log10(r)),
                    (Short b) -> Integer.toString(b & 0xFFFF, radix),
                    false
            );
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr0, r));
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr1, r));
        }
    }

    @Test
    public void deepStringRadixInteger() {
        final int[] arr0 = new int[] {
                Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 12, 1234
        };
        final Integer[] arr1 = new Integer[] {
                Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 12, 1234
        };
        final double lengthLog10 = Math.log10(Math.pow(2, Integer.SIZE) - 1);
        for (int r = MIN_RADIX; r <= MAX_RADIX; r++) {
            final int radix = r;
            final String exp = genString(
                    arr0,
                    (int) Math.ceil(lengthLog10 / Math.log10(r)),
                    (Integer b) -> Integer.toString(b, radix),
                    true
            );
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr0, r));
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr1, r));
        }
    }

    @Test
    public void deepStringRadixLong() {
        final long[] arr0 = new long[] {
                Long.MAX_VALUE, Long.MIN_VALUE, 0L, -1L, 12L, 1234L
        };
        final Long[] arr1 = new Long[] {
                Long.MAX_VALUE, Long.MIN_VALUE, 0L, -1L, 12L, 1234L
        };
        final double lengthLog10 = Math.log10(Math.pow(2, Long.SIZE) - 1);
        for (int r = MIN_RADIX; r <= MAX_RADIX; r++) {
            final int radix = r;
            final String exp = genString(
                    arr0,
                    (int) Math.ceil(lengthLog10 / Math.log10(r)),
                    (Long b) -> Long.toString(b, radix),
                    true
            );
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr0, r));
            assertEquals("Radix: " + r, exp, ArrayTools.deepToString(arr1, r));
        }
    }


    @Test
    public void deepStringRadixMisc() {
        assertEquals("null", ArrayTools.deepToString(null));
        for (int r = MIN_RADIX; r <= MAX_RADIX; r++) {
            final int radix = r;
            {
                final double lengthLog10 = Math.log10(Math.pow(2, Short.SIZE) - 1);
                final short[][][][] multiArr = new short[2][2][2][2];
                StringBuilder exp = new StringBuilder();
                boolean first3 = true;
                exp.append("[");
                for (short[][][] arr3 : multiArr) {
                    if (first3) first3 = false;
                    else exp.append(", ");
                    exp.append("[");
                    boolean first2 = true;
                    for (short[][] arr2 : arr3) {
                        if (first2) first2 = false;
                        else exp.append(", ");
                        exp.append("[");
                        boolean first1 = true;
                        for (short[] arr1 : arr2) {
                            if (first1) first1 = false;
                            else exp.append(", ");
                            exp.append(genString(
                                    arr1,
                                    (int) Math.ceil(lengthLog10 / Math.log10(r)),
                                    (Short b) -> Integer.toString(b & 0xFFFF, radix),
                                    false
                            ));
                        }
                        exp.append("]");
                    }
                    exp.append("]");
                }
                exp.append("]");
                
                assertEquals(exp.toString(), ArrayTools.deepToString(multiArr, r));
            }
            {
                final String str = "Test String";
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(str, radix));
            }
            {
                final float f = 0.5f;
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(f, radix));
            }
            {
                final double d = 0.5;
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(d, radix));
            }
            {
                final String[] str = {"Test", "String"};
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(str, radix));
            }
            {
                final float[] f = new float[] {0.5f, 1.5f};
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(f, radix));
            }
            {
                final double[] d = new double[] {0.5, 1.5};
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(d, radix));
            }
            {
                final boolean bool = true;
                expEx(IllegalArgumentException.class, () -> ArrayTools.deepToString(bool, radix));
            }
            {
                final double lengthLog10 = Math.log10(Math.pow(2, Byte.SIZE) - 1);
                final byte b = 100;
                String exp = fillNumber(
                        Integer.toString(b & 0xFF, radix),
                        (int) Math.ceil(lengthLog10 / Math.log10(r)),
                        false
                );
                assertEquals(exp, ArrayTools.deepToString(b ,radix));
            }
            {
                final double lengthLog10 = Math.log10(Math.pow(2, Character.SIZE) - 1);
                final char c = 100;
                String exp = fillNumber(
                        Integer.toString(c & 0xFFFF, radix),
                        (int) Math.ceil(lengthLog10 / Math.log10(r)),
                        false
                );
                assertEquals(exp, ArrayTools.deepToString(c ,radix));
            }
            {
                final double lengthLog10 = Math.log10(Math.pow(2, Short.SIZE) - 1);
                final short s = 100;
                String exp = fillNumber(
                        Integer.toString(s & 0xFFFF, radix),
                        (int) Math.ceil(lengthLog10 / Math.log10(r)),
                        false
                );
                assertEquals(exp, ArrayTools.deepToString(s ,radix));
            }
            {
                final double lengthLog10 = Math.log10(Math.pow(2, Integer.SIZE) - 1);
                final int i = 100;
                String exp = fillNumber(
                        Integer.toString(i, radix),
                        (int) Math.ceil(lengthLog10 / Math.log10(r)),
                        true
                );
                assertEquals(exp, ArrayTools.deepToString(i ,radix));
            }
            {
                final double lengthLog10 = Math.log10(Math.pow(2, Long.SIZE) - 1);
                final long l = 100;
                String exp = fillNumber(
                        Long.toString(l, radix),
                        (int) Math.ceil(lengthLog10 / Math.log10(r)),
                        true
                );
                assertEquals(exp, ArrayTools.deepToString(l ,radix));
            }
            {
                assertEquals("null", ArrayTools.deepToString(null, radix));
            }
        }
    }
    
    
    /* ------------------------------------------------------------------------
     * Length/depth/dimension functions.
     * ------------------------------------------------------------------------
     */
    private static void checkLengthDepthDim5D(final Object[][][][] arr0, int s0, int s1, int s2, int s3, int s4) {
        assertEquals(s0, ArrayTools.length(arr0));
        assertEquals(5, ArrayTools.calcDepth(arr0));
        assertArrayEquals(new int[] {s0, s1, s2, s3, s4}, ArrayTools.calcDimBalanced(arr0));
        for (Object[][][] arr1 : arr0) {
            assertEquals(s1, ArrayTools.length(arr1));
            assertEquals(4, ArrayTools.calcDepth(arr1));
            assertArrayEquals(new int[] {s1, s2, s3, s4}, ArrayTools.calcDimBalanced(arr1));
            for (Object[][] arr2 : arr1) {
                assertEquals(s2, ArrayTools.length(arr2));
                assertEquals(3, ArrayTools.calcDepth(arr2));
                assertArrayEquals(new int[] {s2, s3, s4}, ArrayTools.calcDimBalanced(arr2));
                for (Object[] arr3 : arr2) {
                    assertEquals(s3, ArrayTools.length(arr3));
                    assertEquals(2, ArrayTools.calcDepth(arr3));
                    assertArrayEquals(new int[] {s3, s4}, ArrayTools.calcDimBalanced(arr3));
                    for (Object arr4 : arr3) {
                        assertEquals(s4, ArrayTools.length(arr4));
                        assertEquals(1, ArrayTools.calcDepth(arr4));
                        assertArrayEquals(new int[]{s4}, ArrayTools.calcDimBalanced(arr4));
                    }
                }
            }
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions") // Still needs a test case.
    public void lengthDepthDimTest() {
        checkLengthDepthDim5D(new boolean[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new byte[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new char[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new short[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new int[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new long[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new float[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new double[5][6][3][8][2], 5, 6, 3, 8, 2);

        checkLengthDepthDim5D(new Boolean[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Byte[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Character[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Short[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Integer[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Long[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Float[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Double[5][6][3][8][2], 5, 6, 3, 8, 2);

        checkLengthDepthDim5D(new Object[5][6][3][8][2], 5, 6, 3, 8, 2);
        checkLengthDepthDim5D(new Object[1][1][1][1][1], 1, 1, 1, 1, 1);
        checkLengthDepthDim5D(new String[5][6][3][8][2], 5, 6, 3, 8, 2);
        
        expEx(NullPointerException.class, () -> ArrayTools.length(null));
        expEx(IllegalArgumentException.class, () -> ArrayTools.length(""));
        expEx(IllegalArgumentException.class, () -> ArrayTools.length(1));
    }
    
    
    /* ------------------------------------------------------------------------
     * Get/set/deepEquals/copyOf functions.
     * ------------------------------------------------------------------------
     */ // TODO: deepEquals case byte[5][] vs byte[5][2] and byte[5][0] vs byte [5][2]
    private static void checkGet5D(final Object[][][][] arr0) {
        for (int i0 = 0; i0 < arr0.length; i0++) {
            {
                Object[] exp = (Object[]) Array.get(arr0, i0);
                Object[] res = ArrayTools.get(arr0, i0);
                assertTrue(Arrays.deepEquals(exp, res));
                assertTrue(ArrayTools.deepEquals(exp, res));
                assertTrue(ArrayTools.deepEquals(
                        Arrays.copyOf(exp, exp.length), Arrays.copyOf(res, res.length)));
            }
            Object[][][] arr1 = arr0[i0];
            for (int i1 = 0; i1 < arr1.length; i1++) {
                {
                    Object[] exp = (Object[]) Array.get(arr1, i1);
                    Object[] res = ArrayTools.get(arr1, i1);
                    assertTrue(Arrays.deepEquals(exp, res));
                    assertTrue(ArrayTools.deepEquals(exp, res));
                    assertTrue(ArrayTools.deepEquals(
                            Arrays.copyOf(exp, exp.length), Arrays.copyOf(res, res.length)));
                }
                Object[][] arr2 = arr1[i1];
                for (int i2 = 0; i2 < arr2.length; i2++) {
                    {
                        Object[] exp = (Object[]) Array.get(arr2, i2);
                        Object[] res = ArrayTools.get(arr2, i2);
                        assertTrue(Arrays.deepEquals(exp, res));
                        assertTrue(ArrayTools.deepEquals(exp, res));
                        assertTrue(ArrayTools.deepEquals(
                                Arrays.copyOf(exp, exp.length), Arrays.copyOf(res, res.length)));
                    }
                    Object[] arr3 = arr2[i2];
                    for (int i3 = 0; i3 < arr3.length; i3++) {
                        {
                            Object exp = Array.get(arr3, i3);
                            Object res = ArrayTools.get(arr3, i3);
                            assertEquals(exp, res);
                            assertTrue(ArrayTools.deepEquals(exp, res));
                        }
                        Object arr4 = arr3[i3];
                        for (int i4 = 0; i4 < ArrayTools.length(arr4); i4++) {
                            {
                                Object exp = Array.get(arr4, i4);
                                Object res = ArrayTools.get(arr4, i4);
                                assertEquals(exp, res);
                                assertTrue(ArrayTools.deepEquals(exp, res));
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions") // Everything should be tested.
    public void getTest() {
        checkGet5D(new boolean[5][4][6][7][12]);
        checkGet5D(new byte[5][4][6][7][12]);
        checkGet5D(new char[5][4][6][7][12]);
        checkGet5D(new short[5][4][6][7][12]);
        checkGet5D(new int[5][4][6][7][12]);
        checkGet5D(new long[5][4][6][7][12]);
        checkGet5D(new float[5][4][6][7][12]);
        checkGet5D(new double[5][4][6][7][12]);

        checkGet5D(new Boolean[5][4][6][7][12]);
        checkGet5D(new Byte[5][4][6][7][12]);
        checkGet5D(new Character[5][4][6][7][12]);
        checkGet5D(new Short[5][4][6][7][12]);
        checkGet5D(new Integer[5][4][6][7][12]);
        checkGet5D(new Long[5][4][6][7][12]);
        checkGet5D(new Float[5][4][6][7][12]);
        checkGet5D(new Double[5][4][6][7][12]);
        
        checkGet5D(new String[5][4][6][7][12]);
        checkGet5D(new Object[5][4][6][7][12]);
        
        expEx(NullPointerException.class, () -> ArrayTools.get(null, 0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.get("Not an array!", 0));
    }
    
    private static void checkSet(final Object arr, final Object[] values) {
        Object exp = ArrayTools.copyOf(arr);
        for (int i = 0; i < ArrayTools.length(arr); i++) {
            for (Object v : values) {
                ArrayTools.set(arr, i, v);
                Array.set(exp, i, v);
                assertTrue(ArrayTools.deepEquals(exp, arr));
                if (arr instanceof Object[]) {
                    assertTrue(Arrays.deepEquals((Object[]) exp, (Object[]) arr));
                }
            }
        }
    }
    
    @Test
    @SuppressWarnings({"ConstantConditions"}) // Everything should be tested.
    public void setCopyOfTest() {
        checkSet(new boolean[10], BOOLEAN_CLASS_DEFAULT_ARR);
        checkSet(new byte[10], BYTE_CLASS_DEFAULT_ARR);
        checkSet(new char[10], CHAR_CLASS_DEFAULT_ARR);
        checkSet(new short[10], SHORT_CLASS_DEFAULT_ARR);
        checkSet(new int[10], INT_CLASS_DEFAULT_ARR);
        checkSet(new long[10], LONG_CLASS_DEFAULT_ARR);
        checkSet(new float[10], FLOAT_CLASS_DEFAULT_ARR);
        checkSet(new double[10], DOUBLE_CLASS_DEFAULT_ARR);

        checkSet(new Boolean[10], BOOLEAN_CLASS_DEFAULT_ARR);
        checkSet(new Byte[10], BYTE_CLASS_DEFAULT_ARR);
        checkSet(new Character[10], CHAR_CLASS_DEFAULT_ARR);
        checkSet(new Short[10],  SHORT_CLASS_DEFAULT_ARR);
        checkSet(new Integer[10], INT_CLASS_DEFAULT_ARR);
        checkSet(new Long[10], LONG_CLASS_DEFAULT_ARR);
        checkSet(new Float[10], FLOAT_CLASS_DEFAULT_ARR);
        checkSet(new Double[10], DOUBLE_CLASS_DEFAULT_ARR);
        
        checkSet(new String[10], new String[] {"a", "b", "c", "d"});
        checkSet(new Object[10], new Object[] {1, "b", 0.5, -0.5f, '5'});

        expEx(NullPointerException.class, () -> ArrayTools.set(null, 0, null));
        expEx(IllegalArgumentException.class, () -> ArrayTools.set("Not an array!", 0, null));
    }
    
    private static void copyCheck(final Object arr) {
        copyCheck(arr, null);
    }
    
    private static void copyCheck(final Object arr, Class<? extends Exception> ex) {
        if (ex != null) {
            expEx(ex, () -> ArrayTools.copyOf(arr));
            expEx(ex, () -> ArrayTools.copyOf(arr, 10, 0, 0, 0));

        } else {
            {
                Object copy = ArrayTools.copyOf(arr);
                assertTrue(ArrayTools.deepEquals(arr, copy));
                assertEquals(arr.getClass(), copy.getClass());
                assertEquals(ArrayTools.length(arr), ArrayTools.length(copy));
                assertNotSame(arr, copy);
                for (int i = 0; i < ArrayTools.length(arr); i++) {
                    checkSame(ArrayTools.get(arr, i), ArrayTools.get(copy, i));
                }
            }
            Class<?> type = arr.getClass().getComponentType();
            Class<?> counterpart = switchPrimObjClass(type);
            if (!Objects.equals(counterpart, type)) {
                Object copy = ArrayTools.copyOf(arr, Array.newInstance(counterpart, ArrayTools.length(arr)));
                assertTrue(ArrayTools.deepEquals(arr, copy));
                checkSameClassPrim(arr.getClass(), copy.getClass());
                assertEquals(ArrayTools.length(arr), ArrayTools.length(copy));
                assertNotSame(arr, copy);
                for (int i = 0; i < ArrayTools.length(arr); i++) {
                    checkSame(ArrayTools.get(arr, i), ArrayTools.get(copy, i));
                }
            }
            
            final int length = ArrayTools.length(arr);
            for (int offArr = -1; offArr < length; offArr++) {
                for (int offCpy = -1; offCpy < length; offCpy++) {
                    for (int len = -1; len < length - offArr + 1; len++) { // Off by 1 on purpose.
                        copyCheck(arr, offArr, offCpy, len);
                    }
                }
            }
        }
    }
    
    private static void copyCheck(final Object arr, int offArr, int offCpy, int len) {
        if (len < 0 || offArr < 0 || offCpy < 0 || offArr + len >= ArrayTools.length(arr) ||
                offCpy + len >= ArrayTools.length(arr)) {
            expEx(IllegalArgumentException.class, () -> ArrayTools.copyOf(arr, offArr, offCpy, len));
            
        } else {
            Object copy = ArrayTools.copyOf(arr, ArrayTools.length(arr), offArr, offCpy, len);
            assertNotNull(copy);
            assertNotSame(arr, copy);
            assertEquals(ArrayTools.length(arr), ArrayTools.length(copy));
            assertEquals(arr.getClass(), copy.getClass());
            for (int i = 0; i < len; i++) {
                checkSame(ArrayTools.get(arr, i + offArr), ArrayTools.get(copy, i + offCpy));
            }
            
            assertEquals(arr.getClass(), copy.getClass());
            assertEquals(ArrayTools.length(arr), ArrayTools.length(copy));
            assertNotSame(arr, copy);
            for (int i = 0; i < len; i++) {
                checkSame(ArrayTools.get(arr, i + offArr), ArrayTools.get(copy, i + offCpy));
            }
            for (int i = 0; i < offCpy; i++) {
                checkInitialValue(ArrayTools.get(copy, i));
            }
            for (int i = len + offCpy + 1; i < ArrayTools.length(copy); i++) {
                checkInitialValue(ArrayTools.get(copy, i));
            }
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions") // Everything should be tested.
    public void copyOf() {
        copyCheck(BOOLEAN_TYPE_DEFAULT_ARR);
        copyCheck(BYTE_TYPE_DEFAULT_ARR);
        copyCheck(CHAR_TYPE_DEFAULT_ARR);
        copyCheck(SHORT_TYPE_DEFAULT_ARR);
        copyCheck(INT_TYPE_DEFAULT_ARR);
        copyCheck(LONG_TYPE_DEFAULT_ARR);
        copyCheck(FLOAT_TYPE_DEFAULT_ARR);
        copyCheck(DOUBLE_TYPE_DEFAULT_ARR);

        copyCheck(BOOLEAN_CLASS_DEFAULT_ARR);
        copyCheck(BYTE_CLASS_DEFAULT_ARR);
        copyCheck(CHAR_CLASS_DEFAULT_ARR);
        copyCheck(SHORT_CLASS_DEFAULT_ARR);
        copyCheck(INT_CLASS_DEFAULT_ARR);
        copyCheck(LONG_CLASS_DEFAULT_ARR);
        copyCheck(FLOAT_CLASS_DEFAULT_ARR);
        copyCheck(DOUBLE_CLASS_DEFAULT_ARR);
        
        copyCheck(new String[] {"a", "b", "c", "d"});
        copyCheck(new Object[] {1, "b", 0.5, -0.5f, '5'});
        
        copyCheck(null, NullPointerException.class);
        copyCheck(1, IllegalArgumentException.class);
        copyCheck("hey", IllegalArgumentException.class);
        copyCheck(new Object(), IllegalArgumentException.class);
        expEx(NullPointerException.class, () -> ArrayTools.copyOf(null, 0, null, 0, 0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.copyOf(new int[1], 2, new int[1], 0, 0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.copyOf(new int[1], 0, new int[1], 2, 0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.copyOf(new int[1], 2, new int[1], 0, 1));
        expEx(IllegalArgumentException.class, () -> ArrayTools.copyOf(new int[1], 0, new int[1], 2, 1));
        expEx(IllegalArgumentException.class, () -> ArrayTools.copyOf(new int[1], 0, new int[1], 0, 2));
        expEx(ClassCastException.class, () -> ArrayTools.copyOf(new int[1], 0, new Boolean[1], 0, 1));
    }
    
    
    /* ------------------------------------------------------------------------
     * asList/toList functions.
     * ------------------------------------------------------------------------
     */
    private static <T> void checkLists(final Object array, final T elem) {
        {
            Object arr = ArrayTools.copyOf(array);
            List<T> list = ArrayTools.toList(arr);
            assertNotNull(list);
            assertEquals(ArrayTools.length(arr), list.size());
            for (int i = 0; i < list.size(); i++) {
                checkSame(ArrayTools.get(arr, i), list.get(i));
                if (list.get(i) == null) {
                    assertEquals(list.set(i, elem), ArrayTools.get(arr, i));
                    assertNotNull(list.get(i));
                    assertNull(ArrayTools.get(arr, i));
                    
                } else {
                    assertEquals(list.set(i, null), ArrayTools.get(arr, i));
                    assertNull(list.get(i));
                    assertNotEquals(ArrayTools.get(arr, i), list.get(i));
                }
            }
        }
        {
            Object arr = ArrayTools.copyOf(array);
            boolean isPrim = arr.getClass().getComponentType().isPrimitive();
            List<T> list = ArrayTools.asList(arr);
            assertNotNull(list);
            assertEquals(ArrayTools.length(arr), list.size());
            for (int i = 0; i < list.size(); i++) {
                final int index = i;
                checkSame(ArrayTools.get(arr, i), list.get(i));
                if (isPrim) {
                    expEx(NullPointerException.class, () -> list.set(index, null));
                    checkSame(ArrayTools.get(arr, i), list.get(i));
                }
                if (list.get(i) == null) {
                    assertEquals(list.set(i, elem), ArrayTools.get(arr, i));
                    assertNotNull(list.get(i));
                    assertNotNull(ArrayTools.get(arr, i));
                    checkSame(ArrayTools.get(arr, i), list.get(i));

                } else {
                    Object old = ArrayTools.get(arr, i);
                    if (isPrim) {
                        checkSame(list.set(i, elem), old);
                        checkSame(list.get(i), ArrayTools.get(arr, i));
                        
                    } else {
                        assertEquals(list.set(i, null), old);
                        assertNull(list.get(i));
                        assertNull(ArrayTools.get(arr, i));
                    }
                }
            }
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions") // Everything should be tested.
    public void asToListTest() {
        checkLists(BOOLEAN_TYPE_DEFAULT_ARR, true);
        checkLists(BYTE_TYPE_DEFAULT_ARR, (byte) 200);
        checkLists(CHAR_TYPE_DEFAULT_ARR, (char) 200);
        checkLists(SHORT_TYPE_DEFAULT_ARR, (short) 200);
        checkLists(INT_TYPE_DEFAULT_ARR, 200);
        checkLists(LONG_TYPE_DEFAULT_ARR, 200L);
        checkLists(FLOAT_TYPE_DEFAULT_ARR, 200.1234f);
        checkLists(DOUBLE_TYPE_DEFAULT_ARR, 200.1234);

        checkLists(BOOLEAN_CLASS_DEFAULT_ARR, true);
        checkLists(BYTE_CLASS_DEFAULT_ARR, (byte) 200);
        checkLists(CHAR_CLASS_DEFAULT_ARR, (char) 200);
        checkLists(SHORT_CLASS_DEFAULT_ARR, (short) 200);
        checkLists(INT_CLASS_DEFAULT_ARR, 200);
        checkLists(LONG_CLASS_DEFAULT_ARR, 200L);
        checkLists(FLOAT_CLASS_DEFAULT_ARR, 200.1234f);
        checkLists(DOUBLE_CLASS_DEFAULT_ARR, 200.1234);

        checkLists(new String[] {"a", "b", "c", "d"}, "x");
        checkLists(new Object[] {1, "b", 0.5, -0.5f, '5'}, 1234.1234);
        
        expEx(NullPointerException.class, () -> ArrayTools.asList((Object) null));
        expEx(IllegalArgumentException.class, () -> ArrayTools.asList("Not an array!"));
        expEx(IllegalArgumentException.class, () -> ArrayTools.asList(0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.asList(new Object()));
        
        expEx(NullPointerException.class, () -> ArrayTools.toList((Object) null));
        expEx(IllegalArgumentException.class, () -> ArrayTools.toList("Not an array!"));
        expEx(IllegalArgumentException.class, () -> ArrayTools.toList(0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.toList(new Object()));
    }

    
    /* ------------------------------------------------------------------------
     * newInstanceOf functions.
     * ------------------------------------------------------------------------
     */
    @Test
    public void newInstanceOfTest() {
        assertArrayEquals(new int[5], ArrayTools.newInstanceOf(new int[5]));
        assertArrayEquals(new int[0], ArrayTools.newInstanceOf(new int[0]));
        assertArrayEquals(new Integer[5], ArrayTools.newInstanceOf(new Integer[5]));
        assertTrue(ArrayTools.deepEquals(
                new boolean[5][][][][][],
                ArrayTools.newInstanceOf(new boolean[5][6][2][7][9][2])
        ));
        assertArrayEquals((Object[]) null, ArrayTools.newInstanceOf(null));
        expEx(IllegalArgumentException.class, () -> ArrayTools.newInstanceOf(1));
        expEx(IllegalArgumentException.class, () -> ArrayTools.newInstanceOf("Not an array!"));
        expEx(IllegalArgumentException.class, () -> ArrayTools.newInstanceOf(new Object()));
        
        assertArrayEquals(new int[5], ArrayTools.newInstanceOf(new int[10], 5));
        assertArrayEquals(new int[5], ArrayTools.newInstanceOf(new int[0], 5));
        assertArrayEquals(new Integer[5], ArrayTools.newInstanceOf(new Integer[10], 5));
        assertArrayEquals(new Integer[5], ArrayTools.newInstanceOf(new Integer[0], 5));
        assertTrue(ArrayTools.deepEquals(
                new boolean[10][][][][][],
                ArrayTools.newInstanceOf(new boolean[5][6][2][7][9][2], 10)
        ));
        assertArrayEquals((Object[]) null, ArrayTools.newInstanceOf(null, 10));
        expEx(IllegalArgumentException.class, () -> ArrayTools.newInstanceOf(1, 5));
        expEx(IllegalArgumentException.class, () -> ArrayTools.newInstanceOf("Not an array!", 5));
        expEx(IllegalArgumentException.class, () -> ArrayTools.newInstanceOf(new Object(), 5));
    }
    
    
    /* ------------------------------------------------------------------------
     * swap functions.
     * ------------------------------------------------------------------------
     */
    private static void checkSwap(final Object array) {
        Object arr = ArrayTools.copyOf(array);
        final int l = ArrayTools.length(array);
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) { 
                ArrayTools.swap(arr, i, j);
                checkArr(array, arr, i, j);
                ArrayTools.swap(arr, i, j);
                checkArr(array, arr, -1, -1);
            }
        }
    }
    
    private static void checkArr(final Object arr1, final Object arr2, int i, int j) {
        if (i >= 0 && j >= 0) {
            assertEquals((Object) ArrayTools.get(arr1, i), ArrayTools.get(arr2, j));
            assertEquals((Object) ArrayTools.get(arr1, j), ArrayTools.get(arr2, i));
        }
        for (int k = 0; k < ArrayTools.length(arr1); k++) {
            if (k != i && k != j) {
                assertEquals((Object) ArrayTools.get(arr1, k), ArrayTools.get(arr2, k));
            }
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions") // Everything should be tested.
    public void swapTest() {
        checkSwap(BOOLEAN_TYPE_DEFAULT_ARR);
        checkSwap(BYTE_TYPE_DEFAULT_ARR);
        checkSwap(CHAR_TYPE_DEFAULT_ARR);
        checkSwap(SHORT_TYPE_DEFAULT_ARR);
        checkSwap(INT_TYPE_DEFAULT_ARR);
        checkSwap(LONG_TYPE_DEFAULT_ARR);
        checkSwap(FLOAT_TYPE_DEFAULT_ARR);
        checkSwap(DOUBLE_TYPE_DEFAULT_ARR);

        checkSwap(BOOLEAN_CLASS_DEFAULT_ARR);
        checkSwap(BYTE_CLASS_DEFAULT_ARR);
        checkSwap(CHAR_CLASS_DEFAULT_ARR);
        checkSwap(SHORT_CLASS_DEFAULT_ARR);
        checkSwap(INT_CLASS_DEFAULT_ARR);
        checkSwap(LONG_CLASS_DEFAULT_ARR);
        checkSwap(FLOAT_CLASS_DEFAULT_ARR);
        checkSwap(DOUBLE_CLASS_DEFAULT_ARR);
        
        expEx(NullPointerException.class, () -> ArrayTools.swap((Object) null, 0, 0));
        expEx(ArrayIndexOutOfBoundsException.class, () -> ArrayTools.swap(new int[2], -1, 0));
        expEx(ArrayIndexOutOfBoundsException.class, () -> ArrayTools.swap(new int[2], 0, 2));
        expEx(IllegalArgumentException.class, () -> ArrayTools.swap("Not an array!", 0, 0));
        expEx(IllegalArgumentException.class, () -> ArrayTools.swap(new Object(), 0, 0));
    }
    
    
    /* ------------------------------------------------------------------------
     * shuffle functions.
     * ------------------------------------------------------------------------
     */
    private static void checkShuffle(final Object array) {
        {
            Object arr = ArrayTools.copyOf(array);
            List<?> list = ArrayTools.toList(arr);
            assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle(arr), arr));
            ArrayTools.forEach(arr, (elem) -> assertTrue(list.remove(elem)));
        }
        {
            Object arr = ArrayTools.copyOf(array);
            List<?> list = ArrayTools.toList(arr);
            
            if (array instanceof boolean[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((boolean[]) arr), arr));
            else if (array instanceof byte[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((byte[]) arr), arr));
            else if (array instanceof char[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((char[]) arr), arr));
            else if (array instanceof short[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((short[]) arr), arr));
            else if (array instanceof int[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((int[]) arr), arr));
            else if (array instanceof long[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((long[]) arr), arr));
            else if (array instanceof float[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((float[]) arr), arr));
            else if (array instanceof double[])
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle((double[]) arr), arr));
            else
                assertTrue(ArrayTools.deepEquals(ArrayTools.shuffle(arr), arr));
            
            ArrayTools.forEach(arr, (elem) -> assertTrue(list.remove(elem)));
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions") // Everything should be tested.
    public void shuffleTest() {
        checkShuffle(BOOLEAN_TYPE_DEFAULT_ARR);
        checkShuffle(BYTE_TYPE_DEFAULT_ARR);
        checkShuffle(CHAR_TYPE_DEFAULT_ARR);
        checkShuffle(SHORT_TYPE_DEFAULT_ARR);
        checkShuffle(INT_TYPE_DEFAULT_ARR);
        checkShuffle(LONG_TYPE_DEFAULT_ARR);
        checkShuffle(FLOAT_TYPE_DEFAULT_ARR);
        checkShuffle(DOUBLE_TYPE_DEFAULT_ARR);

        checkShuffle(BOOLEAN_CLASS_DEFAULT_ARR);
        checkShuffle(BYTE_CLASS_DEFAULT_ARR);
        checkShuffle(CHAR_CLASS_DEFAULT_ARR);
        checkShuffle(SHORT_CLASS_DEFAULT_ARR);
        checkShuffle(INT_CLASS_DEFAULT_ARR);
        checkShuffle(LONG_CLASS_DEFAULT_ARR);
        checkShuffle(FLOAT_CLASS_DEFAULT_ARR);
        checkShuffle(DOUBLE_CLASS_DEFAULT_ARR);
        
        checkShuffle(new Object[] {1, 5.5, "String", new Object(), null});

        expEx(NullPointerException.class, () -> ArrayTools.shuffle((Object) null));
        expEx(IllegalArgumentException.class, () -> ArrayTools.shuffle("Not an array!"));
        expEx(IllegalArgumentException.class, () -> ArrayTools.shuffle(new Object()));
    }

    
    /* ------------------------------------------------------------------------
     * toArray functions.
     * ------------------------------------------------------------------------
     */
//    @Test
//    public void toArrayTest() {
//        
//    }
    
    
}
