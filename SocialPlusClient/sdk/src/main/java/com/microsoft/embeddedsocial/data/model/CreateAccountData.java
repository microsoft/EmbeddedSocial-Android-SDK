/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

/**
 * Information needed to create an account.
 */
public class CreateAccountData implements Parcelable {
    private String firstName;
    private String lastName;
    private String bio;
    private Uri photoUri;
    private IdentityProvider identityProvider;
    private String thirdPartyAccessToken;
    private String thirdPartyRequestToken;

    public CreateAccountData() { }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String getThirdPartyAccessToken() {
        return thirdPartyAccessToken;
    }

    public void setThirdPartyAccessToken(String thirdPartyAccessToken) {
        this.thirdPartyAccessToken = thirdPartyAccessToken;
    }

    public String getThirdPartyRequestToken() {
        return thirdPartyRequestToken;
    }

    public void setThirdPartyRequestToken(String thirdPartyRequestToken) {
        this.thirdPartyRequestToken = thirdPartyRequestToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private CreateAccountData(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.bio = in.readString();
        this.photoUri = in.readParcelable(Uri.class.getClassLoader());
        this.identityProvider = IdentityProvider.fromValue(in.readString());
        this.thirdPartyAccessToken = in.readString();
        this.thirdPartyRequestToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.bio);
        dest.writeParcelable(this.photoUri, 0);
        dest.writeString(this.identityProvider.toValue());
        dest.writeString(this.thirdPartyAccessToken);
        dest.writeString(this.thirdPartyRequestToken);
    }

    public static final Parcelable.Creator<CreateAccountData> CREATOR
            = new Parcelable.Creator<CreateAccountData>() {
        public CreateAccountData createFromParcel(Parcel in) {
            return new CreateAccountData(in);
        }

        public CreateAccountData[] newArray(int size) {
            return new CreateAccountData[size];
        }
    };

    public static class Builder {
        private final CreateAccountData createAccountData = new CreateAccountData();

        public Builder setFirstName(String firstName) {
            createAccountData.setFirstName(firstName);
            return this;
        }

        public Builder setLastName(String lastName) {
            createAccountData.setLastName(lastName);
            return this;
        }

        public Builder setBio(String bio) {
            createAccountData.setBio(bio);
            return this;
        }

        public Builder setPhotoUri(Uri photoUri) {
            createAccountData.setPhotoUri(photoUri);
            return this;
        }

        public Builder setIdentityProvider(IdentityProvider identityProvider) {
            createAccountData.setIdentityProvider(identityProvider);
            return this;
        }

        public Builder setThirdPartyAccessToken(String thirdPartyAccessToken) {
            createAccountData.setThirdPartyAccessToken(thirdPartyAccessToken);
            return this;
        }

        public Builder setThirdPartyRequestToken(String thirdPartyRequestToken) {
            createAccountData.setThirdPartyRequestToken(thirdPartyRequestToken);
            return this;
        }

        public CreateAccountData build() {
            return createAccountData;
        }
    }
}
