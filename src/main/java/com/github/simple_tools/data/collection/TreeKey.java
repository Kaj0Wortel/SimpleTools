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
 * A key interface for a binary tree key.
 *
 * @param <K> The key implementing this interface.
 * 
 * @author Kaj Wortel
 */
public interface TreeKey<K extends TreeKey<K>>
        extends Key {

    /**
     * @return The data of its left child in the tree, or {@code null}
     *         if this node has no left child.
     */
    K left();

    /**
     * @return The data of its right child in the tree, or {@code null}
     *         if this node has no right child.
     */
    K right();

    /**
     * @return The data of the parent in the tree, or {@code null}
     *         if this node has no parent.
     */
    K parent();

    /**
     * @return {@code true} if the node of this key represents the root in the tree.
     *     {@code false} otherwise.
     */
    default boolean isRoot() {
        return parent() == null;
    }

    /**
     * @return {@code true} if the node of this key represents a leaf in the tree.
     *     {@code false} otherwise.
     */
    default boolean isLeaf() {
        return left() == null && right() == null;
    }

    /**
     * @return {@code true} if the node of this key has a left child in the tree.
     *         {@code false} otherwise.
     */
    default boolean hasLeft() {
        return left() != null;
    }

    /**
     * @return {@code true} if the node of this key has a right child in the tree.
     *         {@code false} otherwise.
     */
    default boolean hasRight() {
        return right() != null;
    }

    /**
     * @return {@code true} if the node of this key has a parent in the tree.
     *         {@code false} otherwise.
     */
    default boolean hasParent() {
        return parent() != null;
    }
    
    
}
