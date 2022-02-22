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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultSort
		implements SelectK {
	
	@Override
	public <D> D find(Collection<D> data, int k, boolean inPlace, Comparator<D> compare) {
		if (inPlace && data instanceof List) {
			List<D> list = (List<D>) data;
			list.sort(compare);
			return list.get(k);
		} else {
			return data.stream().sorted(compare).filter(new Predicate<>() {
				int count = 0;
				@Override
				public boolean test(D d) {
					return count++ == k;
				}
			}).collect(Collectors.toList()).get(0);
		}
	}
}
