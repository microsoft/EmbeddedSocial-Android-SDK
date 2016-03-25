package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.server.IImageService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.image.AddImageRequest;

public class ImageServiceWrapper implements IImageService{

    @Override
    public String addImage(AddImageRequest request) throws NetworkRequestException {
        return request.send();
    }
}
