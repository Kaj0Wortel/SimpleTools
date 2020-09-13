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
 * An interface for the nodes used in the {@link com.github.simple_tools.data.collection} package.
 *
 * @param <K> The key value of the node.
 * 
 * @author Kaj Wortel
 */
public interface Node<K> {

    /**
     * @return The data of the node.
     */
    K getData();
    
}
