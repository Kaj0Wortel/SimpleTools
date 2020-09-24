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

import javax.naming.OperationNotSupportedException;

/**
 * This key class is used for the {@link LinkedRBTree} data structure.
 * The user of this data structure should extends this class. <br>
 * <br>
 * When searching through the tree, one can use all function of this
 * class in {@code O(1)} time.
 * 
 * @author Kaj Wortel
 * 
 * @see LinkedRBNode
 * @see LinkedRBTree
 */
public abstract class LinkedRBKey<D extends LinkedRBKey<D>>
        implements TreeKey<D>, LinkedKey<D> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The node representing this key. */
    private LinkedRBNode<D> node;
    
    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Sets the node represented by this key.
     * 
     * @param node The node to link.
     */
    void setNode(LinkedRBNode<D> node) {
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
    LinkedRBNode<D> getNode() {
        return node;
    }

    @Override
    public D prev() {
        return (node == null ? null : node.getPrevData());
    }

    @Override
    public D next() {
        return (node == null ? null : node.getNextData());
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
     * Swaps the data of the two keys.
     * This function should be overridden when you want to use the
     * {@link LinkedRBTree#swap(LinkedRBKey, LinkedRBKey)} function.
     * Note that the data of the keys is not allowed to be modified
     * in any other way when it is inserted in the tree.
     *
     * @param other The key to swap the data with.
     */
    protected void swap(D other) {
        throw new UnsupportedOperationException();
    }
    
    
}
