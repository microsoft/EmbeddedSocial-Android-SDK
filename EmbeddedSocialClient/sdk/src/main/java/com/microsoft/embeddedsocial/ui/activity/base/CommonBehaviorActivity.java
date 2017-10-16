/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.theme.ThemeGroup;
import com.microsoft.embeddedsocial.ui.util.CommonBehaviorEventListener;
import com.microsoft.embeddedsocial.account.AuthorizationCause;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.base.utils.ObjectUtils;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.SignInActivity;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.theme.Theme;

import java.util.List;

import static com.microsoft.embeddedsocial.ui.util.CommonBehaviorEventListener.REQUESTCODE_SIGN_IN;

/**
 * <p>Implements common behavior for app's activities.</p>
 * <p>
 * If an activity requires an authorization and the user is not authorized it will launch the Sign-in activity.
 * If the user cancels the authorization the activity will be closed.
 * </p>
 * <p>
 * If an activity requires an authorization and after return to this activity user is not authorized (for example, after sign-out in not a master app),
 * the activity is closed.
 * </p>
 * <p>
 *  If the user returns to an activity that was launched with user handle different from current one, the activity is restarted.
 * </p>
 */
abstract class CommonBehaviorActivity extends AppCompatActivity {
	private static final int ACTION_PROCEED = 0;
	private static final int ACTION_RESTART = 1;
	private static final int ACTION_CLOSE = 2;

	private Theme theme = Theme.REGULAR;
	private String userHandle;
	private AuthorizationCause ongoingAuthorizationCause;

	private CommonBehaviorEventListener eventListener;

	/**
	 * Sets the theme. Call it only in a constructor.
	 */
	protected void setTheme(Theme theme) {
		this.theme = theme;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setupTheme();
		eventListener = new CommonBehaviorEventListener(this);
		userHandle = UserAccount.getInstance().getUserHandle();
		boolean unauthorizedAccess = isAuthorizationRequired() && !isUserAuthorized();
		super.onCreate(unauthorizedAccess ? null : savedInstanceState); // pass null to avoid possible crashes after immediate finish
		boolean isTablet = getResources().getBoolean(R.bool.es_isTablet);
		if (!isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (unauthorizedAccess) {
			if (savedInstanceState == null) {
				// launch the Sign-in activity
				onUnauthorizedAccess();
			} else {
				// user signed-out, just close the activity
				finish();
			}
		} else {
			initView(savedInstanceState);
		}
	}

	private void setupTheme() {
		ThemeGroup themeGroup = GlobalObjectRegistry.getObject(Options.class).getThemeGroup();
		setTheme(themeGroup.getThemeResId(theme));
	}

	protected void onUnauthorizedAccess() {
		startActivityForResult(new Intent(this, SignInActivity.class), REQUESTCODE_SIGN_IN);
	}

	/**
	 * Setups activity view.
	 */
	protected abstract void initView(Bundle savedInstanceState);

	protected void setupActionBar(ActionBar actionBar) {
		if (hasNavigationButton()) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// do not close if a child fragment has unsaved data
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) {
				if (fragment instanceof BaseFragment && !((BaseFragment) fragment).onBackPressed()) {
					return;
				}
			}
		}
		ViewUtils.hideKeyboard(this);
		super.onBackPressed();
	}

	protected boolean hasNavigationButton() {
		return true;
	}

	/**
	 * Whether an activity shows data available only for authorized users.
	 */
	protected boolean isAuthorizationRequired() {
		return false;
	}

	private boolean isUserAuthorized() {
		return UserAccount.getInstance().isSignedIn();
	}

	@Override
	protected void onPause() {
		EventBus.unregister(eventListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.register(eventListener);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		switch (resolveActionAfterRestart()) {
			case ACTION_RESTART:
				restartActivity();
				break;
			case ACTION_CLOSE:
				finish();
				break;
		}
	}

	private int resolveActionAfterRestart() {
		String newUserHandle = UserAccount.getInstance().getUserHandle();
		if (!ObjectUtils.equal(userHandle, newUserHandle)) {
			return isAuthorizationRequired() && TextUtils.isEmpty(newUserHandle) ? ACTION_CLOSE : ACTION_RESTART;
		} else {
			return ACTION_PROCEED;
		}
	}

	/**
	 * Whether an activity is going to be closed.
	 */
	public boolean isShuttingDown() {
		return resolveActionAfterRestart() != ACTION_PROCEED;
	}

	/**
	 * Closes this activity and launches it's copy. (This is a generic way to update it's content on authorization changes.)
	 */
	protected void restartActivity() {
		finish();
		Intent restartIntent = getRestartIntent();
		EnumUtils.putValue(restartIntent, IntentExtras.REASON_TO_SIGN_IN, ongoingAuthorizationCause);
		restartIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(restartIntent);
	}

	/**
	 * Gets an intent to use for activity restart.
	 */
	protected Intent getRestartIntent() {
		return getIntent();
	}

	/**
	 * Finds a view by id and casts to its class.
	 * @param viewId id
	 * @param <T> view type
	 */
	protected <T extends View> T findView(@IdRes int viewId) {
		return ViewUtils.findView(this, viewId);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUESTCODE_SIGN_IN) {
			if (resultCode == RESULT_OK) {
				ongoingAuthorizationCause = data != null
					? EnumUtils.getValue(data, IntentExtras.REASON_TO_SIGN_IN, AuthorizationCause.class)
					: null;
			} else {
				Preferences.getInstance().clearPendingAction();
				if (isAuthorizationRequired()) {
					finish();
				}
			}
		}
	}

	@Override
	public void startActivity(Intent intent) {
		addHostMenuExtra(intent);
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
		addHostMenuExtra(intent);
		super.startActivityForResult(intent, requestCode, options);
	}

	private void addHostMenuExtra(Intent intent) {
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(BaseActivity.HOST_MENU_BUNDLE_EXTRA)) {
			intent.putExtra(
					BaseActivity.HOST_MENU_BUNDLE_EXTRA,
					extras.getBundle(BaseActivity.HOST_MENU_BUNDLE_EXTRA));
		}
	}
}
