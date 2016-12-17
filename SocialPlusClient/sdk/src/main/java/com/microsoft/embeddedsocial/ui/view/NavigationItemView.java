/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.theme.ThemeGroup;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.ui.theme.Theme;

/**
 * Navigation item view.
 */
public class NavigationItemView extends LinearLayout {

	private static final int MAX_NOTIFICATION_COUNT = 99;

	private Drawable iconNormal;
	private Drawable iconHighlighted;
	private ImageView iconView;
	private TextView notificationCountView;

	public NavigationItemView(Context context, AttributeSet attrs) {
		super(context, attrs, R.attr.es_navigationItemStyle);
		init(attrs);
	}

	public NavigationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setOrientation(HORIZONTAL);
		inflate(getContext(), R.layout.es_navigation_menu_item, this);
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.es_NavigationItemView);
		iconNormal = typedArray.getDrawable(R.styleable.es_NavigationItemView_es_icon_normal);
		iconHighlighted = typedArray.getDrawable(R.styleable.es_NavigationItemView_es_icon_highlight);
		String name = typedArray.getString(R.styleable.es_NavigationItemView_es_name);
		typedArray.recycle();

		iconView = ViewUtils.findView(this, R.id.es_icon);
		iconView.setImageDrawable(iconNormal);

		ViewUtils.findView(this, R.id.es_name, TextView.class).setText(name);
		notificationCountView = ViewUtils.findView(this, R.id.es_notificationCount);
		notificationCountView.setVisibility(View.GONE);
	}

	public void setName(CharSequence name) {
		ViewUtils.findView(this, R.id.es_name, TextView.class).setText(name);
	}

	public void hideIcon() {
		iconView.setVisibility(GONE);
	}

	public void setHighlighted(boolean highlighted) {
		iconView.setImageDrawable(highlighted ? iconHighlighted : iconNormal);
		if (highlighted) {
			setBackgroundColor(Color.BLACK);
		} else {
			Rect padding = ViewUtils.getPadding(this);
			ThemeGroup themeGroup = GlobalObjectRegistry.getObject(Options.class).getThemeGroup();
			TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
				themeGroup.getThemeResId(Theme.REGULAR),
				new int[]{R.attr.es_navigationItemBackground}
			);
			Drawable background = typedArray.getDrawable(0);
			setBackgroundDrawable(background);
			typedArray.recycle();
			ViewUtils.setPadding(this, padding);
		}
	}

	@SuppressLint("SetTextI18n")
	public void setNotificationCount(long count) {
		if (count > 0) {
			notificationCountView.setVisibility(View.VISIBLE);
			if (count > MAX_NOTIFICATION_COUNT) {
				notificationCountView.setText(String.valueOf(MAX_NOTIFICATION_COUNT) + "+");
				notificationCountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.es_notification_num_text_size_small));
			} else {
				notificationCountView.setText(String.valueOf(count));
				notificationCountView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.es_notification_num_text_size));
			}
		} else {
			notificationCountView.setVisibility(View.GONE);
		}
	}

}
