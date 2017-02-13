package com.mfsi.documentlibrary.utils;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper Class for File IO.
 * Created by Bhaskar Pande on 1/25/2017.
 */
public class FileUtils {


    public static String returnExternalFilePath(Context context, String parentDirectory, String fileName) {
        String filePath = context.getExternalFilesDir(null) + File.separator + parentDirectory;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator + fileName;
    }

    public static String returnMd5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] dataBytes = value.getBytes();
            md.update(dataBytes, 0, dataBytes.length);
            byte[] mdBytes = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdBytes.length; i++) {
                sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    /***
     * Reads a File as String
     * @param filePath the absolute Path of the file
     * @return file data as String
     */
    public static String readAsString(String filePath) {

        String json = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            boolean success = writeTo(byteArrayOutputStream, fileInputStream);
            if (success) {
                json = byteArrayOutputStream.toString();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return json;
    }

    /**
     * Transfers Data from InputStream to OutputStream
     * @param outputStream the OutputStream
     * @param inputStream the InputStream
     * @return true if successful.
     */
    public static boolean writeTo(OutputStream outputStream, InputStream inputStream) {

        boolean success = false;
        try {
            byte[] byteArray = new byte[4096];
            int bytesRead = 0;
            try {
                while ((bytesRead = inputStream.read(byteArray)) != -1) {
                    outputStream.write(byteArray, 0, bytesRead);
                }
                success = true;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (NullPointerException | IOException exception) {
            exception.printStackTrace();
        }
        return success;
    }

}
