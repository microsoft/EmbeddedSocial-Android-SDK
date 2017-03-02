/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.event.click.NavigationItemClickedEvent;
import com.microsoft.embeddedsocial.sdk.IDrawerState;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.squareup.otto.Subscribe;

public abstract class EmbeddedSocialNavigationActivity extends EmbeddedSocialActivity implements IDrawerState {
	protected static final String DISPLAY_MENU_EXTRA = "DISPLAY_MENU_EXTRA";

	private static final int NO_SELECTED_ITEM_ID = 0;
	private static final int CLOSE_DRAWER_DELAY = 500;

	private DrawerLayout drawerLayout;
	private DrawerHandler drawerHandler;
	private DrawerHandler.DisplayMenu displayMenu;

	private final Object eventListener = new Object() {
		@Subscribe
		public void onNavigationItemClicked(NavigationItemClickedEvent event) {
			drawerLayout.postDelayed(drawerLayout::closeDrawers, CLOSE_DRAWER_DELAY);
		}
	};

	@Override
	public void setContentView(@LayoutRes int layoutResID) {
		super.setContentView(R.layout.es_sdk_base_drawer_activity_main);

		drawerLayout = (DrawerLayout) findViewById(R.id.es_drawer_layout);
		ViewGroup content = (ViewGroup) findViewById(R.id.es_content);
		content.removeAllViews();
		content.addView(getLayoutInflater().inflate(layoutResID, content, false));

		drawerHandler = DrawerHandlerFactory.createHandler(this, getIntent().getExtras());
		drawerHandler.inflate((ViewGroup) findViewById(R.id.es_container), NO_SELECTED_ITEM_ID);
		drawerHandler.setDisplayMenu(displayMenu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (drawerHandler != null) {
			drawerHandler.onResumeSetup();
		}
		EventBus.register(eventListener);
	}

	@Override
	protected void onPause() {
		EventBus.unregister(eventListener);
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			displayMenu = (DrawerHandler.DisplayMenu) savedInstanceState.getSerializable(DISPLAY_MENU_EXTRA);
		}
		if (displayMenu == null) {
			displayMenu = DrawerHandler.DisplayMenu.HOST_MENU;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(DISPLAY_MENU_EXTRA, drawerHandler.getDisplayMenu());
		super.onSaveInstanceState(outState);
	}

	@Override
	public final void openDrawer() {
		drawerLayout.openDrawer(GravityCompat.START);
	}

	@Override
	public final void closeDrawer() {
		drawerLayout.closeDrawer(GravityCompat.START);
	}

	@Override
	public final boolean isDrawerOpen() {
		return drawerLayout.isDrawerOpen(GravityCompat.START);
	}

	protected DrawerLayout getDrawerLayout() {
		return drawerLayout;
	}
}
