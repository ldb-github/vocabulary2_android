package com.ldb.vocabulary2.android.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ldb.vocabulary2.android.R;
import com.ldb.vocabulary2.android.data.local.LocalDataSource;
import com.ldb.vocabulary2.android.data.local.LocalDataSourceImpl;
import com.ldb.vocabulary2.android.data.remote.RemoteDataSource;
import com.ldb.vocabulary2.android.data.remote.RemoteDataSourceImpl;
import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;
import com.ldb.vocabulary2.android.network.BaseNetworkRequest;
import com.ldb.vocabulary2.android.network.PostParam;
import com.ldb.vocabulary2.android.util.DateUtil;
import com.ldb.vocabulary2.android.util.DeviceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsp on 2016/10/7.
 */
public class Repository {

    public static Repository INSTANCE = null;

    private LocalDataSource mLocalDataSource;
    private RemoteDataSource mRemoteDataSource;

    private Repository(){
        mLocalDataSource = LocalDataSourceImpl.getInstance();
        mRemoteDataSource = RemoteDataSourceImpl.getInstance();
    }

    public static Repository getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }

    public void postCategory(@NonNull final Context context, List<PostParam> category,
                             final Callback callback){
        if (!DeviceUtil.isNetworkConnected(context)) {
            callback.onResult(false, context.getResources().getString(R.string.network_not_connected));
        }else {
            mRemoteDataSource.postCategory(context, category, new BaseNetworkRequest.RequestCallback() {
                @Override
                public void onResult(boolean isOk, String response) {
                    if(isOk){
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt(CommunicationContract.KEY_CODE);
                            String message = result.getString(CommunicationContract.KEY_MESSAGE);
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                callback.onResult(true, message);
                            }else{
                                callback.onResult(false, message);
                            }
                        }catch (JSONException e){
                            callback.onResult(false,
                                    context.getResources().getString(R.string.parse_data_error));
                        }
                    }else{
                        callback.onResult(false, response);
                    }
                }
            });
        }
    }



    public void saveCategoryLocal(Context context, Category category){
        if(category.getLocalId() != null && !category.getLocalId().trim().isEmpty()){
            mLocalDataSource.updateCategory(context, category);
        }else{
            mLocalDataSource.addCategory(context, category);
        }
    }

    public void postCategory(@NonNull final Context context, final Category category,
                             final Callback.PostCategoryCallback callback){
        if (!DeviceUtil.isNetworkConnected(context)) {
            callback.onError( context.getResources().getString(R.string.network_not_connected));
        }else {
            List<PostParam> postParams = getPostParamFrom(category);
            mRemoteDataSource.postCategory(context, postParams, new BaseNetworkRequest.RequestCallback() {
                @Override
                public void onResult(boolean isOk, String response) {
                    if(isOk){
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt(CommunicationContract.KEY_CODE);
                            String message = result.getString(CommunicationContract.KEY_MESSAGE);
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                Category categoryReturn = new Category();
                                callback.onSuccess(message, categoryReturn);
                            }else{
                                callback.onError( message);
                            }
                        }catch (JSONException e){
                            callback.onError(
                                    context.getResources().getString(R.string.parse_data_error));
                        }
                    }else{
                        callback.onError( response);
                    }
                }
            });
        }
    }

    public List<PostParam> getPostParamFrom(Category category){
        List<PostParam> postParams = new ArrayList<>();

        PostParam param = new PostParam();

        String categoryName = category.getName();

        param.setFile(false);
        param.setValue(categoryName);
        param.setFieldName(CommunicationContract.KEY_CATEGORY_NAME);
        postParams.add(param);

        String imagePath = category.getImageLocal();
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            param = new PostParam();
            param.setFile(true);
            File image = new File(imagePath);
            param.setData(image);
            param.setFileName(image.getName());
            // TODO 这里需要更好的方法来自动判断文件的mimeType.
            param.setMimeType("image/jpeg");
            param.setFieldName(CommunicationContract.KEY_CATEGORY_IMAGE);
            postParams.add(param);
        }

        return postParams;
    }

    /**
     * 获取词汇列表
     * @param context
     * @param page
     * @param sort
     * @param sortType
     * @param callback
     */
    public void getCategoryList(@NonNull final Context context, int page, String sort, String sortType,
                                final Callback.RequestCategoryListCallback callback){
        if (!DeviceUtil.isNetworkConnected(context)) {
            callback.onError(context.getResources().getString(R.string.network_not_connected));
        }else {
            mRemoteDataSource.getCategoryList(context, page, sort, sortType,
                    new BaseNetworkRequest.RequestCallback() {
                @Override
                public void onResult(boolean isOk, String response) {
                    if(isOk){
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt(CommunicationContract.KEY_CODE);
                            StringBuilder message = new StringBuilder(
                                    result.getString(CommunicationContract.KEY_MESSAGE));
                            List<Category> categoryList = new ArrayList<Category>();
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                if(result.has(CommunicationContract.KEY_CATEGORY_LIST)) {
                                    JSONArray categoryArray = result.getJSONArray(
                                            CommunicationContract.KEY_CATEGORY_LIST);
                                    JSONObject jsonObject = new JSONObject();
                                    for (int i = 0; i < categoryArray.length(); i++) {
                                        jsonObject = categoryArray.getJSONObject(i);
                                        Category category = new Category();
                                        category.setId(jsonObject.getString(
                                                CommunicationContract.KEY_CATEGORY_ID));
                                        category.setName(jsonObject.getString(
                                                CommunicationContract.KEY_CATEGORY_NAME));
                                        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_IMAGE)) {
                                            category.setImage(jsonObject.getString(
                                                    CommunicationContract.KEY_CATEGORY_IMAGE));
                                        }
                                        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_IMAGE_REMOTE)) {
                                            category.setImageRemote(jsonObject.getString(
                                                    CommunicationContract.KEY_CATEGORY_IMAGE_REMOTE));
                                        }
                                        category.setFavoriteCount(jsonObject.getInt(
                                                CommunicationContract.KEY_CATEGORY_FAVORITE_COUNT));
                                        category.setWordCount(jsonObject.getInt(
                                                CommunicationContract.KEY_CATEGORY_WORD_COUNT));
                                        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_LANGUAGE)) {
                                            category.setLanguage(jsonObject.getString(
                                                    CommunicationContract.KEY_CATEGORY_LANGUAGE));
                                        }
                                        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_CREATER)) {
                                            category.setUsername(jsonObject.getString(
                                                    CommunicationContract.KEY_CATEGORY_CREATER));
                                        }
                                        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_CREATE_TIME)) {
                                            try {
                                                category.setCreateTime(
                                                        DateUtil.parseDateTime(jsonObject.getString(
                                                                CommunicationContract.KEY_CATEGORY_CREATE_TIME)));
                                            } catch (ParseException e) {
                                                message.append(context.getResources()
                                                        .getString(R.string.parse_category_create_time_error));
                                            }
                                        }
                                        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_TRANSLATION)) {
                                            category.setTranslation(jsonObject.getString(
                                                    CommunicationContract.KEY_CATEGORY_TRANSLATION));
                                        }
                                        categoryList.add(category);
                                    }
                                }
                            }
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                callback.onSuccess(message.toString(), categoryList);
                            }else{
                                callback.onError(message.toString());
                            }
                        } catch (JSONException e) {
                            callback.onError(
                                    context.getResources().getString(R.string.parse_data_error));
                        }

                    }else {
                        callback.onError(context.getResources().getString(R.string.request_error));
                    }
                }
            });
        }
    }

    public List<Category> getCollectionList(Context context){
        return  mLocalDataSource.getCollectionList(context);
    }

    public void getVocabularyList(@NonNull final Context context, String categoryId, int page,
                                  String secondLan, final Callback.RequestVocabularyListCallback callback){
        if (!DeviceUtil.isNetworkConnected(context)) {
            callback.onError(context.getResources().getString(R.string.network_not_connected));
        }else {
            mRemoteDataSource.getVocabularyList(context, categoryId, page, secondLan,
                    new BaseNetworkRequest.RequestCallback() {
                @Override
                public void onResult(boolean isOk, String response) {
                    if(isOk){
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt(CommunicationContract.KEY_CODE);
                            StringBuilder message = new StringBuilder(
                                    result.getString(CommunicationContract.KEY_MESSAGE));
                            List<Vocabulary> vocabularyList = new ArrayList<Vocabulary>();
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                if(result.has(CommunicationContract.KEY_VOCABULARY_LIST)) {
                                    JSONArray categoryArray = result.getJSONArray(
                                            CommunicationContract.KEY_VOCABULARY_LIST);
                                    JSONObject jsonObject = new JSONObject();

                                    for (int i = 0; i < categoryArray.length(); i++) {
                                        jsonObject = categoryArray.getJSONObject(i);
                                        Vocabulary vocabulary = new Vocabulary();
                                        vocabulary.setId(jsonObject.getString(
                                                CommunicationContract.KEY_VOCABULARY_ID));
                                        vocabulary.setName(jsonObject.getString(
                                                CommunicationContract.KEY_VOCABULARY_NAME));
                                        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_IMAGE)) {
                                            vocabulary.setImage(jsonObject.getString(
                                                    CommunicationContract.KEY_VOCABULARY_IMAGE));
                                        }
                                        ;
                                        vocabularyList.add(vocabulary);
                                    }
                                }
                            }
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                callback.onSuccess(message.toString(), vocabularyList);
                            }else{
                                callback.onError(message.toString());
                            }
                        } catch (JSONException e) {
                            callback.onError(
                                    context.getResources().getString(R.string.parse_data_error));
                        }

                    }else {
                        callback.onError(context.getResources().getString(R.string.request_error));
                    }
                }
            });
        }
    }

}
