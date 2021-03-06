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

package com.github.simple_tools;


/**
 * A useful interface that forces the implementer to have a public
 * clone method instead of the protected clone method from {@link Object}.
 * 
 * @author Kaj Wortel
 */
public interface PublicCloneable<V extends PublicCloneable<V>>
        extends Cloneable {
    
    /**
     * @return A clone of {@code this}.
     * 
     * @see Object#clone()
     */
    V clone();
    
    
}
