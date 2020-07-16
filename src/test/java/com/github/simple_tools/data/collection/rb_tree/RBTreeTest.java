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

package com.github.simple_tools.data.collection.rb_tree;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.github.simple_tools.AbstractTest;
import com.github.simple_tools.data.array.ArrayTools;

import lombok.AllArgsConstructor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the {@link RBTree} class.
 * 
 * @version 1.0
 * @author Kaj Wortel
 */
public class RBTreeTest
        extends AbstractTest {
    
    /* -------------------------------------------------------------------------
     * Inner classes.
     * -------------------------------------------------------------------------
     */
    /**
     * key class used to simulate key collisions.
     */
    @AllArgsConstructor
    private static class Key
            extends LinkedRBKey<Key> {
        public static final Comparator<Key> COMPARATOR = Comparator.comparingInt(k -> k.i);
        private final int i;
        private final int j;
        
        @Override
        public String toString() {
            return "(" + i + ", " + j + ")";
        }
        
        @Override
        public int hashCode() {
            return i;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) return false;
            Key k = (Key) obj;
            return k.i == i;
        }
    }
    
    /**
     * key class used to simulate key collisions.
     */
    @AllArgsConstructor
    private static class KeyKey
            implements Comparable<Key> {
        final private Key key;
        
        @Override
        public String toString() {
            return key.toString();
        }

        @Override
        public int compareTo(Key k) {
            return Key.COMPARATOR.compare(key, k);
        }
        
        @Override
        public int hashCode() {
            return key.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key k = (Key) obj;
                return Objects.equals(k, key);
                
            } else if (obj instanceof KeyKey) {
                KeyKey k = (KeyKey) obj;
                return Objects.equals(k.key, key);
            }
            return false;
        }
    }
    
    @AllArgsConstructor
    private static class IntKey
            implements Comparable<Integer> {
        final private int key;

        @Override
        public String toString() {
            return Integer.toString(key);
        }

        @Override
        public int compareTo(Integer i) {
            return Integer.compare(key, i);
        }
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Test
    public void get0() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        tree.add(10);
        assertEquals("Error while getting single element!", 10, (int) tree.get(0));
    }
    
    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void get1() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        expEx(IndexOutOfBoundsException.class, () -> tree.get(-1));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(0));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(1));
        tree.add(10);
        expEx(IndexOutOfBoundsException.class, () -> tree.get(-1));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(1));
    }
    
    @Test
    public void get2() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        int amt = 1_000_000;
        for (int i = 0; i < amt; i++) {
            tree.add(i);
        }
        for (int i = 0; i < amt; i++) {
            int rtn = tree.get(i);
            assertEquals("Wrong returned value!", i, rtn);
        }
        expEx(IndexOutOfBoundsException.class, () -> tree.get(-1));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(tree.size()));
    }
    
    @Test
    public void get3() {
        int amt = 1_000_000;
        RBTree<Integer> tree = new RBTree<>(Integer::compare, ArrayTools.asList(genIntArr(amt)));
        for (int i = 0; i < amt; i++) {
            int rtn = tree.get(i);
            assertEquals("Wrong returned value!", i, rtn);
        }
    }
    
    @Test
    public void add0() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        assertTrue("The element could not be added!", tree.add(1));
        assertTrue("The element is not contained in the tree!", tree.contains(1));
        assertEquals("The size of the tree is incorrect", 1, tree.size());
    }
    
    @Test
    public void add1() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        assertTrue("The elements could not be added!", tree.addAll(list));
        for (int i : list) {
            assertTrue("The element " + i + " is not contained in the tree!", tree.contains(i));
        }
        assertTrue("Not all elements were contained in the tree!", tree.containsAll(list));
        assertEquals("The size of the tree is incorrect", 16, tree.size());
    }
    
    /**
     * Tests the remove function.
     */
    @Test
    public void remove0() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        tree.add(0);
        tree.remove(0);
        assertEquals("Incorrect tree size!", 0, tree.size());
        assertFalse("Element was not removed!", tree.contains(0));
    }
    
    /**
     * Tests the remove function.
     */
    @Test
    @SuppressWarnings("UseBulkOperation")
    public void remove1() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        List<Integer> add = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<Integer> rem = new ArrayList<>(List.of(4, 7, 9, 5));
        for (int i : add) {
            tree.add(i);
        }
        int err = 0;
        Set<Integer> remSet = new HashSet<>();
        for (int i : rem) {
            remSet.add(i);
            boolean removed = tree.remove(i);
            if (!removed && add.contains(i)) {
                System.err.println("Could not remove element " + i + "!");
                err++;
            }
            if (removed && !add.contains(i)) {
                System.err.println("Removed the non-existing element " + i + "!");
                err++;
            }
            for (int a : add) {
                if (!tree.contains(a) && !remSet.contains(a)) {
                    System.err.println("Removed element " + a + " after removing " + i + "!");
                    err++;
                }
            }
        }
        for (int i : rem) {
            if (tree.remove(i)) {
                System.err.println("Removed a already remove element " + i + "!");
                err++;
            }
        }
        for (int i : rem) {
            if (tree.contains(i)) {
                System.out.println("The element " + i + " was not removed!");
                err++;
            }
        }
        
        assertEquals("There were " + err + " errors!", 0, err);
        rem.retainAll(add);
        assertEquals("Incorrect tree size!", add.size() - rem.size(), tree.size());
    }
    
    /**
     * Tests the iterator function.
     */
    @Test
    public void iterate0() {
        runAndWait(() -> {
            int err = 0;
            RBTree<Integer> tree = new RBTree<>(Integer::compare);
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
            Collections.shuffle(list);
            tree.addAll(list);
            Collections.sort(list);
            int i = 0;
            for (int val : tree) {
                if (i >= list.size()) {
                    fail("The tree iterated over more elements than the initial list!");
                }
                if (val != list.get(i)) {
                    System.err.println("Expected the value " + list.get(i) + ", but found " + val);
                    err++;
                }
                i++;
            }
            assertEquals("There were " + err + " errors!", 0, err);
        }, 64, 1000);
        
    }
    
    /**
     * Tests the retain function.
     */
    @Test
    public void retainAll0() {
        retainAll(4);
        retainAll(5);
        retainAll(10);
        retainAll(100);
        retainAll(1000);
        retainAll(10_000);
    }
    
    /**
     * Deterministically retains some elements from the tree.
     */
    public void retainAll(int amt) {
        RBTree<Integer> tree = new RBTree<>(Integer::compare);
        Set<Integer> in = new HashSet<>();
        Set<Integer> out = new HashSet<>();
        for (int i = 0; i < amt; i++) {
            tree.add(i);
            if (i % 2 == 0) in.add(i);
            else out.add(i);
        }
        
        int err = 0;
        if (!tree.retainAll(in)) {
            System.err.println("The tree should have been modified by the retain function, but it wasn't!");
            err++;
        }
        for (int i : in) {
            if (!tree.contains(i)) {
                System.err.println("The value " + i + " should be in the tree, but it isn't!");
                err++;
            }
        }
        for (int i : out) {
            if (tree.contains(i)) {
                System.err.println("Tree contained " + i + ", but this value should have been retained.");
                err++;
            }
        }
        assertEquals("There were " + err + " errors! [amt=" + amt + "]", 0, err);
        assertEquals("The size of the tree is incorrect!", (amt + 1) / 2, tree.size());
    }
    
    /**
     * This test should be used to replay scenarios from the random generator.
     */
