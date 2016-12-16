/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Convenient methods for work with views.
 */
public final class ViewUtils {

	private ViewUtils() {
	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T findView(View root, @IdRes int viewId) {
		return (T) root.findViewById(viewId);
	}

	public static <T extends View> T findView(View root, @IdRes int viewId, Class<T> viewClass) {
		return viewClass.cast(root.findViewById(viewId));
	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T findView(Activity activity, @IdRes int viewId) {
		return (T) activity.findViewById(viewId);
	}

	public static <T extends View> T findView(Activity activity, @IdRes int viewId, Class<T> viewClass) {
		return viewClass.cast(activity.findViewById(viewId));
	}

	@SuppressWarnings("unchecked")
	public static <T extends View> T findView(Fragment fragment, @IdRes int viewId) {
		View root = fragment.getView();
		return root != null ? (T) root.findViewById(viewId) : null;
	}

	public static <T extends View> T findView(Fragment fragment, @IdRes int viewId, Class<T> viewClass) {
		View root = fragment.getView();
		return root != null ? viewClass.cast(root.findViewById(viewId)) : null;
	}

	/**
	 * Requests the focus for the view and shows a keyboard.
	 */
	public static void focusAndShowKeyboard(View view) {
		view.requestFocus();
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * Hides a keyboard.
	 * @param containerView a view that contains a focused view (i.e. root activity's or fragment's view)
	 */
	public static void hideKeyboard(View containerView) {
		View focused = containerView.findFocus();
		if (focused != null) {
			InputMethodManager imm = (InputMethodManager) containerView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
		}
	}

	/**
	 * Hide a keyboard.
	 * @param fragment fragment containing the focused view
	 */
	public static void hideKeyboard(Fragment fragment) {
		View containerView = fragment.getView();
		if (containerView != null) {
			hideKeyboard(containerView);
		}
	}

	/**
	 * Hide a keyboard.
	 * @param activity current activity
	 */
	public static void hideKeyboard(Activity activity) {
		View containerView = activity.findViewById(android.R.id.content);
		hideKeyboard(containerView);
	}

	/**
	 * Gets the entered text from an {@link EditText}
	 */
	public static String getText(EditText view) {
		return view.getText().toString();
	}

	/**
	 * Returns whether the {@link EditText}'s input is empty.
	 */
	public static boolean isEmpty(EditText view) {
		return TextUtils.isEmpty(getText(view));
	}

	/**
	 * Sets view's visibility to {@link View#VISIBLE} or {@link View#GONE}.
	 */
	public static void setVisible(View view, boolean visible) {
		view.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	/**
	 * A shorter way to inflate a layout.
	 */
	public static View inflateLayout(@LayoutRes int layoutId, ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
	}

	/**
	 * Shows a short toast.
	 */
	public static void shortToast(Context context, @StringRes int messageId, Object... messageArgs) {
		Toast.makeText(context, context.getString(messageId, messageArgs), Toast.LENGTH_SHORT).show();
	}

	/**
	 * Shows a short toast.
	 */
	public static void shortToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Gets view's padding encapsulated in a {@link Rect} object.
	 */
	public static Rect getPadding(View view) {
		return new Rect(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
	}

	/**
	 * Sets view's padding from a {@link Rect} object.
	 */
	public static void setPadding(View view, Rect padding) {
		view.setPadding(padding.left, padding.top, padding.right, padding.bottom);
	}

	/**
	 * Returns the device's width in pixels.
	 */
	public static int getDisplayWidth(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}
}
