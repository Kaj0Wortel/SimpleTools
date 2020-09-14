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
package com.github.simple_tools.data.collection;


import com.github.simple_tools.iterators.GeneratorIterator;
import lombok.Getter;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Collection implementation which can be used to easily convert
 * type mismatched collections which mainly use a fairly simple
 * conversion rule to switch between the source and target type. <br>
 * <br>
 * All operations are executed on the underlying collection.
 * This implies that modifying the source set outside this class will
 * cause modifications in this class and vice versa.
 *
 * @param <S> The source type of the collection function.
 * @param <T> The target type of the collection function.
 * 
 * @author Kaj Wortel
 */
@Getter
public class CollectionFunction<S, T>
        implements Collection<T> {
    
    /* -------------------------------------------------------------------------
     * Variables.
     * -------------------------------------------------------------------------
     */
    /** The underlying source collection. */
    protected final Collection<S> src;
    /** The function used to transform a target element to a source element. */
    protected final Function<T, S> tsFunction;
    /** The function used to transform a source element to a target element. */
    protected final Function<S, T> stFunction;
    
    
    /* -------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates a new collection function using the given collection and the given
     * transition functions as base.
     * 
     * @param src        The underlying source collection.
     * @param tsFunction The function used to transform a target element to a source element.
     * @param stFunction The function used to transform a source element to a target element.
     */
    public CollectionFunction(Collection<S> src, Function<T, S> tsFunction,
                              Function<S, T> stFunction) {
        this.src = src;
        this.tsFunction = tsFunction;
        this.stFunction = stFunction;
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    public int size() {
        return src.size();
    }
    
    @Override
    public boolean isEmpty() {
        return src.isEmpty();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object obj) {
        return src.contains(tsFunction.apply((T) obj));
    }
    
    @Override
    public Iterator<T> iterator() {
        return new GeneratorIterator<>() {
            final Iterator<S> it = src.iterator();
            @Override
            protected T generateNext() {
                if (it.hasNext()) return stFunction.apply(it.next());
                done();
                return null;
            }
        };
    }
    
    @Override
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1[] toArray(T1[] arr) {
        int i = 0;
        for (S elem : src) {
            arr[i++] = (T1) stFunction.apply(elem);
        }
        return arr;
    }
    
    @Override
    public boolean add(T elem) {
        return src.add(tsFunction.apply(elem));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object obj) {
        return src.remove(tsFunction.apply((T) obj));
    }
    
    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsAll(Collection<?> col) {
        // TODO: optimize.
        for (Object obj : col) {
            if (!contains(obj)) return false;
        }
        return true;
    }
    
    @Override
    public boolean addAll(Collection<? extends T> col) {
        // TODO: optimize.
        boolean mod = false;
        for (T elem : col) {
            if (add(elem)) mod = true;
        }
        return mod;
    }
    
    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean removeAll(Collection<?> col) {
        // TODO: optimize.
        boolean mod = false;
        for (Object obj : col) {
            if (remove(obj)) mod = true;
        }
        return mod;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection<?> col) {
        return src.retainAll(new CollectionFunction<>((Collection<T>) col, stFunction, tsFunction));
    }
    
    @Override
    public void clear() {
        src.clear();
    }
    
    @Override
    public String toString() {
        return src.toString();
    }
    
    @Override
    public int hashCode() {
        return src.hashCode();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (!(obj instanceof Collection)) return false;
        Collection<T> col = (Collection<T>) obj;
        return src.equals(new CollectionFunction<>(col, stFunction, tsFunction));
    }
    
    
}
