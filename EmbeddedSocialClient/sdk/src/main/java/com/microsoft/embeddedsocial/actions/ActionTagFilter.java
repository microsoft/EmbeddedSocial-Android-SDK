/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.actions;

import java.util.Arrays;
import java.util.Collection;

/**
 * Filters actions by tag.
 */
public class ActionTagFilter implements ActionFilter {

	private final Collection<String> tags;

	public ActionTagFilter(String... tags) {
		this.tags = Arrays.asList(tags);
	}

	@Override
	public boolean filter(Action action) {
		String actionTag = action.getTag();
		for (String tag : tags) {
			if (actionTag.equals(tag)) {
				return true;
			}
		}
		return false;
	}
}
