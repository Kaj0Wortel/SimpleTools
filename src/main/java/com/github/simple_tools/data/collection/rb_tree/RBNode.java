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

/**
 * This class stores the information about the node in the {@link RBTree} data structure.
 * The value of this node is calculated from the given object.
 * 
 * @author Kaj Wortel
 * 
 * @see RBTree
 */
public class RBNode<D> {
     
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The color of the node. */
    @NonNull
    private RBColor color = RBColor.BLACK;
    /** The parent of the node. */
    private RBNode<D> parent = null;
    /** The left node of this node in the tree. */
    private RBNode<D> left = null;
    /** The right node of this node in the tree. */
    private RBNode<D> right = null;
    /** The size of the subtree. */
    private int size = 1;
    /** The data element of this node. */
    final private D data;
    
    
    /* ------------------------------------------------------------------------
     * Constructors.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new node with the given data.
     * 
     * @param data The data of this node.
     */
    public RBNode(D data) {
        this.data = data;
    }
    
    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * @param color The new color of the node.
     */
    protected final void setColor(RBColor color) {
        this.color = color;
    }
    
    /**
     * @return The color of the node.
     */
    public final RBColor getColor() {
        return color;
    }
    
    /**
     * @param parent The new parent of this node.
     */
    protected final void setParent(RBNode<D> parent) {
        this.parent = parent;
    }
    
    /**
     * @return The parent of this node, or {@code null} if this is the root node.
     */
    public final RBNode<D> getParent() {
        return parent;
    }
    
    /**
     * @param left The new left child of this node.
     */
    protected final void setLeft(RBNode<D> left) {
        this.left = left;
    }
    
    /**
     * @return The left child of this node.
     */
    public final RBNode<D> getLeft() {
        return left;
    }
    
    /**
     * @param right The new right child of this node.
     */
    protected final void setRight(RBNode<D> right) {
        this.right = right;
    }
    
    /**
     * @return The right child of this node.
     */
    public final RBNode<D> getRight() {
        return right;
    }
    
    /**
     * @return The size of the subtree rooted at this node.
     */
    public final int size() {
        return size;
    }
    
    /**
     * @param size The new size of the subtree routed at this node.
     */
    public final void setSize(int size) {
        this.size = size;
    }
    
    /**
     * @return The data of this node.
     */
    public final D getData() {
        return data;
    }
    
    /**
     * @return The uncle of this node, i.e. the other sibling of the parent.
     *     Returns {@code null} if no such node exists.
     */
    public final RBNode<D> getUncle() {
        if (parent == null) return null;
        return parent.getSibling();
    }
    
    /**
     * @return The sibling of this node, or {@code null} if no such node exist
     */
    public final RBNode<D> getSibling() {
        if (parent == null) return null;
        return (parent.getLeft() == this
                ? parent.getRight()
                : parent.getLeft());
    }
    
    /**
     * @return The grand parent of this node, or {@code null} if no such node exist.
     */
    public final RBNode<D> getGrandParent() {
        return (parent == null
                ? null
                : parent.getParent());
    }
    
    /**
     * @return {@code true} if this node has a left child. {@code false} otherwise.
     */
    public final boolean hasLeft() {
        return left != null;
    }
    
    /**
     * @return {@code true} if this node has a right child. {@code false} otherwise.
     */
    public final boolean hasRight() {
        return right != null;
    }
    
    /**
     * @return {@code true} if this node has a parent. {@code false} otherwise.
     */
    public final boolean hasParent() {
        return parent != null;
    }
    
    /**
     * @return If this node has a child. {@code false} otherwise.
     */
    public final boolean hasChild() {
        return hasLeft() || hasRight();
    }
    
    /**
     * @return {@code true} if this node is the right child. {@code false} otherwise.
     */
    public final boolean isRight() {
        return parent != null && parent.getRight() == this;
    }
    
    /**
     * @return {@code true} if this node is the left child. {@code false} otherwise.
     */
    public final boolean isLeft() {
        return parent != null && parent.getLeft() == this;
    }
    
    /**
     * @return {@code true} if this node is a root node. {@code false} otherwise.
     */
    public final boolean isRoot() {
        return parent == null;
    }
    
    /**
     * @return {@code true} if the color of this node is red.
     */
    public final boolean isRed() {
        return color == RBColor.RED;
    }
    
    /**
     * @return {@code true} if the color of this node is black.
     */
    public final boolean isBlack() {
        return color == RBColor.BLACK;
    }
    
    /**
     * @return {@code true} if the left node is a black node. This includes the NIL leaves.
     */
    public final boolean leftIsBlack() {
        return left == null || left.color == RBColor.BLACK;
    }
    
    /**
     * @return {@code true} if the right node is a black node. This includes the NIL leaves.
     */
    public final boolean rightIsBlack() {
        return right == null || right.color == RBColor.BLACK;
    }
    
    @Override
    public String toString() {
        return "Node[" + System.lineSeparator() +
                "  this  : " + data + System.lineSeparator() + 
                "  color : " + color + System.lineSeparator() + 
                "  parent: " + (!hasParent() ? "null" : parent.data) + System.lineSeparator() +
                "  left  : " + (!hasLeft() ? "null" : left.data) + System.lineSeparator() +
                "  right : " + (!hasRight() ? "null" : right.data) + System.lineSeparator() +
                "  size  : " + size + System.lineSeparator() +
                "]";
    }
    
    
}
