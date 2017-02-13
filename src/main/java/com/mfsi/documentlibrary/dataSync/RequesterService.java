package com.mfsi.documentlibrary.dataSync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * The service component that downloads the requested resource.
 * It can attempt a maximum of {@link RequesterService#THREAD_POOL_LIMIT} at a time.
 *
 * Created by Bhaskar Pande on 1/24/2017.
 */
public class RequesterService extends Service {

    private static final int THREAD_POOL_LIMIT = 50;
    private IBinder mServiceBinder = new ServiceBinder();
    private static RequestObserver sRequestObserver = new RequestObserver();
    private ExecutorService mExecutorService = null;
    private ThreadFactory mDownloadThreadFactory = null;
    /***
     * A HashMap of all Requested Downloads. They are put or removed from this list as a download is
     * requested or cancelled/completed.
     */
    private LinkedHashMap<String, DownloadThread> mRequestedDownloads = null;


    public class ServiceBinder extends Binder {
        public RequesterService getService() {
            return RequesterService.this;
        }
    }


    /***
     * Thread that downloads a resource. It returns a {@link ServerResponse} object,
     * irrespective of whether the download succeeds or fails. The thread does not download a
     * resource in case its already there. Its uses the uniqueIdentifier attribute for this.
     */
    class DownloadThread implements Callable<ServerResponse> {

        /***
         * Controller used to Cancel the download.
         */
        private Future<ServerResponse> mDownloadController;
        private ServerRequest mServerRequest;
        private RequestObserver mRequestObserver = null;
        private String mUniqueIdentifier;
        private int mRequesterCounter;

        public DownloadThread(ServerRequest serverRequest, String uniqueIdentifier) {
            mUniqueIdentifier = uniqueIdentifier;
            mServerRequest = serverRequest;
            mRequesterCounter = 1;
        }

        public void setDownloadController(Future<ServerResponse> future) {
            mDownloadController = future;

        }

        public void setRequestObserver(RequestObserver requestObserver) {
            mRequestObserver = requestObserver;
        }

        public void setRequestListener(RequestObserver.RequestListener requestListener) {
            if (requestListener != null) {
                mRequestObserver.registerListener(requestListener);
            }
        }

        public void removeRequestListener(RequestObserver.RequestListener requestListener) {
            mRequestObserver.deRegisterListener(requestListener);
        }

        private boolean isResourceAvailable() {
            String path = mServerRequest.getPathToPersistResult();
            return new File(path).exists();
        }

        private ServerResponse createResponse(){
            ServerResponse serverResponse = new ServerResponse();
            serverResponse.setDiskDownloadRequested(mServerRequest.getDownloadToDisk());
            serverResponse.setContentType(mServerRequest.getContentType());
            serverResponse.setUniqueIdentifier(mServerRequest.getDownloadIdentifier());
            serverResponse.setResponseCode(SyncConstants.HTTP_OK);
            serverResponse.setResponseResult(mServerRequest.getPathToPersistResult());
            return serverResponse;
        }

        @Override
        public ServerResponse call() throws Exception {
            ServerResponse response;
            if (isResourceAvailable()) {
                response = createResponse();
            } else {
                response = HttpGetHelper.getFromServer(mServerRequest);
            }
            mRequestObserver.notifyRequestComplete(response);
            mRequestedDownloads.remove(mUniqueIdentifier);
            mRequestObserver.clearListeners();

            return response;
        }


        public boolean cancel() {
            mRequestObserver.notifyRequestCancelled(mServerRequest);
            boolean cancelled = false;
            if (mRequesterCounter == 1 && mDownloadController != null) {
                cancelled = mDownloadController.cancel(true);
                String path = mServerRequest.getPathToPersistResult();
                new File(path).delete();
            }
            return cancelled;
        }


        public void incrementRequesterCounter() {
            mRequesterCounter++;
        }

        public void decrementRequesterCounter() {
            mRequesterCounter--;
        }
    }

    /***
     * The thread Factory that creates a thread, whenever required
     */
    class DownloadThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable runnableTask) {
            return new Thread(runnableTask);
        }
    }

    /***
     * Creates the requester service if required.
     * @param context the calling context.
     * @param connection ServiceConnection object that monitors the state of the application service.
     * @return if you are successfully bound.
     */
    public static boolean bindRequestService(Context context, ServiceConnection connection) {

        Intent intent = new Intent(context, RequesterService.class);
        return context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /***
     * Requests to unbind from the service.
     * @param context the calling context.
     * @param connection ServiceConnection object that  monitors the state of the application service.
     */
    public static void unBindRequest(Context context, ServiceConnection connection) {
        if (connection != null) {
            try {
                context.unbindService(connection);
            }catch (IllegalArgumentException exception){
                exception.printStackTrace();
            }
        }
    }


    /***
     * Returns the IBinder object that exposes an API to communicate with the Service Component.
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public void onCreate() {
        mRequestedDownloads = new LinkedHashMap<>();
        mDownloadThreadFactory = new DownloadThreadFactory();
        mExecutorService = Executors.newFixedThreadPool(THREAD_POOL_LIMIT, mDownloadThreadFactory);
        super.onCreate();
    }

    /***
     * Cancels Resource Download.
     * @param serverRequest the request made to cancel a download
     * @param listener the listener that will be notified about the Result.
     */
    public void cancelResourceDownload(ServerRequest serverRequest, RequestObserver.RequestListener listener) {

        if (serverRequest != null) {
            String downloadIdentifier = serverRequest.getDownloadIdentifier();

            synchronized (mRequestedDownloads) {
                DownloadThread downloadThread = mRequestedDownloads.get(downloadIdentifier);
                if (downloadIdentifier != null && downloadThread != null) {
                    downloadThread.decrementRequesterCounter();
                    boolean cancelled = downloadThread.cancel();
                    downloadThread.removeRequestListener(listener);
                    if (cancelled) {
                        mRequestedDownloads.remove(downloadIdentifier);
                    }
                }
            }
        }
    }

    /***
     * Initiate Resource Download.
     * @param serverRequest the request made for a download
     * @param listener the listener that will be notified about the Result.
     */
    public void downloadResource(ServerRequest serverRequest, RequestObserver.RequestListener listener) {

        downloadResource(serverRequest, new RequestObserver(), listener);

    }


    /***
     * Initiate ResourceDownload
     * @param serverRequest the request made for Download
     * @param requestObserver the object that monitors the download.
     * @param listener the listener that will be notified by the observer on the download progress
     *                 whenever it finds it suitable
     */
    private void downloadResource(ServerRequest serverRequest, RequestObserver requestObserver,
                                 RequestObserver.RequestListener listener) {

        if (serverRequest != null) {

            String downloadIdentifier = serverRequest.getDownloadIdentifier();
            synchronized (mRequestedDownloads) {
                if (!mRequestedDownloads.containsKey(downloadIdentifier)) {
                    DownloadThread downloadThread = new DownloadThread(serverRequest, downloadIdentifier);
                    downloadThread.setRequestObserver(requestObserver);
                    downloadThread.setRequestListener(listener);
                    Future<ServerResponse> download = mExecutorService.submit(downloadThread);
                    downloadThread.setDownloadController(download);
                    mRequestedDownloads.put(downloadIdentifier, downloadThread);
                } else {
                    DownloadThread thread = mRequestedDownloads.get(downloadIdentifier);
                    thread.setRequestListener(listener);
                    thread.incrementRequesterCounter();
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
