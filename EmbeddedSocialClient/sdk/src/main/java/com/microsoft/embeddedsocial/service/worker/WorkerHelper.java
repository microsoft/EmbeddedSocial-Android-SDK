/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.google.common.util.concurrent.ListenableFuture;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import static androidx.work.WorkInfo.State.CANCELLED;
import static androidx.work.WorkInfo.State.FAILED;
import static androidx.work.WorkInfo.State.SUCCEEDED;

/**
 * Implements utility functions to aid in setting up and handling androidx
 */
public class WorkerHelper {

    /**
     * Serialize the given Object to a String
     * @param data data to serialize
     * @return String containing the serialized data
     */
    public static String serialize(Serializable data) {
        String serializedData = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(data);
            serializedData = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            os.close();
        } catch (IOException e) {
            DebugLog.logException(e);
        }

        return serializedData;
    }

    /**
     * Deserialize the given string to an object
     * @param serializedData data to deserialize
     * @param <DataType> type to cast the data
     * @return deserialized object
     */
    public static <DataType> DataType deserialize(String serializedData) {
        if (serializedData == null) {
            return null;
        }

        DataType data = null;
        InputStream inputStream = new ByteArrayInputStream(Base64.decode(serializedData, Base64.DEFAULT));
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            data = (DataType)objectInputStream.readObject();
        } catch (ClassNotFoundException|IOException e) {
            DebugLog.logException(e);
        }

        return data;
    }

    /**
     * Determines if any workers matching the given tag are enqueued or running
     * @param tag Tag to match with active workers
     * @return true if any workers matching the tag are enqueued or running; false otherwise
     */
    public static boolean isOngoing(String tag) {
        ListenableFuture<List<WorkInfo>> workInfoListenableFutureList =
                WorkManager.getInstance().getWorkInfosByTag(tag);
        try {
            List<WorkInfo> workInfoList = workInfoListenableFutureList.get();
            boolean running = false;
            for (WorkInfo workInfo : workInfoList) {
                running = running || !workInfo.getState().isFinished();
            }
            return running;
        } catch (InterruptedException|ExecutionException e) {
            return false;
        }

    }

    /**
     * Handle the result of a completed work request
     * @param lifecycleOwner the lifecycle owner used to observe the work request
     * @param workerId ID of the work request
     * @param handler ResultHandler to invoke upon work completion
     */
    public static void handleResult(LifecycleOwner lifecycleOwner, UUID workerId, ResultHandler handler) {
        LiveData<WorkInfo> liveData = WorkManager.getInstance().getWorkInfoByIdLiveData(workerId);
        liveData.observe(lifecycleOwner, workInfo -> {
            WorkInfo.State state = workInfo.getState();
            if (state.isFinished()) {
                if (state.equals(SUCCEEDED)) {
                    handler.onSuccess();
                } else if (state.equals(FAILED) || state.equals(CANCELLED)) {
                    handler.onFailure();
                }
            }
        });
    }

    /**
     * Defines functions to run upon success or failure of a single work request
     */
    public interface ResultHandler {
        void onSuccess();
        void onFailure();
    }
}
