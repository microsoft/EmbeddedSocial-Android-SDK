/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.embeddedsocial.actions.ActionTagFilter;
import com.microsoft.embeddedsocial.actions.OngoingActions;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.base.ActionListener;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragmentWithProgress;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.actions.ActionsLauncher;

import java.util.List;

/**
 * Fragment for deleting an account.
 */
public class DeleteAccountFragment extends BaseFragmentWithProgress {

	public DeleteAccountFragment() {
		addActionListener(new ActionTagFilter(Action.Tags.DELETE_ACCOUNT), new ActionListener() {
			@Override
			protected void onActionSucceeded(Action action) {
				onAccountDeleted();
			}

			@Override
			protected void onActionFailed(Action action, String error) {
				onError();
			}

			@Override
			protected void onActionsCompletionMissed(List<Action> completedActions, List<Action> succeededActions, List<Action> failedActions) {
				if (!succeededActions.isEmpty()) {
					onAccountDeleted();
				} else if (!failedActions.isEmpty()) {
					onError();
				}
			}
		});
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.es_fragment_delete_account;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setOnClickListener(view, R.id.es_cancelButton, v -> finishActivity());
		setOnClickListener(view, R.id.es_deleteButton, v -> {
			setProgressVisible(true);
			ActionsLauncher.deleteAccount(v.getContext());
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		setProgressVisible(OngoingActions.hasActionsWithTag(Action.Tags.DELETE_ACCOUNT));
	}

	private void onAccountDeleted() {
		showToast(R.string.es_account_deleted);
		finishActivity();
		updateActivitiesStackOnLogOut();
	}

	private void onError() {
		setProgressVisible(false);
		showToast(R.string.es_message_network_error);
	}
}
