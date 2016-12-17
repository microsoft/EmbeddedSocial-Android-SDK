/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

/**
 * User third party account linked status was changed
 */
@HandlingThread(ThreadType.MAIN)
public class LinkUserThirdPartyAccountEvent extends AbstractEvent {
	private final IdentityProvider identityProvider;
	private final SocialNetworkAccount account;
	private final State state;
	private final String error;
	private final boolean isSuccess;

	private LinkUserThirdPartyAccountEvent(SocialNetworkAccount account, boolean isSuccess, String error) {
		this.account = account;
		this.isSuccess = isSuccess;
		this.error = error;
		this.identityProvider = null;
		this.state = State.LINK;
	}

	private LinkUserThirdPartyAccountEvent(IdentityProvider identityProvider, boolean isSuccess, String error) {
		this.identityProvider = identityProvider;
		this.isSuccess = isSuccess;
		this.error = error;
		this.account = null;
		this.state = State.UNLINK;
	}

	public static LinkUserThirdPartyAccountEvent createLinkEvent(SocialNetworkAccount account) {
		return new LinkUserThirdPartyAccountEvent(account, true, "");
	}

	public static LinkUserThirdPartyAccountEvent createLinkEvent(SocialNetworkAccount account, String error) {
		return new LinkUserThirdPartyAccountEvent(account, false, error);
	}

	public static LinkUserThirdPartyAccountEvent createUnlinkEvent(IdentityProvider identityProvider) {
		return new LinkUserThirdPartyAccountEvent(identityProvider, true, "");
	}

	public static LinkUserThirdPartyAccountEvent createUnlinkEvent(IdentityProvider identityProvider, String error) {
		return new LinkUserThirdPartyAccountEvent(identityProvider, false, error);
	}

	public boolean iSuccess() {
		return isSuccess;
	}

	public String getError() {
		return error;
	}

	public IdentityProvider getIdentityProvider() {
		return identityProvider;
	}

	public SocialNetworkAccount getAccount() {
		return account;
	}

	public State getState() {
		return state;
	}

	public enum State {
		LINK,
		UNLINK
	}
}
