package com.ldb.vocabulary2.android.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by lsp on 2016/9/22.
 */
public class NetworkRequestViaVolley implements BaseNetworkRequest {

    private static final String TAG = NetworkRequestViaVolley.class.getSimpleName();

//    private Context mContext;
//    private RequestQueue mQueue;
//    private ImageLoader imageLoader;
//
//    public NetworkRequestViaVolley(Context context){
//        mContext = context;
//        mQueue = Volley.newRequestQueue(context);
//        imageLoader = new ImageLoader(mQueue, new BitmapCache());
//    }

    @Override
    public void stringRequest(@NonNull Context context, String url, final RequestCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(TAG, s);
                callback.onResult(true, s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                Log.d(TAG, volleyError.getMessage());
                callback.onResult(false, volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    @Override
    public void imageRequest(@NonNull Context context, String url, ImageView imageView, int maxWidth, int maxHeight,
                             int defaultImage, int errorImage) {
        // approach 1 use ImageRequest
//        imageRequestBase(context, url, imageView, maxWidth, maxHeight, errorImage);

        // approach 2 use ImageLoader
        imageRequestByImageLoader(context, url, imageView, maxWidth, maxHeight, defaultImage, errorImage);

        // approach 3 use NetworkImageView
//        imageRequestByNetworkImageView(context, url, (NetworkImageView) imageView, defaultImage, errorImage);

    }

    /**
     * 使用 ImageRequest
     * @param context
     * @param url
     * @param imageView
     * @param maxWidth
     * @param maxHeight
     * @param errorImage
     */
    public void imageRequestBase(Context context, String url, final ImageView imageView, int maxWidth, int maxHeight,
                                 final int errorImage) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // TODO 删除日志
                        Log.d(TAG, "imageRequestBase onResponse");
                        imageView.setImageBitmap(response);
                    }
                }, maxWidth, maxHeight,
                /**
                 * 指定图片的颜色属性，Bitmap.Config下的几个常量都可以在这里使用，
                 * 其中ARGB_8888可以展示最好的颜色属性，每个图片像素占据4个字节的大小，
                 * 而RGB_565则表示每个图片像素占据2个字节大小
                 */
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO 删除日志
                        Log.d(TAG, "imageRequestBase onErrorResponse: " + error.getMessage(), error);
                        // TODO 指定默认图片
                        imageView.setImageResource(errorImage);
                    }
                });
        mQueue.add(imageRequest);
    }

    /**
     * 使用ImageLoader
     * @param context
     * @param url
     * @param imageView
     * @param maxWidth
     * @param maxHeight
     * @param defaultImage
     * @param errorImage
     */
    public void imageRequestByImageLoader(Context context, String url, final ImageView imageView,
                                          int maxWidth, int maxHeight, final int defaultImage,
                                          final int errorImage){
        // TODO 局部变量，这样缓存用不上了，由于context不好处理，暂时先这样。在DataSource做缓存吧。
        RequestQueue mQueue = Volley.newRequestQueue(context);
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener listener =
                ImageLoader.getImageListener(imageView, defaultImage, errorImage);
        // TODO 删除日志
        Log.d(TAG, "imageRequestByImageLoader");
        imageLoader.get(url,listener, maxWidth, maxHeight);
    }

    /**
     * 使用NetworkImageView
     * @param context
     * @param url
     * @param imageView
     * @param defaultImage
     * @param errorImage
     */
    public void imageRequestByNetworkImageView(Context context, String url, final NetworkImageView imageView,
                                          final int defaultImage, final int errorImage){
        RequestQueue mQueue = Volley.newRequestQueue(context);
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        imageView.setDefaultImageResId(defaultImage);
        imageView.setErrorImageResId(errorImage);
        // TODO 删除日志
        Log.d(TAG, "imageRequestByNetworkImageView");
        imageView.setImageUrl(url, imageLoader);
    }

    @Override
    public void postRequest(@NonNull Context context, String url, List<PostParam> postParams, RequestCallback callback) {
        // TODO Volley实现post
    }

    /**
     * 图片缓存
     */
    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            // 10M
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            // TODO 删除日志
            Log.d(TAG, "getBitmap:" + mCache.size());
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
            Log.d(TAG, url + " putBitmap:" + mCache.size());
        }

    }
}
