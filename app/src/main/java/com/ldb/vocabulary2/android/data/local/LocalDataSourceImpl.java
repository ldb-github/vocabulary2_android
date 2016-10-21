package com.ldb.vocabulary2.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;

import com.ldb.vocabulary2.android.model.Category;
import com.ldb.vocabulary2.android.model.Vocabulary;

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

    //region Category
    @Override
    public Category getCategoryById(Context context, String id) {
//        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = VocabularyContract.CategoryEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {id};
//        Cursor cursor = db.query(VocabularyContract.CategoryEntry.TABLE_NAME,
//                VocabularyContract.CategoryEntry.PROJECTION,
//                whereClause, whereArgs, null, null, null);
        List<Category> categories = queryCategory(context, selection, selectionArgs, null, null, null);

        if(!categories.isEmpty()){
            return categories.get(0);
        }
        return null;
    }
    @Override
    public void addCategory(Context context, Category category){
        ContentValues values = category2ContentValues(category);
        insert(context, VocabularyContract.CategoryEntry.TABLE_NAME, null, values);
//        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.insert(VocabularyContract.CategoryEntry.TABLE_NAME, null, values);
    }
    @Override
    public void updateCategory(Context context, Category category){
        ContentValues values = category2ContentValues(category);

//        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = VocabularyContract.CategoryEntry._ID + " = ? ";
        String[] whereArgs = {category.getLocalId()};
        update(context, VocabularyContract.CategoryEntry.TABLE_NAME, values, whereClause, whereArgs);
//        db.update(VocabularyContract.CategoryEntry.TABLE_NAME, values, whereClause, whereArgs);
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
    @Override
    public int deleteCollections(Context context, List<String> localIds){
        StringBuilder sb = new StringBuilder();
        for(String id : localIds){
            sb.append(id).append(",");
        }
        if(sb.length() > 0){
            sb.delete(sb.lastIndexOf(","), sb.length());
        }
        String whereClause = VocabularyContract.CategoryEntry._ID +  " in ( ? )";
        String[] whereArgs = { sb.toString() };
        return deleteCategory(context, whereClause, whereArgs);
    }

    /**
     * 词汇分类查询
     * @param context
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    private List<Category> queryCategory(Context context, String selection, String[] selectionArgs,
                                         String groupBy, String having,  String orderBy){
        Cursor cursor = query(context, VocabularyContract.CategoryEntry.TABLE_NAME,
                VocabularyContract.CategoryEntry.PROJECTION, selection, selectionArgs, groupBy,
                having, orderBy);
        List<Category> categories = getCategoryFrom(cursor);
        cursor.close();
        return categories;
    }
    /**
     * 删除词汇分类
     * @param context
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private int deleteCategory(Context context, String whereClause, String[] whereArgs){
//        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return delete(context, VocabularyContract.CategoryEntry.TABLE_NAME, whereClause, whereArgs);
    }
    /**
     * 从Cursor中提取Category
     * @param cursor
     * @return
     */
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
    /**
     * Category转换成ContentValues
     * @param category
     * @return
     */
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
        if(category.getLocalId() != null){
            values.put(VocabularyContract.CategoryEntry._ID, category.getLocalId());
        }
        return values;
    }
    //endregion

    //region Vocabulary
    @Override
    public Vocabulary getVocabularyById(Context context, String id) {
        String selection = VocabularyContract.VocabularyEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {id};
        List<Vocabulary> vocabularies = queryVocabulary(context, selection, selectionArgs, null, null, null);
        if(!vocabularies.isEmpty()){
            return vocabularies.get(0);
        }
        return null;
    }
    @Override
    public int getVocabularyCountForCategory(Context context, String cId) {
        String sql = " SELECT COUNT(*) FROM " + VocabularyContract.VocabularyEntry.TABLE_NAME +
                " WHERE " + VocabularyContract.VocabularyEntry.COLUMN_CID + " = ? ";
        String[] selectionArgs = { cId };
        Cursor cursor = rawQuery(context, sql, selectionArgs, null);
        return cursor.getInt(0);
    }
    @Override
    public List<Vocabulary> getVocabulariesByCId(Context context, String cId, int page) {
        String selection = VocabularyContract.VocabularyEntry.COLUMN_CID + " = ? ";
        String[] selectionArgs = {cId};
        List<Vocabulary> vocabularies = queryVocabulary(context, selection, selectionArgs, null, null, null);
        if(!vocabularies.isEmpty()){
            return vocabularies;
        }
        return null;
    }
    @Override
    public List<Vocabulary> getVocabulariesByCIdLocal(Context context, String cIdLocal, int page) {
        String selection = VocabularyContract.VocabularyEntry.COLUMN_CID_LOCAL + " = ? ";
        String[] selectionArgs = {cIdLocal};
        List<Vocabulary> vocabularies = queryVocabulary(context, selection, selectionArgs, null, null, null);
        if(!vocabularies.isEmpty()){
            return vocabularies;
        }
        return null;
    }
    @Override
    public List<Vocabulary> getVocabulariesToUploadByCIdLocal(Context context, String cIdLocal) {
        String selection = VocabularyContract.VocabularyEntry.COLUMN_CID_LOCAL + " = ? " +
                " AND " + VocabularyContract.VocabularyEntry.COLUMN_UPLOADED + " = ? ";
        String[] selectionArgs = {cIdLocal, "0"};
        List<Vocabulary> vocabularies = queryVocabulary(context, selection, selectionArgs, null, null, null);
        if(!vocabularies.isEmpty()){
            return vocabularies;
        }
        return null;
    }
    @Override
    public void addVocabulary(Context context, Vocabulary vocabulary) {
        ContentValues values = vocabulary2ContentValues(vocabulary);
        insert(context, VocabularyContract.VocabularyEntry.TABLE_NAME, null, values);
    }
    @Override
    public void updateVocabulary(Context context, Vocabulary vocabulary) {
        ContentValues values = vocabulary2ContentValues(vocabulary);
        String whereClause = VocabularyContract.VocabularyEntry._ID + " = ? ";
        String[] whereArgs = {vocabulary.getLocalId()};
        update(context, VocabularyContract.VocabularyEntry.TABLE_NAME, values, whereClause, whereArgs);
    }
    @Override
    public int deleteVocabularies(Context context, List<String> localIds) {
        StringBuilder sb = new StringBuilder();
        for(String id : localIds){
            sb.append(id).append(",");
        }
        if(sb.length() > 0){
            sb.delete(sb.lastIndexOf(","), sb.length());
        }
        String whereClause = VocabularyContract.VocabularyEntry._ID +  " in ( ? )";
        String[] whereArgs = { sb.toString() };
        return deleteVocabulary(context, whereClause, whereArgs);
    }
    @Override
    public int deleteVocabulariesFor(Context context, String cIdLocal) {
        String whereClause = VocabularyContract.VocabularyEntry.COLUMN_CID_LOCAL +  " = ? ";
        String[] whereArgs = { cIdLocal };
        return deleteVocabulary(context, whereClause, whereArgs);
    }

    /**
     * 词汇查询
     * @param context
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    private List<Vocabulary> queryVocabulary(Context context, String selection, String[] selectionArgs,
                                             String groupBy, String having,  String orderBy){
        Cursor cursor = query(context, VocabularyContract.VocabularyEntry.TABLE_NAME,
                VocabularyContract.VocabularyEntry.PROJECTION, selection, selectionArgs, groupBy,
                having, orderBy);
        List<Vocabulary> vocabularies = getVocabularyFrom(cursor);
        cursor.close();
        return vocabularies;
    }
    /**
     * 删除词汇
     * @param context
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private int deleteVocabulary(Context context, String whereClause, String[] whereArgs){
        return delete(context, VocabularyContract.VocabularyEntry.TABLE_NAME, whereClause, whereArgs);
    }
    /**
     * 从Cursor提取Vocabulary
     * @param cursor
     * @return
     */
    private List<Vocabulary> getVocabularyFrom(Cursor cursor){
        List<Vocabulary> vocabularies = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Vocabulary vocabulary = new Vocabulary();
                vocabulary.setLocalId(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry._ID)));
                vocabulary.setId(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_ID)));
                vocabulary.setName(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_NAME)));
                vocabulary.setCId(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_CID)));
                vocabulary.setImage(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_IMAGE)));
                vocabulary.setImageRemote(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_IMAGE_REMOTE)));
                vocabulary.setLanguage(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_LANGUAGE)));
                vocabulary.setUsername(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_CREATER)));
                vocabulary.setCreateTime(new Date(cursor.getLong(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_CREATE_TIME))));
                vocabulary.setTranslation(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_TRANSLATION)));
                vocabulary.setImageLocal(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_IMAGE_LOCAL)));
                int index = cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_UPLOADED);
                int value = cursor.getInt(
                        index );
                boolean uploaded = (value == 1);
                vocabulary.setUploaded(uploaded);
                vocabulary.setCIdLocal(cursor.getString(
                        cursor.getColumnIndex(VocabularyContract.VocabularyEntry.COLUMN_CID_LOCAL)));
                vocabularies.add(vocabulary);
            }while(cursor.moveToNext());
        }
        return vocabularies;
    }

    /**
     * Vocabulary转换为ContentValues
     * @param vocabulary
     * @return
     */
    private ContentValues vocabulary2ContentValues(Vocabulary vocabulary){
        ContentValues values = new ContentValues();
        if(vocabulary.getLocalId() != null){
            values.put(VocabularyContract.VocabularyEntry._ID, vocabulary.getLocalId());
        }
        values.put(VocabularyContract.VocabularyEntry.COLUMN_ID, vocabulary.getId());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_NAME, vocabulary.getName());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_CID, vocabulary.getCId());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_IMAGE, vocabulary.getImage());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_IMAGE_REMOTE, vocabulary.getImageRemote());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_LANGUAGE, vocabulary.getLanguage());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_CREATER, vocabulary.getUsername());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_CREATE_TIME, vocabulary.getCreateTime().getTime());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_TRANSLATION, vocabulary.getTranslation());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_IMAGE_LOCAL, vocabulary.getImageLocal());
        values.put(VocabularyContract.VocabularyEntry.COLUMN_UPLOADED, vocabulary.isUploaded() ? 1 : 0);
        values.put(VocabularyContract.VocabularyEntry.COLUMN_CID_LOCAL, vocabulary.getCIdLocal());
        return values;
    }
    //endregion

    //region 数据库
    /**
     * 数据库 插入
     * @param context
     * @param table
     * @param nullColumnHack
     * @param values
     * @return
     */
    private long insert(Context context, String table, String nullColumnHack, ContentValues values){
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        return dbHelper.insert(table, nullColumnHack, values);
    }
    /**
     * 数据库 更新
     * @param context
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private int update(Context context, String table, ContentValues values, String whereClause,
                      String[] whereArgs){
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        return dbHelper.update(table, values, whereClause, whereArgs);
    }
    /**
     * 数据库 查找
     * @param context
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    private Cursor query(Context context, String table, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy){
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        return dbHelper.query(table, columns, selection, selectionArgs,
                groupBy, having, orderBy, null);
    }
    /**
     * 数据库 原生sql
     * @param context
     * @param sql
     * @param selectionArgs
     * @param cancellationSignal
     * @return
     */
    public Cursor rawQuery(Context context, String sql, String[] selectionArgs,
                           CancellationSignal cancellationSignal) {
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        return dbHelper.rawQuery(sql, selectionArgs, cancellationSignal);
    }
    /**
     * 数据库 删除
     * @param context
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private int delete(Context context, String table, String whereClause, String[] whereArgs){
        VocabularyDbHelper dbHelper = VocabularyDbHelper.getInstance(context);
        return dbHelper.delete(table, whereClause, whereArgs);
    }
    //endregion
}
