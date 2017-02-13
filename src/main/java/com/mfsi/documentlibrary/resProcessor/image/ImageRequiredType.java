package com.mfsi.documentlibrary.resProcessor.image;

import android.graphics.Point;

import com.mfsi.documentlibrary.resProcessor.ResProcessingDetails;


/**
 * Specifies the details that would be used as reference to processing an image resource.
 * Created by Bhaskar Pande on 1/26/2017.
 */
public class ImageRequiredType implements ResProcessingDetails {

    private Point mTargetDimensions;
    private String mContentType;
    private boolean mCache;


    public Point getTargetDims() {
        return mTargetDimensions;
    }

    @Override
    public String getContentType() {
        return mContentType;
    }

    @Override
    public void setContentType(String contentType) {
       mContentType = contentType;
    }

    @Override
    public boolean requiresCache() {
        return mCache;
    }

    @Override
    public void setCache(boolean cache) {
       mCache = cache;
    }

    public void setTargetDims(Point targetDims) {
        mTargetDimensions = targetDims;
    }
}
