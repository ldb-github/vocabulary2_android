package com.ldb.vocabulary2.android.network;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlStr);
            urlConnection
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
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                callback.onResult(true, new String(getUrlBytes(urlConnection)));
            }else{
                callback.onResult(false, urlConnection.getResponseMessage());
            }
        }finally {
            if(requestData != null){
                requestData.close();
            }
            if(urlConnection != null ){
                urlConnection.disconnect();
            }
        }
    }

    public byte[] getUrlBytes(HttpURLConnection connection) throws IOException{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
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
