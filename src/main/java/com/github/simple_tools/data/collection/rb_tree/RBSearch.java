package com.github.simple_tools.data.collection.rb_tree;

/**
 * Search function used to find items in the {@link RBTree} data structure. <br>
 * 
 * @author Kaj Wortel
 * 
 * @see RBTree
 */
@FunctionalInterface
public interface RBSearch<D extends Comparable<D>> {
    
    /**
     * Enum denoting the options allowed for searching through the tree.
     */
    enum Choice {
        /** Returns the current element. */
        CURRENT,
        /** Returns the left element. */
        LEFT,
        /** Returns the right element. */
        RIGHT,
        /** Traverses the tree to the left. */
        GO_LEFT,
        /** Traverses the tree to the right. */
        GO_RIGHT
    }
    
    /**
     * For every level, the user gets the current data, and it's two children. <br>
     * If a child is {@code null}, then it doesn't exist. <br>
     * The key value of the left node is always smaller than the key value of the
     * current node, and the key value of the right node is always smaller than
     * the key value current node. <br>
     * <br>
     * As return value, one of the values of {@link Choice} should be returned.
     * If {@code null} is returned, then the search is aborted and {@code null} is
     * returned by tne search function.
     * 
     * @param cur The current data. Is never {@code null}.
     * @param left The data on the left. Can be {@code null}.
     * @param right The data on the right. Can be {@code null}.
     * 
     * @return The option to be made, or {@code null} to stop the search {@code null}.
     * 
     * @see Choice
     */
    Choice evaluate(D cur, D left, D right);
    
    
}
