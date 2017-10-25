/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.event.click.NavigationItemClickedEvent;
import com.microsoft.embeddedsocial.sdk.BuildConfig;
import com.microsoft.embeddedsocial.sdk.IDrawerState;
import com.microsoft.embeddedsocial.sdk.INavigationDrawerHandler;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.sdk.ui.ToolbarColorizer;
import com.microsoft.embeddedsocial.ui.fragment.NavigationFragment;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.PermissionRequestResultEvent;
import com.microsoft.embeddedsocial.sdk.ui.DrawerHandler;
import com.microsoft.embeddedsocial.sdk.ui.DrawerHandlerFactory;
import com.squareup.otto.Subscribe;

/**
 * Base activity class.
 */
public abstract class BaseActivity extends CommonBehaviorActivity implements ActivityCompat.OnRequestPermissionsResultCallback, IDrawerState {
	public static final String HOST_MENU_BUNDLE_EXTRA = "HOST_APP_ARGS_LEVEL_EXTRA";
	private static final String DISPLAY_MENU_EXTRA = "DISPLAY_MENU_EXTRA";
	private static final int CLOSE_DRAWER_DELAY = 500;

	private final int activeNavigationItemId;

	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private View frameContentView;
	private Toolbar toolbar;
	private DrawerHandler drawerHandler;
	private DrawerHandler.DisplayMenu displayMenu;
	protected static ToolbarColorizer customToolbarColorizer;
	private INavigationDrawerHandler navigationDrawerHandler;

	private boolean navigationLocked = false;

	private final Object eventListener = new Object() {
		@Subscribe
		public void onNavigationItemClicked(NavigationItemClickedEvent event) {
			drawerLayout.postDelayed(drawerLayout::closeDrawers, CLOSE_DRAWER_DELAY);
		}
	};

	protected BaseActivity() {
		this(0);
	}

	protected BaseActivity(int activeNavigationItemId) {
		this.activeNavigationItemId = activeNavigationItemId;
	}

	public static void setToolbarColorizer(ToolbarColorizer colorizer) {
		customToolbarColorizer = colorizer;
	}

	public static ToolbarColorizer getToolbarColorizer() {
		return customToolbarColorizer;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setContentView(getLayoutResId());
		toolbar = findView(R.id.es_toolbar);
		setSupportActionBar(toolbar);

		if (customToolbarColorizer != null) {
			toolbar.setBackgroundColor(ContextCompat.getColor(this, customToolbarColorizer.getBackgroundColor()));
			int textColor = ContextCompat.getColor(this, customToolbarColorizer.getTextColor());
			toolbar.setTitleTextColor(textColor);
			toolbar.setSubtitleTextColor(textColor);
		}

		ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			setupActionBar(actionBar);
		}
		navigationDrawerHandler = GlobalObjectRegistry.getObject(INavigationDrawerHandler.class);
		if (navigationDrawerHandler == null) {
			if (savedInstanceState != null) {
				displayMenu = (DrawerHandler.DisplayMenu) savedInstanceState.getSerializable(DISPLAY_MENU_EXTRA);
			}
			if (displayMenu == null) {
				displayMenu = DrawerHandler.DisplayMenu.SOCIAL_MENU;
			}
		}

		if (hasNavigationMenu()) {
			initDrawerLayout();
		} else {
			setNonNavDrawerToolbar();
		}

