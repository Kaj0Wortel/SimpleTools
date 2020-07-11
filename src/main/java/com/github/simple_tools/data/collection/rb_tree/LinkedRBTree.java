package com.github.simple_tools.data.collection.rb_tree;

import java.util.Collection;

/**
 * Class implementing a linked red-black tree search tree. It supports the following operations:
 * <table border='1'>
 *   <tr><th>Operation</th><th>Average</th><th>Worst case</th><th>Function</th></tr>
 *   <tr><td><b>Space</b></td><td>O(n)</td><td>O(n)</td><td></td></tr>
 *   <tr><td><b>Search</b></td><td>O(log n)</td><td>O(log n)</td><td>{@link LinkedRBKey#parent()},
 *       {@link LinkedRBKey#left()}, <br> {@link LinkedRBKey#right()}, {@link LinkedRBKey#next()},<br>
 *       {@link LinkedRBKey#prev()}</td></tr>
 *   <tr><td><b>Insert</b></td><td>O(log n)</td><td>O(log n)</td><td>{@link #add(LinkedRBKey)}</td></tr>
 *   <tr><td><b>Delete</b></td><td>O(log n)</td><td>O(log n)</td><td>{@link #remove(Object)}</td></tr>
 *   <tr><td><b>Neighbor</b></td><td>O(1)</td><td>O(1)</td><td>{@link #next(LinkedRBKey)},
 *       {@link #prev(LinkedRBKey)}, <br> {@link LinkedRBKey#next()}, {@link LinkedRBKey#prev()}</td></tr>
 * </table>
 * Note that it is necessary that the functions {@link Object#hashCode()} and {@link Object#equals(Object)}
 * are correctly implemented and that their behaviour doesn't change for any inserted nodes. <br>
 * <br>
 * This balanced binary search tree supports inserting unequal keys with equal value and hash code, but. <br>
 * Inserting the same element multiple times is not supported. <br>
 * <br>
 * This implementation is <b>NOT</b> thread safe. <br>
 * <br>
 * For an implementation of a red-black tree using only an interface as key, take a look at {@link RBTree}.
 * 
 * @author Kaj Wortel
 * 
 * @see LinkedRBKey
 * @see RBTree
 */
@SuppressWarnings("unused")
public class LinkedRBTree<D extends LinkedRBKey<D>>
        extends RBTree<D> {
    
    /* -------------------------------------------------------------------------
     * Constructor.
     * -------------------------------------------------------------------------
     */
    /**
     * Creates a new empty linked red-black tree.
     */
    public LinkedRBTree() {
    }
    
    /**
     * Creates a new linked red-black tree from the given collection.
     * 
     * @param col The collection to add.
     * 
     * @see RBTree#RBTree(Collection)
     */
    public LinkedRBTree(Collection<D> col) {
        super(col);
    }

    /**
     * Creates a new linked red-black tree from the given collection.
     *
     * @param elems The collection to add.
     *
     * @see RBTree#RBTree(Comparable...)
     */
    @SafeVarargs
    public LinkedRBTree(D... elems) {
        super(elems);
    }
    
    
    /* -------------------------------------------------------------------------
     * Functions.
     * -------------------------------------------------------------------------
     */
    @Override
    public boolean add(D data) {
        return super.add(data);
    }
    
    @Override
    protected boolean remove(RBNode<D> node) {
        if (super.remove(node)) {
            D data = gd(node);
            if (data != null) data.setNode(null);
            return true;
        }
        return false;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected <N extends RBNode<D>> void initTree(N[] nodes) {
        for (int i = 1; i < nodes.length; i++) {
            link((LinkedRBNode<D>) nodes[i - 1], (LinkedRBNode<D>) nodes[i]);
        }
        super.initTree(nodes);
    }
    
    @Override
    protected LinkedRBNode<D> createNode(D data) {
        return LinkedRBNode.createNode(data);
    }
    
    @Override
    public D next(D data) {
        return data.next();
    }
    
    @Override
    public RBNode<D> next(RBNode<D> node) {
        if (node instanceof LinkedRBNode) {
            return ((LinkedRBNode<D>) node).getNext();
        } else {
            return super.next(node);
        }
    }
    
    @Override
    public RBNode<D> prev(RBNode<D> node) {
        if (node instanceof LinkedRBNode) {
            return ((LinkedRBNode<D>) node).getPrev();
        } else {
            return super.prev(node);
        }
    }
    
    @Override
    public D prev(D data) {
        return data.prev();
    }
    
    @Override
    protected LinkedRBNode<D> bstInsert(D data) {
        LinkedRBNode<D> node = (LinkedRBNode<D>) super.bstInsert(data);
        if (node == null) return null;
        node.getData().setNode(node);
        LinkedRBNode<D> p = (LinkedRBNode<D>) node.getParent();
        if (p == null) return node;
        // node != root
        if (node.isRight()) link(p, node, p.getNext());
        else link(p.getPrev(), node, p);
        return node;
    }
    
    @Override
    protected LinkedRBNode<D> bstDelete(RBNode<D> n) {
        LinkedRBNode<D> node = (LinkedRBNode<D>) super.bstDelete(n);
        if (node == null) return null;
        
        link(node.getPrev(), node.getNext());
        node.setNext(null);
        node.setPrev(null);
        return node;
    }
    
    /**
     * Links the nodes {@code left}, {@code mid} and {@code right} together.
     * 
     * @param left The left node of the chain to make.
     * @param mid The middle node of the chain to make.
     * @param right The right node of the chain to make.
     */
    protected void link(LinkedRBNode<D> left, LinkedRBNode<D> mid, LinkedRBNode<D> right) {
        if (left != null) left.setNext(mid);
        if (right != null) right.setPrev(mid);
        if (mid != null) {
            mid.setPrev(left);
            mid.setNext(right);
        }
    }
    
    /**
     * Links the nodes {@code left} and {@code right} together.
     * 
     * @param left The left node of the chain to make.
     * @param right The right node of the chain to make.
     */
    protected void link(LinkedRBNode<D> left, LinkedRBNode<D> right) {
        if (left != null) left.setNext(right);
        if (right != null) right.setPrev(left);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @deprecated This function should not be used in the linked variant of the tree. <br>
     *     Use the {@link LinkedRBKey#parent()}, {@link LinkedRBKey#left()}, {@link LinkedRBKey#right()},
     *     {@link LinkedRBKey#next()} and {@link LinkedRBKey#prev()} functions instead.
     */
    @Override
    @Deprecated
    public D search(RBSearch<D> search) {
        return super.search(search);
    }
    
    
}
