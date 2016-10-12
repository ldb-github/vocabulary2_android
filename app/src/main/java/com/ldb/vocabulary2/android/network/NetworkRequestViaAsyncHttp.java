package com.ldb.vocabulary2.android.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by lsp on 2016/9/29.
 */
public class NetworkRequestViaAsyncHttp implements BaseNetworkRequest {

    // TODO 官方推荐使用static，但是就这个项目而言目前的框架不太好处理，暂时先这样吧
    AsyncHttpClient mClient = new AsyncHttpClient();

    @Override
    public void stringRequest(@NonNull Context context, String url, RequestCallback callback) {
        // TODO 用AsyncHttpClient实现字符类型请求
    }

    @Override
    public void imageRequest(@NonNull Context context, String url, ImageView imageView,
                             int maxWidth, int maxHeight, int defaultImage, int errorImage) {
        // TODO 用AsyncHttpClient实现类型图片请求
    }

    @Override
    public void postRequest(@NonNull Context context, String url, List<PostParam> postParams,
                            final RequestCallback callback) {
        RequestParams params = new RequestParams();
        try {
            for (PostParam param : postParams) {
                if (param.isFile()) {
                    params.put(param.getFieldName(), param.getData());
                } else {
                    params.put(param.getFieldName(), param.getValue());
                }
            }
        }catch (FileNotFoundException fnf){
            callback.onResult(false, "File not found.");
            return;
        }

        // 没有文件的时候竟然是以普通参数的形式post，有文件的时候才一表单的形式post
        mClient.post(context, url, params, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String content) {
                callback.onResult(true, content);
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                callback.onResult(false, error.toString());
            }
        });


    }
}
