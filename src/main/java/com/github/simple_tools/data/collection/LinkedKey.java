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
package com.github.simple_tools.data.collection.rb_tree.bag;

import com.github.simple_tools.data.collection.rb_tree.RBNode;

public abstract class LinkedKey<K extends LinkedKey<K>> {
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The node representing this key. */
    private LinkedKey<K> node;


    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Sets the node represented by this key.
     *
     * @param node The node to link.
     */
    void setNode(LinkedKey<K> node) {
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
    LinkedKey<K> getNode() {
        return node;
    }

    /**
     * @return The next element of the chain, or {@code null}
     *     if this node has no next element.
     */
    public D next() {
        return (node == null ? null : node.getNextData());
    }

    /**
     * @return The previous element of the chain, or {@code null}
     *     if this node has no previous element.
     */
    public D prev() {
        return (node == null ? null : node.getPrevData());
    }

    /**
     * @return The data of its left child in the tree, or {@code null}
     *     if this node has no left child.
     */
    public D left() {
        if (node == null) return null;
        RBNode<D> left = node.getLeft();
        return (left == null ? null : left.getData());
    }

    /**
     * @return The data of its right child in the tree, or {@code null}
     *     if this node has no right child.
     */
    public D right() {
        if (node == null) return null;
        RBNode<D> right = node.getRight();
        return (right == null ? null : right.getData());
    }

    /**
     * @return The data of the parent in the tree, or {@code null}
     *     if this node has no parent.
     */
    public D parent() {
        if (node == null) return null;
        RBNode<D> parent = node.getParent();
        return (parent == null ? null : parent.getData());
    }

    /**
     * @return {@code true} if the node of this key has a left child in the tree.
     *     {@code false} otherwise.
     */
    public boolean hasLeft() {
        return (node != null && node.hasLeft());
    }

    /**
     * @return {@code true} if the node of this key has a right child in the tree.
     *     {@code false} otherwise.
     */
    public boolean hasRight() {
        return node != null && node.hasRight();
    }

    /**
     * @return {@code true} if the node of this key has a parent in the tree.
     *     {@code false} otherwise.
     */
    public boolean hasParent() {
        return node != null && node.hasParent();
    }

    /**
     * @return {@code true} if the node of this key has a next node in the tree.
     *     {@code false} otherwise.
     */
    public boolean hasPrev() {
        return node != null && node.hasPrev();
    }

    /**
     * @return {@code true} if the node of this key has a previous node in the tree.
     *     {@code false} otherwise.
     */
    public boolean hasNext() {
        return node != null && node.hasNext();
    }

    /**
     * @return {@code true} if the node of this key represents the root in the tree.
     *     {@code false} otherwise.
     */
    public boolean isRoot() {
        return node != null && node.isRoot();
    }

    /**
     * @return {@code true} if the node of this key represents a leaf in the tree.
     *     {@code false} otherwise.
     */
    public boolean isLeaf() {
        return node != null && !node.hasChild();
    }


}