		setupLayout();
		if (savedInstanceState == null) {
			setupFragments();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (customToolbarColorizer != null) {
			int color = ContextCompat.getColor(this, customToolbarColorizer.getTextColor());
			for (int i = 0; i < toolbar.getChildCount(); i++) {
				View v = toolbar.getChildAt(i);
				if (v instanceof ImageButton) {
					((ImageButton) v).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
				}
			}
		}
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Setup fragments here.
	 */
	protected void setupFragments() {
	}

	/**
	 * Setup inner layout here.
	 */
	protected void setupLayout() {
	}

	private void initDrawerLayout() {
		frameContentView = findView(android.R.id.content);
		drawerLayout = findView(R.id.es_drawerLayout);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.es_open_side_menu, R.string.es_close_side_menu) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				ViewUtils.hideKeyboard(frameContentView);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				ViewUtils.hideKeyboard(frameContentView);
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		drawerToggle.syncState();

		navigationDrawerHandler = GlobalObjectRegistry.getObject(INavigationDrawerHandler.class);
		if (navigationDrawerHandler != null) {
			Fragment customNavFragment = navigationDrawerHandler.getFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.es_navigationLayout, customNavFragment)
					.commit();
			getSupportFragmentManager().executePendingTransactions();

			customNavFragment.onActivityCreated(null);
			FrameLayout navigation = (FrameLayout)findViewById(R.id.es_navigationLayout);

			View view = customNavFragment.onCreateView(getLayoutInflater(), drawerLayout, null);

			navigation.addView(view);

			// scale
			Resources res = getResources();
			DrawerLayout.LayoutParams layoutParams =
					new DrawerLayout.LayoutParams(res.getDimensionPixelSize(navigationDrawerHandler.getWidth()),
							ViewGroup.LayoutParams.MATCH_PARENT);
			layoutParams.gravity = Gravity.START;
			navigation.setLayoutParams(layoutParams);
			navigation.setBackgroundColor(res.getColor(navigationDrawerHandler.getBackgroundColor()));

			if (navigationDrawerHandler.displayToolbar()) {
				((ViewGroup)toolbar.getParent()).removeView(toolbar);
				((LinearLayout)findView(R.id.es_drawerContainer)).addView(toolbar, 0);
			}

			navigationDrawerHandler.setUp(R.id.es_navigationLayout, drawerLayout);
		} else {
			if (BuildConfig.STANDALONE_APP) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.es_navigationLayout, NavigationFragment.create(activeNavigationItemId))
						.commit();
			} else {
				drawerHandler = DrawerHandlerFactory.createHandler(this, getIntent().getBundleExtra(HOST_MENU_BUNDLE_EXTRA));
				drawerHandler.inflate((ViewGroup) findViewById(R.id.es_navigationLayout), activeNavigationItemId);
				drawerHandler.setDisplayMenu(displayMenu);
			}
		}
	}

	/**
	 * Configures the toolbar to end the activity when the back arrow is pressed
	 */
	protected void setNonNavDrawerToolbar() {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * Layout id for the activity.
	 */
	@LayoutRes
	protected int getLayoutResId() {
		return hasNavigationMenu() ? R.layout.es_activity_base_navigation : R.layout.es_activity_base;
	}

	/**
	 * Inflates a layout and set that view as an inner activity content ("inner" means that the toolbar and navigation menu is not affected)
	 * @param layoutId id of layout
	 */
	protected void setActivityContent(@LayoutRes int layoutId) {
		ViewGroup parent = findView(R.id.es_content);
		parent.removeAllViews();
		LayoutInflater.from(this).inflate(layoutId, parent);
	}

	/**
	 * Sets a fragment as an inner activity content.
	 */
	protected void setActivityContent(Fragment fragment) {
		ViewGroup parent = findView(R.id.es_content);
		if (parent != null) {
			parent.removeAllViews();
			getSupportFragmentManager().beginTransaction().replace(R.id.es_content, fragment).commit();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (toolbar != null) {
				if (toolbar.isOverflowMenuShowing()) {
					toolbar.hideOverflowMenu();
				} else {
					toolbar.showOverflowMenu();
				}
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	protected boolean hasNavigationMenu() {
		return activeNavigationItemId != 0 && !isNavigationDrawerDisabled();
	}

	private boolean isNavigationDrawerDisabled() {
		return isNavigationDrawerDisabled(getName());
	}

	public static boolean isNavigationDrawerDisabled(String name) {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		return options.disableNavigationDrawerForActivities().contains(name);
	}

	public final boolean isTablet() {
		return getResources().getBoolean(R.bool.es_isTablet);
	}

	@Override
	protected void onPause() {
		EventBus.unregister(eventListener);
		super.onPause();
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (drawerToggle != null) {
			drawerToggle.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (navigationLocked) {
			return true;
		}
		if (navigationDrawerHandler != null) {
			return false;
		}
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Locks navigation from this activity from the navigation menu and by back key
	 */
	public void disableNavigationPanel() {
		navigationLocked = true;
		if (drawerLayout != null) {
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}
	}

	@Override
	public void onBackPressed() {
		if (!navigationLocked) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (drawerHandler != null) {
			outState.putSerializable(DISPLAY_MENU_EXTRA, drawerHandler.getDisplayMenu());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		new PermissionRequestResultEvent(requestCode, permissions, grantResults).submit();
	}

	@Override
	public final void openDrawer() {
		if (drawerLayout != null) {
			drawerLayout.openDrawer(GravityCompat.START);
		}
	}

	@Override
	public final void closeDrawer() {
		if (drawerLayout != null) {
			drawerLayout.postDelayed(drawerLayout::closeDrawers, CLOSE_DRAWER_DELAY);
		}
	}

	@Override
	public final boolean isDrawerOpen() {
		if (drawerLayout == null) {
			return false;
		}
		return drawerLayout.isDrawerOpen(GravityCompat.START);
	}

	protected String getName() {
		return null;
	}
}

