package com.endlesslist.utility;

import org.json.simple.JSONObject;

public class ResponseTools {
    public static String plainSuccess(){
        JSONObject result = new JSONObject();
        result.put("result", true);
        return result.toString();
    }
    
    public static String plainError(String errorMessage){
        JSONObject result = new JSONObject();
        result.put("result", false);
        result.put("error", errorMessage);
        return result.toString();
    }
}
