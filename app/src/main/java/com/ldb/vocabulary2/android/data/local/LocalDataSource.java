package com.ldb.vocabulary2.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.network.BaseNetworkRequest;

import java.util.List;

/**
 * Created by lsp on 2016/9/17.
 */
public interface LocalDataSource {

    /**
     * 新增
     * @param context
     * @param category
     */
    void addCategory(Context context, Category category);
    /**
     * 修改
     * @param context
     * @param category
     */
    void updateCategory(Context context, Category category);
    /**
     * 获取收藏
     * @param context
     * @return
     */
    List<Category> getCollectionList(Context context);
}
