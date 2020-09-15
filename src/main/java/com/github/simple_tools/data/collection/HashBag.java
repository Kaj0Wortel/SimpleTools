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

public class HashBag<E>
        extends AbstractCollection<E>
        implements Bag<E> {

    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    protected final Map<E, Integer> map = new HashMap<>();
    protected int bagSize = 0;
    
    
    /* ------------------------------------------------------------------------
     * Constructors.
     * ------------------------------------------------------------------------
     */
    public HashBag() {
    }

    public HashBag(Collection<? extends E> col) {
        addAll(col);
    }
    
    public HashBag(Bag<? extends E> map) {
        addAll(map);
    }

    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    @Override
    public boolean add(E elem) {
        return Bag.super.add(elem);
    }
    
    @Override
    public boolean add(E elem, int amt) {
        if (amt <= 0) throw new IllegalArgumentException("amt <= 0");
        if (map.merge(elem, amt, Integer::sum) < 0) {
            throw new ArithmeticException("Integer overflow!");
        }
        bagSize += amt;
        return true;
    }
    
    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean contains(Object elem) {
        return map.containsKey(elem);
    }
    
    @Override
    public boolean remove(Object elem) {
        return Bag.super.remove(elem);
    }

    @Override
    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    public boolean remove(Object elem, int amt) {
        if (amt <= 0) throw new IllegalArgumentException("amt <= 0");
        Integer val = map.get(elem);
        if (val == null) return false;
        if (amt >= val) {
            map.remove(elem);
            bagSize -= val;
        } else {
            map.put((E) elem, val - amt);
            bagSize -= amt;
        }
        return true;
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public int count(Object elem) {
        return map.getOrDefault(elem, 0);
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public long bagSize() {
        return bagSize;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
        bagSize = 0;
    }
    
    
}
