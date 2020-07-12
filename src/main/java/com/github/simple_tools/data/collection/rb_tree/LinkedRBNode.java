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

/**
 * This class stores the information about a node in the {@link LinkedRBTree} data structure.
 * 
 * @version 1.1
 * @author Kaj Wortel
 * 
 * @see RBNode
 * @see LinkedRBTree
 * @see LinkedRBKey
 */
public class LinkedRBNode<D extends LinkedRBKey<D>>
        extends RBNode<D> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The sorted next node of this node. */
    private LinkedRBNode<D> next;
    /** The sorted previous node of this node. */
    private LinkedRBNode<D> prev;
    
    
    /* ------------------------------------------------------------------------
     * Constructors.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new node with the given data.
     * 
     * @param data The data of this node.
     */
    private LinkedRBNode(D data) {
        super(data);
    }
    
    /**
     * Creates a new {@link LinkedRBNode}. Automatically links the key to the node. <br>
     * This factory design pattern is used to prevent leaking of this instance
     * during initialisation.
     * 
     * @param <D1> The type of the node to be initialised.
     * 
     * @param data The data of the new node.
     * 
     * @return A freshly initialised node.
     * 
     * @throws IllegalArgumentException If the given data already belongs to another
     *     linked node.
     */
    public static <D1 extends LinkedRBKey<D1>> LinkedRBNode<D1> createNode(D1 data) {
        if (data.getNode() != null) {
            throw new IllegalArgumentException(
                    "A linked key cannot be added to more than one search tree!");
        }
        LinkedRBNode<D1> node = new LinkedRBNode<>(data);
        data.setNode(node);
        return node;
    }
    
    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * @param next The new next node of this node.
     */
    protected final void setNext(LinkedRBNode<D> next) {
        this.next = next;
    }
    
    /**
     * @param prev The new previous node of this node.
     */
    protected final void setPrev(LinkedRBNode<D> prev) {
        this.prev = prev;
    }
    
    /**
     * @return The next node of this node.
     */
    public final LinkedRBNode<D> getNext() {
        return next;
    }
    
    /**
     * @return The previous node of this node.
     */
    public final LinkedRBNode<D> getPrev() {
        return prev;
    }
    
    /**
     * @return {@code true} if this node has a next node. {@code false} otherwise.
     */
    public final boolean hasNext() {
        return next != null;
    }
    
    /**
     * @return {@code true} if this node has a previous node. {@code false} otherwise.
     */
    public final boolean hasPrev() {
        return prev != null;
    }
    
    /**
     * @return The data of the next node, or {@code null} if there is no next node.
     */
    public final D getNextData() {
        return (next == null ? null : next.getData());
    }
    
    /**
     * @return The data of the previous node, or {@code null} if there is no previous node.
     */
    public final D getPrevData() {
        return (prev == null ? null : prev.getData());
    }
    
    @Override
    public String toString() {
        return "Node[" + System.lineSeparator() +
                "  this  : " + getData() + System.lineSeparator() + 
                "  color : " + getColor() + System.lineSeparator() + 
                "  parent: " + (!hasParent() ? "null" : getParent().getData()) + System.lineSeparator() +
                "  left  : " + (!hasLeft() ? "null" : getLeft().getData()) + System.lineSeparator() +
                "  right : " + (!hasRight() ? "null" : getRight().getData()) + System.lineSeparator() +
                "  next  : " + (!hasNext() ? "null" : getNext().getData()) + System.lineSeparator() +
                "  prev  : " + (!hasPrev() ? "null" : getPrev().getData()) + System.lineSeparator() +
                "]";
    }
    
    
}
