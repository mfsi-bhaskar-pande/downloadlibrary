package com.mfsi.documentlibrary.dataSync;

/**
 * The response from Server to a Download Request made.
 * Created by Bhaskar Pande on 11/5/2016.
 */
public class ServerResponse {

    private int mResponseCode;
    private String mResponseResult;
    private String mUniqueIdentifier;
    private String mContentType;
    private boolean mIsOnDisk;

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        this.mContentType = contentType;
    }

    public void setUniqueIdentifier(String uniqueId){
        this.mUniqueIdentifier = uniqueId;
    }

    public String getUniqueIdentifier(){
        return mUniqueIdentifier;
    }

    public int getResponseCode(){
        return mResponseCode;
    }
    public void setResponseCode(int responseCode) {
        mResponseCode = responseCode;
    }

    @Override
    public String toString() {
        return "HTTP STATUS CODE: "+mResponseCode;
    }

    public void setResponseResult(String result) {
        mResponseResult = result;
    }

    public String getResponseResult() {
        return mResponseResult;
    }


    public void setDiskDownloadRequested(boolean downloadedToDisk) {
        this.mIsOnDisk = downloadedToDisk;
    }

    public boolean isDiskDownloadRequested(){
        return mIsOnDisk;
    }
}
