/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.worker.DeleteAccountWorker;
import com.microsoft.embeddedsocial.service.worker.WorkerHelper;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragmentWithProgress;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
                    .addTag(DeleteAccountWorker.TAG)
                    .build();
            WorkManager.getInstance().enqueue(workRequest);
            WorkerHelper.handleResult(this, workRequest.getId(), new WorkerHelper.ResultHandler() {
                @Override
                public void onSuccess() {
                    getActivity().runOnUiThread(() -> onAccountDeleted());
                }

                @Override
                public void onFailure() {
                    getActivity().runOnUiThread(() -> onError());
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setProgressVisible(WorkerHelper.isOngoing(DeleteAccountWorker.TAG));
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
