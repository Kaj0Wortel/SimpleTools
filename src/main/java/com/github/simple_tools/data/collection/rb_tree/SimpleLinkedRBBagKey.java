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

import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

/**
 * A class for a simple wrapper of the {@link LinkedRBKey}.
 *
 * @param <D> The data type of the key.
 *
 * @author Kaj Wortel
 *
 * @see LinkedRBKey
 */
public class SimpleLinkedRBBagKey<D>
        extends LinkedRBBagKey<SimpleLinkedRBBagKey<D>> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    /** The data object for the key. */
    @Getter
    private D data;


    /* ------------------------------------------------------------------------
     * Constructor.
     * ------------------------------------------------------------------------
     */
    /**
     * Creates a new simple linked red-black bag key using the given data.
     * 
     * @param data The data of the key.
     */
    public SimpleLinkedRBBagKey(D data) {
        this.data = data;
    }


    /* ------------------------------------------------------------------------
     * Functions.
     * ------------------------------------------------------------------------
     */
    @Override
    public String toString() {
        return Objects.toString(data);
    }

    /**
     * Creates a comparator for this class using a comparator for the data type.
     * 
     * @param cmp The comparator to compare the data.
     * @param <D> The data type to compare.
     * 
     * @return A comparator which compares the data of two {@link SimpleLinkedRBBagKey}s.
     */
    public static <D> Comparator<SimpleLinkedRBBagKey<D>> compare(Comparator<D> cmp) {
        return (d1, d2) -> cmp.compare(d1.getData(), d2.getData());
    }

    @Override
    protected void swap(SimpleLinkedRBBagKey<D> other) {
        D data = this.data;
        this.data = other.data;
        other.data = data;
    }
    

}
