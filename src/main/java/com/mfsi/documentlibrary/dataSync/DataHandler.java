package com.mfsi.documentlibrary.dataSync;


import android.util.Log;

import com.mfsi.documentlibrary.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Bhaskar Pande on 1/24/2017.
 */
public class DataHandler {


    /**
     * Reads an inputStream.
     *
     * @param inputStream InputStream that needs to be read;
     * @return String the data read as String
     */
    public static String readAsString(InputStream inputStream) {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        FileUtils.writeTo(result, inputStream);
        return result.toString();

    }

    /***
     * Write from String data to the OutputStream.
     *
     * @param outputStream
     * @param data the data to be written
     * @return the success/failure
     */
    public static boolean writeFromString(OutputStream outputStream, String data) {
        boolean success = false;
        try {
            outputStream.write(data.getBytes());
            success = true;
        } catch (NullPointerException | IOException exception) {
            exception.printStackTrace();
        }
        return success;
    }

    /***
     * Write from File to the OutputStream.
     *
     * @param outputStream
     * @param data the data to be written
     * @return the success/failure
     */
    public static boolean writeFromFile(OutputStream outputStream, File data) {

        boolean success = false;
        try {
            FileInputStream fileInputStream = new FileInputStream(data);
            success = FileUtils.writeTo(outputStream, fileInputStream);
        } catch (NullPointerException | IOException exception) {
            exception.printStackTrace();
        }
        return success;

    }

    /***
     * Write data from InputStream to the file at the specified destination.
     *
     * @param inputStream
     * @param destination the absolute path of a file.
     * @return the destination of the file if the process succeeds else null.
     */
    public static String readAsFile(InputStream inputStream, String destination) {

        String result = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(destination);
            boolean success = FileUtils.writeTo(fileOutputStream, inputStream);
            File file = new File(destination);
            if (success && file.exists() && file.length() > 0) {
                result = destination;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }


}
