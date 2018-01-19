/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.module;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.data.display.DisplayMethod;
import com.microsoft.embeddedsocial.event.click.DisplayMethodChangedEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.sdk.ui.ToolbarColorizer;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.fragment.base.Module;
import com.microsoft.embeddedsocial.ui.theme.ThemeAttributes;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Adds gallery/list switch to the options menu.
 */
public class FeedViewMenuModule extends Module {
	private BaseFragment owner;

	public FeedViewMenuModule(BaseFragment owner) {
		super(owner);
		this.owner = owner;
	}

	public void inflateMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.es_feed_display_method, menu);
	}

	@Override
	protected void onPrepareOptionsMenu(Menu menu) {
		MenuItem viewSwitch = menu.findItem(R.id.es_viewSwitch);
		if (viewSwitch != null) {
			DisplayMethod displayMethod = Preferences.getInstance().getDisplayMethod();

			int attrId = displayMethod == DisplayMethod.GALLERY ? R.styleable.es_AppTheme_es_listIndicator : R.styleable.es_AppTheme_es_galleryIndicator;
			int iconId = ThemeAttributes.getResourceId(owner.getContext(), attrId);
			viewSwitch.setIcon(iconId);
			ToolbarColorizer colorizer = BaseActivity.getToolbarColorizer();
			if (colorizer != null) {
				viewSwitch.getIcon().setColorFilter(
						ContextCompat.getColor(getContext(), colorizer.getTextColor()),
						PorterDuff.Mode.SRC_ATOP);
			}

			viewSwitch.setTitle(displayMethod == DisplayMethod.GALLERY ? R.string.es_menu_list : R.string.es_menu_gallery);
		}
	}

	@Override
	protected boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.es_viewSwitch) {
			DisplayMethod newDisplayMethod = Preferences.getInstance().getDisplayMethod().next();
			Preferences.getInstance().setDisplayMethod(newDisplayMethod);
			EventBus.post(new DisplayMethodChangedEvent());
			getOwner().invalidateMenu();
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		getOwner().invalidateMenu();
	}
}
