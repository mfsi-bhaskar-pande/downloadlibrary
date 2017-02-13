package com.mfsi.documentlibrary.model;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class AppLruCache{

    private static final int cacheSize = 4 * 1024 * 1024; // 4MiB
    private static AppLruCache sAppLruCache;

    private AppLruCache(){

    }

    public static AppLruCache getInstance(){

        if(sAppLruCache == null){
            sAppLruCache = new AppLruCache();
        }
        return sAppLruCache;

    }

    private LruCache<String, Object> mLruCache = new LruCache<String , Object>(cacheSize){

        @Override
        protected int sizeOf(String key, Object value) {

            int size = 0;
            if(value instanceof String){
               size = ((String) value).length();
            }else if(value instanceof Bitmap){
                size = ((Bitmap) value).getByteCount();
            }
            return size;
        }
    };

    private boolean isSupportedDataType(Object value){

        return value != null &&
                ((value instanceof String) || (value instanceof Bitmap));

    }

    public Object getFromCache(String key){

        synchronized (mLruCache){
          return mLruCache.get(key);
        }
    }


    public Object removeFromCache(String key){
        synchronized (mLruCache){
            return mLruCache.remove(key);
        }
    }





    public void addToCache(String key, Object value){
        synchronized (mLruCache){
            if(isSupportedDataType(value)) {
                mLruCache.put(key, value);
            }
        }
    }

}
