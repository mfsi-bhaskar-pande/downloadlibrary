package com.mfsi.documentlibrary.client;

import android.graphics.Bitmap;

import com.mfsi.documentlibrary.resProcessor.ResProcessingDetails;


/**
 * Any class that wishes to make use of the {@link com.mfsi.documentlibrary.presenter.ResourceServer}
 * to download/get resources, must implement this interface.
 * Created by Bhaskar Pande on 1/25/2017.
 */
public interface ResourceClient {

    /***
     * gets the details as to how a given resource must be processed.
     *
     * @param resUid String uniquely Identifying the resource.
     * @return ResProcessingDetails instance giving details as to how the processing must be done
     */
    ResProcessingDetails getResProcessingDetails(String resUid);

    /***
     * callback invoked by ResourceServer to set the retrieved object to the ResourceClient
     *
     * @param resUid String uniquely identifying the resource
     * @param data   the object retrieved by ResourceServer.
     */
    void setData(String resUid, Object data);

    /***
     * callback invoked by ResourceServer after it has cancelled the download Request of s ResourceClient.
     *
     * @param uid String uniquely identifying the resource.
     */
    void downloadCancelled(String uid);
}
