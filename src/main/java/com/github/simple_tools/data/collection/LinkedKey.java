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
 * A Key interface for linked keys.
 * 
 * @param <K> The key implementing this interface.
 *
 * @author Kaj Wortel
 */
public interface LinkedKey<K extends LinkedKey<K>>
        extends Key {

    /**
     * @return The previous element of the chain, or {@code null}
     *         if this node has no previous element.
     */
    K prev();

    /**
     * @return The next element of the chain, or {@code null}
     *         if this node has no next element.
     */
    K next();

    /**
     * @return {@code true} if the node of this key has a next node in the tree.
     *         {@code false} otherwise.
     */
    default boolean hasPrev() {
        return prev() != null;
    }

    /**
     * @return {@code true} if the node of this key has a previous node in the tree.
     *         {@code false} otherwise.
     */
    default boolean hasNext() {
        return next() != null;
    }


}
