package com.ldb.vocabulary2.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by lsp on 2016/9/25.
 */
public class Vocabulary implements Parcelable{

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
    private boolean mIsUploaded;
    private String mCIdLocal;

    public Vocabulary() {
    }

    private Vocabulary(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mImage = source.readString();
        mImageRemote = source.readString();
        mLanguage = source.readString();
        mUsername = source.readString();
        Long time = source.readLong();
        if (time == 0) {
            mCreateTime = null;
        } else {
            mCreateTime = new Date(time);
        }
        mTranslation = source.readString();
        mImageLocal = source.readString();
        mLocalId = source.readString();
        mIsUploaded = source.readInt() == 1;
        mCIdLocal = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mImage);
        dest.writeString(mImageRemote);
        dest.writeString(mLanguage);
        dest.writeString(mUsername);
        if (mCreateTime == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(mCreateTime.getTime());
        }
        dest.writeString(mTranslation);
        dest.writeString(mImageLocal);
        dest.writeString(mLocalId);
        dest.writeInt(mIsUploaded ? 1 : 0);
        dest.writeString(mCIdLocal);
    }

    public static final Parcelable.Creator<Vocabulary> CREATOR = new Parcelable.Creator<Vocabulary>() {
        @Override
        public Vocabulary createFromParcel(Parcel source) {
            return new Vocabulary(source);
        }

        @Override
        public Vocabulary[] newArray(int size) {
            return new Vocabulary[size];
        }
    };

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

    public void setCId(String cId) {
        mCId = cId;
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

    public boolean isUploaded() {
        return mIsUploaded;
    }

    public void setUploaded(boolean uploaded) {
        mIsUploaded = uploaded;
    }

    public String getCIdLocal() {
        return mCIdLocal;
    }

    public void setCIdLocal(String cIdLocal) {
        mCIdLocal = cIdLocal;
    }
}
