/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class to handle common auth related tasks.
 */
public class AuthUtils {
    private static final MessageDigest DIGEST;

    static {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            DebugLog.logException(e);
        }
        DIGEST = digest;
    }

    /**
     * Updates app state based on the credentials used to authenticate the current user.
     * Stores if an account is currently an account stored on the device.
     * The user is signed out if the current account is removed from the device.
     * @param context runtime context
     */
    public static void checkAccountStatus(Context context) {
        UserAccount account = UserAccount.getInstance();
        AccountData data = account.getAccountDetails();
        boolean isDeviceAccount = isDeviceAccount(context, data.getIdentityProvider(), data.getHashedEmail());
        if (isDeviceAccount) {
            account.updateIsDeviceAccount(true);
        } else if (data.getIsDeviceAccount()) {
            account.signOut();
        }
    }

    /**
     * Determines if the provided email is a google account on the device
     * @param context runtime context
     * @param accountType Identity provider for the account associated with the email
     * @param hashedEmail Hashed email address used to authenticate the user
     * @return true if the provided email exists as a google account on the device, false otherwise
     */
    public static boolean isDeviceAccount(Context context, IdentityProvider accountType, String hashedEmail) {
        if (accountType == IdentityProvider.GOOGLE && hashedEmail != null) {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccounts();

            for (Account googleAccount : accounts) {
                String hash = hashString(googleAccount.name);
                if (hashedEmail.equals(hash)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Hashes the given string using a cryptographic hash function
     * @param input String to hash
     * @return result of the hash function as a hexadecimal String
     */
    public static String hashString(String input) {
        if (DIGEST != null) {
            byte[] hash = DIGEST.digest(input.getBytes());
            return bytesToHexString(hash);
        } else {
            return null;
        }
    }

    /**
     * Converts an array of bytes into a hexadecimal String
     * @param input byte array
     * @return String representation of the input array in hexadecimal
     */
    private static String bytesToHexString(byte[] input) {
        StringBuilder result = new StringBuilder();
        for (Byte b : input) {
            String byteHex = Integer.toHexString(b & 0xff);
            // toHexString does not include leading 0s so ensure each byte is aligned
            if (byteHex.length() == 1) {
                result.append('0');
            }
            result.append(byteHex);
        }

        return result.toString();
    }
}
