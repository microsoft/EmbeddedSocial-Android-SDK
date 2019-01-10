/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class WorkerSerializationHelper {

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
}
