package com.ldb.vocabulary2.android.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;

import com.ldb.vocabulary2.android.model.Vocabulary;

/**
 * Created by lsp on 2016/10/7.
 */
public class VocabularyDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "vocabulary.db";
    private static VocabularyDbHelper mInstance;

    private VocabularyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static VocabularyDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VocabularyDbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VocabularyContract.CategoryEntry.CREATE_TABLE);
        db.execSQL(VocabularyContract.VocabularyEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO 正式发布需要删除
        switch (oldVersion){
            case 1:

        }
    }

    /**
     * 插入
     * @param table
     * @param nullColumnHack
     * @param values
     * @return
     */
    public long insert(String table, String nullColumnHack, ContentValues values){
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(table, nullColumnHack, values);
    }

    /**
     * 更新
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase db = getWritableDatabase();
        return db.update(table, values, whereClause, whereArgs);
    }

    /**
     * 查询
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy, String limit){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * 原生sql
     * @param sql
     * @param selectionArgs
     * @param cancellationSignal
     * @return
     */
    public Cursor rawQuery(String sql, String[] selectionArgs,
                           CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs, cancellationSignal);
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * 删除
     * @param table
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(String table, String whereClause, String[] whereArgs){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(table, whereClause, whereArgs);
    }



}
