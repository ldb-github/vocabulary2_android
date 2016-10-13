package com.ldb.vocabulary2.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;
import com.ldb.vocabulary2.android.network.BaseNetworkRequest;

import java.util.List;

/**
 * Created by lsp on 2016/9/17.
 */
public interface LocalDataSource {
    /**
     * 根据分类id获取本地分类
     * @param context
     * @param id
     * @return
     */
    Category getCategoryById(Context context, String id);
    /**
     * 新增本地词汇分类
     * @param context
     * @param category
     */
    void addCategory(Context context, Category category);
    /**
     * 更新本地词汇分类
     * @param context
     * @param category
     */
    void updateCategory(Context context, Category category);

    /**
     * 删除收藏
     * @param context
     * @param ids 本地id
     */
    int deleteCollections(Context context, List<String> ids);
    /**
     * 获取本地词汇类别列表
     * @param context
     * @return
     */
    List<Category> getCategoryList(Context context);
    /**
     * 获取收藏
     * @param context
     * @return
     */
    List<Category> getCollectionList(Context context);

    /**
     * 根据词汇id获取本地词汇
     * @param context
     * @param id
     * @return
     */
    Vocabulary getVocabularyById(Context context, String id);
    /**
     * 新增本地词汇
     * @param context
     * @param vocabulary
     */
    void addVocabulary(Context context, Vocabulary vocabulary);
    /**
     * 更新本地词汇
     * @param context
     * @param vocabulary
     */
    void updateVocabulary(Context context, Vocabulary vocabulary);

    /**
     * 删除词汇
     * @param context
     * @param ids
     * @return
     */
    int deleteVocabularies(Context context, List<String> ids);
}
