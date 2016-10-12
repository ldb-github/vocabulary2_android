package com.ldb.vocabulary2.android.data.remote;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ldb.vocabulary2.android.data.CommunicationContract;
import com.ldb.vocabulary2.android.model.DeviceInfo;
import com.ldb.vocabulary2.android.network.BaseNetworkRequest;
import com.ldb.vocabulary2.android.network.NetworkRequestByPost;
import com.ldb.vocabulary2.android.network.NetworkRequestViaAsyncHttp;
import com.ldb.vocabulary2.android.network.NetworkRequestViaVolley;
import com.ldb.vocabulary2.android.network.PostParam;
import com.ldb.vocabulary2.android.util.DeviceInfoUtil;

import java.util.List;

/**
 * 请求服务器数据，通过回调函数返回服务器传回的数据，并且不做任何处理，
 * 而是由请求者自己根据业务需求对数据进行处理。
 *
 * TODO 有几个方法还没有使用mNetworkRequest进行网络请求，需要调整
 * Created by lsp on 2016/9/17.
 */
public class RemoteDataSourceImpl implements RemoteDataSource {
    private static final String TAG = RemoteDataSourceImpl.class.getSimpleName();

    private static final Uri BASE_URI =
            Uri.parse("http://android.ldb.com:8080/vocabulary2");

    private static RemoteDataSourceImpl INSTANCE = null;

    private ImageCache mImageCache;

    private BaseNetworkRequest mNetworkRequest = new NetworkRequestViaVolley();

    private RemoteDataSourceImpl(){

    }

    public static RemoteDataSourceImpl getInstance(){
        if(INSTANCE == null){
            INSTANCE = new RemoteDataSourceImpl();
        }
        return INSTANCE;
    }

    @Override
    public boolean requestPhoneCheckCode(@NonNull Context context, String phoneNumber, String type,
                                         final BaseNetworkRequest.RequestCallback callback) {
        DeviceInfo deviceInfo = getDeviceInfo(context);
        String url = BASE_URI.buildUpon()
                .appendPath("checkcode")
                .appendQueryParameter("phonenumber", phoneNumber)
                .appendQueryParameter("type", type)
                .appendQueryParameter("source", deviceInfo.getSource())
                .appendQueryParameter("ip", deviceInfo.getIp())
                .appendQueryParameter("mac", deviceInfo.getMac())
                .appendQueryParameter("imei", deviceInfo.getImei())
                .appendQueryParameter("location", deviceInfo.getLocation())
                .build().toString();
//        request(context, url, 0, callback);
        mNetworkRequest.stringRequest(context, url, callback);
        return true;
    }

    @Override
    public String register(Context context, String phoneNumber, String checkCode,
                           BaseNetworkRequest.RequestCallback callback) {
        DeviceInfo deviceInfo = getDeviceInfo(context);
        String url = BASE_URI.buildUpon()
                .appendPath("AndroidRegister")
                .appendQueryParameter("phonenumber", phoneNumber)
                .appendQueryParameter("checkcode", checkCode)
                .appendQueryParameter("source", deviceInfo.getSource())
                .appendQueryParameter("ip", deviceInfo.getIp())
                .appendQueryParameter("mac", deviceInfo.getMac())
                .appendQueryParameter("imei", deviceInfo.getImei())
                .appendQueryParameter("location", deviceInfo.getLocation())
                .build().toString();
//        request(context, url, 0, callback);
        mNetworkRequest.stringRequest(context, url, callback);
        return null;
    }

    @Override
    public String login(Context context, String phoneNumber, String checkCode,
                        BaseNetworkRequest.RequestCallback callback) {
        DeviceInfo deviceInfo = getDeviceInfo(context);
        String url = BASE_URI.buildUpon()
                .appendPath("AndroidLogin")
                .appendQueryParameter("phonenumber", phoneNumber)
                .appendQueryParameter("checkcode", checkCode)
                .appendQueryParameter("source", deviceInfo.getSource())
                .appendQueryParameter("ip", deviceInfo.getIp())
                .appendQueryParameter("mac", deviceInfo.getMac())
                .appendQueryParameter("imei", deviceInfo.getImei())
                .appendQueryParameter("location", deviceInfo.getLocation())
                .build().toString();
//        request(context, url, 0, callback);
        mNetworkRequest.stringRequest(context, url, callback);
        return null;
    }

