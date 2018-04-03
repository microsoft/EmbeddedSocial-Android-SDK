/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.event.dialog.OnNegativeButtonClickedEvent;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.dialog.AlertDialogFragment;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.ui.util.FieldNotEmptyValidator;
import com.microsoft.embeddedsocial.ui.view.TextInput;
import com.microsoft.embeddedsocial.image.UserPhotoLoader;
import com.squareup.otto.Subscribe;

/**
 * Base class for Add post / Edit post fragments.
 */
public abstract class BaseEditPostFragment extends BaseFragment {
	private static final String PREF_TITLE = "title";
	private static final String PREF_DESCRIPTION = "description";

	private static final String CONFIRM_QUIT_DIALOG_ID = "confirmQuit";

	private ImageView coverView;
	private TextView imageMessageView;

	private TextInput titleView;
	private TextInput descriptionView;

	@SuppressWarnings("FieldCanBeLocal")
	private ImageViewContentLoader photoLoader;

	protected BaseEditPostFragment() {
		addEventListener(new Object() {
			@Subscribe
			public void onNegativeButtonClicked(OnNegativeButtonClickedEvent event) {
				finishActivity();
			}
		});
	}

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_add_post;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initAccountView(view);
		coverView = findView(view, R.id.es_postImage);
		titleView = findView(view, R.id.es_titleLayout);
		descriptionView = findView(view, R.id.es_description);
		descriptionView.setValidator(new FieldNotEmptyValidator(getContext()));
		imageMessageView = findView(view, R.id.es_imageMessage);
		View bottomBar = findView(view, R.id.es_bottomBar);
		bottomBar.setVisibility(View.VISIBLE);
		Button doneButton = findView(view, R.id.es_doneButton);
		doneButton.setOnClickListener(v -> uploadPost());

		if (savedInstanceState != null) {
			setTitle(savedInstanceState.getString(PREF_TITLE));
			setDescription(savedInstanceState.getString(PREF_DESCRIPTION));
		}
	}

	private void initAccountView(View view) {
		AccountData accountData = UserAccount.getInstance().getAccountDetails();
		findView(view, R.id.es_username, TextView.class).setText(accountData.getFullName());

		ImageView profileImage = findView(view, R.id.es_profileImage, ImageView.class);
		photoLoader = new UserPhotoLoader(profileImage);
		ContentUpdateHelper.setProfileImage(getContext(), photoLoader, accountData.getUserPhotoUrl());
	}

	protected ImageView getCoverView() {
		return coverView;
	}

	protected TextInput getTitleView() {
		return titleView;
	}

	protected TextInput getDescriptionView() {
		return descriptionView;
	}

	protected TextView getImageMessageView() {
		return imageMessageView;
	}

	protected abstract boolean isInputEmpty();

	public boolean uploadPost() {
		if (descriptionView.validate()) {
			hideKeyboard();
			onFinishedEditing();
			return true;
		} else {
			Toast.makeText(getActivity(), R.string.es_message_enter_description, Toast.LENGTH_SHORT).show();
			descriptionView.focusAndShowKeyboard();
			return false;
		}
	}

	protected abstract void onFinishedEditing();

	protected String getTitle() {
		return titleView.getText();
	}

	protected String getDescription() {
		return descriptionView.getText();
	}

	protected void setTitle(String title) {
		titleView.setText(title);
	}

	protected void setDescription(String description) {
		descriptionView.setText(description);
	}

	@Override
	public boolean onBackPressed() {
		return close() && super.onBackPressed();
	}

	public boolean close() {
		if (!isInputEmpty()) {
			new AlertDialogFragment.Builder(getActivity(), CONFIRM_QUIT_DIALOG_ID)
				.setTitle(R.string.es_title_add_post_confirm_quit)
				.setMessage(R.string.es_message_add_post_confirm_quit)
				.setPositiveButton(android.R.string.cancel)
				.setNegativeButton(R.string.es_option_add_post_leave)
				.show(getActivity(), null);
			return false;
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PREF_TITLE, getTitle());
		outState.putString(PREF_DESCRIPTION, getDescription());
	}
}
