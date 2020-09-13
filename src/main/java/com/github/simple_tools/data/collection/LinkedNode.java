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
 * A node interface for linked nodes.
 * 
 * @param <N> The node implementing this interface.
 * @param <K> The key value of the node.
 *
 * @author Kaj Wortel
 */
public interface LinkedNode<N extends LinkedNode<N, K>, K>
        extends Node<K> {
    
    /**
     * @return The previous node, or {@code null} if no such node exists.
     */
    N getPrev();
    
    /**
     * @return The next node, or {@code null} if no such node exists.
     */
    N getNext();

    /**
     * Sets the previous node.
     *
     * @param node The new previous node.
     */
    void setPrev(N node);
    
    /**
     * Sets the next node.
     * 
     * @param node The new next node.
     */
    void setNext(N node);
    
    /**
     * @return The data of the previous node, or {@code null} if there is no previous node.
     */
    default K getPrevData() {
        final N prev = getNext();
        if (prev == null) return null;
        return prev.getData();
    }
    
    /**
     * @return The data of the next node, or {@code null} if there is no next node.
     */
    default K getNextData() {
        return (getNext() == null ? null : getNext().getData());
    }

    /**
     * @return {@code true} if this node has a previous node. {@code false} otherwise.
     */
    default boolean hasPrev() {
        return getPrev() != null;
    }

    /**
     * @return {@code true} if this node has a next node. {@code false} otherwise.
     */
    default boolean hasNext() {
        return getNext() != null;
    }
    
    
}
