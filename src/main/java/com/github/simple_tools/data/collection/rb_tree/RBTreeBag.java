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

import com.github.simple_tools.data.collection.Bag;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * TODO
 * 
 * @param <D> 
 */
public class RBTreeBag<D>
        extends RBTree<D>
        implements Bag<D> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The bagSize of the bag. */
    protected long bagSize = 0;
    

    /* ------------------------------------------------------------------------
     * Constructors.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new empty red-black tree.
     *
     * @param comparator The comparator used to compare the elements.
     */
    public RBTreeBag(Comparator<D> comparator) {
        super(comparator);
    }

    /**
     * Creates a new red-black tree from the given collection. <br>
     * Initializes the tree as balanced as possible. This initialization
     * is preferred over creating a new tree and then adding all elements
     * with {@link #add(Object)}. <br>
     * Note that this is equivalent to creating a new tree and then add all
     * elements using {@link #addAll(Collection)} in one go.
     *
     * @param comparator The comparator used to compare the elements.
     * @param col        The collection to add.
     * @apiNote This operation takes {@code O(n log(n))} time for unsorted data
     * and {@code O(n)} time for sorted data.
     */
    public RBTreeBag(Comparator<D> comparator, Collection<D> col) {
        super(comparator, col);
    }

    /**
     * Creates a new red-black tree from the given array. <br>
     * Initializes the tree as balanced as possible. This initialization
     * is preferred over creating a new tree and then adding all elements
     * with {@link #add(Object)}. <br>
     * Note that this is equivalent to creating a new tree and then add all
     * elements using {@link #addAll(Collection)} in one go.
     * 
     * @param comparator The comparator used to compare the elements.
     * @param elems      The array to add.
     * @apiNote If the collection is almost sorted, then this creation takes
     * only {@code O(n)} time. Otherwise {@code O(n log(n))}.
     */
    @SafeVarargs
    public RBTreeBag(Comparator<D> comparator, D... elems) {
        super(comparator, elems);
    }

    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    @Override
    public boolean add(D data) {
        return add(data, 1);
    }

    /**
     * Adds the given data {@code amt} times.
     * 
     * @param data The data to add.
     * @param amt  The amount of data to add.
     * 
     * @return {@code true} if the data was added. {@code false} otherwise.
     * 
     * @throws IllegalArgumentException If {@code amt <= 0}.
     */
    @Override
    public boolean add(D data, int amt)
            throws IllegalArgumentException {
        if (data == null) throw new NullPointerException("data = null");
        if (amt <= 0) throw new IllegalArgumentException("amt <= 0");
        RBBagNode<D> node = (RBBagNode<D>) bstInsert(data, amt);
        if (node == null) return true;
        updateBagSizeParents(node, amt);
        balanceTreeInsert(node);
        size++;
        bagSize += amt;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Bag<? extends D> bag) {
        if (bag.isEmpty()) return false;
        if (isEmpty()) {
            RBNode<D>[] nodes = new RBNode[bag.size()];
            {
                int i = 0;
                for (D d : bag) {
                    nodes[i++] = createNode(d, bag.count(d));
                }
            }
            Arrays.sort(nodes, (n1, n2) -> comparator.compare(n1.getData(), n2.getData()));
            initTree(nodes);
            return true;
                
        } else {
            return super.addAll(bag);
        }
    }
    
    @Override
    protected RBNode<D> insertExisting(@NonNull RBNode<D> existing, @NonNull D data, Object args) {
        int amt = (args instanceof Integer ? (int) args : 1);
        RBBagNode<D> bagNode = (RBBagNode<D>) existing;
        bagNode.addCount(amt);
        updateBagSizeParents(bagNode, amt);
        bagSize += amt;
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object obj, int amt) {
        if (amt <= 0) throw new IllegalArgumentException("amt <= 0");
        return removeNode((RBBagNode<D>) get((D) obj), amt);
    }
    
    /**
     * {@inheritDoc}
     * <br>
     * Note that this function does not necessarily remove a node to remove the data.
     */
    @Override
    protected boolean removeNode(RBNode<D> node, Object args) {
        return removeNode((RBBagNode<D>) node, (args instanceof Integer ? (int) args : Integer.MAX_VALUE));
    }

    /**
     * Bag implementation of {@link super#removeNode(RBNode, Object)}.
     * <br>
     * Note that this function does not necessarily has to remove a node to remove the data.
     * If a more or equal amount of items are removed than available, then the node will be removed.
     *
     * @param node The node to delete.
     * @param amt  The amount of items to remove.
     *
     * @return The inserted node, or {@code null} if no new node was created.
     *
     * @throws IllegalArgumentException If {@code amt <= 0}.
     */
    protected boolean removeNode(RBBagNode<D> node, int amt) {
        if (node == null) return false;
        if (amt <= 0) throw new IllegalArgumentException("amt <= 0");
        if (node.getCount() > amt) {
            bagSize -= amt;
            node.addCount(-amt);
            updateBagSizeParents(node, -amt);
        } else {
            bagSize -= node.getCount();
            updateBagSizeParents(node, -node.getCount());
            node.addCount(-node.getCount());
            if (!super.removeNode(node, null)) {
                throw new IllegalStateException();
            }
        }
        return true;
    }
    
    @Override
    protected void swap(RBNode<D> n1, RBNode<D> n2) {
        RBBagNode<D> n1Bag = (RBBagNode<D>) n1;
        RBBagNode<D> n2Bag = (RBBagNode<D>) n2;
        int diff = n2Bag.getCount() - n1Bag.getCount();
        if (diff != 0) {
            updateBagSizeParents(n1Bag, diff);
            updateBagSizeParents(n2Bag, -diff);
        }
        super.swap(n1, n2);
        long bagSize1 = n2Bag.getBagSize() - n2Bag.getCount() + n1Bag.getCount();
        long bagSize2 = n1Bag.getBagSize() - n1Bag.getCount() + n2Bag.getCount();
        n1Bag.setBagSize(bagSize1);
        n2Bag.setBagSize(bagSize2);
    }
    
    @Override
    protected void updateSize(RBNode<D> p) {
        if (p != null) {
            super.updateSize(p);
            RBBagNode<D> n = (RBBagNode<D>) p;
            long bagSize = n.getCount();
            if (n.hasLeft()) bagSize += ((RBBagNode<D>) n.getLeft()).getBagSize();
            if (n.hasRight()) bagSize += ((RBBagNode<D>) n.getRight()).getBagSize();
            n.setBagSize(bagSize);
        }
    }

    /**
     * Updates the size of the subtree of the parents of the given node.
     *
     * @param node The starting node.
     */
    protected void updateBagSizeParents(RBBagNode<D> node, int diff) {
        if (node == null) return;
        while (node.hasParent()) {
            node = (RBBagNode<D>) node.getParent();
            node.setBagSize(node.getBagSize() + diff);
        }
    }
    
    @Override
    protected RBBagNode<D> createNode(@NonNull D data, Object args) {
        return createNode(data, (args == null ? 1 : (int) args));
    }

    /**
     * Creates a new node using the given data and amount.
     * 
     * @param data The data to create the node for.
     * @param amt  The amount of elements the node should have.
     * 
     * @return A new node with the given data and amount.
     */
    protected RBBagNode<D> createNode(@NonNull D data, int amt) {
        return new RBBagNode<>(data, amt);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public int count(Object data) {
        RBBagNode<D> node = (RBBagNode<D>) get((D) data);
        if (node == null) return 0;
        return node.getCount();
    }

    @Override
    public long bagSize() {
        return bagSize;
    }
    
    @Override
    public void clear() {
        super.clear();
        bagSize = 0L;
    }
    
    @Override
    protected <N extends RBNode<D>> void initTree(N[] nodes) {
        bagSize = size;
        super.initTree(nodes);
    }


}
