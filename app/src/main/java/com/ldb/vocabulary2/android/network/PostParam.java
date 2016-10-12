package com.ldb.vocabulary2.android.network;

import java.io.File;

/**
 * Created by lsp on 2016/9/28.
 */
public class PostParam {
    private String mFieldName;
    private boolean mIsFile;
    private String mMimeType;
    private String mFileName;
    // 文件数据
    private File mData;
    // 字符数据
    private String mValue;

    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(String fieldName) {
        mFieldName = fieldName;
    }

    public boolean isFile() {
        return mIsFile;
    }

    public void setFile(boolean file) {
        mIsFile = file;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public File getData() {
        return mData;
    }

    public void setData(File data) {
        mData = data;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}