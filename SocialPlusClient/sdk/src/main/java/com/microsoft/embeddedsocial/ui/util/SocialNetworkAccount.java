/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

/**
 * Model for social network data
 */
public class SocialNetworkAccount implements Parcelable {

	private final IdentityProvider identityProvider;
	private final String thirdPartyAccountHandle;
	private String thirdPartyAccessToken;
	private String thirdPartyRequestToken;
	private final String firstName;
	private final String lastName;

	public SocialNetworkAccount(IdentityProvider identityProvider, String thirdPartyAccountHandle,
								String thirdPartyAccessToken, String thirdPartyRequestToken,
								String firstName, String lastName) {
		this.identityProvider = identityProvider;
		this.thirdPartyAccountHandle = thirdPartyAccountHandle;
		this.thirdPartyAccessToken = thirdPartyAccessToken;
		this.thirdPartyRequestToken = thirdPartyRequestToken;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public SocialNetworkAccount(IdentityProvider identityProvider, String thirdPartyAccountHandle,
								String thirdPartyAccessToken, String firstName, String lastName) {
		this(identityProvider, thirdPartyAccountHandle, thirdPartyAccessToken, null, firstName, lastName);
	}

	public SocialNetworkAccount(IdentityProvider identityProvider, String thirdPartyAccountHandle,
	                            String thirdPartyAccessToken) {
		this(identityProvider, thirdPartyAccountHandle, thirdPartyAccessToken, null, null, null);
	}

	public SocialNetworkAccount(IdentityProvider identityProvider, String accessToken) {
		this(identityProvider, "", accessToken, null, null, null);
	}

	/**
	 * Creates an instance from OAuth request token and verifier pair.
	 * @param   identityProvider   type of account
	 * @param   requestToken  OAuth request token
	 * @param   oauthVerifier OAuth verifier
	 * @return  {@link SocialNetworkAccount} instance.
	 */
	public static SocialNetworkAccount fromOauthTokenAndVerifier(IdentityProvider identityProvider,
			String requestToken, String oauthVerifier) {
		return new SocialNetworkAccount(identityProvider, null, oauthVerifier, requestToken, null, null);
	}

	/**
	 * Creates an instance from OAuth access code.
	 * @param   identityProvider   type of account
	 * @param   code          OAuth authentication code
	 * @return  {@link SocialNetworkAccount} instance.
	 */
	public static SocialNetworkAccount fromOauthCode(IdentityProvider identityProvider, String code) {
		return new SocialNetworkAccount(identityProvider, "", code, null, null, null);
	}

	public IdentityProvider getAccountType() {
		return identityProvider;
	}

	public String getThirdPartyAccountHandle() {
		return thirdPartyAccountHandle;
	}

	public String getThirdPartyAccessToken() {
		return thirdPartyAccessToken;
	}

	public String getThirdPartyRequestToken() {
		return thirdPartyRequestToken;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void clearTokens() {
		this.thirdPartyAccessToken = null;
		this.thirdPartyRequestToken = null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeString(identityProvider.toValue());
		out.writeString(thirdPartyAccountHandle);
		out.writeString(thirdPartyAccessToken);
		out.writeString(thirdPartyRequestToken);
		out.writeString(firstName);
		out.writeString(lastName);
	}

	private SocialNetworkAccount(Parcel in) {
		identityProvider = IdentityProvider.fromValue(in.readString());
		thirdPartyAccountHandle = in.readString();
		thirdPartyAccessToken = in.readString();
		thirdPartyRequestToken = in.readString();
		firstName = in.readString();
		lastName = in.readString();
	}


	public static final Parcelable.Creator<SocialNetworkAccount> CREATOR
			= new Parcelable.Creator<SocialNetworkAccount>() {
		public SocialNetworkAccount createFromParcel(Parcel in) {
			return new SocialNetworkAccount(in);
		}

		public SocialNetworkAccount[] newArray(int size) {
			return new SocialNetworkAccount[size];
		}
	};
}
