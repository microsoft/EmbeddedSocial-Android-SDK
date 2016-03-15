/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.data.storage.request.wrapper.account.GetUserAccountWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.account.GetUserProfileWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.account.GetUserWrapper;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.account.CreateUserRequest;
import com.microsoft.socialplus.server.model.account.DeleteUserRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.account.GetUserProfileRequest;
import com.microsoft.socialplus.server.model.account.GetUserProfileResponse;
import com.microsoft.socialplus.server.model.account.GetUserRequest;
import com.microsoft.socialplus.server.model.account.GetUserResponse;
import com.microsoft.socialplus.server.model.account.UnlinkUserThirdPartyAccountRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserPhotoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserPublicAccountInfoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserVisibilityRequest;
import com.microsoft.socialplus.server.model.account.UserPasswordResponse;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.SignInWithThirdPartyRequest;

import retrofit2.Response;

/**
 * Provides transparent cache functionality on top of {@linkplain IAccountService}.
 */
public class AccountServiceCachingWrapper implements IAccountService {

	private final GetUserWrapper getUserWrapper;
	private final GetUserProfileWrapper getUserProfileWrapper;
	private final GetUserAccountWrapper getUserAccountWrapper;
	private final IAccountService wrappedService;

	public AccountServiceCachingWrapper(IAccountService wrappedService) {
		this.wrappedService = wrappedService;
		UserCache userCache = new UserCache();
		getUserWrapper = new GetUserWrapper(wrappedService::getUser, userCache);
		getUserProfileWrapper = new GetUserProfileWrapper(wrappedService::getUserProfile, userCache);
		getUserAccountWrapper = new GetUserAccountWrapper(wrappedService::getUserAccount, userCache);
	}

	@Override
	public AuthenticationResponse createUser(CreateUserRequest request) throws NetworkRequestException {
		return wrappedService.createUser(request);
	}

	@Override
	public Response deleteUser(DeleteUserRequest request) throws NetworkRequestException {
		return wrappedService.deleteUser(request);
	}

	@Override
	public GetUserResponse getUser(GetUserRequest request) throws NetworkRequestException {
		return getUserWrapper.getResponse(request);
	}

	@Override
	public GetUserAccountResponse getUserAccount(UserRequest request) throws NetworkRequestException {
		return getUserAccountWrapper.getResponse(request);
	}

	@Override
	public GetUserProfileResponse getUserProfile(GetUserProfileRequest request) throws NetworkRequestException {
		return getUserProfileWrapper.getResponse(request);
	}

	@Override
	public UserPasswordResponse linkUserThirdPartyAccount(SignInWithThirdPartyRequest request) throws NetworkRequestException {
		return wrappedService.linkUserThirdPartyAccount(request);
	}

	@Override
	public UserPasswordResponse unlinkUserThirdPartyAccount(UnlinkUserThirdPartyAccountRequest request) throws NetworkRequestException {
		return wrappedService.unlinkUserThirdPartyAccount(request);
	}

	@Override
	public Response updateUserPhoto(UpdateUserPhotoRequest request) throws NetworkRequestException {
		return wrappedService.updateUserPhoto(request);
	}

	@Override
	public Response updateUserPublicAccountInfo(UpdateUserPublicAccountInfoRequest request) throws NetworkRequestException {
		return wrappedService.updateUserPublicAccountInfo(request);
	}

	@Override
	public Response updateUserVisibility(UpdateUserVisibilityRequest request) throws NetworkRequestException {
		return wrappedService.updateUserVisibility(request);
	}
}
