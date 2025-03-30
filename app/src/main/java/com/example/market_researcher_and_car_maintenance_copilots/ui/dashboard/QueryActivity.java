package com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.market_researcher_and_car_maintenance_copilots.R;

import java.util.List;

public class QueryActivity extends AppCompatActivity {

    private EditText etQuery;
    private Button btnAsk;
    private TextView tvAnswer;
    private DocumentDatabaseHelper dbHelper;
    private GeminiLLM geminiLLM; // our fictional LLM class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        etQuery = findViewById(R.id.et_query);
        btnAsk = findViewById(R.id.btn_ask);
        tvAnswer = findViewById(R.id.tv_answer);
        dbHelper = new DocumentDatabaseHelper(this);

        // Initialize the Gemini LLM model (assume offline initialization)
        geminiLLM = new GeminiLLM(this);
        geminiLLM.initialize();  // load the model locally

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etQuery.getText().toString();
                processQuery(query);
            }
        });
    }

    private void processQuery(String query) {
        // 1. Retrieve relevant document text
        List<String> docs = dbHelper.searchDocuments(query);
        StringBuilder contextBuilder = new StringBuilder();
        for (String doc : docs) {
            contextBuilder.append(doc).append("\n");
        }
        String context = contextBuilder.toString();
        if (context.isEmpty()) {
            tvAnswer.setText("No relevant document content found.");
            return;
        }

        // 2. Feed query and context to the LLM (RAG process)
        // This is a synchronous example – in production you would run on a background thread.
        String answer = geminiLLM.generateAnswer(context, query);
        tvAnswer.setText(answer);
    }
}