//    @Test
    public void replay() {
        LinkedRBTree<Key> tree = new LinkedRBTree<>(Key.COMPARATOR);
        Key[] add = new Key[] {
            new Key(0, 1), new Key(1, 0), new Key(0, 0)
        };
        Key[] rem = new Key[] {
            new Key(0, 1)
        };
        for (Key k : add) {
            System.out.println("added" + k + ": " + tree.add(k));
        }
        System.out.println("added!");
//        System.out.println("==========");
//        System.out.println(tree.debug());
//        System.out.println("==========");
//        MultiTool.sleepThread(10);
        for (Key k : rem) {
//            System.out.println("==========");
//            System.out.println(tree.debug());
//            System.out.println("==========");
//            MultiTool.sleepThread(10);
            System.out.println("removed" + k + ": " + tree.remove(k));
        }
        System.out.println("removed!");
        System.out.println(tree);
        System.out.println("root: " + tree.getRoot());
        System.out.println("min : " + tree.getMin());
        System.out.println("max : " + tree.getMax());
        
        for (Key k : rem) {
            if (tree.contains(k)) {
                System.err.println("ERROR: Unexpected: " + k);
            }
        }
        for (Key k : add) {
            if (!tree.contains(k) && !ArrayTools.toList(rem).contains(k)) {
                System.err.println("ERROR: Expected: " + k);
            }
        }
    }
    
    /**
     * Runs a set of random test cases and reports the attempt if something went wrong.
     */
    @Test
    public void genRandom() {
        runAndWait(() -> genRandom(10_000, 500, 1, RBTree.class), 32, 10_000);
        runAndWait(() -> genRandom(10_000, 500, 100, RBTree.class), 32, 10_000);
        runAndWait(() -> genRandom(250_000, 4000, 50, RBTree.class), 4, 40_000);
        runAndWait(() -> genRandom(10_000, 500, 1, LinkedRBTree.class), 32, 10_000);
        runAndWait(() -> genRandom(10_000, 500, 100, LinkedRBTree.class), 32, 10_000);
        runAndWait(() -> genRandom(250_000, 4000, 50, LinkedRBTree.class), 4, 40_000);
    }
    
    /**
     * Generates a random scenario with the given size.
     * 
     * @param addAmt The number of elements to add.
     * @param remAmt The number of elements to remove.
     * @param colAmt The number of collisions per element.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void genRandom(int addAmt, int remAmt, int colAmt, Class<? extends RBTree> treeClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Key[] add = new Key[addAmt];
        for (int i = 0; i < addAmt / colAmt + 1; i++) {
            for (int j = 0; j < colAmt; j++) {
                int addIndex = i * colAmt + j;
                if (addIndex >= addAmt) break;
                add[addIndex] = new Key(i, j);
            }
        }
        //System.out.println("added!");
        Key[] rem = new Key[remAmt];
        for (int i = 0; i < remAmt; i++) {
            rem[i] = add[ThreadLocalRandom.current().nextInt(addAmt)];
        }
        Set<Key> addSet = new HashSet<>(ArrayTools.toList(ArrayTools.shuffle(add)));
        Set<Key> remSet = new HashSet<>(ArrayTools.toList(ArrayTools.shuffle(rem)));
        //System.out.println("removed!");
        
        //LinkedRBTree<Key> tree = new LinkedRBTree<>(Arrays.asList(add));
//        RBTree<Key> tree = new RBTree<>();
        RBTree<Key> tree = treeClass.getConstructor(Comparator.class).newInstance(Key.COMPARATOR);
        
        int err = 0;
        for (Key k : add) {
            if (!tree.contains(k) && !tree.add(k)) {
                System.err.println("ERROR: Key was not added: " + k);
                err++;
            }
            if (!tree.contains(k)) {
                System.err.println("ERROR: Key was added, but not contained: " + k);
                err++;
            }
        }
        for (Key k : rem) {
            if (tree.contains(k) != tree.remove(k)) {
                System.err.println("ERROR: Key was not removed: " + k);
                err++;
            }
            if (tree.contains(k)) {
                System.err.println("ERROR: Key was removed, but is contained: " + k);
                err++;
            }
        }
        for (Key k : rem) {
            if (tree.contains(k)) {
                System.err.println("ERROR: Unexpected: " + k);
                err++;
            }
        }
        //System.out.println("checked removed!");
        for (Key k : add) {
            if (!tree.contains(k) && !remSet.contains(k)) {
                System.err.println("ERROR: Expected: " + k);
                err++;
            }
        }
        
        if (tree.size() != addSet.size() - remSet.size()) {
            System.err.println("ERROR: The expected size of the tree is " + (addSet.size() - remSet.size()) +
                    ", but found " + tree.size() + " elements!");
            err++;
        }
        
        //System.out.println("checked added!");
        if (err != 0) {
            System.out.println("add: " + Arrays.toString(add).replaceAll("\\(", "new Key\\("));
            System.out.println("rem: " + Arrays.toString(rem).replaceAll("\\(", "new Key\\("));
            fail("There were " + err + " errors!");
        }
        //System.out.println("DONE");
    }
    
    @Test
    public void rootMinMax0() {
        RBTree<Integer> tree = new RBTree<>(Integer::compare, 1);
        assertEquals((Object) tree.getRoot(), 1);
        assertEquals((Object) tree.getMin(), 1);
        assertEquals((Object) tree.getMax(), 1);
        assertNull(tree.prev(1));
        assertNull(tree.next(1));
        
        tree.remove(1);
        assertNull(tree.getRoot());
        assertNull(tree.getMin());
        assertNull(tree.getMax());
    }

    @Test
    public void rootMinMax1() {
        Key key = new Key(0, 0);
        LinkedRBTree<Key> tree = new LinkedRBTree<>(Key.COMPARATOR, key);
        
        assertEquals(tree.getRoot(), key);
        assertTrue(tree.getRoot().isRoot());
        assertFalse(tree.getRoot().hasParent());
        
        assertEquals(tree.getMin(), key);
        assertFalse(tree.getMin().hasLeft());
        assertNull(tree.getMin().left());
        assertFalse(tree.getMin().hasPrev());
        assertNull(tree.getMin().prev());
        assertNull(tree.prev(tree.getMin()));
        
        assertEquals(tree.getMax(), key);
        assertFalse(tree.getMax().hasRight());
        assertNull(tree.getMax().right());
        assertFalse(tree.getMax().hasNext());
        assertNull(tree.getMin().next());
        assertNull(tree.next(tree.getMax()));
        
        tree.remove(key);
        assertNull(tree.getRoot());
        assertNull(tree.getMin());
        assertNull(tree.getMax());
    }

    @Test
    public void rootMinMax2() {
        final int amt = 100_000;
        final Integer[] arr = new Integer[amt];
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < amt; i++) {
            arr[i] = 3 * i + 5;
            min = Math.min(min, arr[i]);
            max = Math.max(max, arr[i]);
        }
        final RBTree<Integer> tree = new RBTree<>(Integer::compare, ArrayTools.shuffle(arr));
        
        assertNotNull(tree.getRoot());
        
        assertEquals((Object) tree.getMin(), min);
        assertNull(tree.prev(tree.getMin()));
        assertNotNull(tree.next(tree.getMin()));
        
        assertEquals((Object) tree.getMax(), max);
        assertNull(tree.next(tree.getMax()));
        assertNotNull(tree.prev(tree.getMax()));
    }

    @Test
    public void rootMinMax3() {
        final int amt = 100_000;
        final Key[] arr = new Key[amt];
        Key min = new Key(Integer.MAX_VALUE, 0);
        Key max = new Key(Integer.MIN_VALUE, 0);
        for (int i = 0; i < amt; i++) {
            arr[i] = new Key(4 * i + 6, 0);
            min = (arr[i].i < min.i ? arr[i] : min);
            max = (arr[i].i > max.i ? arr[i] : max);
        }
        final LinkedRBTree<Key> tree = new LinkedRBTree<>(Key.COMPARATOR, Arrays.asList(ArrayTools.shuffle(arr)));
        
        final Key root = tree.getRoot();
        assertNotNull(root);
        assertTrue(root.isRoot());
        
        for (Key key : arr) {
            assertEquals(key.isRoot(), key.equals(root));
            if (key.equals(root)) {
                assertFalse(key.hasParent());
                assertNull(key.parent());
                assertNull(key.getNode().getGrandParent());
                
            } else {
                assertTrue(key.hasParent());
                assertNotNull(key.getNode().getParent());
                if (key.parent().equals(root)) {
                    assertNull(key.getNode().getGrandParent());
                } else {
                    assertNotNull(key.getNode().getGrandParent());
                }
            }
            
            if (key.equals(min)) {
                assertEquals(key, tree.getMin());
                assertFalse(key.hasPrev());
                assertNull(key.prev());
                assertNull(tree.prev(key));
                assertEquals(key.hasRight(), !key.isLeaf());
                
            } else {
                assertNotEquals(key, tree.getMin());
                assertTrue(key.hasPrev());
                assertNotNull(key.prev());
                assertNotNull(tree.prev(key));
            }
            
            if (key.equals(max)) {
                assertEquals(key, tree.getMax());
                assertFalse(key.hasNext());
                assertNull(key.next());
                assertNull(tree.next(key));
                assertEquals(key.hasLeft(), !key.isLeaf());
                
            } else {
                assertNotEquals(key, tree.getMax());
                assertTrue(key.hasNext());
                assertNotNull(key.next());
                assertNotNull(tree.next(key));
            }
            
            if (!key.hasLeft() && !key.hasRight()) {
                assertTrue(key.isLeaf());
            } else {
                assertFalse(key.isLeaf());
            }
        }
    }
    
    @Test
    public void search0() {
        final int amt = 100_000;
        final Integer[] arr = new Integer[amt];
        for (int i = 0; i < amt; i++) {
            arr[i] = 4 * i + 6;
        }
        
        RBTree<Integer> tree = new RBTree<>(Integer::compare, ArrayTools.shuffle(arr));
        
        runAndWait(() -> {
            final int target = arr[ThreadLocalRandom.current().nextInt(arr.length)];
            final Integer resultSearch = tree.search((comparator, cur, left, right) -> {
                if (cur == null) return null;
                int cmp = comparator.compare(target, cur);
                if (cmp < 0) {
                    return RBSearch.Choice.GO_LEFT;
                } else if (cmp > 0) {
                    return RBSearch.Choice.GO_RIGHT;
                } else {
                    return RBSearch.Choice.CURRENT;
                }
            });
            assertEquals((Integer) target, resultSearch);
            
            final Integer resultBinary0 = tree.binarySearch(target);
            assertEquals((Integer) target, resultBinary0);
            
            final Integer resultBinary1 = tree.binarySearch(new IntKey(target));
            assertEquals((Integer) target, resultBinary1);
            
        }, 64, 5000);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void search1() {
        final int amt = 100_000;
        final Key[] arr = new Key[amt];
        for (int i = 0; i < amt; i++) {
            arr[i] = new Key(4 * i + 6, 0);
        }
        
        LinkedRBTree<Key> tree = new LinkedRBTree<>(Key.COMPARATOR, ArrayTools.shuffle(arr));

        runAndWait(() -> {
            final Key target = arr[ThreadLocalRandom.current().nextInt(arr.length)];
            final Key resultSearch = tree.search((comparator, cur, left, right) -> {
                if (cur == null) return null;
                int cmp = comparator.compare(target, cur);
                if (cmp < 0) {
                    return RBSearch.Choice.GO_LEFT;
                } else if (cmp > 0) {
                    return RBSearch.Choice.GO_RIGHT;
                } else {
                    return RBSearch.Choice.CURRENT;
                }
            });
            assertEquals(target, resultSearch);

            final Key resultBinary0 = tree.binarySearch(target);
            assertEquals(target, resultBinary0);

            final Key resultBinary1 = tree.binarySearch(new KeyKey(target));
            assertEquals(target, resultBinary1);

        }, 64, 5000);
    }
    
    @Test
    public void listIterator0() {
        final int amt = 100_000;
        final Key[] arr = new Key[amt];
        Key min = new Key(Integer.MAX_VALUE, 0);
        Key max = new Key(Integer.MIN_VALUE, 0);
        for (int i = 0; i < amt; i++) {
            arr[i] = new Key(4 * i + 6, 0);
            min = (arr[i].i < min.i ? arr[i] : min);
            max = (arr[i].i > max.i ? arr[i] : max);
        }
        Key[] order = new Key[amt];
        System.arraycopy(arr, 0, order, 0, amt);
        final LinkedRBTree<Key> tree = new LinkedRBTree<>(Key.COMPARATOR, Arrays.asList(ArrayTools.shuffle(arr)));

        {
            ListIterator<Key> it = tree.listIterator(true);
            int i;
            for (i = 0; i < order.length && it.hasNext(); i++) {
                assertEquals(order[i], it.next());
            }
            if (i != order.length) {
                fail("Not all items were processed!");
            }
            if (it.hasNext()) {
                fail("Too many items remaining!");
            }
        }

        {
            ListIterator<Key> it = tree.listIterator(false);
            int i;
            for (i = order.length - 1; i >= 0 && it.hasPrevious(); i--) {
                assertEquals(order[i], it.previous());
            }
            if (i != -1) {
                fail("Not all items were processed!");
            }
            if (it.hasPrevious()) {
                fail("Too many items remaining!");
            }
        }
    }
    
    @Test
    public void listIteratorToArray() {
        final int amt = 100_000;
        final Key[] arr = new Key[amt];
        for (int i = 0; i < amt; i++) {
            arr[i] = new Key(4 * i + 6, 0);
        }
        Key[] orderExp = new Key[amt];
        System.arraycopy(arr, 0, orderExp, 0, amt);
        LinkedRBTree<Key> tree = new LinkedRBTree<>(Key.COMPARATOR, ArrayTools.shuffle(arr));
        
        Object[] order0 = tree.toArray();
        Key[] order1 = tree.toArray(new Key[0]);
        
        assertEquals(order0.length, orderExp.length);
        assertEquals(order1.length, orderExp.length);
        
        for (int i = 0; i < amt; i++) {
            assertEquals(orderExp[i], order0[i]);
            assertEquals(orderExp[i], order1[i]);
        }
    }
    
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void toStringTest() {
        final int amt = 100_000;
        final Key[] arr = new Key[amt];
        for (int i = 0; i < amt; i++) {
            arr[i] = new Key(4 * i + 6, 0);
        }
        LinkedRBTree<Key> tree1 = new LinkedRBTree<>(Key.COMPARATOR, ArrayTools.shuffle(arr));
        tree1.toString();
        tree1.debug();
        RBTree<Key> tree2 = new RBTree<>(Key.COMPARATOR, arr);
        tree2.toString();
        tree2.debug();
    }
    
    @Test
    public void queueTest() {
        Integer[] add = new Integer[] {1, 2, 5, 6, 7, 8, 12, 12};
        RBTree<Integer> tree = new RBTree<>(Integer::compareTo);

        expEx(NoSuchElementException.class, tree::element);
        expEx(NoSuchElementException.class, tree::remove);
        assertNull(tree.peek());
        assertNull(tree.poll());
        
        Integer min = null;
        Set<Integer> added = new HashSet<>();
        for (int i : add) {
            assertEquals(min, tree.peek());
            assertEquals(added.add(i), tree.offer(i));
            min = Math.min((min == null ? Integer.MAX_VALUE : min), i);
            assertEquals(min, tree.peek());
        }
        
        RBTree<Integer> tree2 = new RBTree<>(Integer::compareTo, tree);
        
        while (!tree.isEmpty()) {
            Integer val = tree.element();
            assertNotNull(val);
            assertEquals(val, tree.getMin());
            assertEquals(val, tree.remove());
        }
        
        expEx(NoSuchElementException.class, tree::element);
        expEx(NoSuchElementException.class, tree::remove);
        assertNull(tree.peek());
        assertNull(tree.poll());
        
        while (!tree2.isEmpty()) {
            Integer val = tree2.peek();
            assertNotNull(val);
            assertEquals(val, tree2.getMin());
            assertEquals(val, tree2.poll());
        }

        expEx(NoSuchElementException.class, tree2::element);
        expEx(NoSuchElementException.class, tree2::remove);
        assertNull(tree2.peek());
        assertNull(tree2.poll());
    }
    
    // TODO:
    // removeAll(Collection)
    // prev(D)
    // addAll(Collection) # non-empty
    // retainAll(Collection) # set, remove case
    // listIterator.nextIndex()
    // listIterator.previousIndex()
    // listIterator.remove()
    // listIterator.set()
    // listIterator.add()
    
    
}
