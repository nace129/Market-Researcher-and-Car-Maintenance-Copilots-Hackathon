package com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.example.market_researcher_and_car_maintenance_copilots.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeminiLLM {

    private static final String TAG = "GeminiLLM";
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private String apiKey;

    public GeminiLLM(Context context) {
        // Get API Key from resources
        this.apiKey = context.getString(R.string.gemini_api_key);

        // For demo: allow network access on main thread (Not recommended in production!)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void initialize() {
        Log.d(TAG, "Gemini model ready.");
    }

    public String generateAnswer(String contextText, String question) {
        HttpURLConnection conn = null;
        try {
            // Create prompt for Gemini
            String prompt = "Context:\n" + contextText + "\n\nQuestion: " + question + "\nAnswer:";

            // JSON request body
//            JSONObject requestBody = new JSONObject();
//            JSONArray contents = new JSONArray();
//            JSONObject part = new JSONObject();
//            part.put("text", prompt);
//            JSONObject content = new JSONObject();
//            content.put("parts", new JSONArray().put(part));
//            contents.put(content);
//            requestBody.put("contents", contents);

            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", prompt);
            JSONObject content = new JSONObject();
            content.put("parts", new JSONArray().put(part));
            contents.put(content);
            requestBody.put("contents", contents);


            // Create HTTP connection
            URL url = new URL(GEMINI_ENDPOINT + "?key=" + apiKey);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Send request
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes("UTF-8"));
            os.close();

            if (conn.getResponseCode() == 429) {
                return "Gemini API rate Quota Exceeded";
            }

            // Read response
            Scanner in = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (in.hasNext()) {
                response.append(in.nextLine());
            }
            in.close();

            // Parse JSON response
//            JSONObject jsonResponse = new JSONObject(response.toString());
//            JSONArray candidates = jsonResponse.getJSONArray("candidates");
//            JSONObject firstCandidate = candidates.getJSONObject(0);
//            JSONObject output = firstCandidate.getJSONArray("content").getJSONObject(0);
//            JSONArray parts = output.getJSONArray("parts");
//            return parts.getJSONObject(0).getString("text");
try{
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject output = firstCandidate.getJSONObject("content");  // ✅ it's an object, not an array
            JSONArray parts = output.getJSONArray("parts");
            return parts.getJSONObject(0).getString("text");
} catch (Exception e) {
    Log.e(TAG, "Failed to parse Gemini response", e);
    return "Error parsing Gemini response.";
}


        } catch (Exception e) {
            Log.e(TAG, "Error calling Gemini API", e);
            InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                Scanner errorScanner = new Scanner(errorStream);
                StringBuilder errorResponse = new StringBuilder();
                while (errorScanner.hasNextLine()) {
                    errorResponse.append(errorScanner.nextLine());
                }
                errorScanner.close();
                Log.e(TAG, "Gemini API Error: " + errorResponse.toString());
            }

            return "Error generating answer. Please check logs.";
        }
    }
}

