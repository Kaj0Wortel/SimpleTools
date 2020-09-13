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
package com.github.simple_tools.data.collection;

/**
 * A node interface for a binary tree node.
 * 
 * @param <N> The node implementing this interface.
 * @param <K> The key value of the node.
 *
 * @author Kaj Wortel
 */
public interface TreeNode<N extends TreeNode<N, K>, K>
        extends Node<K> {

    /**
     * @return The left child of this node, or {@code null} if this node has no parent.
     */
    N getLeft();

    /**
     * @return The right child of this node, or {@code null} if this node has no parent.
     */
    N getRight();

    /**
     * @return The parent of this node, or {@code null} if this node has no parent.
     */
    N getParent();
    
    /**
     * @return The uncle of this node, i.e. the sibling of the parent,
     *         or {@code null} if no such node exists.
     */
    default N getUncle() {
        return (getParent() == null ? null : getParent().getSibling());
    }
    
    /**
     * @return The sibling of this node, or {@code null} if no such node exist.
     */
    default N getSibling() {
        if (getParent() == null) return null;
        return (getParent().getLeft() == this
                ? getParent().getRight()
                : getParent().getLeft());
    }
    
    /**
     * @return The grand parent of this node, or {@code null} if no such node exist.
     */
    default N getGrandParent() {
        return (getParent() == null ? null : getParent().getParent());
    }

    /**
     * @return {@code true} if this node has a left child. {@code false} otherwise.
     */
    default boolean hasLeft() {
        return getLeft() != null;
    }

    /**
     * @return {@code true} if this node has a right child. {@code false} otherwise.
     */
    default boolean hasRight() {
        return getRight() != null;
    }

    /**
     * @return {@code true} if this node has a parent. {@code false} otherwise.
     */
    default boolean hasParent() {
        return getParent() != null;
    }

    /**
     * @return If this node has a child. {@code false} otherwise.
     */
    default boolean hasChild() {
        return hasLeft() || hasRight();
    }

    /**
     * @return If this node has a grand parent. {@code false} otherwise.
     */
    default boolean hasGrandParent() {
        return getGrandParent() != null;
    }

    /**
     * @return If this node has an uncle. {@code false} otherwise.
     */
    default boolean hasUncle() {
        return getUncle() != null;
    }

    /**
     * @return If this node has a sibling. {@code false} otherwise.
     */
    default boolean hasSibling() {
        return getSibling() != null;
    }

    /**
     * @return {@code true} if this node is the right child. {@code false} otherwise.
     */
    default boolean isRight() {
        return getParent() != null && getParent().getRight() == this;
    }

    /**
     * @return {@code true} if this node is the left child. {@code false} otherwise.
     */
    default boolean isLeft() {
        return getParent() != null && getParent().getLeft() == this;
    }

    /**
     * @return {@code true} if this node is a root node. {@code false} otherwise.
     */
     default boolean isRoot() {
        return getParent() == null;
    }


}
