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

import com.github.simple_tools.data.collection.rb_tree.RBColor;
import com.github.simple_tools.data.collection.rb_tree.RBNode;
import com.github.simple_tools.data.collection.rb_tree.RBTreeBag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * The node class for the {@link RBTreeBag} class.
 * 
 * @param <D> The data type of the node.
 *
 * @author Kaj Wortel
 * 
 * @see RBTreeBag
 */
@Getter
@Setter
public class RBBagNode<D>
        extends RBNode<D> {

    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The count of the element. */
    protected int count = 1;
    /** The size of the bag sub-tree starting at this node. */
    protected long bagSize = 1L;

    
    /* ------------------------------------------------------------------------
     * Constructors.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new node with the given data.
     * Sets the count to 1.
     *
     * @param data The data of this node.
     */
    public RBBagNode(D data) {
        super(data);
    }

    /**
     * Creates a new node with the given data and count.
     *
     * @param data  The data of this node.
     * @param count The count of the data.
     */
    public RBBagNode(D data, int count) {
        super(data);
        this.bagSize = this.count = count;
    }

    
    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    /**
     * Adds the given amount to the count.
     * Also updates the bag size of this node accordingly.
     * 
     * @param amt The amount to add to the count.
     */
    protected void addCount(int amt) {
        count += amt;
        bagSize += amt;
    }
    
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
                "]";
    }
    
}
