/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.embeddedsocial.test;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.Reason;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.CommentFeedType;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.IReportService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.auth.AuthenticationResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.RemoveCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.RemoveReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.UpdateTopicRequest;
import com.microsoft.embeddedsocial.server.model.like.AddLikeRequest;
import com.microsoft.embeddedsocial.server.model.like.GetLikeFeedRequest;
import com.microsoft.embeddedsocial.server.model.like.RemoveLikeRequest;
import com.microsoft.embeddedsocial.server.model.pin.AddPinRequest;
import com.microsoft.embeddedsocial.server.model.pin.GetPinFeedRequest;
import com.microsoft.embeddedsocial.server.model.pin.RemovePinRequest;
import com.microsoft.embeddedsocial.server.model.report.ReportContentRequest;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;

import java.util.List;

public class ContentTest extends BaseRestServicesTest {

    private IContentService contentService;
    private IReportService reportService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentService = getServiceProvider().getContentService();
        reportService = getServiceProvider().getReportService();
    }

    public void testTopic() throws Exception {

        final AuthenticationResponse authenticationResponse = createRandomUser();
        String topicHandle = addTopic(authenticationResponse);
        assertNotNull("topic handle is empty", topicHandle);

        UpdateTopicRequest updateTopicRequest
            = new UpdateTopicRequest(topicHandle, "updated title", "updated text", "updatedCategory");
        contentService.updateTopic(prepareUserRequest(updateTopicRequest, authenticationResponse));

        GetTopicRequest getTopicRequest
                = prepareUserRequest(new GetTopicRequest(topicHandle), authenticationResponse);
        GetTopicResponse getTopicResponse = contentService.getTopic(getTopicRequest);
        TopicView topic = getTopicResponse.getTopic();
        assertNotNull("retrieved topic is empty", topic);
        assertNotNull("retrieved topic handle is empty", topic.getHandle());
        assertNotNull("topic title is empty", topic.getTopicTitle());
        assertNotNull("topic text is empty", topic.getTopicText());

        final GetTopicFeedRequest topicFeedRequest = new GetTopicFeedRequest(TopicFeedType.FOLLOWING_RECENT);

        TopicsListResponse topicFeed
            = contentService.getTopicFeed(prepareUserRequest(topicFeedRequest, authenticationResponse));
        assertNotNull("topic list is empty", topicFeed.getData());
        assertTrue("topic feed has no topics", topicFeed.getData().size() > 0);

        removeTopic(authenticationResponse, topicHandle);
    }

    public void testComment() throws Exception {
        AuthenticationResponse authenticationResponse = createRandomUser();
        String topicHandle = addTopic(authenticationResponse);
        String commentHandle = addComment(authenticationResponse, topicHandle);
        assertNotNull("comment handle is empty", commentHandle);
        delay();

        GetCommentRequest getCommentRequest = new GetCommentRequest(commentHandle);
        GetCommentResponse getCommentResponse
            = contentService.getComment(prepareUserRequest(getCommentRequest, authenticationResponse));
        CommentView comment = getCommentResponse.getComment();
        assertNotNull("retrieved comment is empty", comment);
        assertNotNull("retrieved topic handle is empty", comment.getHandle());
        assertNotNull("retrieved comment handle is empty", comment.getHandle());
        assertNotNull("retrieved comment text is empty", comment.getCommentText());

        GetCommentFeedRequest getCommentFeedRequest = new GetCommentFeedRequest(CommentFeedType.RECENT, topicHandle);

        GetCommentFeedResponse getCommentFeedResponse
            = contentService.getCommentFeed(prepareUserRequest(getCommentFeedRequest, authenticationResponse));
        List<CommentView> comments = getCommentFeedResponse.getData();
        assertNotNull("comment feed is empty", comments);
        assertTrue("comment feed is empty", comments.size() > 0);

        removeComment(authenticationResponse, commentHandle);

        try {
            removeTopic(authenticationResponse, topicHandle);
        } catch (NetworkRequestException e) {
            //ignoring in this test
        }
    }

    public void testReply() throws Exception {
        AuthenticationResponse authenticationResponse = createRandomUser();
        String topicHandle = addTopic(authenticationResponse);
        String commentHandle = addComment(authenticationResponse, topicHandle);

        AddReplyRequest addReplyRequest = new AddReplyRequest(commentHandle, "Test reply");
        AddReplyResponse addReplyResponse
            = contentService.addReply(prepareUserRequest(addReplyRequest, authenticationResponse));
        String replyHandle = addReplyResponse.getReplyHandle();
        assertNotNull("reply handle is empty", replyHandle);
        delay();

        GetReplyRequest getReplyRequest = new GetReplyRequest(replyHandle);
        GetReplyResponse replyResponse
            = contentService.getReply(prepareUserRequest(getReplyRequest, authenticationResponse));
        ReplyView reply = replyResponse.getReply();
        assertNotNull("retrieved reply is empty", reply);
        assertNotNull("comment handle is empty", reply.getCommentHandle());
        assertNotNull("reply handle is empty", reply.getReplyHandle());
        assertNotNull("reply text is empty", reply.getReplyText());

        GetReplyFeedRequest getReplyFeedRequest = new GetReplyFeedRequest(commentHandle);
        GetReplyFeedResponse replyFeedReponse
            = contentService.getReplyFeed(prepareUserRequest(getReplyFeedRequest, authenticationResponse));
        List<ReplyView> replies = replyFeedReponse.getData();
        assertNotNull("replies feed is empty", replies);
        assertTrue("replies feed is empty", replies.size() > 0);

        RemoveReplyRequest removeReplyRequest = new RemoveReplyRequest(replyHandle);
        contentService.removeReply(prepareUserRequest(removeReplyRequest, authenticationResponse));

        try {
            removeComment(authenticationResponse, commentHandle);
            removeTopic(authenticationResponse, topicHandle);
        } catch (NetworkRequestException e) {
            //ignoring in this test
        }
    }

    public void testLike() throws Exception {
        AuthenticationResponse authenticationResponse = createRandomUser();
        String topicHandle = addTopic(authenticationResponse);
        try {
            AddLikeRequest addLikeRequest = new AddLikeRequest(topicHandle, ContentType.TOPIC);
            contentService.addLike(addLikeRequest);
            delay();
            GetLikeFeedRequest getLikeFeedRequest = new GetLikeFeedRequest(topicHandle, ContentType.TOPIC);
            UsersListResponse likeFeed = contentService.getLikeFeed(prepareUserRequest(getLikeFeedRequest, authenticationResponse));
            assertNotNull("like feed is empty", likeFeed);
            assertTrue("no users in like feed", likeFeed.getData().size() > 0);

            RemoveLikeRequest removeLikeRequest = new RemoveLikeRequest(topicHandle, ContentType.TOPIC);
            contentService.removeLike(removeLikeRequest);
        } finally {
            try {
                removeTopic(authenticationResponse, topicHandle);
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public void testPin() throws Exception {
        AuthenticationResponse authenticationResponse = createRandomUser();
        String topicHandle = addTopic(authenticationResponse);
        try {
            AddPinRequest addPinRequest = new AddPinRequest(topicHandle);
            contentService.addPin(prepareUserRequest(addPinRequest, authenticationResponse));
            delay();
            GetPinFeedRequest getPinFeedRequest = new GetPinFeedRequest();
            TopicsListResponse pinFeedResponse
                = contentService.getPinFeed(prepareUserRequest(getPinFeedRequest, authenticationResponse));
            List<TopicView> pinTopics = pinFeedResponse.getData();
            assertNotNull("like feed is empty", pinTopics);
            DebugLog.i("users liked: " + pinTopics.size());
            assertTrue("no users in like feed", pinTopics.size() > 0);
            RemovePinRequest removePinRequest = new RemovePinRequest(topicHandle);
            contentService.removePin(removePinRequest);
        } finally {
            try {
                removeTopic(authenticationResponse, topicHandle);
            } catch (Exception e) {
                //ignore
            }
        }
    }

    public void testReportContent() throws Exception {
        AuthenticationResponse authenticationResponse = createRandomUser();
        String topicHandle = addTopic(authenticationResponse);
        try {
            ReportContentRequest reportContentRequest
                = new ReportContentRequest(ContentType.TOPIC, topicHandle, Reason.OTHER);
            reportService.reportContent(prepareUserRequest(reportContentRequest, authenticationResponse));
        } finally {
            try {
                removeTopic(authenticationResponse, topicHandle);
            } catch (Exception e) {
                //ignore
            }
        }
    }

    private String addComment(AuthenticationResponse authenticationResponse, String topicHandle)
        throws NetworkRequestException {
        AddCommentRequest addCommentRequest = new AddCommentRequest(topicHandle, "Test Comment");
        AddCommentResponse addCommentResponse
            = contentService.addComment(prepareUserRequest(addCommentRequest, authenticationResponse));
        return addCommentResponse.getCommentHandle();
    }

    private void removeComment(AuthenticationResponse authenticationResponse, String commentHandle)
        throws NetworkRequestException {
        RemoveCommentRequest removeCommentRequest = new RemoveCommentRequest(commentHandle);
        contentService.removeComment(prepareUserRequest(removeCommentRequest, authenticationResponse));
    }


}
