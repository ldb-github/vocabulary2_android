package com.ldb.vocabulary2.android.data.local;

import android.content.Context;

import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;

import java.util.List;

/**
 * Created by lsp on 2016/9/17.
 */
public interface LocalDataSource {
    /**
     * 根据分类id获取本地分类
     * @param context
     * @param id 分类id
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
     * @param localIds 本地id
     */
    int deleteCollections(Context context, List<String> localIds);
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
     * @param id 词汇id
     * @return
     */
    Vocabulary getVocabularyById(Context context, String id);
    /**
     * 获取词汇分类本地保存的数量
     * @param context
     * @param cId
     * @return
     */
    int getVocabularyCountForCategory(Context context, String cId);
    /**
     * 根据词汇分类id获取本地词汇列表
     * @param context
     * @param cId 词汇分类id
     * @param page
     * @return
     */
    List<Vocabulary> getVocabulariesByCId(Context context, String cId, int page);

    /**
     * 根据词汇分类本地id获取本地词汇列表
     * @param context
     * @param cIdLocal 词汇分类本地id
     * @param page
     * @return
     */
    List<Vocabulary> getVocabulariesByCIdLocal(Context context, String cIdLocal, int page);

    /**
     * 根据词汇分类本地id获取本地待上传的词汇列表
     * @param context
     * @param cIdLocal 词汇分类本地id
     * @return
     */
    List<Vocabulary> getVocabulariesToUploadByCIdLocal(Context context, String cIdLocal);
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
     * @param localIds 本地id
     * @return
     */
    int deleteVocabularies(Context context, List<String> localIds);

    /**
     * 删除本地某一词汇分类的所有词汇
     * @param context
     * @param cIdLocal 词汇分类本地id
     * @return
     */
    int deleteVocabulariesFor(Context context, String cIdLocal);
}
