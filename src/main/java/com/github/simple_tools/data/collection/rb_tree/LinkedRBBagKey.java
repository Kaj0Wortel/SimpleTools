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

import com.github.simple_tools.data.collection.LinkedKey;
import com.github.simple_tools.data.collection.TreeKey;

import java.util.function.BiConsumer;

/**
 * The key used for the {@link LinkedRBTreeBag} class.
 * 
 * @param <D> The data type of the key.
 *
 * @author Kaj Wortel
 * 
 * @see LinkedRBTreeBag
 */
public abstract class LinkedRBBagKey<D extends LinkedRBBagKey<D>>
        implements TreeKey<D>, LinkedKey<D> {

    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The node representing this key. */
    private LinkedRBBagNode<D> node;


    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Sets the node represented by this key.
     *
     * @param node The node to link.
     */
    void setNode(LinkedRBBagNode<D> node) {
        if (this.node != node) {
            if (node == null || this.node == null) this.node = node;
            else {
                throw new IllegalStateException("Key cannot be added to multiple trees!");
            }
        }
    }

    /**
     * @return The node represented by this key.
     */
    LinkedRBBagNode<D> getNode() {
        return node;
    }

    @Override
    public D next() {
        return (node == null ? null : node.getNextData());
    }

    @Override
    public D prev() {
        return (node == null ? null : node.getPrevData());
    }

    @Override
    public D left() {
        if (node == null) return null;
        RBNode<D> left = node.getLeft();
        return (left == null ? null : left.getData());
    }

    @Override
    public D right() {
        if (node == null) return null;
        RBNode<D> right = node.getRight();
        return (right == null ? null : right.getData());
    }

    @Override
    public D parent() {
        if (node == null) return null;
        RBNode<D> parent = node.getParent();
        return (parent == null ? null : parent.getData());
    }

    @Override
    public boolean isRoot() {
        return node != null && node.isRoot();
    }

    @Override
    public boolean isLeaf() {
        return node != null && !node.hasChild();
    }

    /**
     * @return The amount of nodes in the subtree starting at this node,
     *     including this node.
     */
    public int getSize() {
        return (node == null ? 0 : node.getSize());
    }

    /**
     * @return The count of this key.
     */
    public int getCount() {
        return (node == null ? 0 : node.getCount());
    }

    /**
     * @return The amount of nodes in the subtree starting at this node,
     *     including this node.
     */
    public long getBagSize() {
        return (node == null ? 0 : node.getBagSize());
    }

    /**
     * Swaps the data of the two keys.
     * This function should be overridden when you want to use the
     * {@link LinkedRBTreeBag#swap(LinkedRBBagKey, LinkedRBBagKey)} function.
     * Note that the data of the keys is not allowed to be modified
     * in any other way when it is inserted in the tree.
     * 
     * @param other The key to swap the data with.
     */
    protected void swap(D other) {
        throw new UnsupportedOperationException();
    }
    
    
}
