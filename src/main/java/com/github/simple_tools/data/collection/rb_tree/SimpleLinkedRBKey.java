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
public class SimpleLinkedRBKey<D>
        extends LinkedRBKey<SimpleLinkedRBKey<D>> {
    
    /* ------------------------------------------------------------------------
     * Variables.
     * ------------------------------------------------------------------------
     */
    @Getter
    private final D data;
    
    
    /* ------------------------------------------------------------------------
     * Constructor.
     * ------------------------------------------------------------------------
     */
    public SimpleLinkedRBKey(D data) {
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


}
