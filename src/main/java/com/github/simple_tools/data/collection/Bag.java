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

import java.util.*;

public interface Bag<E>
        extends Set<E> {

    /**
     * {@inheritDoc}
     * <br>
     * Adds a single element to the bag.
     */
    @Override
    default boolean add(E elem) {
        return add(elem, 1);
    }

    /**
     * Adds {@code count} times {@code elem} to the bag.
     * 
     * @param elem The element to be added.
     * @param amt  The amount of elements to be added.
     * 
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     *
     * @throws IllegalArgumentException If {@code amt <= 0}.
     */
    boolean add(E elem, int amt);

    /**
     * Adds all elements of the given bag to this bag.
     * The element counts of the given bag will be used for the addition.
     * 
     * @param bag The bag to add the elements of.
     * 
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     */
    default boolean addAll(Bag<? extends E> bag) {
        boolean mod = false;
        for (E elem : bag) {
            if (add(elem, bag.count(elem))) {
                mod = true;
            }
        }
        return mod;
    }

    /**
     * Adds all elements in the map with their respective count.
     *
     * @param map The map contain in the elements to add and their respective counts.
     *
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     *
     * @throws IllegalArgumentException If any element has {@code count <= 0}.
     */
    default boolean addAll(Map<? extends E, Integer> map) {
        boolean mod = false;
        for (Map.Entry<? extends E, Integer> entry : map.entrySet()) {
            if (add(entry.getKey(), entry.getValue())) {
                mod = true;
            }
        }
        return mod;
    }

    /**
     * Removes all elements of the given bag from this bag.
     * The element counts of the given bag will be used for the removal.
     *
     * @param bag The bag used to remove the elements.
     *
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     */
    default boolean removeAll(Bag<?> bag) {
        boolean mod = false;
        for (Object elem : bag) {
            if (remove(elem, bag.count(elem))) {
                mod = true;
            }
        }
        return mod;
    }
    
    /**
     * Removes all elements in the map with their respective count.
     *
     * @param map The map contain in the elements to remove and their respective counts.
     *
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     *
     * @throws IllegalArgumentException If any element has {@code count <= 0}.
     */
    default boolean removeAll(Map<? extends E, Integer> map) {
        boolean mod = false;
        for (Map.Entry<? extends E, Integer> entry : map.entrySet()) {
            if (remove(entry.getKey(), entry.getValue())) {
                mod = true;
            }
        }
        return mod;
    }

    /**
     * Retains all elements of the given bag from this bag.
     * The element counts of the given bag will be used for the retaining.
     *
     * @param bag The bag to retain the elements from.
     *
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     */
    default  boolean retainAll(Bag<?> bag) {
        Map<E, Integer> removeMap = new HashMap<>();
        for (E elem : this) {
            int otherCount = bag.count(elem);
            if (otherCount == 0) {
                removeMap.put(elem, Integer.MAX_VALUE);
            } else {
                int count = this.count(elem);
                if (count > otherCount) {
                    removeMap.put(elem, count - otherCount);
                }
            }
        }
        return removeAll(removeMap);
    }

    /**
     * {@inheritDoc}
     * <br>
     * Removes the element from the bag according to the {@link Set} definition.
     */
    @Override
    default boolean remove(Object elem) {
        return remove(elem, Integer.MAX_VALUE);
    }

    /**
     * Removes {@code count} times {@code elem} from the bag,
     * 
     * @param elem The element to be removed.
     * @param amt  The amount of elements to be removed.
     * 
     * @return {@code true} if the bag was modified. {@code false} otherwise.
     *
     * @throws IllegalArgumentException If {@code amt <= 0}.
     */
    boolean remove(Object elem, int amt);

    /**
     * Counts the amount of elements in the bag.
     * 
     * @param elem The element to count.
     * 
     * @return The count of {@code elem} in the bag.
     */
    int count(Object elem);

    /**
     * @return The sum of the counts of the elements in the bag.
     */
    long bagSize();
    
    @Override
    default boolean isEmpty() {
        return size() == 0;
    }
    
    @Override
    default boolean contains(Object obj) {
        return count(obj) != 0;
    }
    
    
}