    @Override
    public String getCategoryList(Context context, int page, String sort, String sortType,
                                  BaseNetworkRequest.RequestCallback callback) {
        Uri.Builder builder = BASE_URI.buildUpon()
                .appendPath(CommunicationContract.KEY_CATEGORY_PATH)
                .appendPath(CommunicationContract.METHOD_LIST)
                .appendQueryParameter(CommunicationContract.KEY_PAGE, page + "");
        if (sort != null && !sort.trim().isEmpty()) {
            builder.appendQueryParameter(CommunicationContract.KEY_SORT, sort);
        }
        if (sortType != null && !sortType.trim().isEmpty()) {
            builder.appendQueryParameter(CommunicationContract.KEY_SORT_TYPE, sortType);
        }
        String url = builder.build().toString();
        mNetworkRequest.stringRequest(context, url, callback);
        return null;
    }

    @Override
    public void getImageForView(@NonNull Context context, String url, ImageView imageView, int maxWidth,
                                int maxHeight, int defaultImage, int errorImage) {
        if(mImageCache == null){

        }
        mNetworkRequest.imageRequest(context, url, imageView, maxWidth, maxHeight, defaultImage, errorImage);
    }

    @Override
    public String getVocabularyList(@NonNull Context context, String categoryId, int page,
                                    String secondLan, BaseNetworkRequest.RequestCallback callback) {
        String url = BASE_URI.buildUpon()
                .appendPath(CommunicationContract.KEY_CATEGORY_PATH)
                .appendPath(CommunicationContract.METHOD_LIST_VOCABULARY)
                .appendQueryParameter(CommunicationContract.KEY_CATEGORY_ID, categoryId)
                .appendQueryParameter(CommunicationContract.KEY_PAGE, page + "")
                .appendQueryParameter(CommunicationContract.KEY_VOCABULARY_SECOND_LANGUAGE, secondLan)
                .build().toString();
        mNetworkRequest.stringRequest(context, url, callback);
        return null;
    }

    @Override
    public void postCategory(@NonNull Context context, List<PostParam> category,
                             BaseNetworkRequest.RequestCallback callback) {
        String url = BASE_URI.buildUpon()
                .appendPath(CommunicationContract.KEY_CATEGORY_PATH)
                .appendPath(CommunicationContract.METHOD_ADD)
                .build().toString();
//        NetworkRequestViaAsyncHttp postClient = new NetworkRequestViaAsyncHttp();
//        postClient.postRequest(context, url, category, callback);

        // 使用自定义的一个处理post请求的类
        NetworkRequestByPost postClient = new NetworkRequestByPost();
        postClient.postRequest(url, category, callback);
    }

    @Override
    public void postVocabulary(@NonNull Context context, List<PostParam> vocabulary,
                               BaseNetworkRequest.RequestCallback callback) {
        String url = BASE_URI.buildUpon()
                .appendPath(CommunicationContract.KEY_CATEGORY_PATH)
                .appendPath(CommunicationContract.METHOD_ADD_VOCABULARY)
                .build().toString();
        NetworkRequestViaAsyncHttp postClient = new NetworkRequestViaAsyncHttp();
        postClient.postRequest(context, url, vocabulary, callback);

        // 使用自定义的一个处理post请求的类
//        NetworkRequestByPost postClient = new NetworkRequestByPost();
//        postClient.postRequest(url, vocabulary, callback);
    }

    /**
     * 获取设备信息
     * @param context
     * @return
     */
    public static DeviceInfo getDeviceInfo(@NonNull Context context){
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSource(DeviceInfoUtil.isTablet(context));
        deviceInfo.setIp(DeviceInfoUtil.getDeviceInfo(context,
                DeviceInfoUtil.Device.DEVICE_IP_ADDRESS_IPV4));
        deviceInfo.setMac(DeviceInfoUtil.getDeviceInfo(context,
                DeviceInfoUtil.Device.DEVICE_MAC_ADDRESS));
        deviceInfo.setImei(DeviceInfoUtil.getDeviceInfo(context, DeviceInfoUtil.Device.DEVICE_ID));
        deviceInfo.setLocation("Unknown");
//        Log.d(TAG, "deviceInfo: " + deviceInfo.toString());
//        Log.d(TAG, "device: " + DeviceUtil.getBuildInfo());
//        Log.d(TAG, "device: " + DeviceUtil.getInfosAboutDevice(context));
        return deviceInfo;
    }

    /**
     * 进行网络请求
     * @param context
     * @param url
     * @param requestCode
     * @param callback
     */
    public void request(@NonNull Context context, String url, int requestCode,
                        final BaseNetworkRequest.RequestCallback callback){
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
                Log.e(TAG, volleyError.getMessage(), volleyError);
                callback.onResult(false, volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    public class ImageCache {
        private LruCache<String, Bitmap> mCache;

        public ImageCache() {
            // 10M
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        public Bitmap getBitmap(String url) {
            // TODO 删除日志
            Log.d(TAG, "getBitmap:" + mCache.size());
            return mCache.get(url);
        }

        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
            Log.d(TAG, url + " putBitmap:" + mCache.size());
        }
    }
}
