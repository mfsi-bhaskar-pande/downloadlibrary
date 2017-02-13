package com.mfsi.documentlibrary.resProcessor.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Helper Class for Image Processing
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class ImageProcessing {


    /**
     * retrieves a Bitmap
     * @param filePath The absolute path of the file
     * @param imageDetails the processing details
     * @return a WeakReference<Bitmap> or null in case of failure
     */
    public static WeakReference<Bitmap> getBitmap(String filePath, ImageRequiredType imageDetails) {

        Point targetDims = imageDetails.getTargetDims();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = (int) calculateScaleFactor(options.outHeight, options.outWidth, targetDims);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        WeakReference<Bitmap> result = bitmap != null ? new WeakReference<>(bitmap) : null;


        return result;


    }

    /**
     * Calculate the factor by which the Image Resource be sampled.
     * @param actualHeight The Actual Height of the downloaded Resource
     * @param actualWidth the Actual Width of the Downloaded Resource
     * @param targetDims the destination dimension of the resource.
     *
     * @return the factor
     */
    public static float calculateScaleFactor(int actualHeight, int actualWidth, Point targetDims) {

        float scale = 1.0f;
        if (actualHeight <= actualWidth && targetDims.y > 0) {

            float targetHeight = targetDims.y;
            scale = (actualHeight / targetHeight);

        } else if (targetDims.x > 0) {
            float targetWidth = targetDims.x;
            scale = (actualWidth / targetWidth);
        }
        return scale;


    }




}
