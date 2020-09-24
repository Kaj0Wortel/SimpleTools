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

import lombok.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 
 * @param <D> 
 * 
 * @author Kaj Wortel
 */
public class LinkedRBTreeBag<D extends LinkedRBBagKey<D>>
        extends RBTreeBag<D> {

    /* -------------------------------------------------------------------------
     * Constructors.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates a new empty linked red-black tree.
     *
     * @param comparator The comparator used to compare the elements.
     */
    public LinkedRBTreeBag(Comparator<D> comparator) {
        super(comparator);
    }

    /**
     * Creates a new linked red-black tree from the given collection.
     *
     * @param comparator The comparator used to compare the elements.
     * @param col        The collection to add.
     */
    public LinkedRBTreeBag(Comparator<D> comparator, Collection<D> col) {
        super(comparator, col);
    }

    /**
     * Creates a new linked red-black tree from the given collection.
     *
     * @param comparator The comparator used to compare the elements.
     * @param elems      The collection to add.
     */
    @SafeVarargs
    public LinkedRBTreeBag(Comparator<D> comparator, D... elems) {
        super(comparator, elems);
    }

    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    protected boolean removeNode(RBBagNode<D> node, int amt) {
        if (super.removeNode(node, amt)) {
            D data = gd(node);
            if (data != null) data.setNode(null);
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <N extends RBNode<D>> void initTree(N[] nodes) {
        for (int i = 1; i < nodes.length; i++) {
            link((LinkedRBBagNode<D>) nodes[i - 1], (LinkedRBBagNode<D>) nodes[i]);
        }
        super.initTree(nodes);
    }
    
    @Override
    protected LinkedRBBagNode<D> createNode(D data, int amt) {
        return LinkedRBBagNode.createNode(data, amt);
    }

    @Override
    public D next(D data) {
        return data.next();
    }

    @Override
    protected RBNode<D> next(RBNode<D> node) {
        if (node instanceof LinkedRBBagNode) {
            return ((LinkedRBBagNode<D>) node).getNext();
        } else {
            return super.next(node);
        }
    }

    @Override
    public RBNode<D> prev(RBNode<D> node) {
        if (node instanceof LinkedRBBagNode) {
            return ((LinkedRBBagNode<D>) node).getPrev();
        } else {
            return super.prev(node);
        }
    }

    @Override
    public D prev(D data) {
        return data.prev();
    }

    @Override
    protected LinkedRBBagNode<D> bstInsert(D data, Object args) {
        LinkedRBBagNode<D> node = (LinkedRBBagNode<D>) super.bstInsert(data, args);
        if (node == null) return null;
        node.getData().setNode(node);
        LinkedRBBagNode<D> p = (LinkedRBBagNode<D>) node.getParent();
        if (p == null) return node;
        // node != root
        if (node.isRight()) link(p, node, p.getNext());
        else link(p.getPrev(), node, p);
        return node;
    }

    @Override
    protected LinkedRBBagNode<D> bstDelete(RBNode<D> n) {
        LinkedRBBagNode<D> node = (LinkedRBBagNode<D>) super.bstDelete(n);
        if (node == null) return null;

        link(node.getPrev(), node.getNext());
        node.setNext(null);
        node.setPrev(null);
        return node;
    }

    /**
     * Links the nodes {@code left}, {@code mid} and {@code right} together.
     *
     * @param left The left node of the chain to make.
     * @param mid The middle node of the chain to make.
     * @param right The right node of the chain to make.
     */
    protected void link(LinkedRBBagNode<D> left, LinkedRBBagNode<D> mid, LinkedRBBagNode<D> right) {
        if (left != null) left.setNext(mid);
        if (right != null) right.setPrev(mid);
        if (mid != null) {
            mid.setPrev(left);
            mid.setNext(right);
        }
    }

    /**
     * Links the nodes {@code left} and {@code right} together.
     *
     * @param left The left node of the chain to make.
     * @param right The right node of the chain to make.
     */
    protected void link(LinkedRBBagNode<D> left, LinkedRBBagNode<D> right) {
        if (left != null) left.setNext(right);
        if (right != null) right.setPrev(left);
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated This function should not be used in the linked variant of the tree,
     *     as it is slower compared to the alternative. <br>
     *     Use the {@link LinkedRBBagKey#parent()}, {@link LinkedRBBagKey#left()}, {@link LinkedRBBagKey#right()},
     *     {@link LinkedRBBagKey#next()} and {@link LinkedRBBagKey#prev()} functions instead.
     */
    @Override
    @Deprecated
    public D search(RBSearch<D> search) {
        return super.search(search);
    }

    @Override
    public void clear() {
        for (LinkedRBBagKey<D> key : this) {
            key.setNode(null);
        }
        super.clear();
    }

    /**
     * Swaps the given two keys.
     * Both keys must be already inserted in this tree and override the
     * {@link LinkedRBBagKey#swap(LinkedRBBagKey)} function.
     * Note that this does <b>NOT</b> swap the count of the two keys.
     * 
     * @apiNote
     * This action is performed in {@code O(1)}.
     * 
     * @throws IllegalStateException If the data of the two keys was not correctly swapped.
     *     If this exception is thrown, then the state of the tree is unspecified.
     */
    public void swap(@NonNull D k1, @NonNull D k2)
            throws IllegalStateException {
        LinkedRBBagNode<D> n1 = k1.getNode();
        LinkedRBBagNode<D> n2 = k2.getNode();
        k1.setNode(null);
        k2.setNode(null);
        n1.setData(null);
        n2.setData(null);
        k1.swap(k2);
        n1.setData(k2);
        n2.setData(k1);
        k1.setNode(n2);
        k2.setNode(n1);
        if (!isValid(k1) || !isValid(k2)) {
            throw new IllegalStateException();
        }
    }

    /**
     * Verifies whether a given key has been placed correctly.
     *
     * @param k The key to verify.
     *
     * @return {@code true} if the key has been placed correctly.
     */
    private boolean isValid(D k) {
        return (k.prev() == null || comparator.compare(k.prev(), k) < 0) &&
                (k.next() == null || comparator.compare(k, k.next()) < 0);
    }
    

}
