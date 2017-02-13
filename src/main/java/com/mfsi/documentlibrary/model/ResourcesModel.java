package com.mfsi.documentlibrary.model;

import java.io.File;

/**
 * The Model Class that serves as the interface to retrieve Data.
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class ResourcesModel {

    private static ResourcesModel sResourcesModel;
    private AppLruCache mAppLruCache;


    private ResourcesModel(){
        mAppLruCache= AppLruCache.getInstance();
    }

    public static ResourcesModel getInstance(){
        if(sResourcesModel == null){
            sResourcesModel = new ResourcesModel();
        }
        return sResourcesModel;
    }


    public Object getFromCache(String resourceIdentifier) {
        return mAppLruCache.getFromCache(resourceIdentifier);
    }


    public void addToCache(String resUid, Object result) {
        mAppLruCache.addToCache(resUid, result);
    }
}
