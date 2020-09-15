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
import lombok.Getter;
import lombok.Setter;


/**
 * The node class for the {@link LinkedRBTreeBag} class.
 * 
 * @param <D> The data type of the node.
 * 
 * @author Kaj Wortel
 * 
 * @see LinkedRBTreeBag
 */
@Getter
@Setter
public class LinkedRBBagNode<D extends LinkedRBBagKey<D>>
        extends RBBagNode<D>
        implements LinkedNode<LinkedRBBagNode<D>, D> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The sorted next node of this node. */
    private LinkedRBBagNode<D> next;
    /** The sorted previous node of this node. */
    private LinkedRBBagNode<D> prev;


    /* ------------------------------------------------------------------------
     * Constructors.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new node with the given data.
     *
     * @param data The data of this node.
     */
    private LinkedRBBagNode(D data, int count) {
        super(data, count);
    }

    /**
     * Creates a new {@link LinkedRBNode}. Automatically links the key to the node. <br>
     * This factory design pattern is used to prevent leaking of this instance
     * during initialisation.
     *
     * @param <D1> The type of the node to be initialised.
     *
     * @param data The data of the new node.
     * @param count The count of the data.
     *
     * @return A freshly initialised node.
     *
     * @throws IllegalArgumentException If the given data already belongs to another
     *         linked node.
     */
    public static <D1 extends LinkedRBBagKey<D1>> LinkedRBBagNode<D1> createNode(D1 data, int count) {
        if (data.getNode() != null) {
            throw new IllegalArgumentException(
                    "A linked key cannot be added to more than one search tree!");
        }
        LinkedRBBagNode<D1> node = new LinkedRBBagNode<>(data, count);
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
                "  this   : " + getData() + System.lineSeparator() +
                "  size   : " + getSize() + System.lineSeparator() +
                "  count  : " + getCount() + System.lineSeparator() +
                "  bagSize: " + getBagSize() + System.lineSeparator() +
                "  color  : " + getColor() + System.lineSeparator() +
                "  parent : " + (!hasParent() ? "null" : getParent().getData()) + System.lineSeparator() +
                "  left   : " + (!hasLeft() ? "null" : getLeft().getData()) + System.lineSeparator() +
                "  right  : " + (!hasRight() ? "null" : getRight().getData()) + System.lineSeparator() +
                "  next   : " + (!hasNext() ? "null" : getNext().getData()) + System.lineSeparator() +
                "  prev   : " + (!hasPrev() ? "null" : getPrev().getData()) + System.lineSeparator() +
                "]";
    }
    
    
}
