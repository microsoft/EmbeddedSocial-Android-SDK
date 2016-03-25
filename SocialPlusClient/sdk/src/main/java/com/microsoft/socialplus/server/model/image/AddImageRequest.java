/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.image;

import com.google.common.reflect.TypeToken;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.rest.ServiceResponseBuilder;
import com.microsoft.socialplus.autorest.ImagesOperations;
import com.microsoft.socialplus.autorest.SocialPlusClient;
import com.microsoft.socialplus.autorest.models.ImageType;
import com.microsoft.socialplus.autorest.models.PostImageResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Model for add image request.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AddImageRequest extends UserRequest {

	private final File image;
	private final ImageType imageType;

	public AddImageRequest(File image, ImageType imageType) {
		this.imageType = imageType;
		this.image = image;
	}

	@Override
	public String send() throws NetworkRequestException {
		ServiceResponse<PostImageResponse> serviceResponse;
		try {
			RequestBody body = RequestBody.create(MediaType.parse("image/gif"), image);
			serviceResponse = new ImagesOperationsImpl(RETROFIT, CLIENT).postImage(imageType, bearerToken, body);
		} catch (ServiceException |IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);
		System.out.println("Response: " + serviceResponse.getResponse().code());
		System.out.println(serviceResponse.getBody().getBlobHandle());
		return serviceResponse.getBody().getBlobHandle();
	}


	/**
	 * New implementation of ImagesOperations as a workaround
	 */
	public static final class ImagesOperationsImpl {
		/**
		 * The Retrofit service to perform REST calls.
		 */
		private ImagesService service;
		/**
		 * The service client containing this operation class.
		 */
		private SocialPlusClient client;

		/**
		 * Initializes an instance of ImagesOperations.
		 *
		 * @param retrofit the Retrofit instance built from a Retrofit Builder.
		 * @param client   the instance of the service client containing this operation class.
		 */
		public ImagesOperationsImpl(Retrofit retrofit, SocialPlusClient client) {
			this.service = retrofit.create(ImagesService.class);
			this.client = client;
		}

		/**
		 * The interface defining all the services for ImagesOperations to be
		 * used by Retrofit to perform actually REST calls.
		 */
		interface ImagesService {
			@Headers("Content-Type: image/gif")
			@POST("v0.2/images/{imageType}")
			Call<ResponseBody> postImage(@Path("imageType") String imageType, @Header("Authorization") String authorization, @Body RequestBody image);
		}

		/**
		 * Upload a new image.
		 * &lt;para&gt;Images will be resized. To access a resized image, append the 1 character size identifier to the blobHandle that is returned.&lt;/para&gt;
		 * &lt;para&gt;d = 25 pixels wide&lt;/para&gt;
		 * &lt;para&gt;h = 50 pixels wide&lt;/para&gt;
		 * &lt;para&gt;l = 100 pixels wide&lt;/para&gt;
		 * &lt;para&gt;p = 250 pixels wide&lt;/para&gt;
		 * &lt;para&gt;t = 500 pixels wide&lt;/para&gt;
		 * &lt;para&gt;x = 1000 pixels wide&lt;/para&gt;
		 * &lt;para&gt;ImageType.UserPhoto supports d,h,l,p,t,x&lt;/para&gt;
		 * &lt;para&gt;ImageType.ContentBlob supports d,h,l,p,t,x&lt;/para&gt;
		 * &lt;para&gt;ImageType.AppIcon supports l&lt;/para&gt;
		 * &lt;para&gt;All resized images will maintain their aspect ratio. Any orientation specified in the EXIF headers will be honored.&lt;/para&gt;.
		 *
		 * @param imageType     Image type. Possible values include: 'UserPhoto', 'ContentBlob', 'AppIcon'
		 * @param authorization Authenication (must begin with string "Bearer ")
		 * @param image         MIME encoded contents of the image
		 * @return the PostImageResponse object wrapped in {@link ServiceResponse} if successful.
		 * @throws ServiceException         exception thrown from REST call
		 * @throws IOException              exception thrown from serialization/deserialization
		 * @throws IllegalArgumentException exception thrown from invalid parameters
		 */
		public ServiceResponse<PostImageResponse> postImage(ImageType imageType, String authorization, RequestBody image) throws ServiceException, IOException, IllegalArgumentException {
			if (imageType == null) {
				throw new IllegalArgumentException("Parameter imageType is required and cannot be null.");
			}
			if (authorization == null) {
				throw new IllegalArgumentException("Parameter authorization is required and cannot be null.");
			}
			if (image == null) {
				throw new IllegalArgumentException("Parameter image is required and cannot be null.");
			}
			Call<ResponseBody> call = service.postImage(this.client.getMapperAdapter().serializeRaw(imageType), authorization, image);
			return postImageDelegate(call.execute());
		}

		private ServiceResponse<PostImageResponse> postImageDelegate(Response<ResponseBody> response) throws ServiceException, IOException, IllegalArgumentException {
			return new ServiceResponseBuilder<PostImageResponse, ServiceException>(this.client.getMapperAdapter())
					.register(201, new TypeToken<PostImageResponse>() { }.getType())
					.register(400, new TypeToken<Void>() { }.getType())
					.register(401, new TypeToken<Void>() { }.getType())
					.register(500, new TypeToken<Void>() { }.getType())
					.build(response);
		}
	}
}
