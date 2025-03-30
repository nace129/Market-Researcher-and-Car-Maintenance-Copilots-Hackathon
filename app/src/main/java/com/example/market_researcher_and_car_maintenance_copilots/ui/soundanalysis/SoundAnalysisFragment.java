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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.market_researcher_and_car_maintenance_copilots.R;

import java.io.InputStream;

public class SoundAnalysisFragment extends Fragment {

    private static final int PICK_AUDIO_REQUEST = 1;
    private Uri audioUri;
    private TextView predictionResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sound_analysis, container, false);

        Button uploadButton = root.findViewById(R.id.uploadButton);
        Button callMLButton = root.findViewById(R.id.callMLButton);
        predictionResult = root.findViewById(R.id.predictionResult);

        uploadButton.setOnClickListener(v -> selectAudioFile());

        callMLButton.setOnClickListener(v -> {
            if (audioUri != null) {
                predictionResult.setText("Prediction: Engine Sound | Confidence: 95%");
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
}
