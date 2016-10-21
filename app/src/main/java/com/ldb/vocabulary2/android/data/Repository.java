package com.ldb.vocabulary2.android.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
 * 数据请求入口
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

    //region Category
    /**
     * 保存词汇分类信息到本地
     * @param context
     * @param category
     */
    public void saveCategoryLocal(Context context, Category category){
        // TODO 图片保存到本地

        if(category.getLocalId() != null && !category.getLocalId().trim().isEmpty()){
            mLocalDataSource.updateCategory(context, category);
        }else{
            Category newCategory = null;
            if(category.getId() != null && !category.getId().trim().isEmpty() ){
                newCategory = mLocalDataSource.getCategoryById(context, category.getId());
            }
            if(newCategory != null ){
                category.setLocalId(newCategory.getLocalId());
                mLocalDataSource.updateCategory(context, category);
            }else {
                mLocalDataSource.addCategory(context, category);
                mergeWithLocalCategory(context, category);
            }
        }
    }

    /**
     * 提交词汇分类到服务器
     * @param context
     * @param category
     * @param callback
     */
    public void postCategory(@NonNull final Context context, final Category category,
                             final Callback.PostCategoryCallback callback){
        if (!DeviceUtil.isNetworkConnected(context)) {
            callback.onError( context.getResources().getString(R.string.network_not_connected));
        }else {
            List<PostParam> postParams = getPostParamFrom(category);
            mRemoteDataSource.postCategory(context, postParams,
                    new BaseNetworkRequest.RequestCallback() {
                @Override
                public void onResult(boolean isOk, String response) {
                    if(isOk){
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt(CommunicationContract.KEY_CODE);
                            StringBuilder message = new StringBuilder(result.getString(CommunicationContract.KEY_MESSAGE));
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                Category categoryReturn = new Category();
                                if(result.has(CommunicationContract.KEY_CATEGORY_LIST)) {
                                    try{
                                        categoryReturn = getCategoryFrom(result.getJSONObject(CommunicationContract.KEY_CATEGORY_LIST));
                                    } catch (ParseException e) {
                                        message.append(context.getResources()
                                                .getString(R.string.parse_create_time_error));
                                    }
                                }
                                callback.onSuccess(message.toString(), categoryReturn);
                            }else{
                                callback.onError( message.toString());
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

    /**
     * 根据词汇分类信息生成提交参数
     * @param category
     * @return
     */
    public List<PostParam> getPostParamFrom(Category category){
        List<PostParam> postParams = new ArrayList<>();

        PostParam param;

        param = new PostParam();
        param.setFile(false);
        param.setValue(category.getName());
        param.setFieldName(CommunicationContract.KEY_CATEGORY_NAME);
        postParams.add(param);

        param = new PostParam();
        param.setFile(false);
        param.setValue(category.getTranslation());
        param.setFieldName(CommunicationContract.KEY_CATEGORY_TRANSLATION);
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
     * 获取词汇分类列表
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
                                        try {
                                            Category category = getCategoryFrom(categoryArray.getJSONObject(i));
                                            category.setUploaded(true);
                                            mergeWithLocalCategory(context, category);
                                            categoryList.add(category);
                                        } catch (ParseException e) {
                                            message.append(context.getResources()
                                                    .getString(R.string.parse_create_time_error));
                                        }
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

    /**
     * 合并本地词汇分类信息
     * @param context
     * @param remoteCategory
     */
    private void mergeWithLocalCategory(Context context, Category remoteCategory){
        Category localCategory = mLocalDataSource.getCategoryById(context, remoteCategory.getId());
        if(localCategory != null){
            remoteCategory.setImageLocal(localCategory.getImageLocal());
            remoteCategory.setFavorite(localCategory.isFavorite());
            remoteCategory.setLastRead(localCategory.getLastRead());
            remoteCategory.setHasNew(localCategory.isHasNew());
            remoteCategory.setLastUpdate(localCategory.getLastUpdate());
            remoteCategory.setLocalId(localCategory.getLocalId());
            mLocalDataSource.updateCategory(context, remoteCategory);
        }
    }

    /**
     * 解析Json生成Category
     * @param jsonObject
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    private Category getCategoryFrom(JSONObject jsonObject) throws JSONException, ParseException{
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
            category.setCreateTime(
                    DateUtil.parseDateTime(jsonObject.getString(
                            CommunicationContract.KEY_CATEGORY_CREATE_TIME)));
        }
        if(jsonObject.has(CommunicationContract.KEY_CATEGORY_TRANSLATION)) {
            category.setTranslation(jsonObject.getString(
                    CommunicationContract.KEY_CATEGORY_TRANSLATION));
        }

        return category;
    }
    //endregion

    //region Collection

    /**
     * 收藏词汇分类
     * @param context
     * @param category
     */
    public void saveCollection(final Context context, Category category){
        saveCategoryLocal(context, category);
        // TODO 是全部都下载到本地呢,还是只要1页就好了?
        int page = 0;
        page = page + 1;
        getVocabularyList(context, category, page, null, new Callback.RequestVocabularyListCallback() {
            @Override
            public void onSuccess(String message, List<Vocabulary> vocabularyList) {
                for(Vocabulary vocabulary : vocabularyList){
                    saveVocabularyLocal(context, vocabulary);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    /**
     * 获取收藏的词汇分类
     * @param context
     * @return
     */
    public List<Category> getCollectionList(Context context){
        return  mLocalDataSource.getCollectionList(context);
    }

    /**
     * 删除收藏
     * @param context
     * @param ids 本地id
     * @return
     */
    public int deleteCollections(Context context, List<String> ids){
        mLocalDataSource.deleteCollections(context, ids);
        for(String id : ids) {
            deleteVocabularyLocalFor(context, id);
        }
        return 1;
    }
    //endregion

    //region Vocabulary
    public void uploadVocabulariesFor(final Context context, String localId, Category category){
        List<Vocabulary> vocabularies =
                mLocalDataSource.getVocabulariesToUploadByCIdLocal(context, localId);
        if(vocabularies != null){
            for(int i = 0; i < vocabularies.size(); i++){
                final Vocabulary vocabulary = vocabularies.get(i);
                vocabulary.setCIdLocal(localId);
                vocabulary.setCId(category.getId());
                postVocabulary(context, vocabulary, new Callback.PostVocabularyCallback() {
                    @Override
                    public void onSuccess(String message, Vocabulary vocabularyReturn) {
                        // 更新本地数据
                        vocabulary.setUploaded(true);
                        vocabulary.setId(vocabularyReturn.getId());
                        vocabulary.setImage(vocabularyReturn.getImage());
                        vocabulary.setImageRemote(vocabularyReturn.getImageRemote());
                        vocabulary.setCreateTime(vocabularyReturn.getCreateTime());
                        saveVocabularyLocal(context, vocabulary);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        }
    }

    /**
     * 保存词汇信息到本地
     * @param context
     * @param vocabulary
     */
    public void saveVocabularyLocal(Context context, Vocabulary vocabulary){
        // TODO 图片保存到本地

        if(vocabulary.getLocalId() != null && !vocabulary.getLocalId().trim().isEmpty()){
            mLocalDataSource.updateVocabulary(context, vocabulary);
        }else{
            Vocabulary newVocabulary = null;
            if(vocabulary.getId() != null && !vocabulary.getId().trim().isEmpty() ){
                newVocabulary = mLocalDataSource.getVocabularyById(context, vocabulary.getId());
            }
            if(newVocabulary != null ){
                vocabulary.setLocalId(newVocabulary.getLocalId());
                mLocalDataSource.updateVocabulary(context, vocabulary);
            }else {
                mLocalDataSource.addVocabulary(context, vocabulary);
            }
        }
    }

    /**
     * 提交词汇到服务器
     * @param context
     * @param vocabulary
     * @param callback
     */
    public void postVocabulary(@NonNull final Context context, Vocabulary vocabulary,
                               final Callback.PostVocabularyCallback callback){
        if (!DeviceUtil.isNetworkConnected(context)) {
            callback.onError(context.getResources().getString(R.string.network_not_connected));
        }else {
            List<PostParam> postParams = getPostParamFrom(vocabulary);
            mRemoteDataSource.postVocabulary(context, postParams,
                    new BaseNetworkRequest.RequestCallback() {
                @Override
                public void onResult(boolean isOk, String response) {
                    if(isOk){
                        try {
                            JSONObject result = new JSONObject(response);
                            int code = result.getInt(CommunicationContract.KEY_CODE);
                            StringBuilder message = new StringBuilder(
                                    result.getString(CommunicationContract.KEY_MESSAGE));
                            if(code == CommunicationContract.VALUE_CODE_OK){
                                Vocabulary vocabularyReturn = new Vocabulary();
                                if(result.has(CommunicationContract.KEY_VOCABULARY_LIST)) {
                                    JSONObject jsonObject = result.getJSONObject(
                                            CommunicationContract.KEY_VOCABULARY_LIST);
                                        try {
                                            vocabularyReturn = getVocabularyFrom(jsonObject);
                                        } catch (ParseException e) {
                                            message.append(context.getResources()
                                                    .getString(R.string.parse_create_time_error));
                                        }
                                    }
                                callback.onSuccess(message.toString(), vocabularyReturn);
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

    /**
     * 根据词汇信息生成提交参数
     * @param vocabulary
     * @return
     */
    public List<PostParam> getPostParamFrom(Vocabulary vocabulary){
        List<PostParam> postParams = new ArrayList<>();

        PostParam param;

        param = new PostParam();
        param.setFile(false);
        param.setValue(vocabulary.getName());
        param.setFieldName(CommunicationContract.KEY_VOCABULARY_NAME);
        postParams.add(param);

        param = new PostParam();
        param.setFile(false);
        param.setValue(vocabulary.getTranslation());
        param.setFieldName(CommunicationContract.KEY_VOCABULARY_TRANSLATION);
        postParams.add(param);

        param = new PostParam();
        param.setFile(false);
        param.setValue(vocabulary.getCId());
        param.setFieldName(CommunicationContract.KEY_CATEGORY_ID);
        postParams.add(param);

        String imagePath = vocabulary.getImageLocal();
        if(imagePath != null && !imagePath.trim().isEmpty()) {
            param = new PostParam();
            param.setFile(true);
            File image = new File(imagePath);
            param.setData(image);
            param.setFileName(image.getName());
            // TODO 这里需要更好的方法来自动判断文件的mimeType.
            param.setMimeType("image/jpeg");
            param.setFieldName(CommunicationContract.KEY_VOCABULARY_IMAGE);
            postParams.add(param);
        }

        return postParams;
    }

    /**
     * 从服务器获取词汇信息
     * @param context
     * @param category
     * @param page
     * @param secondLan
     * @param callback
     */
    public void getVocabularyList(@NonNull final Context context, Category category, int page,
                                  String secondLan, final Callback.RequestVocabularyListCallback callback){
        String categoryId = category.getId();
        final String categoryLocalId = category.getLocalId();
        // 先根据词汇分类本地id取本地词汇列表
        if(!TextUtils.isEmpty(categoryLocalId)) {
            List<Vocabulary> vocabularies =
                    mLocalDataSource.getVocabulariesByCIdLocal(context, categoryLocalId, page);
            if(vocabularies != null) {
                callback.onSuccess(context.getResources()
                        .getString(R.string.request_vocabulary_local), vocabularies);
                return;
            }
        }
        // 再根据词汇分类id取本地词汇列表
        if(!TextUtils.isEmpty(categoryId)) {
            List<Vocabulary> vocabularies =
                    mLocalDataSource.getVocabulariesByCId(context, categoryId, page);
            if(vocabularies != null) {
                callback.onSuccess(context.getResources()
                        .getString(R.string.request_vocabulary_local), vocabularies);
                return;
            }
        }
        // 如果本地没有，再从服务器获取
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
                                        try {
                                            vocabulary = getVocabularyFrom(jsonObject);
                                            vocabulary.setCIdLocal(categoryLocalId);
                                            vocabulary.setUploaded(true);
                                        } catch (ParseException e) {
                                            message.append(context.getResources()
                                                    .getString(R.string.parse_create_time_error));
                                        }
                                        vocabularyList.add(vocabulary);
                                    }
                                }
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

    /**
     * 删除本地词汇
     * @param context
     * @param ids 本地id
     * @return
     */
    public int deleteVocabularyLocal(Context context, List<String> ids){
        return mLocalDataSource.deleteVocabularies(context, ids);
    }

    public int deleteVocabularyLocalFor(Context context, String cIdLocal){
        mLocalDataSource.deleteVocabulariesFor(context, cIdLocal);
        return 1;
    }

    /**
     * 解析Json生成Vocabulary
     * @param jsonObject
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    private Vocabulary getVocabularyFrom(JSONObject jsonObject) throws JSONException, ParseException{
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setId(jsonObject.getString(
                CommunicationContract.KEY_VOCABULARY_ID));
        vocabulary.setName(jsonObject.getString(
                CommunicationContract.KEY_VOCABULARY_NAME));
        vocabulary.setCId(jsonObject.getString(
                CommunicationContract.KEY_CATEGORY_ID));
        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_IMAGE)) {
            vocabulary.setImage(jsonObject.getString(
                    CommunicationContract.KEY_VOCABULARY_IMAGE));
        }
        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_IMAGE_REMOTE)) {
            vocabulary.setImageRemote(jsonObject.getString(
                    CommunicationContract.KEY_VOCABULARY_IMAGE_REMOTE));
        }
        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_LANGUAGE)) {
            vocabulary.setLanguage(jsonObject.getString(
                    CommunicationContract.KEY_VOCABULARY_LANGUAGE));
        }
        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_CREATER)) {
            vocabulary.setUsername(jsonObject.getString(
                    CommunicationContract.KEY_VOCABULARY_CREATER));
        }
        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_CREATE_TIME)) {
            vocabulary.setCreateTime(
                    DateUtil.parseDateTime(jsonObject.getString(
                            CommunicationContract.KEY_VOCABULARY_CREATE_TIME)));

        }
        if(jsonObject.has(CommunicationContract.KEY_VOCABULARY_TRANSLATION)) {
            vocabulary.setTranslation(jsonObject.getString(
                    CommunicationContract.KEY_VOCABULARY_TRANSLATION));
        }
        return vocabulary;
    }
    //endregion

}
