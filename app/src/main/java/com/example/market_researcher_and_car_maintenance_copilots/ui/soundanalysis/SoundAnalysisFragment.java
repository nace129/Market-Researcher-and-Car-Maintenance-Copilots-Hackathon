package com.example.market_researcher_and_car_maintenance_copilots.ui.soundanalysis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.market_researcher_and_car_maintenance_copilots.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundAnalysisFragment extends Fragment {

    private static final int PICK_AUDIO_REQUEST = 1;
    private Uri audioUri;
    private TextView predictionResult;
    private RequestQueue requestQueue;

    private static final String SERVER_URL = "http://10.0.2.2:5001/predict";  // For Emulator
    // For Real Device: "http://<Your-IP-Address>:5001/predict"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sound_analysis, container, false);

        Button uploadButton = root.findViewById(R.id.uploadButton);
        Button callMLButton = root.findViewById(R.id.callMLButton);
        predictionResult = root.findViewById(R.id.predictionResult);

        requestQueue = Volley.newRequestQueue(requireContext());

        uploadButton.setOnClickListener(v -> selectAudioFile());

        callMLButton.setOnClickListener(v -> {
            if (audioUri != null) {
                uploadAudioFile(audioUri);
            } else {
                predictionResult.setText("Please upload an audio file first.");
            }
        });

        return root;
    }

    private void selectAudioFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            audioUri = data.getData();
            predictionResult.setText("Audio File Selected");
        }
    }

    private void uploadAudioFile(Uri fileUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            byte[] fileData = byteArrayOutputStream.toByteArray();
            inputStream.close();

            // Create the file params map
            Map<String, VolleyMultipartRequest.DataPart> fileParams = new HashMap<>();
            fileParams.put("file", new VolleyMultipartRequest.DataPart("audio.wav", fileData, "audio/wav"));

            // Create the request with the file params
            VolleyMultipartRequest request = new VolleyMultipartRequest(
                    Request.Method.POST,
                    SERVER_URL,
                    fileParams,
                    response -> {
                        String responseString = new String(response.data);
                        predictionResult.setText("Prediction Result: " + responseString);
                    },
                    error -> {
                        error.printStackTrace();
                        predictionResult.setText("Error: " + error.getMessage());
                    });

            requestQueue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error reading audio file.", Toast.LENGTH_SHORT).show();
        }
    }
}