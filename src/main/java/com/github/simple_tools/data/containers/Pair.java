package com.github.simple_tools.data.containers;

import java.util.Objects;

/**
 * This class contains two elements of the same type.
 * The two elements are ordered, so {@code (0, 1)} and {@code (1, 0)} are considered different pairs.
 *
 * @param <A> The type of the first element.
 * @param <B> The type of the second element.
 * 
 * @author Kaj Wortel
 */
@SuppressWarnings("unused")
public class Pair<A, B> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The first element of the pair. */
    private A first;
    /** The second element of the pair. */
    private B second;


    /* ------------------------------------------------------------------------
     * Constructor.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new pair.
     *
     * @param first  The first element of the pair.
     * @param second The second element of the pair.
     */
    public Pair(A first, B second) {
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
    public A getFirst() {
        return first;
    }

    /**
     * @return The second element of the pair.
     */
    public B getSecond() {
        return second;
    }

    /**
     * Sets the first element of the pair.
     *
     * @param first The new first element of the pair.
     */
    public void setFirst(A first) {
        this.first = first;
    }

    /**
     * Sets the second element of the pair.
     *
     * @param second The new first element of the pair.
     */
    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "[first=" + first + ",second=" + second + "]";
    }

    @Override
    public int hashCode() {
        return 41 * first.hashCode() + 83 * second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) return false;
        else return equals((Pair<?, ?>) obj);
    }

    /**
     * @param pair The pair to compare with.
     *
     * @return {@code true} if both pairs are equal. {@code false} otherwise.
     *
     * @see #equals(Object)
     */
    public boolean equals(Pair<?, ?> pair) {
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }


}
