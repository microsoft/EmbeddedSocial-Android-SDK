/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.autorest.models.AppCompactView;
import com.microsoft.socialplus.autorest.models.LinkedAccountView;
import com.microsoft.socialplus.data.storage.request.wrapper.account.GetMyProfileWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.account.GetUserAccountWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.account.GetUserProfileWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.account.GetUserWrapper;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.account.CreateUserRequest;
import com.microsoft.socialplus.server.model.account.DeleteUserRequest;
import com.microsoft.socialplus.server.model.account.GetLinkedAccountsRequest;
import com.microsoft.socialplus.server.model.account.GetMyAppsRequest;
import com.microsoft.socialplus.server.model.account.GetMyProfileRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.account.GetUserProfileRequest;
import com.microsoft.socialplus.server.model.account.GetUserProfileResponse;
import com.microsoft.socialplus.server.model.account.GetUserRequest;
import com.microsoft.socialplus.server.model.account.GetUserResponse;
import com.microsoft.socialplus.server.model.account.LinkThirdPartyRequest;
import com.microsoft.socialplus.server.model.account.UnlinkUserThirdPartyAccountRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserPhotoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserPublicAccountInfoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserVisibilityRequest;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;

import java.util.List;

import retrofit2.Response;

/**
 * Provides transparent cache functionality on top of {@linkplain IAccountService}.
 */
public class AccountServiceCachingWrapper implements IAccountService {

	private final GetUserWrapper getUserWrapper;
	private final GetUserProfileWrapper getUserProfileWrapper;
	private final GetMyProfileWrapper getMyProfileWrapper;
	private final GetUserAccountWrapper getUserAccountWrapper;

	public AccountServiceCachingWrapper() {
		UserCache userCache = new UserCache();
		getUserWrapper = new GetUserWrapper(this::getUser, userCache);
		getUserProfileWrapper = new GetUserProfileWrapper(this::getUserProfile, userCache);
		getMyProfileWrapper = new GetMyProfileWrapper(this::getMyProfile, userCache);
		getUserAccountWrapper = new GetUserAccountWrapper(this::getUserAccount, userCache);
	}

	@Override
	public AuthenticationResponse createUser(CreateUserRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response deleteUser(DeleteUserRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public GetUserResponse getUser(GetUserRequest request) throws NetworkRequestException {
		return getUserWrapper.getResponse(request);
	}

	@Override
	public GetUserAccountResponse getUserAccount(GetUserAccountRequest request) throws NetworkRequestException {
		return getUserAccountWrapper.getResponse(request);
	}

	@Override
	public GetUserProfileResponse getUserProfile(GetUserProfileRequest request) throws NetworkRequestException {
		return getUserProfileWrapper.getResponse(request);
	}

	@Override
	public GetUserProfileResponse getMyProfile(GetMyProfileRequest request) throws NetworkRequestException {
		return getMyProfileWrapper.getResponse(request);
	}

	@Override
	public List<LinkedAccountView> getLinkedAccounts(GetLinkedAccountsRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response linkUserThirdPartyAccount(LinkThirdPartyRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response unlinkUserThirdPartyAccount(UnlinkUserThirdPartyAccountRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response updateUserPhoto(UpdateUserPhotoRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response updateUserPublicAccountInfo(UpdateUserPublicAccountInfoRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response updateUserVisibility(UpdateUserVisibilityRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public List<AppCompactView> getMyApps(GetMyAppsRequest request) throws NetworkRequestException {
		return request.send();
	}
}
