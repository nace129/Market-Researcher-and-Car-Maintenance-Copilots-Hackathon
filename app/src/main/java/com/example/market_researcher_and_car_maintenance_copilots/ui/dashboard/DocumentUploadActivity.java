package com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.market_researcher_and_car_maintenance_copilots.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DocumentUploadActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    private TextView pdfStatus;
    private EditText inputQuestion;
    private TextView tvAnswer;
    private String extractedText = "";
    private GeminiLLM geminiLLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        pdfStatus = findViewById(R.id.pdf_status);
        inputQuestion = findViewById(R.id.input_question);
        tvAnswer = findViewById(R.id.tv_answer);
        geminiLLM = new GeminiLLM(this);

        Button btnUpload = findViewById(R.id.btn_upload);
        Button btnAsk = findViewById(R.id.btn_ask);

        btnUpload.setOnClickListener(v -> pickPDF());
        btnAsk.setOnClickListener(v -> {
            String question = inputQuestion.getText().toString().trim();
            if (!extractedText.isEmpty() && !question.isEmpty()) {
                String answer = geminiLLM.generateAnswer(extractedText, question);
                tvAnswer.setText(answer);
            } else {
                tvAnswer.setText("Please upload a PDF and ask a valid question.");
            }
        });
    }

    private void pickPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                //extractedText = PDFUtils.extractTextFromPDF(inputStream);
//                extractedText = PDFUtils.extractTextFromPDF(this, inputStream);
//                pdfStatus.setText("PDF uploaded and text extracted.");
                assert uri != null;
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                extractedText = PDFUtils.extractTextFromPDF(this, inputStream);
                pdfStatus.setText("PDF uploaded and text extracted.");
                reader.close();
                extractedText = builder.toString();

            } catch (Exception e) {
                pdfStatus.setText("Failed to read PDF.");
                e.printStackTrace();
            }
        }
    }
}
//public class DocumentUploadActivity extends AppCompatActivity {
//
//    private static final int PICK_FILE_REQUEST_CODE = 1001;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_document_upload);
//
//        Button uploadButton = findViewById(R.id.btn_upload);
//        uploadButton.setOnClickListener(view -> openFileChooser());
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("*/*");  // you can limit to "application/pdf" or "text/plain" as needed
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
//            if(data != null) {
//                Uri fileUri = data.getData();
//                String text = extractTextFromUri(fileUri);
//                // Save the text in your local storage / database
//                DocumentDatabaseHelper dbHelper = new DocumentDatabaseHelper(this);
//                dbHelper.insertDocument(text);
//                Toast.makeText(this, "Document uploaded and processed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private String extractTextFromUri(Uri uri) {
//        StringBuilder stringBuilder = new StringBuilder();
//        try (InputStream is = getContentResolver().openInputStream(uri);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//            String line;
//            while((line = reader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//        } catch (Exception e) {
//            Log.e("DocumentUpload", "Error reading file", e);
//        }
//        return stringBuilder.toString();
//    }
//}

