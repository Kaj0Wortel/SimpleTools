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

package com.github.simple_tools.data.containers;

import java.util.Objects;

/**
 * This class contains two elements of the same type.
 * The order of these two elements is ignored for the {@link #equals(Object)},
 * {@link #equals(UnorderedPair)} and {@link #hashCode()} functions.
 * 
 * @param <V> The type of the two elements.
 *
 * @author Kaj Wortel
 */
@SuppressWarnings("unused")
public class UnorderedPair<V> {

    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The first element of the pair. */
    private V first;
    /** The second element of the pair. */
    private V second;


    /* ------------------------------------------------------------------------
     * Constructor.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new unordered pair.
     * 
     * @param first  The first element of the pair.
     * @param second The second element of the pair.
     */
    public UnorderedPair(V first, V second) {
        this.first = first;
        this.second = second;
    }


    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * @return The first element of the pair.
     */
    public V getFirst() {
        return first;
    }

    /**
     * @return The second element of the pair.
     */
    public V getSecond() {
        return second;
    }

    /**
     * Sets the first element of the pair.
     *
     * @param first The new first element of the pair.
     */
    public void setFirst(V first) {
        this.first = first;
    }

    /**
     * Sets the second element of the pair.
     *
     * @param second The new first element of the pair.
     */
    public void setSecond(V second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "[first=" + first + ",second=" + second + "]";
    }

    @Override
    public int hashCode() {
        int h1 = first.hashCode();
        int h2 = second.hashCode();
        return 53 * (h1 + h2) + 73 * (h1 - h2);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnorderedPair)) return false;
        else return equals((UnorderedPair<?>) obj);
    }

    /**
     * @param pair The pair to compare with.
     *
     * @return {@code true} if both pairs are equal. {@code false} otherwise.
     *
     * @see #equals(Object)
     */
    public boolean equals(UnorderedPair<?> pair) {
        return (Objects.equals(first, pair.first) && Objects.equals(second, pair.second)) ||
               (Objects.equals(first, pair.second) && Objects.equals(second, pair.first));
    }


}
