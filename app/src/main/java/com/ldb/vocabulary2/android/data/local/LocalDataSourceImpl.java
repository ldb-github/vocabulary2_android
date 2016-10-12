package com.ldb.vocabulary2.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ldb.vocabulary2.android.data.remote.RemoteDataSourceImpl;
import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.network.BaseNetworkRequest;
import com.ldb.vocabulary2.android.network.NetworkRequestViaVolley;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lsp on 2016/10/7.
 */
public class LocalDataSourceImpl implements LocalDataSource{

    private static LocalDataSourceImpl INSTANCE = null;

    private LocalDataSourceImpl(){

    }

    public static LocalDataSourceImpl getInstance(){
        if(INSTANCE == null){
            INSTANCE = new LocalDataSourceImpl();
        }
        return INSTANCE;
    }

    @Override
    public Category getCategoryById(Context context, String id) {
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String whereClause = VocabularyContract.CategoryEntry.COLUMN_ID + " = ? ";
        String[] whereArgs = {id};
        Cursor cursor = db.query(VocabularyContract.CategoryEntry.TABLE_NAME,
                VocabularyContract.CategoryEntry.PROJECTION,
                whereClause, whereArgs, null, null, null);
        List<Category> categories = getCategoryFrom(cursor);
        if(!categories.isEmpty()){
            return categories.get(0);
        }
        return null;
    }

    @Override
    public void addCategory(Context context, Category category){
        ContentValues values = category2ContentValues(category);

        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(VocabularyContract.CategoryEntry.TABLE_NAME, null, values);
    }
    @Override
    public void updateCategory(Context context, Category category){
        ContentValues values = category2ContentValues(category);

        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = VocabularyContract.CategoryEntry._ID + " = ? ";
        String[] whereArgs = {category.getLocalId()};
        db.update(VocabularyContract.CategoryEntry.TABLE_NAME, values, whereClause, whereArgs);
    }
    @Override
    public List<Category> getCollectionList(Context context){
//        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = VocabularyContract.CategoryEntry.COLUMN_FAVORITE + " = ? ";
        String[] selectionArgs = {"1"};
        String orderBy = VocabularyContract.CategoryEntry.COLUMN_LAST_READ + " ASC";
//        Cursor cursor = db.query(VocabularyContract.CategoryEntry.TABLE_NAME,
//                VocabularyContract.CategoryEntry.PROJECTION,
//                selection, selectionArgs, null, null, orderBy);
//        List<Category> categories = getCategoryFrom(cursor);
//        cursor.close();

        List<Category> categories =
                queryCategory(context, selection, selectionArgs, null, null, orderBy);
        return categories;
    }

    @Override
    public List<Category> getCategoryList(Context context) {
        String orderBy = VocabularyContract.CategoryEntry.COLUMN_LAST_READ + " ASC";
        List<Category> categories =
                queryCategory(context, null, null, null, null, orderBy);
        return categories;
    }

    private List<Category> queryCategory(Context context, String selection, String[] selectionArgs,
                                         String groupBy, String having,  String orderBy){
        Cursor cursor = query(context, VocabularyContract.CategoryEntry.TABLE_NAME,
                VocabularyContract.CategoryEntry.PROJECTION, selection, selectionArgs, groupBy,
                having, orderBy);
        List<Category> categories = getCategoryFrom(cursor);
        cursor.close();
        return categories;
    }

    private Cursor query(Context context, String table, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy){
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        cursor.moveToFirst();
        return cursor;
    }

    private void deleteCollection(){

    }


    private List<Category> getCategoryFrom(Cursor cursor){
        List<Category> categories = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Category category = new Category();
                category.setLocalId(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry._ID)));
                category.setId(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_ID)));
                category.setName(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_NAME)));
                category.setImage(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_IMAGE)));
                category.setImageRemote(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_IMAGE_REMOTE)));
                category.setFavoriteCount(cursor.getInt(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_FAVORITE_COUNT)));
                category.setWordCount(cursor.getInt(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_WORD_COUNT)));
                category.setLanguage(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_LANGUAGE)));
                category.setUsername(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_CREATER)));
                category.setCreateTime(new Date(cursor.getLong(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_CREATE_TIME))));
                category.setTranslation(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_TRANSLATION)));
                category.setImageLocal(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_IMAGE_LOCAL)));
                category.setUploaded(cursor.getInt(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_UPLOADED)) == 1);
                category.setFavorite(cursor.getInt(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_FAVORITE)) == 1);
                category.setLastRead(new Date(cursor.getLong(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_LAST_READ))));
                category.setHasNew(cursor.getInt(
                        cursor.getColumnIndex(VocabularyContract.CategoryEntry.COLUMN_HAS_NEW)) == 1);
                categories.add(category);
            }while(cursor.moveToNext());
        }
        return categories;
    }

    private ContentValues category2ContentValues(Category category){
        ContentValues values = new ContentValues();
        values.put(VocabularyContract.CategoryEntry.COLUMN_ID, category.getId());
        values.put(VocabularyContract.CategoryEntry.COLUMN_NAME, category.getName());
        values.put(VocabularyContract.CategoryEntry.COLUMN_IMAGE, category.getImage());
        values.put(VocabularyContract.CategoryEntry.COLUMN_IMAGE_REMOTE, category.getImageRemote());
        values.put(VocabularyContract.CategoryEntry.COLUMN_FAVORITE_COUNT, category.getFavoriteCount());
        values.put(VocabularyContract.CategoryEntry.COLUMN_WORD_COUNT, category.getWordCount());
        values.put(VocabularyContract.CategoryEntry.COLUMN_LANGUAGE, category.getLanguage());
        values.put(VocabularyContract.CategoryEntry.COLUMN_CREATER, category.getUsername());
        values.put(VocabularyContract.CategoryEntry.COLUMN_CREATE_TIME, category.getCreateTime().getTime());
        values.put(VocabularyContract.CategoryEntry.COLUMN_TRANSLATION, category.getTranslation());
        values.put(VocabularyContract.CategoryEntry.COLUMN_IMAGE_LOCAL, category.getImageLocal());
        values.put(VocabularyContract.CategoryEntry.COLUMN_UPLOADED, category.isUploaded() ? 1 : 0);
        values.put(VocabularyContract.CategoryEntry.COLUMN_FAVORITE, category.isFavorite() ? 1 : 0);
        values.put(VocabularyContract.CategoryEntry.COLUMN_LAST_READ, category.getLastRead().getTime());
        values.put(VocabularyContract.CategoryEntry.COLUMN_HAS_NEW, category.isHasNew() ? 1 : 0);
        return values;
    }
}
