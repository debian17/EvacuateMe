package com.example.evacuateme.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Net {
    public static boolean isAvailable(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            return true;
        }
        else {
            return false;
        }
    }
}