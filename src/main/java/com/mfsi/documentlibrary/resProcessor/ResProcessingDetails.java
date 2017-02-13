package com.mfsi.documentlibrary.resProcessor;

/**
 * Every class that  specifies the processing details for a resource must implement this interface
 * Created by Bhaskar Pande on 1/26/2017.
 */
public interface ResProcessingDetails {

    void setContentType(String contentType);

    boolean requiresCache();

    void setCache(boolean cache);

    String getContentType();


}
