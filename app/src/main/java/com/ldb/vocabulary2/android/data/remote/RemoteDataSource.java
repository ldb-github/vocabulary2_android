package com.ldb.vocabulary2.android.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.ldb.vocabulary2.android.network.BaseNetworkRequest;
import com.ldb.vocabulary2.android.network.PostParam;

import java.util.List;

/**
 * Created by lsp on 2016/9/17.
 */
public interface RemoteDataSource {

    /**
     * 请求短信验证码
     * @param context
     * @param phoneNumber
     * @param type 类型:re注册；lg登录
     * @param callback
     * @return
     */
    boolean requestPhoneCheckCode(Context context, String phoneNumber, String type,
                                  BaseNetworkRequest.RequestCallback callback);
    /**
     * 注册
     * @param context
     * @param phoneNumber 手机号
     * @param checkCode 验证码
     * @param callback
     * @return
     */
    String register(Context context, String phoneNumber, String checkCode,
                    BaseNetworkRequest.RequestCallback callback);

    /**
     * 登录
     * @param context
     * @param phoneNumber
     * @param checkCode
     * @param callback
     * @return
     */
    String login(Context context, String phoneNumber, String checkCode,
                 BaseNetworkRequest.RequestCallback callback);

    /**
     * 获取词汇列表
     * @param context
     * @param page
     *@param sort
     * @param sortType
     * @param callback  @return
     */
    String getCategoryList(Context context, int page, String sort, String sortType,
                           BaseNetworkRequest.RequestCallback callback);

    /**
     * 获取图片
     * @param context
     * @param url
     * @param imageView
     * @param maxWidth
     * @param maxHeight
     * @param defaultImage
     * @param errorImage
     */
    public void getImageForView(@NonNull Context context, String url, ImageView imageView, int maxWidth,
                                int maxHeight, int defaultImage, int errorImage);

    /**
     * 获取词汇列表
     * @param context
     * @param categoryId
     * @param page
     * @param secondLan
     * @param callback   @return
     * */
    public String getVocabularyList(@NonNull Context context, String categoryId, int page,
                                    String secondLan, BaseNetworkRequest.RequestCallback callback);

    /**
     * 新增词汇分类
     * @param context
     * @param category
     * @param callback
     */
    public void postCategory(@NonNull Context context, List<PostParam> category,
                             BaseNetworkRequest.RequestCallback callback);

    /**
     * 新增词汇
     * @param context
     * @param vocabulary
     * @param callback
     */
    public void postVocabulary(@NonNull Context context, List<PostParam> vocabulary,
                               BaseNetworkRequest.RequestCallback callback);
}
