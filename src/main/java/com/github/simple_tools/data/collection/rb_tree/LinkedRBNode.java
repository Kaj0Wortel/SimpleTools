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

import com.github.simple_tools.data.collection.LinkedNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * This class stores the information about a node in the {@link LinkedRBTree} data structure.
 *
 * @param <D> The key value of the node.
 * 
 * @author Kaj Wortel
 * 
 * @see RBNode
 * @see LinkedRBTree
 * @see LinkedRBKey
 */
@Getter
@Setter
public class LinkedRBNode<D extends LinkedRBKey<D>>
        extends RBNode<D>
        implements LinkedNode<LinkedRBNode<D>, D> {
    
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
     *         linked node.
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
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + System.lineSeparator() +
                "  this  : " + getData() + System.lineSeparator() +
                "  size  : " + getSize() + System.lineSeparator() +
                "  color : " + getColor() + System.lineSeparator() + 
                "  parent: " + (!hasParent() ? "null" : getParent().getData()) + System.lineSeparator() +
                "  left  : " + (!hasLeft() ? "null" : getLeft().getData()) + System.lineSeparator() +
                "  right : " + (!hasRight() ? "null" : getRight().getData()) + System.lineSeparator() +
                "  next  : " + (!hasNext() ? "null" : getNext().getData()) + System.lineSeparator() +
                "  prev  : " + (!hasPrev() ? "null" : getPrev().getData()) + System.lineSeparator() +
                "]";
    }
    
    
}
