package com.abner.automatic.util;

import com.abner.automatic.model.ReturnData;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

public class JsonUtils {

    /**
     * Business is successful
     */
    public static String successfulJson(Object data) {
        ReturnData returnData = new ReturnData();
        returnData.setSuccess(true);
        returnData.setErrorCode(200);
        returnData.setData(data);
        return JSON.toJSONString(returnData);
    }

    /**
     * Business is failed
     */
    public static String failedJson(int code, String message) {
        ReturnData returnData = new ReturnData();
        returnData.setSuccess(false);
        returnData.setErrorCode(code);
        returnData.setErrorMsg(message);
        return JSON.toJSONString(returnData);
    }

    /**
     * Converter object to json string
     */
    public static String toJsonString(Object data) {
        return JSON.toJSONString(data);
    }

    /**
     * Parse json to object
     */
    public static <T> T parseJson(String json, Type type) {
        return JSON.parseObject(json, type);
    }
}