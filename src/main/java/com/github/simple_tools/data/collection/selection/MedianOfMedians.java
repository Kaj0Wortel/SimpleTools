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
package com.github.simple_tools.data.collection.selection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MedianOfMedians
		implements SelectK {
	private static final int NUM_SELECT = 10;
	
	private <D> int selectMedian(List<D> list, int k, int left, int right, Comparator<D> compare) {
		List<D> pivots = new ArrayList<>(NUM_SELECT);
		for (int i = 0; i < NUM_SELECT; i++) {
			pivots.add(list.get(ThreadLocalRandom.current().nextInt(right - left + 1) + left));
		}
		pivots.sort(compare);
		return NUM_SELECT * (k - left) / (right - left + 1);
	}
	
	private <D> D findKIn(List<D> list, int k, int left, int right, Comparator<D> compare) {
		list.subList(left, right + 1).sort(compare);
		return list.get(k);
	}
	
	@Override
	public <D> D find(Collection<D> data, int k, boolean inPlace, Comparator<D> compare) {
		if (k < 0 || k >= data.size()) {
			throw new IllegalArgumentException("Invalid k: " + k);
		}
		List<D> list;
		if (inPlace && data instanceof List) {
			list = (List<D>) data;
		} else {
			list = new ArrayList<>(data);
		}
		
		int left = 0;
		int right = list.size() - 1;
		while (right - left + 1 > NUM_SELECT) { // # remaining > NUM_SELECT
			// Select pivot.
			int pivotIndex = selectMedian(list, k, left, right, compare);
			
			// Sort using pivot.
			D pivot = list.set(pivotIndex, list.get(left)); // Remove pivot from list.
			int r = right;
			int l = left + 1;
			while (l <= r) {
				D d = list.get(l);
				if (compare.compare(pivot, d) >= 0) {
					list.set(l++ - 1, d);
				} else {
					list.set(l, list.get(r));
					list.set(r--, d);
				}
			}
			// Place back pivot.
			pivotIndex = l - 1;
			list.set(pivotIndex, pivot);
			
			// Update left/right for the next iteration, or return.
			if (k < pivotIndex) {
				right = pivotIndex - 1;
			} else if (k > pivotIndex) {
				left = pivotIndex + 1;
			} else {
				return pivot;
			}
		}
		return findKIn(list, k, left, right, compare);
	}
	
	public static void main(String[] args) {
		SelectK sk = new MedianOfMedians();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		for (int i = 0; i < 2; i++) {
			System.out.println(sk.find(list, i, true));
		}
		System.out.println(list);
	}
	
}
