package com.mfsi.documentlibrary.dataSync;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Used To make a GET request For Resource Download.
 * Created by Bhaskar Pande on 11/5/2016.
 */
public class ServerRequest implements Serializable{

    private String mContentType;
    private String mUrl;
    private String mPathToPersistResult;
    private boolean mDownloadToDisk;
    private String mAuthorizationHeader;

    private String mDownloadIdentifier;


    public void setAuthorizationHeader(String authHeader){
        mAuthorizationHeader = authHeader;
    }

    public String getAuthorizationHeader(){
        return mAuthorizationHeader;
    }

    public String getDownloadIdentifier() {
        return mDownloadIdentifier;
    }

    public void setDownloadIdentifier(String identifier){
        mDownloadIdentifier = identifier;
    }


    public String getPathToPersistResult() {
        return mPathToPersistResult;
    }

    public void setPathToPersistResult(String pathToPersistResult) {
        this.mPathToPersistResult = pathToPersistResult;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType){
        mContentType = contentType;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url){
        mUrl = url;
    }

    public void setDownloadToDisk(boolean downloadToDisk) {
        this.mDownloadToDisk = downloadToDisk;
    }

    public boolean getDownloadToDisk(){
        return mDownloadToDisk;
    }

}
