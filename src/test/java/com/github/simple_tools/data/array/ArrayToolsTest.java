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
import com.github.simple_tools.FastMath;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

public class ArrayToolsTest
        extends AbstractTest {
    private static final int MIN_RADIX = 2;
    private static final int MAX_RADIX = 36;
    
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
    
    @SuppressWarnings("DuplicateExpressions")
    private static <V> String genString(Object arr, int length, Function<V, String> function, boolean allowNeg) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (int i = 0; i < ArrayTools.length(arr); i++) {
            if (first) first = false;
            else sb.append(", ");
            String str = function.apply(ArrayTools.get(arr, i));
            if (allowNeg) {
                if (str.startsWith("-")) {
                    sb.append("-");
                    sb.append("0".repeat(length - str.length() + 1));
                    sb.append(str.substring(1));
                } else {
                    sb.append(" ");
                    sb.append("0".repeat(length - str.length()));
                    sb.append(str);
                }
            } else {
                sb.append("0".repeat(length - str.length()));
                sb.append(str);
            }
        }
        sb.append("]");
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
    
    
}
