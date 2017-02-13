package com.mfsi.documentlibrary.resProcessor.json;

import com.mfsi.documentlibrary.resProcessor.ResProcessingDetails;

import java.lang.reflect.Type;

/**
 * Specifies the details that would be used as reference to processing a JSON resource.
 * Created by Bhaskar Pande on 1/26/2017.
 */
public class JsonRequiredType implements ResProcessingDetails {

    private Type mType;
    private String mContentType;
    private boolean mCache;


    public Type getClassType() {
        return mType;
    }

    public void setClassType(Type classType) {
        mType = classType;
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
}
