/*
 * Copyright 2021 Kaj Wortel
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
package com.github.simple_tools.data.collection.kd_tree;

import com.github.simple_tools.FastMath;
import com.github.simple_tools.data.collection.Node;

public class KdNode<D> {
	
	private interface Elem<D> {
	}
	
	private static class NodeElem<D>
			implements Elem<D> {
		KdNode<D> node;
	}
	
	private static class ValueElem<D>
			implements Elem<D> {
		Node<D> value;
	}
	
	private final Elem<D>[] elems;

	@SuppressWarnings("unchecked")
	public KdNode(int k) {
		elems = (Elem<D>[]) new Elem[2 << (k - 1)];
	}
	
	public boolean isNode(int i) {
		return elems[i] instanceof NodeElem;
	}
	
	public boolean isValue(int i) {
		return elems[i] instanceof KdNode.ValueElem;
	}
	
	public KdNode<D> getNode(int i) {
		return ((NodeElem<D>) elems[i]).node;
	}
	
	public Node<D> getValue(int i) {
		return ((ValueElem<D>) elems[i]).value;
	}
	
}
