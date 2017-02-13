package com.mfsi.documentlibrary.model;

import com.mfsi.documentlibrary.dataSync.SyncConstants;

import java.security.cert.Extension;

/**
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class AppResource {


    private String mUid;
    private String mFileExtension;
    private String mDownloadUrl;
    private String mDownloadDestination;
    private String mResourceTitle;
    private boolean mDownloadResource;
    private String mAccessAuthorizationKey;

    private AppResource(String resIdentifier, String localPath, String resName) {
        mUid = resIdentifier;
        mDownloadDestination = localPath;
        mResourceTitle = resName;
        mDownloadResource = true;
    }

    /**
     * Returns an AppResource instance for a resource locally available.
     *
     * @param resIdentifier a unique Identifier assigned to the resource for identification
     * @param localPath     the loaction of the resource
     * @param resName       the Name of tHe resource.
     * @return The AppResource instance
     */
    public static AppResource instantiateLocalResource(String resIdentifier, String localPath, String resName) {
        return new AppResource(resIdentifier, localPath, resName);
    }

    /**
     * Returns an AppResource instance for a JPEG image on Web Server.
     *
     * @param resIdentifier a unique Identifier assigned to the resource for identification
     * @param downloadDest  the loaction of the resource
     * @param resName       the Name of tHe resource.
     * @param downloadRes   a boolean indicating whether the Resource needs to be downloaded and Saved.
     * @param downloadUrl   the download URL.
     * @return The AppResource instance
     */
    public static AppResource instantiateJpgWebResource(String resIdentifier, String downloadDest, String resName, boolean downloadRes,
                                                        String downloadUrl) {
        AppResource appResource = new AppResource(resIdentifier, downloadDest, resName);
        appResource.setDownload(downloadRes);
        appResource.setDownloadUrl(downloadUrl);
        appResource.setFileExtension(SyncConstants.EXTENSION_JPEG);
        return appResource;
    }

    /**
     * Returns an AppResource instance for an XML on Web Server.
     *
     * @param resIdentifier a unique Identifier assigned to the resource for identification
     * @param downloadDest  the loaction of the resource
     * @param resName       the Name of tHe resource.
     * @param downloadRes   a boolean indicating whether the Resource needs to be downloaded and saved.
     * @param downloadUrl   the download URL.
     * @return The AppResource instance
     */
    public AppResource instantiateXmlWebResource(String resIdentifier, String downloadDest, String resName, boolean downloadRes,
                                                 String downloadUrl) {
        AppResource appResource = new AppResource(resIdentifier, downloadDest, resName);
        appResource.setDownload(downloadRes);
        appResource.setDownloadUrl(downloadUrl);
        appResource.setFileExtension(SyncConstants.EXTENSION_XML);
        return appResource;

    }

    /**
     * Returns an AppResource instance for an HTML resource on Web Server.
     *
     * @param resIdentifier a unique Identifier assigned to the resource for identification
     * @param downloadDest  the loaction of the resource
     * @param resName       the Name of tHe resource.
     * @param downloadRes   a boolean indicating whether the Resource needs to be downloaded and saved.
     * @param downloadUrl   the download URL.
     * @return The AppResource instance
     */
    public AppResource instantiateHtmlWebResource(String resIdentifier, String downloadDest, String resName, boolean downloadRes,
                                                  String downloadUrl) {

        AppResource appResource = new AppResource(resIdentifier, downloadDest, resName);
        appResource.setDownload(downloadRes);
        appResource.setDownloadUrl(downloadUrl);
        appResource.setFileExtension(SyncConstants.EXTENSION_HTML);

        return appResource;

    }


    /**
     * Returns an AppResource instance for a PNG image on Web Server.
     *
     * @param resIdentifier a unique Identifier assigned to the resource for identification
     * @param downloadDest  the loaction of the resource
     * @param resName       the Name of tHe resource.
     * @param downloadRes   a boolean indicating whether the Resource needs to be downloaded and saved.
     * @param downloadUrl   the download URL.
     * @return The AppResource instance
     */
    public AppResource instantiatePngWebResource(String resIdentifier, String downloadDest, String resName, boolean downloadRes,
                                                 String downloadUrl) {

        AppResource appResource = new AppResource(resIdentifier, downloadDest, resName);
        appResource.setDownload(downloadRes);
        appResource.setDownloadUrl(downloadUrl);
        appResource.setFileExtension(SyncConstants.EXTENSION_JPEG);

        return appResource;

    }

    /**
     * Returns an AppResource instance for a JSON resource on Web Server.
     *
     * @param resIdentifier a unique Identifier assigned to the resource for identification
     * @param downloadDest  the loaction of the resource
     * @param resName       the Name of tHe resource.
     * @param downloadRes   a boolean indicating whether the Resource needs to be downloaded and saved.
     * @param downloadUrl   the download URL.
     * @return The AppResource instance
     */
    public static AppResource instantiateJsonWebResource(String resIdentifier, String downloadDest, String resName, boolean downloadRes,
                                                         String downloadUrl) {

        AppResource appResource = new AppResource(resIdentifier, downloadDest, resName);
        appResource.setDownload(downloadRes);
        appResource.setDownloadUrl(downloadUrl);
        appResource.setFileExtension(SyncConstants.EXTENSION_JSON);

        return appResource;

    }


    public String getUid() {
        return mUid;
    }

    public String getFileExtension() {
        return mFileExtension;
    }

    private void setFileExtension(String fileExt) {
        mFileExtension = fileExt;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    private void setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
    }


    public String getDownloadDestination() {
        return mDownloadDestination;
    }


    public String getResTitle() {
        return mResourceTitle;
    }

    private void setDownload(boolean download) {
        mDownloadResource = download;
    }

    public boolean downloadResource() {
        return mDownloadResource;
    }

    public void setAuthKey(String authorizationKey) {
        mAccessAuthorizationKey = authorizationKey;
    }

    public String getAuthKey() {
        return mAccessAuthorizationKey;
    }


}
