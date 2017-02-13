package com.mfsi.documentlibrary.resProcessor.json;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mfsi.documentlibrary.utils.FileUtils;

import java.lang.reflect.Type;

/**
 * Helper Class for JSON Processing.
 * Created by Bhaskar Pande on 1/26/2017.
 */
public class JsonProcessing {

    /**
     * Get json data in the required Type
     * @param json the String
     * @param jsonRequiredType the {@link Type} in which the data will be returned
     * @return the Data
     */
    public static Object getData(String json, JsonRequiredType jsonRequiredType) {
        Object data = null;
        try {
            Type jsonClass = jsonRequiredType.getClassType();
            data = new Gson().fromJson(json, jsonClass);
        } catch (JsonSyntaxException exception) {
            exception.printStackTrace();
        }
        return data;
    }

    /**
     * Get json data in the required Type
     * @param filePath json file
     * @param jsonRequiredType the {@link Type} in which the data will be returned
     * @return the Data
     */
    public static Object getFileData(String filePath, JsonRequiredType jsonRequiredType) {

        String json = FileUtils.readAsString(filePath);
        return getData(json, jsonRequiredType);

    }




}
