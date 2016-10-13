package com.ldb.vocabulary2.android.model;

import java.util.Date;

/**
 * Created by lsp on 2016/9/25.
 */
public class Vocabulary {

    private String mId;
    private String mName;
    private String mCId;
    private String mImage;
    private String mImageRemote;
    private String mLanguage;
    private String mUsername;
    private Date mCreateTime;
    private String mTranslation;
    private String mImageLocal;
    private String mLocalId;
    private boolean mIsUpload;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCId() {
        return mCId;
    }

    public void setCId(String CId) {
        mCId = CId;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getImageRemote() {
        return mImageRemote;
    }

    public void setImageRemote(String imageRemote) {
        mImageRemote = imageRemote;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public Date getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(Date createTime) {
        mCreateTime = createTime;
    }

    public String getTranslation() {
        return mTranslation;
    }

    public void setTranslation(String translation) {
        mTranslation = translation;
    }

    public String getImageLocal() {
        return mImageLocal;
    }

    public void setImageLocal(String imageLocal) {
        mImageLocal = imageLocal;
    }

    public String getLocalId() {
        return mLocalId;
    }

    public void setLocalId(String localId) {
        mLocalId = localId;
    }

    public boolean isUpload() {
        return mIsUpload;
    }

    public void setUpload(boolean upload) {
        mIsUpload = upload;
    }
}
