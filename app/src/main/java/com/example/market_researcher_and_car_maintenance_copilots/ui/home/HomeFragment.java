package com.example.market_researcher_and_car_maintenance_copilots.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.market_researcher_and_car_maintenance_copilots.R;
import com.example.market_researcher_and_car_maintenance_copilots.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.util.Log;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private LineChart lineChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lineChart = new LineChart(getContext());
        binding.chartContainer.addView(lineChart);

        // Set up click listeners for buttons
        binding.btnGdp.setOnClickListener(v -> fetchData("NY.GDP.MKTP.KD.ZG"));
        binding.btnCo2.setOnClickListener(v -> fetchData("EN.GHG.CO2.ZG.AR5"));
        binding.btnAgri.setOnClickListener(v -> fetchData("AG.LND.AGRI.ZS"));

        return root;
    }

    private void fetchData(String indicator) {
        String url = "https://api.worldbank.org/v2/country/WLD/indicator/" + indicator + "?format=json";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API_Response", "Received data: " + response.toString());

                        if (response.length() > 1) {
                            JSONArray dataArray = response.getJSONArray(1);
                            List<Entry> entries = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataPoint = dataArray.getJSONObject(i);
                                double value = dataPoint.isNull("value") ? 0 : dataPoint.getDouble("value");
                                int year = dataPoint.getInt("date");

                                // Log the parsed values to check for issues
                                Log.d("API_DataPoint", "Year: " + year + ", Value: " + value);

                                // Round the year to an integer and the value to 2 decimal places
                                int x = year; // Year as integer
                                float y = (float) Math.round(value * 100) / 100; // Round to 2 decimal places

                                // Ensure non-negative values before adding to entries
                                if (y >= 0) {
                                    entries.add(new Entry(x, y));
                                } else {
                                    Log.w("API_DataPoint", "Invalid data point with negative value: " + y);
                                }
                            }

                            if (entries.isEmpty()) {
                                Log.w("API_Response", "No valid data found for indicator: " + indicator);
                                return;
                            }

                            // Sort the entries by year
                            Collections.sort(entries, new Comparator<Entry>() {
                                @Override
                                public int compare(Entry e1, Entry e2) {
                                    return Float.compare(e1.getX(), e2.getX());
                                }
                            });

                            // Create LineDataSet and set properties
                            LineDataSet dataSet = new LineDataSet(entries, indicator);
                            dataSet.setColor(R.color.teal_200);
                            dataSet.setValueTextColor(R.color.black);
                            dataSet.setDrawCircles(false); // Disable points (circles)
                            dataSet.setDrawHighlightIndicators(true); // Enable highlight on hover

                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);

                            Description description = new Description();
                            description.setText("World Bank Data for " + indicator);
                            lineChart.setDescription(description);

                            // Set custom x-axis and y-axis ranges based on the indicator
                            XAxis xAxis = lineChart.getXAxis();
                            YAxis yAxis = lineChart.getAxisLeft();

//                             Configure x-axis for each indicator
                            if (indicator.equals("NY.GDP.MKTP.KD.ZG")) {
                                // For GDP, x-axis range: 1960 - 2023
                                xAxis.setAxisMinimum(1972f);
                                xAxis.setAxisMaximum(2020f);
                                yAxis.setAxisMinimum(0f);
                            } else if (indicator.equals("EN.GHG.CO2.ZG.AR5")) {
                                // For CO2, x-axis range: 1991 - 2023
                                xAxis.setAxisMinimum(1991f);
                                xAxis.setAxisMaximum(2022f);
                                yAxis.setAxisMinimum(0f);
                            } else if (indicator.equals("AG.LND.AGRI.ZS")) {
                                // For Agri Land, x-axis range: 1961 - 2022
                                xAxis.setAxisMinimum(1971f);
                                xAxis.setAxisMaximum(2022f);

                                // For Agri Land, y-axis should start from 35
                                yAxis.setAxisMinimum(35f);
                            }

                            // Redraw the chart
                            lineChart.invalidate();
                            Log.d("API_DataPoint", "Line Chart:" + lineChart);
                        } else {
                            Log.w("API_Response", "No data available in response for indicator: " + indicator);
                        }

                    } catch (Exception e) {
                        Log.e("API_Error", "Error parsing data: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("API_Request_Error", "API request failed: " + error.getMessage(), error);
                    Toast.makeText(getContext(), "API request failed", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}