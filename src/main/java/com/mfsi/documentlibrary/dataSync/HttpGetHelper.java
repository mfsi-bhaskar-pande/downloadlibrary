package com.mfsi.documentlibrary.dataSync;

import android.text.TextUtils;
import android.util.Log;

import com.mfsi.documentlibrary.utils.FileUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Helper Class to retrieve data from server
 * Created by Bhaskar Pande on 11/5/2016.
 */
public class HttpGetHelper implements SyncConstants {

    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * retrieves (GET) resources from Sever
     *
     * @param serverRequest {@link ServerRequest} object
     * @return {@link ServerResponse} object. Will Not be null
     */
    public static ServerResponse getFromServer(ServerRequest serverRequest) {


        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setUniqueIdentifier(serverRequest.getDownloadIdentifier());
        serverResponse.setContentType(serverRequest.getContentType());

        try {

            URL url = new URL(serverRequest.getUrl());
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestProperty("Authorization",serverRequest.getAuthorizationHeader());
            httpUrlConnection.setRequestProperty("Accept", serverRequest.getContentType());
            httpUrlConnection.setConnectTimeout(DEFAULT_TIMEOUT);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.connect();

            int responseCode = httpUrlConnection.getResponseCode();
            Log.d("RESPONSE STATUS", "HTTP: " + responseCode);
            handleInputStream(serverResponse,serverRequest, httpUrlConnection);
            serverResponse.setResponseCode(httpUrlConnection.getResponseCode());


        } catch (NullPointerException | IOException exception) {
            exception.printStackTrace();
            Log.d("RESPONSE STATUS", "HTTP: " + exception.getMessage());

        }
        return serverResponse;
    }


    private static String getContentType(HttpURLConnection httpURLConnection){
        String type = httpURLConnection.getContentType();
        if(type == null){
            type = "";
        }
        return type;
    }

    /***
     * handles server response, by reading and saving it in appropriate format.
     */
    private static void handleInputStream(ServerResponse serverResponse,ServerRequest serverRequest,
                                          HttpURLConnection httpUrlConnection) throws IOException {

        int responseCode = httpUrlConnection.getResponseCode();
        String contentType = serverRequest.getContentType();
        if(TextUtils.isEmpty(contentType)) {
          contentType =  getContentType(httpUrlConnection);
        }

        boolean saveOnDisk = serverRequest.getDownloadToDisk();

        if (responseCode == HTTP_OK) {
            InputStream inputStream = httpUrlConnection.getInputStream();
            String path = serverRequest.getPathToPersistResult();
            String response = getResponse(inputStream,contentType ,saveOnDisk, path);
            serverResponse.setResponseResult(response);
        } else {
            InputStream inputStream = httpUrlConnection.getErrorStream();
            String response = getResponse(inputStream, contentType,saveOnDisk, null);
            serverResponse.setResponseResult(response);
        }
        serverResponse.setResponseCode(httpUrlConnection.getResponseCode());
        serverResponse.setContentType(contentType);
        serverResponse.setDiskDownloadRequested(saveOnDisk);
    }

    private static String getResponse(InputStream inputStream, String contentType, boolean saveOnDisk, String file){

        String result;
        switch (contentType) {
            case CONTENT_TYPE_JPEG:
            case CONTENT_TYPE_AUDIO_MPEG:
            case CONTENT_TYPE_VIDEO_MPEG:
                result = DataHandler.readAsFile(inputStream,file);
                break;
            case CONTENT_TYPE_JSON:
            default:
                result = saveOnDisk ? DataHandler.readAsFile(inputStream, file) :
                        DataHandler.readAsString(inputStream);
                break;
        }
        return result;
    }

}
