/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import com.microsoft.embeddedsocial.base.function.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Holds fetcher's data.
 *
 * @param <T> data type
 */
class DataHolder<T> {

	private final List<T> data = new ArrayList<>();
	private final List<T> unmodifiableDataLink = Collections.unmodifiableList(data);
	private final CallbackNotifier callbackNotifier;

	DataHolder(CallbackNotifier callbackNotifier) {
		this.callbackNotifier = callbackNotifier;
	}

	int size() {
		return data.size();
	}

	List<T> getAll() {
		return unmodifiableDataLink;
	}

	boolean isEmpty() {
		return data.isEmpty();
	}

	boolean removeAllMatches(Predicate<? super T> predicate) {
		boolean dataChanged = false;
		Iterator<? extends T> iterator = data.iterator();
		while (iterator.hasNext()) {
			if (predicate.test(iterator.next())) {
				iterator.remove();
				dataChanged = true;
			}
		}
		if (dataChanged) {
			callbackNotifier.notifyDataRemoved();
		}
		return dataChanged;
	}

	int removeFirstMatch(Predicate<? super T> predicate) {
		for (int i = 0; i < data.size(); i++) {
			if (predicate.test(data.get(i))) {
				data.remove(i);
				callbackNotifier.notifyDataRemoved();
				return i;
			}
		}
		return -1;
	}

	void insertItem(T item, int position) {
		data.add(position, item);
		callbackNotifier.notifyDataUpdated();
	}

	void add(List<T> newData) {
		data.addAll(newData);
	}

	void replaceData(List<T> newData) {
		data.clear();
		data.addAll(newData);
	}
}
