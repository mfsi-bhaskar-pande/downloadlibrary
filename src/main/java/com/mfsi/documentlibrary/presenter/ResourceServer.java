package com.mfsi.documentlibrary.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.mfsi.documentlibrary.dataSync.RequestObserver;
import com.mfsi.documentlibrary.dataSync.RequesterService;
import com.mfsi.documentlibrary.dataSync.ServerRequest;
import com.mfsi.documentlibrary.dataSync.ServerResponse;
import com.mfsi.documentlibrary.dataSync.SyncConstants;
import com.mfsi.documentlibrary.model.AppResource;
import com.mfsi.documentlibrary.model.ResourcesModel;
import com.mfsi.documentlibrary.resProcessor.ResProcessingDetails;
import com.mfsi.documentlibrary.resProcessor.image.ImageProcessing;
import com.mfsi.documentlibrary.resProcessor.image.ImageRequiredType;
import com.mfsi.documentlibrary.resProcessor.json.JsonProcessing;
import com.mfsi.documentlibrary.resProcessor.json.JsonRequiredType;
import com.mfsi.documentlibrary.utils.FileUtils;
import com.mfsi.documentlibrary.client.ResourceClient;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Serves(Handles) download/cancellation requests.
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class ResourceServer implements ServiceConnection, RequestObserver.RequestListener {

    private ResourcesModel mResourcesModel = ResourcesModel.getInstance();
    private ResourceClient mResourceView = null;
    private ServerRequest mServerRequest = null;
    private AppResource mAppResource = null;
    private Context mContext;
    private Handler mHandler = new Handler();
    private RequesterService mRequesterService;


    private ResourceServer(AppResource appResource, Context context) {

        mAppResource = appResource;
        mContext = context;
        RequesterService.bindRequestService(context, this);
    }

    /**
     * returns an instance of the ResourceServer that shall manage the download of the given app resource.
     *
     * @param appResource the AppResource to download
     * @param context     the calling Context
     * @return the ResourceServer instance
     */
    public static ResourceServer getServer(AppResource appResource, Context context) {

        return new ResourceServer(appResource, context);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        /*this ensures that this runs just once in the lifetime of the object*/
        if (mRequesterService == null && mServerRequest != null) {
            mRequesterService = ((RequesterService.ServiceBinder) service).getService();
            Log.i("==","=======================STARTDOWNLOAD==================");
            mRequesterService.downloadResource(mServerRequest, this);
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * Set the Client to this Server. It must Implement ResourceClient.
     *
     * @param resView the ResourceClient implementation
     */
    public void setClient(ResourceClient resView) {
        mResourceView = resView;
    }


    public void getImageResource() {
        getResource(mAppResource, mContext, null);
    }


    public void getBitmapJpg() {
        getResource(mAppResource, mContext, SyncConstants.CONTENT_TYPE_JPEG);
    }


    public void getStringResponse() {
        getResource(mAppResource, mContext, null);
    }

    public void getStringJsonResponse() {
        getResource(mAppResource, mContext, SyncConstants.CONTENT_TYPE_JSON);
    }

    public void getXmlResponse() {
        getResource(mAppResource, mContext, SyncConstants.CONTENT_TYPE_XML);
    }

    public void cancelDownload() {
        if (mRequesterService != null) {
            mRequesterService.cancelResourceDownload(mServerRequest, this);
        }

    }

    /**
     * retrieve the Resource
     *
     * @param resource    AppResource instance
     * @param context     the calling Context
     * @param contentType the Content type of the AppResource to be downloaded.
     */
    private void getResource(AppResource resource, Context context, String contentType) {

        final String resourceIdentifier = !TextUtils.isEmpty(resource.getUid()) ? resource.getUid() :
                FileUtils.returnMd5(resource.getDownloadUrl());

        /*test if it is already in Cache*/
        final Object object = mResourcesModel.getFromCache(resourceIdentifier);

        Log.i("===","================================"+object);

        if (object == null) {

            String fileExtension = resource.getFileExtension();
            String downloadDestination = resource.getDownloadDestination();

            String filePath = mAppResource.downloadResource() && !TextUtils.isEmpty(downloadDestination) ?
                    downloadDestination : FileUtils.returnExternalFilePath(context, "Temp", resourceIdentifier + fileExtension);

            /*ensure that there is no resource already present at the download location.*/
            if (!mAppResource.downloadResource()) {
                new File(filePath).delete();
            }

            /* Initialize  the Server Request*/
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.setUrl(resource.getDownloadUrl());
            serverRequest.setContentType(contentType);
            serverRequest.setDownloadToDisk(mAppResource.downloadResource());
            serverRequest.setPathToPersistResult(filePath);
            serverRequest.setDownloadIdentifier(resourceIdentifier);
            serverRequest.setAuthorizationHeader(resource.getAuthKey());
            mServerRequest = serverRequest;
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mResourceView.setData(resourceIdentifier, object);
                }
            });
        }
    }

    @Override
    public void requestCancelled(final ServerRequest serverRequest) {

        if (mRequesterService != null) {
            RequesterService.unBindRequest(mContext, this);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mResourceView != null) {
                    mResourceView.downloadCancelled(serverRequest.getDownloadIdentifier());
                    mResourceView = null;
                    mRequesterService = null;
                    mAppResource = null;
                    mServerRequest = null;
                }
            }
        });

    }

    private void handleBitmapReqResponse(ServerResponse serverResponse) {

        final String resUid = serverResponse.getUniqueIdentifier();
        ResProcessingDetails processingDetails = mResourceView.getResProcessingDetails(resUid);
        String responseResult = serverResponse.getResponseResult();

        WeakReference<Bitmap> result = null;

        if (serverResponse.getResponseCode() == SyncConstants.HTTP_OK && processingDetails != null) {
            result = ImageProcessing.getBitmap(responseResult, (ImageRequiredType) processingDetails);
            if (processingDetails.requiresCache()) {
                if (result != null && result.get() != null) {
                    mResourcesModel.addToCache(resUid, result.get());
                }
            }
        }

        if (!serverResponse.isDiskDownloadRequested()) {
            File file = new File(responseResult);
            file.delete();
        }

        final Bitmap responseBitmap = result != null ? result.get() : null;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mResourceView != null) {
                    mResourceView.setData(resUid, responseBitmap);
                }
            }
        });

    }

    private void handleJsonReqResponse(ServerResponse serverResponse) {

        final String resUid = serverResponse.getUniqueIdentifier();
        ResProcessingDetails processingDetails = mResourceView.getResProcessingDetails(resUid);
        String responseResult = serverResponse.getResponseResult();

        Object data = serverResponse.isDiskDownloadRequested() ?
                JsonProcessing.getFileData(responseResult, (JsonRequiredType) processingDetails) :
                JsonProcessing.getData(responseResult, (JsonRequiredType) processingDetails);

        if (processingDetails.requiresCache()) {
            mResourcesModel.addToCache(resUid, data);
        }
        mResourceView.setData(resUid, data);
    }


    @Override
    public void requestComplete(ServerResponse serverResponse) {

        if (serverResponse != null) {
            String type = serverResponse.getContentType();
            switch (type) {
                case SyncConstants.CONTENT_TYPE_JPEG:
                    handleBitmapReqResponse(serverResponse);
                    break;
                case SyncConstants.CONTENT_TYPE_JSON:
                    handleJsonReqResponse(serverResponse);
                    break;
            }
        } else {

            /*SERVER RESPONSE SHOULD NOT BE NULL*/

        }
        RequesterService.unBindRequest(mContext, this);
    }

}
