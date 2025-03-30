package com.example.market_researcher_and_car_maintenance_copilots.ui.soundanalysis;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final String boundary = "volley_boundary_" + System.currentTimeMillis();
    private final Response.Listener<NetworkResponse> listener;
    private final Map<String, String> headers;
    private final Map<String, DataPart> fileParams;

    public VolleyMultipartRequest(int method, String url, Map<String, DataPart> fileParams,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.fileParams = fileParams;
        this.headers = new HashMap<>();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, DataPart> entry : fileParams.entrySet()) {
                DataPart dataPart = entry.getValue();
                writeDataToStream(bos, entry.getKey(), dataPart);
            }
            bos.write(("--" + boundary + "--\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private void writeDataToStream(ByteArrayOutputStream bos, String key, DataPart dataPart) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"; filename=\"")
                .append(dataPart.getFileName()).append("\"\r\n");
        sb.append("Content-Type: ").append(dataPart.getType()).append("\r\n\r\n");

        bos.write(sb.toString().getBytes());
        bos.write(dataPart.getContent());
        bos.write("\r\n".getBytes());
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
