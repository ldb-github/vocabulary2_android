package com.ldb.vocabulary2.android.network;

import android.util.Base64;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;

/**
 * TODO 需要更加完善才好
 * Created by lsp on 2016/9/28.
 */
public class NetworkRequestByPost {
    private static final String CRLF = "\r\n";
    private static final String CHARSET = "UTF-8";

    public void postRequest(final String urlStr, final List<PostParam> postParams,
                            final BaseNetworkRequest.RequestCallback callback) {
        new Thread(new Runnable()  {
            @Override
            public void run() {
                try {
                    request(urlStr, postParams, callback);
                }catch (IOException e){
                    // TODO 这里需要更加完善的错误提示
                    callback.onResult(false, "服务请求出错");
                }
            }
        }).start();
    }

    private void request(String urlStr, List<PostParam> postParams, BaseNetworkRequest.RequestCallback callback)
            throws IOException {
        DataOutputStream requestData = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection
                    = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            // Create a random boundary string
            Random random = new Random();
            byte[] randomBytes = new byte[16];
            random.nextBytes(randomBytes);
            String boundary = Base64.
                    encodeToString(randomBytes, Base64.NO_WRAP);

            // Set the HTTP headers
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            // Open connection to server...
            OutputStream outputStream = urlConnection.getOutputStream();
            requestData = new DataOutputStream(outputStream);

            boolean isFirst = true;
            for(PostParam postParam : postParams) {
//                if(isFirst){
//                    isFirst = false;
//                }else{
//                    requestData.write((CRLF).getBytes(CHARSET));
//                }
                // Write boundary indicating start of one parameter
                requestData.write(("--" + boundary + CRLF).getBytes(CHARSET));
                if(postParam.isFile()) {
                    // Write the field name and filename
                    requestData.write(("Content-Disposition: form-data; name=\"" +
                            postParam.getFieldName() + "\"; filename=\"" +
                            postParam.getFileName() + "\"" + CRLF).getBytes(CHARSET));
                    // Write the mime type of the file
                    requestData.write(("Content-Type: " + postParam.getMimeType() + CRLF).getBytes(CHARSET));
                    // Write an empty line
                    requestData.write((CRLF).getBytes(CHARSET));
                    // Open file for reading
                    FileInputStream fileInput = new FileInputStream(postParam.getData());
                    // Read the local file and write to the server in one loop
                    int bytesRead;
                    byte[] buffer = new byte[8192];
                    while ((bytesRead = fileInput.read(buffer)) != -1) {
                        requestData.write(buffer, 0, bytesRead);
                    }
                    // Change the line indicating the end of this file
                    requestData.write((CRLF).getBytes(CHARSET));
                }else{
                    // Write the field name
                    requestData.write(("Content-Disposition: form-data; name=\"" +
                            postParam.getFieldName()  + "\"" + CRLF).getBytes(CHARSET));
                    // Write an empty line
                    requestData.write((CRLF).getBytes(CHARSET));
                    // Write the field value and change the line
                    requestData.write((postParam.getValue() + CRLF).getBytes(CHARSET));
                }
            }
            // Write boundary indicating end of this post
            requestData.write(("--" + boundary + "--" + CRLF).getBytes(CHARSET));
            requestData.flush();
            if(urlConnection.getResponseCode() == 200){
                callback.onResult(true, urlConnection.getResponseMessage());
            }else{
                callback.onResult(false, urlConnection.getResponseMessage());
            }
        }finally {
            if(requestData != null){
                requestData.close();
            }
        }
    }

//    for(PostParam postParam : postParams) {
//        // Write boundary indicating start of this file
//        requestData.writeBytes("--" + boundary + CRLF);
//        if(postParam.isFile()) {
//            // Write the field name and filename
//            requestData.writeBytes("Content-Disposition: form-data; name=\"" +
//                    postParam.getFieldName() + "\"; filename=\"" +
//                    postParam.getFileName() + "\"" + CRLF);
//            // Write the mime type of the file
//            requestData.writeBytes("Content-Type: " + postParam.getMimeType() + CRLF);
//            // Write an empty line
//            requestData.writeBytes(CRLF);
//            // Open file for reading
//            FileInputStream fileInput = new FileInputStream(postParam.getData());
//            // Read the local file and write to the server in one loop
//            int bytesRead;
//            byte[] buffer = new byte[8192];
//            while ((bytesRead = fileInput.read(buffer)) != -1) {
//                requestData.write(buffer, 0, bytesRead);
//            }
//            requestData.writeBytes(CRLF);
//        }else{
//            // Write the field name
//            requestData.writeBytes("Content-Disposition: form-data; name=\"" +
//                    postParam.getFieldName()  + "\"" + CRLF);
//            // Write an empty line
//            requestData.writeBytes(CRLF);
//            // Write the field value
//            requestData.writeBytes(postParam.getValue() + CRLF);
//        }
//    }
//    // Write an empty line
//    requestData.writeBytes(CRLF);
//    // Write boundary indicating end of this post
//    requestData.writeBytes("--" + boundary + "--" + CRLF);
//    requestData.flush();
}
