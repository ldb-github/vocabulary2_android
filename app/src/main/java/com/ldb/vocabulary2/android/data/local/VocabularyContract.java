package com.ldb.vocabulary2.android.data.local;

import android.provider.BaseColumns;

/**
 * Created by lsp on 2016/10/7.
 */
public class VocabularyContract {

    public static final class CategoryEntry implements BaseColumns{

        public static final String TABLE_NAME = "category";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_IMAGE_REMOTE = "image_remote";
        public static final String COLUMN_FAVORITE_COUNT = "f_count";
        public static final String COLUMN_WORD_COUNT = "w_count";
        public static final String COLUMN_LANGUAGE = "lan";
        public static final String COLUMN_CREATER = "creater";
        public static final String COLUMN_CREATE_TIME = "create_time";
        public static final String COLUMN_TRANSLATION = "translation";
        public static final String COLUMN_IMAGE_LOCAL = "image_local";
        public static final String COLUMN_UPLOADED = "uploaded";
        public static final String COLUMN_FAVORITE = "favorite";
        public static final String COLUMN_LAST_READ = "last_read";
        public static final String COLUMN_HAS_NEW = "has_new";

        public static final String[] PROJECTION = {
                _ID, COLUMN_ID, COLUMN_NAME, COLUMN_IMAGE, COLUMN_IMAGE_REMOTE,
                COLUMN_FAVORITE_COUNT, COLUMN_WORD_COUNT, COLUMN_LANGUAGE,
                COLUMN_CREATER, COLUMN_CREATE_TIME, COLUMN_TRANSLATION,
                COLUMN_IMAGE_LOCAL, COLUMN_UPLOADED, COLUMN_FAVORITE,
                COLUMN_LAST_READ, COLUMN_HAS_NEW
        };

        public static final String CREATE_TABLE = "CREATE TABLE " +
                VocabularyContract.CategoryEntry.TABLE_NAME + " (" +
                VocabularyContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VocabularyContract.CategoryEntry.COLUMN_ID + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                VocabularyContract.CategoryEntry.COLUMN_IMAGE + " TEXT, " +
                VocabularyContract.CategoryEntry.COLUMN_IMAGE_REMOTE + " TEXT, " +
                VocabularyContract.CategoryEntry.COLUMN_FAVORITE_COUNT + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_WORD_COUNT + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_LANGUAGE + " TEXT, " +
                VocabularyContract.CategoryEntry.COLUMN_CREATER + " TEXT, " +
                VocabularyContract.CategoryEntry.COLUMN_CREATE_TIME + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_TRANSLATION + " TEXT, " +
                VocabularyContract.CategoryEntry.COLUMN_IMAGE_LOCAL + " TEXT, " +
                VocabularyContract.CategoryEntry.COLUMN_UPLOADED + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_FAVORITE + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_LAST_READ + " INTEGER, " +
                VocabularyContract.CategoryEntry.COLUMN_HAS_NEW + " INTEGER );";
    }

    public static final class VocabularyEntry implements BaseColumns{

        public static final String TABLE_NAME = "vocabulary";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CID = "cid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_IMAGE_REMOTE = "image_remote";
        public static final String COLUMN_LANGUAGE = "lan";
        public static final String COLUMN_CREATER = "creater";
        public static final String COLUMN_CREATE_TIME = "create_time";
        public static final String COLUMN_TRANSLATION = "translation";
        public static final String COLUMN_IMAGE_LOCAL = "image_local";
        public static final String COLUMN_UPLOADED = "uploaded";

        public static final String[] PROJECTION = {
                _ID, COLUMN_ID, COLUMN_CID, COLUMN_NAME, COLUMN_IMAGE, COLUMN_IMAGE_REMOTE,
                COLUMN_LANGUAGE, COLUMN_CREATER, COLUMN_CREATE_TIME, COLUMN_TRANSLATION,
                COLUMN_IMAGE_LOCAL, COLUMN_UPLOADED
        };

        public static final String CREATE_TABLE = "CREATE TABLE " +
                VocabularyContract.VocabularyEntry.TABLE_NAME + " (" +
                VocabularyContract.CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VocabularyContract.VocabularyEntry.COLUMN_ID + " INTEGER, " +
                VocabularyContract.VocabularyEntry.COLUMN_CID + " INTEGER, " +
                VocabularyContract.VocabularyEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                VocabularyContract.VocabularyEntry.COLUMN_IMAGE + " TEXT, " +
                VocabularyContract.VocabularyEntry.COLUMN_IMAGE_REMOTE + " TEXT, " +
                VocabularyContract.VocabularyEntry.COLUMN_LANGUAGE + " TEXT, " +
                VocabularyContract.VocabularyEntry.COLUMN_CREATER + " TEXT, " +
                VocabularyContract.VocabularyEntry.COLUMN_CREATE_TIME + " INTEGER, " +
                VocabularyContract.VocabularyEntry.COLUMN_TRANSLATION + " TEXT, " +
                VocabularyContract.VocabularyEntry.COLUMN_IMAGE_LOCAL + " TEXT, " +
                VocabularyContract.VocabularyEntry.COLUMN_UPLOADED + " INTEGER, " +
                " FOREIGN KEY (" + VocabularyContract.VocabularyEntry.COLUMN_CID +
                ") REFERENCES " + VocabularyContract.CategoryEntry.TABLE_NAME +
                " (" + VocabularyContract.CategoryEntry.COLUMN_ID + ") );";

    }
}
