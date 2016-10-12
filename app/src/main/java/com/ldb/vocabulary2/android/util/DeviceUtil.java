package com.ldb.vocabulary2.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by lsp on 2016/9/17.
 */
public class DeviceUtil {

    //region 判断网络是否可用，是否已经连接
    public static boolean isNetworkAvailable(@NonNull Context context){
        return isWifiNetworkAvailable(context) || isMobileNetworkAvailable(context);
    }

    public static boolean isNetworkConnected(@NonNull Context context){
        return isWifiNetworkConnected(context) || isMobileNetworkConnected(context);
    }

    public static boolean isWifiNetworkAvailable(@NonNull Context context){
        NetworkInfo networkInfo = getNetworkInfo(context, ConnectivityManager.TYPE_WIFI);
        return networkInfo != null;
    }

    public static boolean isWifiNetworkConnected(@NonNull Context context){
        NetworkInfo networkInfo = getNetworkInfo(context, ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isMobileNetworkAvailable(@NonNull Context context){
        NetworkInfo networkInfo = getNetworkInfo(context, ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null;
    }

    public static boolean isMobileNetworkConnected(@NonNull Context context){
        NetworkInfo networkInfo = getNetworkInfo(context, ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static NetworkInfo getNetworkInfo(@NonNull Context context, int type){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            Network[] networks = connectivityManager.getAllNetworks();
            for(Network network : networks){
                networkInfo = connectivityManager.getNetworkInfo(network);
                if(networkInfo.getType() == type){
                    break;
                }
            }
        }else{
            networkInfo = connectivityManager.getNetworkInfo(type);
        }
        return networkInfo;
    }
    //endregion

    public static String getInfosAboutDevice(Context a) {
        String s = "";
        try {
            PackageInfo pInfo = a.getPackageManager().getPackageInfo(
                    a.getPackageName(), PackageManager.GET_META_DATA);
            s += "\n APP Package Name: " + a.getPackageName();
            s += "\n APP Version Name: " + pInfo.versionName;
            s += "\n APP Version Code: " + pInfo.versionCode;
            s += "\n";
        } catch (NameNotFoundException e) {
        }
        s += "\n OS Version: " + System.getProperty("os.version") + " ("
                + Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + Build.VERSION.SDK;
        s += "\n Device: " + Build.DEVICE;
        s += "\n Model (and Product): " + Build.MODEL + " ("
                + Build.PRODUCT + ")";

        // more from
        // http://developer.android.com/reference/android/os/Build.html :
        s += "\n Manufacturer: " + Build.MANUFACTURER;
        s += "\n Other TAGS: " + Build.TAGS;

//        s += "\n screenWidth: "
//                + a.getWindow().getWindowManager().getDefaultDisplay()
//                .getWidth();
//        s += "\n screenHeigth: "
//                + a.getWindow().getWindowManager().getDefaultDisplay()
//                .getHeight();
        s += "\n Keyboard available: "
                + (a.getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS);

        s += "\n Trackball available: "
                + (a.getResources().getConfiguration().navigation == Configuration.NAVIGATION_TRACKBALL);
        s += "\n SD Card state: " + Environment.getExternalStorageState();
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        String key = "";
        while (keys.hasMoreElements()) {
            key = (String) keys.nextElement();
            s += "\n > " + key + " = " + (String) p.get(key);
        }
        return s;
    }

    public static String getBuildInfo(){
        String  details =  "VERSION.RELEASE : "+Build.VERSION.RELEASE
                +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                +"\nBOARD : "+Build.BOARD
                +"\nBOOTLOADER : "+Build.BOOTLOADER
                +"\nBRAND : "+Build.BRAND
                +"\nCPU_ABI : "+Build.CPU_ABI
                +"\nCPU_ABI2 : "+Build.CPU_ABI2
                +"\nDISPLAY : "+Build.DISPLAY
                +"\nFINGERPRINT : "+Build.FINGERPRINT
                +"\nHARDWARE : "+Build.HARDWARE
                +"\nHOST : "+Build.HOST
                +"\nID : "+Build.ID
                +"\nMANUFACTURER : "+Build.MANUFACTURER
                +"\nMODEL : "+Build.MODEL
                +"\nPRODUCT : "+Build.PRODUCT
                +"\nSERIAL : "+Build.SERIAL
                +"\nTAGS : "+Build.TAGS
                +"\nTIME : "+Build.TIME
                +"\nTYPE : "+Build.TYPE
                +"\nUNKNOWN : "+Build.UNKNOWN
                +"\nUSER : "+Build.USER;

        return details;
    }

}
