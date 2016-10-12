package com.ldb.vocabulary2.android.model;

/**
 * Created by lsp on 2016/9/18.
 */
public class DeviceInfo {
    private String mSource;
    private String mIp;
    private String mMac;
    private String mImei;
    private String mLocation;

    public String getSource() {
        return mSource;
    }

    public void setSource(boolean isTablet) {
        if(isTablet){
            mSource = "np"; // 平板
        }else{
            mSource = "an";
        }
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    public String getImei() {
        return mImei;
    }

    public void setImei(String imei) {
        mImei = imei;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    @Override
    public String toString() {
        return "source:" + mSource
                + " ip:" + mIp
                + " mac:" + mMac
                + " imei:" + mImei
                + " location:" + mLocation;
    }
}
