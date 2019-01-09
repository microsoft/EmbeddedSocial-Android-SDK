/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.actions.OngoingActions;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.worker.DeleteAccountWorker;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragmentWithProgress;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkInfo.State;
import androidx.work.WorkManager;

import static androidx.work.WorkInfo.State.CANCELLED;
import static androidx.work.WorkInfo.State.FAILED;
import static androidx.work.WorkInfo.State.SUCCEEDED;

/**
 * Fragment for deleting an account.
 */
public class DeleteAccountFragment extends BaseFragmentWithProgress {

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
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DeleteAccountWorker.class)
                    .build();
            WorkManager.getInstance().enqueue(workRequest);
            LiveData<WorkInfo> liveData = WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId());
            liveData.observe(this, workInfo -> {
                State state = workInfo.getState();
                if (state.isFinished()) {
                    if (state.equals(SUCCEEDED)) {
                        getActivity().runOnUiThread(() -> onAccountDeleted());
                    } else if (state.equals(FAILED) || state.equals(CANCELLED)) {
                        getActivity().runOnUiThread(() -> onError());
                    }
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setProgressVisible(OngoingActions.hasActionsWithTag(Action.Tags.DELETE_ACCOUNT));
    }

    private void onAccountDeleted() {
        showToast(R.string.es_msg_general_account_deleted);
        finishActivity();
        updateActivitiesStackOnLogOut();
    }

    private void onError() {
        setProgressVisible(false);
        showToast(R.string.es_message_network_error);
    }
}
