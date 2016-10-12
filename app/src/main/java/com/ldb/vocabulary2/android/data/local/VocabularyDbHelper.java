package com.ldb.vocabulary2.android.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    }



}
