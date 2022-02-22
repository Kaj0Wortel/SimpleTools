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

import com.github.simple_tools.AbstractTest;
import com.github.simple_tools.data.collection.Bag;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for all {@link SelectK} implementation classes.
 *
 * @author Kaj Wortel
 */
public class SelectionTest
		extends AbstractTest {

	@Test(timeout = 1000L)
	public void manyTest() {
		final int AMT = 20;
		SelectK sk = new MedianOfMedians();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < AMT; i++) {
			list.add(i);
		}
		Set<Integer> expSet = new HashSet<>(list);
		Collections.shuffle(list);
		
		for (int i = 0; i < AMT; i++) {
			List<Integer> expList = new ArrayList<>(list);
			assertEquals((Integer) i, sk.find(list, i, false));
			assertEquals(expSet.size(), list.size());
			assertEquals(expList, list);

			assertEquals((Integer) i, sk.find(list, i, true));
			assertEquals(expSet.size(), list.size());
			assertEquals(expSet, new HashSet<>(list));
		}
	}
	
	
}
