package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.exception.StatusException;
import com.microsoft.embeddedsocial.server.model.account.LinkThirdPartyRequest;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Sends a link user third party account request to the server.
 */
public class LinkUserThirdPartyAccountWorker extends Worker {
    public static final String SOCIAL_NETWORK_ACCOUNT = "socialNetworkAccount";

    public LinkUserThirdPartyAccountWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        final SocialNetworkAccount account = WorkerHelper.deserialize(getInputData().getString(SOCIAL_NETWORK_ACCOUNT));

        IAccountService service = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
        LinkThirdPartyRequest linkUserThirdPartyAccountRequest = new LinkThirdPartyRequest(
                account.getAccountType(),
                account.getThirdPartyAccessToken());

        account.clearTokens();
        try {
            service.linkUserThirdPartyAccount(linkUserThirdPartyAccountRequest);
            EventBus.post(LinkUserThirdPartyAccountEvent.createLinkEvent(account));
            return Result.success();
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            LinkUserThirdPartyAccountEvent event;
            // Notify the handler that the request failed
            if (e instanceof StatusException) {
                event = LinkUserThirdPartyAccountEvent.createLinkEvent(account, e.getMessage(),
                        ((StatusException)e).getStatusCode());
            } else {
                event = LinkUserThirdPartyAccountEvent.createLinkEvent(account, e.getMessage());
            }
            EventBus.post(event);
            return Result.failure();
        }
    }
}
