/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment.module;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.data.display.DisplayMethod;
import com.microsoft.socialplus.event.click.DisplayMethodChangedEvent;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.base.BaseFragment;
import com.microsoft.socialplus.ui.fragment.base.Module;

/**
 * Adds gallery/list switch to the options menu.
 */
public class FeedViewMenuModule extends Module {

	public FeedViewMenuModule(BaseFragment owner) {
		super(owner);
	}

	public void inflateMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sp_feed_display_method, menu);
	}

	@Override
	protected void onPrepareOptionsMenu(Menu menu) {
		MenuItem viewSwitch = menu.findItem(R.id.sp_viewSwitch);
		if (viewSwitch != null) {
			DisplayMethod displayMethod = Preferences.getInstance().getDisplayMethod();

			int attrId = displayMethod == DisplayMethod.GALLERY ? R.styleable.sp_AppTheme_sp_listIndicator : R.styleable.sp_AppTheme_sp_galleryIndicator;
			TypedArray typedArray = getContext().obtainStyledAttributes(R.styleable.sp_AppTheme);
			int iconId = typedArray.getResourceId(attrId, 0);
			typedArray.recycle();
			viewSwitch.setIcon(iconId);
			if (BaseActivity.isValidTextColor()) {
				viewSwitch.getIcon().setColorFilter(
						ContextCompat.getColor(getContext(),BaseActivity.getTextColor()),
						PorterDuff.Mode.SRC_ATOP);
			}

			viewSwitch.setTitle(displayMethod == DisplayMethod.GALLERY ? R.string.sp_menu_list : R.string.sp_menu_gallery);
		}
	}

	@Override
	protected boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.sp_viewSwitch) {
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
