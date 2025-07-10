package com.journal.smartjournaling;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AIResponseHelper {

    private static final String TAG = "AIResponseHelper";

    public static Map<String, String> loadResponses(Context context) {
        Map<String, String> responses = new HashMap<>();

        try {
            InputStream is = context.getAssets().open("responses.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(json);
            Iterator<String> keys = obj.keys();

            while (keys.hasNext()) {
                String keyword = keys.next();
                String message = obj.getString(keyword);
                responses.put(keyword.toLowerCase(), message);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading responses: " + e.getMessage());
        }

        return responses;
    }

    public static String getMatchingResponse(String entryText, Map<String, String> responses) {
        String text = entryText.toLowerCase();

        for (Map.Entry<String, String> entry : responses.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
