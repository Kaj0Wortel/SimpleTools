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

import com.github.simple_tools.data.collection.Node;
import com.github.simple_tools.data.collection.TreeNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * This class stores the information about the node in the {@link RBTree} data structure.
 * The value of this node is calculated from the given object.
 *
 * @param <D> The key value of the node.
 *           
 * @author Kaj Wortel
 * 
 * @see RBTree
 */
@Getter
@Setter(AccessLevel.PROTECTED)
public class RBNode<D>
        implements TreeNode<RBNode<D>, D> {
     
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The color of the node. */
    @NonNull
    protected RBColor color = RBColor.BLACK;
    /** The parent of the node. */
    protected RBNode<D> parent = null;
    /** The left node of this node in the tree. */
    protected RBNode<D> left = null;
    /** The right node of this node in the tree. */
    protected RBNode<D> right = null;
    /** The size of the subtree. */
    @Getter(AccessLevel.NONE)
    protected int size = 1;
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
     * @return The size of the subtree rooted at this node.
     */
    public int size() {
        return size;
    }
    
    /**
     * @return {@code true} if the color of this node is red.
     */
    public boolean isRed() {
        return color == RBColor.RED;
    }
    
    /**
     * @return {@code true} if the color of this node is black.
     */
    public boolean isBlack() {
        return color == RBColor.BLACK;
    }
    
    /**
     * @return {@code true} if the left node is a black node. This includes the NIL leaves.
     */
    public boolean isLeftBlack() {
        return left == null || left.color == RBColor.BLACK;
    }
    
    /**
     * @return {@code true} if the right node is a black node. This includes the NIL leaves.
     */
    public boolean isRightBlack() {
        return right == null || right.color == RBColor.BLACK;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + System.lineSeparator() +
                "  this  : " + data + System.lineSeparator() + 
                "  color : " + color + System.lineSeparator() + 
                "  parent: " + (!hasParent() ? "null" : parent.data) + System.lineSeparator() +
                "  left  : " + (!hasLeft() ? "null" : left.data) + System.lineSeparator() +
                "  right : " + (!hasRight() ? "null" : right.data) + System.lineSeparator() +
                "  size  : " + size + System.lineSeparator() +
                "]";
    }
    
    
}
