/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.actions.ActionFilter;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.event.action.ActionCompletedEvent;
import com.microsoft.socialplus.event.action.ActionStartedEvent;
import com.microsoft.socialplus.sdk.BuildConfig;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.PopularActivity;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;
import java.util.List;

/**
 * Base fragment class.
 */
public abstract class BaseFragment extends Fragment {

	private List<Integer> themesToMerge = new LinkedList<>();

	private final List<Module> modules = new LinkedList<>();

	private final List<Pair<ActionFilter, ActionListener>> actionListeners = new LinkedList<>();
	private final List<Object> eventListeners = new LinkedList<>();

	@SuppressWarnings("FieldCanBeLocal")
	private final Object actionEventListener = new Object() {

		@Subscribe
		public void onActionStartedEvent(ActionStartedEvent event) {
			Action action = event.getAction();
			for (Pair<ActionFilter, ActionListener> pair : actionListeners) {
				ActionFilter actionFilter = pair.first;
				if (actionFilter.filter(action)) {
					ActionListener actionListener = pair.second;
					actionListener.notifyActionStarted(action);
				}
			}
		}

		@Subscribe
		public void onActionCompletedEvent(ActionCompletedEvent event) {
			Action action = event.getAction();
			for (Pair<ActionFilter, ActionListener> pair : actionListeners) {
				ActionFilter actionFilter = pair.first;
				if (actionFilter.filter(action)) {
					ActionListener actionListener = pair.second;
					actionListener.notifyActionCompleted(action);
				}
			}
		}

	};

	protected BaseFragment() {
		addThemeToMerge(R.style.SocialPlusSdkThemeOverlayBaseFragment);
		setRetainInstance(true);
		eventListeners.add(this);
		eventListeners.add(actionEventListener);
	}

	/**
	 * Registers a module. Call it only from a constructor.
	 */
	protected void addModule(Module module) {
		modules.add(module);
	}

	protected void addActionListener(ActionFilter filter, ActionListener listener) {
		actionListeners.add(new Pair<>(filter, listener));
	}

	/**
	 * Adds an event listener. Call it only from constructor (or at least before onResume)
	 */
	protected void addEventListener(Object listener) {
		eventListeners.add(listener);
	}

	/**
	 * Return a layout id to inflate in {#onCreateView}. May be 0 if the fragment has no view.
	 */
	@LayoutRes
	protected abstract int getLayoutId();

	/**
	 * Adds a theme overlay (to add or overwrite fragment specific attributes).
	 */
	protected void addThemeToMerge(@StyleRes int themeId) {
		themesToMerge.add(themeId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		int layoutId = getLayoutId();
		return (layoutId == 0) ? null : inflater.inflate(layoutId, container, false);
	}

	private LayoutInflater getThemedLayoutInflater(LayoutInflater inflater) {
		if (!themesToMerge.isEmpty()) {
			return inflater.cloneInContext(getContext());
		} else {
			return inflater;
		}
	}

	@Override
	public Context getContext() {
		Context context = super.getContext();
		for (int themeId : themesToMerge) {
			context = new ContextThemeWrapper(context, themeId);
		}
		return context;
	}

	@Override
	public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
		LayoutInflater inflater = super.getLayoutInflater(savedInstanceState);
		return getThemedLayoutInflater(inflater);
	}

	protected void setOnClickListener(View rootView, @IdRes int viewId, View.OnClickListener onClickListener) {
		findView(rootView, viewId).setOnClickListener(onClickListener);
	}

	protected <T extends View> T findView(View root, @IdRes int viewId) {
		return ViewUtils.findView(root, viewId);
	}

	protected <T extends View> T findView(View root, @IdRes int viewId, Class<T> viewClass) {
		return ViewUtils.findView(root, viewId, viewClass);
	}

	protected void updateActivitiesStackOnLogOut() {
		if (BuildConfig.STANDALONE_APP) {
			Intent intent = new Intent(getActivity(), PopularActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		finishActivity();
	}

	protected void hideKeyboard() {
		View view = getView();
		if (view != null) {
			ViewUtils.hideKeyboard(view);
		}
	}

	protected void showToast(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}

	protected void showToast(@StringRes int messageId) {
		showToast(getString(messageId));
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		for (Module module : modules) {
			module.onAttach(context);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		for (Module module : modules) {
			module.onDetach();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		for (Module module : modules) {
			module.onDestroyView();
		}
	}

	@Override
	public void onPause() {
		for (Object eventListener : eventListeners) {
			EventBus.unregister(eventListener);
		}
		for (Module module : modules) {
			module.onPause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		for (Module module : modules) {
			module.onResume();
		}
		for (Pair<ActionFilter, ActionListener> actionListener : actionListeners) {
			actionListener.second.notifyOnResume();
		}
		for (Object eventListener : eventListeners) {
			EventBus.register(eventListener);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		for (Module module : modules) {
			module.onStart();
		}
	}

	@Override
	public void onStop() {
		for (Module module : modules) {
			module.onStop();
		}
		super.onStop();
	}

	protected void finishActivity() {
		getActivity().finish();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (Module module : modules) {
			module.onSaveInstanceState(outState);
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		for (Module module : modules) {
			module.onViewCreated(view, savedInstanceState);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		for (Module module : modules) {
			module.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (Module module : modules) {
			module.onCreate(savedInstanceState);
		}
	}

	protected BaseActivity getOwner() {
		return (BaseActivity) getActivity();
	}

	protected void hideView(@IdRes int viewId) {
		findView(getView(), viewId).setVisibility(View.GONE);
	}

	protected void hideView(View root, @IdRes int viewId) {
		findView(root, viewId).setVisibility(View.GONE);
	}

	protected void showView(@IdRes int viewId) {
		findView(getView(), viewId).setVisibility(View.VISIBLE);
	}

	public final boolean isTablet() {
		return getResources().getBoolean(R.bool.sp_isTablet);
	}

	public void invalidateMenu() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			activity.invalidateOptionsMenu();
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		for (Module module : modules) {
			module.onPrepareOptionsMenu(menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		for (Module module : modules) {
			if (module.onOptionsItemSelected(item)) {
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Is called from {@link Activity#onBackPressed()}.
	 * If you need to prevent a user from leaving a page, overwrite this method and return <code>false</code>
	 */
	public boolean onBackPressed() {
		return true;
	}

	protected void startActivity(Class<? extends Activity> activityClass) {
		startActivity(new Intent(getContext(), activityClass));
	}

	protected boolean isRestarting() {
		return getOwner().isShuttingDown();
	}

}
