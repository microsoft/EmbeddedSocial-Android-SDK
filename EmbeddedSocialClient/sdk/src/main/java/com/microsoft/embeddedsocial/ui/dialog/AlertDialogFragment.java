/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.event.dialog.OnDialogItemSelectedEvent;
import com.microsoft.embeddedsocial.event.dialog.OnNegativeButtonClickedEvent;
import com.microsoft.embeddedsocial.event.dialog.OnPositiveButtonClickedEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.base.event.EventBus;

/**
 * Wraps {@link AlertDialog} in a fragment. Imitates a {@link android.app.AlertDialog.Builder} functionality.
 */
public class AlertDialogFragment extends DialogFragment {

	private static final String PREF_DIALOG_ID = "dialogId";
	private static final String PREF_TITLE = "title";
	private static final String PREF_MESSAGE = "message";
	private static final String PREF_IS_PASSWORD_INPUT = "isPasswordInput";
	private static final String PREF_POSITIVE_BUTTON = "positiveButton";
	private static final String PREF_NEGATIVE_BUTTON = "negativeButton";
	private static final String PREF_ITEMS = "items";

	private EditText inputView;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return createFromArguments(getActivity(), getArguments());
	}

	private void hideKeyboard() {
		if (inputView != null) {
			ViewUtils.hideKeyboard(inputView);
		}
	}

	private Dialog createFromArguments(Context context, Bundle arguments) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
			.setTitle(arguments.getString(PREF_TITLE))
			.setMessage(arguments.getString(PREF_MESSAGE));
		setupPositiveButton(builder, arguments);
		setupNegativeButton(builder, arguments);
		setupInput(builder, context, arguments);
		setupItems(builder, context, arguments);
		AlertDialog dialog = builder.create();
		if (inputView != null) {
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
		return dialog;
	}

	private void setupInput(AlertDialog.Builder builder, Context context, Bundle arguments) {
		if (arguments.getBoolean(PREF_IS_PASSWORD_INPUT)) {
			@SuppressLint("InflateParams")
			View view = LayoutInflater.from(context).inflate(R.layout.es_dialog_input_password, null);
			inputView = ViewUtils.findView(view, R.id.es_input);
			builder.setView(view);
		}
	}

	private void setupItems(AlertDialog.Builder builder, Context context, Bundle arguments) {
		int[] items = arguments.getIntArray(PREF_ITEMS);
		if (items != null) {
			String[] itemStrings = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				itemStrings[i] = context.getString(items[i]);
			}
			builder.setItems(
				itemStrings,
				(dialog, which) -> EventBus.post(new OnDialogItemSelectedEvent(getDialogId(), which, items[which]))
			);
		}
	}

	private void setupNegativeButton(AlertDialog.Builder builder, Bundle arguments) {
		String negativeButton = arguments.getString(PREF_NEGATIVE_BUTTON);
		if (!TextUtils.isEmpty(negativeButton)) {
			builder.setNegativeButton(negativeButton,
				(dialog, which) -> {
					hideKeyboard();
					EventBus.post(new OnNegativeButtonClickedEvent(getDialogId()));
				});
		}
	}

	private void setupPositiveButton(AlertDialog.Builder builder, Bundle arguments) {
		String positiveButton = arguments.getString(PREF_POSITIVE_BUTTON);
		if (!TextUtils.isEmpty(positiveButton)) {
			builder.setPositiveButton(positiveButton,
				(dialog, which) -> {
					hideKeyboard();
					EventBus.post(new OnPositiveButtonClickedEvent(getDialogId()));
				}
			);
		}
	}

	private String getInput() {
		return inputView != null ? ViewUtils.getText(inputView) : null;
	}

	public String getDialogId() {
		return getArguments().getString(PREF_DIALOG_ID);
	}

	public static AlertDialogFragment createErrorDialogFragment(Context context, String dialogId, String title, String message) {
		return new Builder(context, dialogId)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(android.R.string.ok)
			.create();
	}

	public static AlertDialogFragment createErrorDialogFragment(Context context, String dialogId, @StringRes int title, @StringRes int message) {
		return createErrorDialogFragment(context, dialogId, context.getString(title), context.getString(message));
	}

	/**
	 * Builder.
	 */
	public static class Builder {

		private final Context context;
		private Bundle arguments = new Bundle();

		public Builder(Context context, String dialogId) {
			this.context = context;
			arguments.putString(PREF_DIALOG_ID, dialogId);
		}

		public Builder setTitle(String title) {
			arguments.putString(PREF_TITLE, title);
			return this;
		}

		public Builder setTitle(@StringRes int titleId) {
			return setTitle(context.getString(titleId));
		}

		public Builder setMessage(String message) {
			arguments.putString(PREF_MESSAGE, message);
			return this;
		}

		public Builder setMessage(@StringRes int messageId) {
			return setMessage(context.getString(messageId));
		}

		public Builder setPositiveButton(String buttonText) {
			arguments.putString(PREF_POSITIVE_BUTTON, buttonText);
			return this;
		}

		public Builder setPositiveButton(@StringRes int buttonTextId) {
			return setPositiveButton(context.getString(buttonTextId));
		}

		public Builder setNegativeButton(String buttonText) {
			arguments.putString(PREF_NEGATIVE_BUTTON, buttonText);
			return this;
		}

		public Builder setNegativeButton(@StringRes int buttonTextId) {
			return setNegativeButton(context.getString(buttonTextId));
		}

		public Builder setIsPasswordInput(boolean isPasswordInput) {
			arguments.putBoolean(PREF_IS_PASSWORD_INPUT, isPasswordInput);
			return this;
		}

		public Builder setItems(int... items) {
			arguments.putIntArray(PREF_ITEMS, items);
			return this;
		}

		public AlertDialogFragment create() {
			AlertDialogFragment fragment = new AlertDialogFragment();
			fragment.setArguments(arguments);
			return fragment;
		}

		public void show(FragmentActivity activity, String tag) {
			AlertDialogFragment fragment = create();
			fragment.show(activity.getSupportFragmentManager(), tag);
		}

	}

}
