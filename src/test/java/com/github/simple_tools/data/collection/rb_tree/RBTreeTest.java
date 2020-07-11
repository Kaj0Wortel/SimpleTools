package com.github.simple_tools.data.collection.rb_tree;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.github.simple_tools.AbstractTest;
import com.github.simple_tools.data.array.ArrayTools;
import lombok.AllArgsConstructor;
import org.junit.Ignore;
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
        final private int i;
        final private int j;
        
        @Override
        public int compareTo(Key key) {
            return Integer.compare(i, key.i);
        }
        
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
            Key key = (Key) obj;
            return key.i == i;
        }
        
        
    }
    
    @AllArgsConstructor
    private static class IntKey
            implements Comparable<Integer> {
        final private int key;

        @Override
        public int compareTo(Integer i) {
            return Integer.compare(key, i);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Integer)) return false;
            return (int) obj == key;
        }
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Test
    public void get0() {
        RBTree<Integer> tree = new RBTree<>();
        tree.add(10);
        assertEquals("Error while getting single element!", 10, (int) tree.get(0));
    }
    
    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public void get1() {
        RBTree<Integer> tree = new RBTree<>();
        expEx(IndexOutOfBoundsException.class, () -> tree.get(-1));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(0));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(1));
        tree.add(10);
        expEx(IndexOutOfBoundsException.class, () -> tree.get(-1));
        expEx(IndexOutOfBoundsException.class, () -> tree.get(1));
    }
    
    @Test
    public void get2() {
        RBTree<Integer> tree = new RBTree<>();
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
        RBTree<Integer> tree = new RBTree<>(ArrayTools.toList(genIntArr(amt)));
        for (int i = 0; i < amt; i++) {
            int rtn = tree.get(i);
            assertEquals("Wrong returned value!", i, rtn);
        }
    }
    
    @Test
    public void add0() {
        RBTree<Integer> tree = new RBTree<>();
        assertTrue("The element could not be added!", tree.add(1));
        assertTrue("The element is not contained in the tree!", tree.contains(1));
        assertEquals("The size of the tree is incorrect", 1, tree.size());
    }
    
    @Test
    public void add1() {
        RBTree<Integer> tree = new RBTree<>();
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
        RBTree<Integer> tree = new RBTree<>();
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
        RBTree<Integer> tree = new RBTree<>();
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
            RBTree<Integer> tree = new RBTree<>();
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
        RBTree<Integer> tree = new RBTree<>();
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
    @Ignore
    public void replay() {
        LinkedRBTree<Key> tree = new LinkedRBTree<>();
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
        runAndWait(() -> genRandom(10_000, 500, 1, RBTree.class), 32, 2000);
        runAndWait(() -> genRandom(10_000, 500, 100, RBTree.class), 32, 2000);
        runAndWait(() -> genRandom(250_000, 4000, 50, RBTree.class), 4, 5_000);
        runAndWait(() -> genRandom(10_000, 500, 1, LinkedRBTree.class), 32, 2000);
        runAndWait(() -> genRandom(10_000, 500, 100, LinkedRBTree.class), 32, 2000);
        runAndWait(() -> genRandom(250_000, 4000, 50, LinkedRBTree.class), 4, 5_000);
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
        ArrayTools.shuffle(add);
        ArrayTools.shuffle(rem);
        Set<Key> addSet = new HashSet<>(ArrayTools.toList(add));
        Set<Key> remSet = new HashSet<>(ArrayTools.toList(rem));
        //System.out.println("removed!");
        
        //LinkedRBTree<Key> tree = new LinkedRBTree<>(Arrays.asList(add));
//        RBTree<Key> tree = new RBTree<>();
        RBTree<Key> tree = treeClass.getConstructor().newInstance();
        
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
        RBTree<Integer> tree = new RBTree<>(1);
        assertEquals((Object) tree.getRoot(), 1);
        assertEquals((Object) tree.getMin(), 1);
        assertEquals((Object) tree.getMax(), 1);
        
        tree.remove(1);
        assertNull(tree.getRoot());
        assertNull(tree.getMin());
        assertNull(tree.getMax());
    }

    @Test
    public void rootMinMax1() {
        Key key = new Key(0, 0);
        LinkedRBTree<Key> tree = new LinkedRBTree<>(key);
        assertEquals(tree.getRoot(), key);
        assertTrue(tree.getRoot().isRoot());
        assertFalse(tree.getRoot().hasParent());
        
        assertEquals(tree.getMin(), key);
        assertFalse(tree.getMin().hasLeft());
        assertNull(tree.getMin().left());
        assertFalse(tree.getMin().hasPrev());
        assertNull(tree.getMin().prev());
        
        assertEquals(tree.getMax(), key);
        assertFalse(tree.getMax().hasRight());
        assertNull(tree.getMax().right());
        assertFalse(tree.getMax().hasNext());
        assertNull(tree.getMin().next());
        
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
        ArrayTools.shuffle(arr);
        final RBTree<Integer> tree = new RBTree<>(arr);
        assertNotNull(tree.getRoot());
        assertEquals((Object) tree.getMin(), min);
        assertEquals((Object) tree.getMax(), max);
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
        ArrayTools.shuffle(arr);
        final LinkedRBTree<Key> tree = new LinkedRBTree<>(arr);
        final Key root = tree.getRoot();
        assertNotNull(root);
        assertTrue(root.isRoot());
        
        for (Key key : arr) {
            assertEquals(key.isRoot(), key.equals(root));
            
            if (key.equals(min)) {
                assertEquals(key, tree.getMin());
                assertFalse(key.hasPrev());
                assertNull(key.prev());
                
            } else {
                assertNotEquals(key, tree.getMin());
                assertTrue(key.hasPrev());
                assertNotNull(key.prev());
            }
            
            if (key.equals(max)) {
                assertEquals(key, tree.getMax());
                assertFalse(key.hasNext());
                assertNull(key.next());
                
            } else {
                assertNotEquals(key, tree.getMax());
                assertTrue(key.hasNext());
                assertNotNull(key.next());
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
        
        ArrayTools.shuffle(arr);
        RBTree<Integer> tree = new RBTree<>(arr);
        
        runAndWait(() -> {
            int target = arr[ThreadLocalRandom.current().nextInt(arr.length)];
            int resultSearch = tree.search((cur, left, right) -> {
                if (cur == target) {
                    return RBSearch.Choice.CURRENT;
                } else if (target < cur) {
                    return RBSearch.Choice.GO_LEFT;
                } else {
                    return RBSearch.Choice.GO_RIGHT;
                }
            });
            assertEquals(target, resultSearch);
            
            int resultBinary0 = tree.binarySearch(target);
            assertEquals(target, resultBinary0);
            
            int resultBinary1 = tree.binarySearch(new IntKey(target));
            assertEquals(target, resultBinary1);
            
        }, 64, 5000);
        
        
    }
    
    
}
