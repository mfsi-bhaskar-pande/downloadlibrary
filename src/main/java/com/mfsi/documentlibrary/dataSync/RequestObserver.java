package com.mfsi.documentlibrary.dataSync;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Observes the download of a resource.
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class RequestObserver {

    private ArrayList<RequestListener> mListeners = new ArrayList<>();

    public void clearListeners() {
        mListeners.clear();
    }

    /***
     * Any class that wishes to be notified about the Download State muct implement this Interface
     */
    public interface RequestListener {
        /**
         * Callback when a server Request Completes
         * @param serverResponse The ServerResponse returned both in case of success and failure.
         */
        void requestComplete(ServerResponse serverResponse);

        /**
         * Callback when a Download Request is Cancelled
         * @param serverRequest the ServerRequest cancelled.
         */
        void requestCancelled(ServerRequest serverRequest);
    }


    public void registerListener(RequestListener listener){
        if(!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void deRegisterListener(RequestListener listener){
        mListeners.remove(listener);
    }


    public void notifyRequestComplete(ServerResponse serverResponse){

        Iterator<RequestListener> iterator = mListeners.iterator();
        while (iterator.hasNext()){
            RequestListener listener = iterator.next();
            if(listener != null){
                listener.requestComplete(serverResponse);
            }else{
                iterator.remove();
            }
        }
    }

    public void notifyRequestCancelled(ServerRequest serverRequest){

        Iterator<RequestListener> iterator = mListeners.iterator();

        while (iterator.hasNext()){
            RequestListener listener = iterator.next();
            if(listener != null){
                listener.requestCancelled(serverRequest);
            }else{
                iterator.remove();
            }
        }
    }
}
