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

import com.github.simple_tools.AbstractTest;
import com.github.simple_tools.data.collection.rb_tree.LinkedRBTreeBag;
import com.github.simple_tools.data.collection.rb_tree.RBTreeBag;
import com.github.simple_tools.data.collection.rb_tree.SimpleLinkedRBBagKey;
import com.github.simple_tools.data.containers.Pair;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

/**
 * Test class for all {@link Bag} implementation classes.
 * 
 * @author Kaj Wortel
 */
public class BagTest
        extends AbstractTest {
    
    protected List<Bag<Integer>> createBags() {
        return List.of(
                new HashBag<>(),
                new RBTreeBag<>(Integer::compareTo),
                new BagFunction<SimpleLinkedRBBagKey<Integer>, Integer>(
                        new LinkedRBTreeBag<SimpleLinkedRBBagKey<Integer>>(
                                Comparator.comparingInt(SimpleLinkedRBBagKey::getData)),
                        SimpleLinkedRBBagKey::new,
                        SimpleLinkedRBBagKey::getData
                )
        );
    }
    
    @Test(timeout = 20_000L)
    @SuppressWarnings({"ConstantConditions", "RedundantOperationOnEmptyContainer"})
    public void addRemoveContainsCountTest() {
        List<Bag<Integer>> bags = createBags();
        for (Bag<Integer> bag : bags) {
            System.out.println(bag.getClass().getName());
            assertFalse(bag.contains(1));
            assertEquals(0, bag.count(1));
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            assertTrue(bag.isEmpty());
            
            assertTrue(bag.add(1));
            assertTrue(bag.contains(1));
            assertEquals(1, bag.count(1));
            assertEquals(1, bag.size());
            assertEquals(1, bag.bagSize());
            assertFalse(bag.isEmpty());
            
            assertTrue(bag.add(1));
            assertTrue(bag.contains(1));
            assertEquals(2, bag.count(1));
            assertEquals(1, bag.size());
            assertEquals(2, bag.bagSize());
            assertFalse(bag.isEmpty());

            assertTrue(bag.add(1, 2));
            assertTrue(bag.contains(1));
            assertEquals(4, bag.count(1));
            assertEquals(1, bag.size());
            assertEquals(4, bag.bagSize());
            assertFalse(bag.isEmpty());
            
            assertTrue(bag.add(2, 100));
            assertTrue(bag.contains(2));
            assertEquals(100, bag.count(2));
            assertEquals(2, bag.size());
            assertEquals(104, bag.bagSize());
            assertFalse(bag.isEmpty());
            
            assertTrue(bag.remove(1));
            assertFalse(bag.contains(1));
            assertEquals(0, bag.count(1));
            assertEquals(1, bag.size());
            assertEquals(100, bag.bagSize());
            assertFalse(bag.isEmpty());
            
            assertFalse(bag.remove(1));
            assertFalse(bag.remove(1, 1));
            expEx(IllegalArgumentException.class, () -> bag.remove(1, 0));
            expEx(IllegalArgumentException.class, () -> bag.remove(1, -1));
            assertEquals(1, bag.size());
            assertEquals(100, bag.bagSize());
            assertFalse(bag.isEmpty());
            
            assertTrue(bag.remove(2, 5));
            assertTrue(bag.contains(2));
            assertEquals(95, bag.count(2));
            assertEquals(1, bag.size());
            assertEquals(95, bag.bagSize());
            assertFalse(bag.isEmpty());
            
            bag.clear();
            assertFalse(bag.contains(2));
            assertEquals(0, bag.count(2));
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            assertTrue(bag.isEmpty());
            
            assertFalse(bag.remove(2));
            assertFalse(bag.contains(2));
            assertEquals(0, bag.count(2));
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            assertTrue(bag.isEmpty());
        }
    }
    
    @Test
    public void addAllTest() {
        List<Bag<Integer>> bags = createBags();
        final int NUM = 100_000;
        List<Integer> list = new ArrayList<>(NUM);
        for (int i = 0; i < NUM; i++) {
            list.add(i);
        }
        
        for (Bag<Integer> bag : bags) {
            System.out.println(bag.getClass().getName());
            bag.addAll(list);
            
            for (int i = 0; i < NUM; i++) {
                assertTrue(bag.contains(i));
            }
            assertFalse(bag.contains(-1));
            assertFalse(bag.contains(NUM));
            
            for (int i = 0; i < NUM; i++) {
                assertEquals(bag.count(i), 1);
            }
            assertEquals(bag.count(-1), 0);
            assertEquals(bag.count(NUM), 0);
        }
    }
    
    @Test
    @SuppressWarnings("ConstantConditions")
    public void randomTest() {
        final int NUM = 100_000;
        final int MAX_BAG_SIZE = 100;
        Map<Integer, Integer> map = new HashMap<>();
        List<Pair<Integer, Integer>> pairList = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            int value = ThreadLocalRandom.current().nextInt();
            int amt = ThreadLocalRandom.current().nextInt(MAX_BAG_SIZE) + 1;
            if (map.merge(value, amt, Integer::sum) < 0) {
                throw new ArithmeticException();
            }
            pairList.add(new Pair<>(value, amt));
        }
        
        List<Bag<Integer>> bags = createBags();
        for (Bag<Integer> bag : bags) {
            System.out.println(bag.getClass().getName());
            
            int size = 0;
            int bagSize = 0;
            Set<Integer> contains = new HashSet<>();
            for (Pair<Integer, Integer> pair : pairList) {
                if (contains.add(pair.getFirst())) {
                    size++;
                }
                bagSize += pair.getSecond();
                
                assertTrue(bag.add(pair.getFirst(), pair.getSecond()));
                assertEquals(size, bag.size());
                assertEquals(bagSize, bag.bagSize());
            }
            final int BAG_SIZE = bagSize;
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                assertTrue(bag.contains(entry.getKey()));
                assertEquals((int) entry.getValue(), bag.count(entry.getKey()));
            }
            for (Pair<Integer, Integer> pair : pairList) {
                if (contains.remove(pair.getFirst())) {
                    size--;
                    bagSize -= map.get(pair.getFirst());
                    assertTrue(bag.remove(pair.getFirst()));
                } else {
                    assertFalse(bag.remove(pair.getFirst()));
                }
                
                assertFalse(bag.contains(pair.getFirst()));
                assertEquals(0, bag.count(pair.getFirst()));
                assertEquals(bagSize, bag.bagSize());
                assertEquals(size, bag.size());
            }
            assertTrue(bag.isEmpty());
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            for (int val : map.keySet()) {
                assertFalse(bag.contains(val));
                assertEquals(0, bag.count(val));
            }
            contains.clear();
            
            
            bag.addAll(map);
            bagSize = BAG_SIZE;
            size = map.size();
            Map<Integer, Integer> mapClone = new HashMap<>(map);
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                assertTrue(bag.contains(entry.getKey()));
                assertEquals((int) entry.getValue(), bag.count(entry.getKey()));
            }
            for (Pair<Integer, Integer> pair : pairList) {
                assertTrue(bag.remove(pair.getFirst(), pair.getSecond()));
                
                if (mapClone.merge(pair.getFirst(), -pair.getSecond(), Integer::sum) == 0) {
                    size--;
                    assertFalse(bag.contains(pair.getFirst()));
                }
                assertEquals((int) mapClone.get(pair.getFirst()), bag.count(pair.getFirst()));
                bagSize -= pair.getSecond();
                assertEquals(bagSize, bag.bagSize());
                assertEquals(size, bag.size());
            }
            assertTrue(bag.isEmpty());
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            for (int val : map.keySet()) {
                assertFalse(bag.contains(val));
                assertEquals(0, bag.count(val));
            }

            
            bag.addAll(map);
            bag.clear();
            assertTrue(bag.isEmpty());
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            for (int val : map.keySet()) {
                assertFalse(bag.contains(val));
                assertEquals(0, bag.count(val));
            }


            bag.addAll(map);
            bag.removeAll(map);
            assertTrue(bag.isEmpty());
            assertEquals(0, bag.size());
            assertEquals(0, bag.bagSize());
            for (int val : map.keySet()) {
                assertFalse(bag.contains(val));
                assertEquals(0, bag.count(val));
            }
            
            // TODO: retainAll
        }
    }


}
