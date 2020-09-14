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

import java.util.Map;
import java.util.function.Function;

/**
 * Similar to {@link CollectionFunction}, but then for a {@link Bag}.
 * 
 * @param <S> The source type of the bag function.
 * @param <T> The target type of the bag function.
 *
 * @author Kaj Wortel 
 */
public class BagFunction<S, T>
        extends CollectionFunction<S, T>
        implements Bag<T> {

    /**
     * Creates a new bag function using the given bag and the given transition functions as base.
     *
     * @param src        The underlying source collection.
     * @param tsFunction The function used to transform a target element to a source element.
     * @param stFunction The function used to transform a source element to a target element.
     */
    public BagFunction(Bag<S> src, Function<T, S> tsFunction, Function<S, T> stFunction) {
        super(src, tsFunction, stFunction);
    }
    
    @Override
    public Bag<S> getSrc() {
        return (Bag<S>) src;
    }

    @Override
    public boolean add(T elem, int amt) {
        return getSrc().add(tsFunction.apply(elem), amt);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Bag<? extends T> bag) {
        return getSrc().addAll(new BagFunction<>((Bag<T>) bag, stFunction, tsFunction));
    }

    @Override
    public boolean addAll(Map<? extends T, Integer> map) {
        // TODO: optimize with MapFunction.
        boolean mod = false;
        for (Map.Entry<? extends T, Integer> entry : map.entrySet()) {
            if (getSrc().add(tsFunction.apply(entry.getKey()), entry.getValue())) {
                mod = true;
            }
        }
        return mod;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object elem, int amt) {
        return getSrc().remove(tsFunction.apply((T) elem), amt);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(Bag<?> bag) {
        return getSrc().removeAll(new BagFunction<>((Bag<T>) bag, stFunction, tsFunction));
    }
    
    @Override
    public boolean removeAll(Map<? extends T, Integer> map) {
        // TODO: optimize with MapFunction.
        boolean mod = false;
        for (Map.Entry<? extends T, Integer> entry : map.entrySet()) {
            if (getSrc().remove(tsFunction.apply(entry.getKey()), entry.getValue())) {
                mod = true;
            }
        }
        return mod;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Bag<?> bag) {
        return getSrc().retainAll(new BagFunction<>((Bag<T>) bag, stFunction, tsFunction));
    }

    @Override
    @SuppressWarnings("unchecked")
    public int count(Object elem) {
        return getSrc().count(tsFunction.apply((T) elem));
    }

    @Override
    public int bagSize() {
        return getSrc().bagSize();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (!(obj instanceof Bag)) return false;
        Bag<T> col = (Bag<T>) obj;
        return src.equals(new BagFunction<>(col, stFunction, tsFunction));
    }
    
    
}
