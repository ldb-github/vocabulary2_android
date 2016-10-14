package com.ldb.vocabulary2.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by lsp on 2016/10/7.
 */
public class Category implements Parcelable {

    private String mId;
    private String mName;
    private String mImage;
    private String mImageRemote;
    private int mFavoriteCount;
    private int mWordCount;
    private String mLanguage;
    private String mUsername;
    private Date mCreateTime;
    private String mTranslation;
    // local properties
    private String mImageLocal;
    private boolean mIsUploaded;
    private boolean mIsFavorite;
    private Date mLastRead;
    private boolean mHasNew;
    private Date mLastUpdate;
    private String mLocalId;

    public Category() {
    }

    private Category(Parcel source) {
        mId = source.readString();
        mName = source.readString();
        mImage = source.readString();
        mImageRemote = source.readString();
        mFavoriteCount = source.readInt();
        mWordCount = source.readInt();
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
        mIsUploaded = source.readInt() == 1;
        mIsFavorite = source.readInt() == 1;
        time = source.readLong();
        if (time == 0) {
            mLastRead = null;
        } else {
            mLastRead = new Date(time);
        }
        mHasNew = source.readInt() == 1;
        time = source.readLong();
        if (time == 0) {
            mLastUpdate = null;
        } else {
            mLastUpdate = new Date(time);
        }
        mLocalId = source.readString();
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
        dest.writeInt(mFavoriteCount);
        dest.writeInt(mWordCount);
        dest.writeString(mLanguage);
        dest.writeString(mUsername);
        if (mCreateTime == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(mCreateTime.getTime());
        }
        dest.writeString(mTranslation);
        dest.writeString(mImageLocal);
        dest.writeInt(mIsUploaded ? 1 : 0);
        dest.writeInt(mIsFavorite ? 1 : 0);
        if (mLastRead == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(mLastRead.getTime());
        }
        dest.writeInt(mHasNew ? 1 : 0);
        if (mLastUpdate == null) {
            dest.writeLong(0);
        } else {
            dest.writeLong(mLastUpdate.getTime());
        }
        dest.writeString(mLocalId);
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
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

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public int getFavoriteCount() {
        return mFavoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        mFavoriteCount = favoriteCount;
    }

    public int getWordCount() {
        return mWordCount;
    }

    public void setWordCount(int wordCount) {
        mWordCount = wordCount;
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

    public boolean isUploaded() {
        return mIsUploaded;
    }

    public void setUploaded(boolean uploaded) {
        mIsUploaded = uploaded;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public Date getLastRead() {
        return mLastRead;
    }

    public void setLastRead(Date lastRead) {
        mLastRead = lastRead;
    }

    public boolean isHasNew() {
        return mHasNew;
    }

    public void setHasNew(boolean hasNew) {
        mHasNew = hasNew;
    }

    public Date getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        mLastUpdate = lastUpdate;
    }

    public String getLocalId() {
        return mLocalId;
    }

    public void setLocalId(String localId) {
        mLocalId = localId;
    }
}