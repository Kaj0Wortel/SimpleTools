package com.github.simple_tools;


/**
 * A useful interface that forces the implementer to have a public
 * clone method instead of the protected clone method from {@link Object}.
 * 
 * @author Kaj Wortel
 */
public interface PublicCloneable
        extends Cloneable {
    
    /**
     * @return A clone of {@code this}.
     * 
     * @see Object#clone()
     */
    PublicCloneable clone();
    
    
}
