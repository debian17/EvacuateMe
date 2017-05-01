package com.example.edriver.Utils;

import android.content.Context;
import android.provider.Settings;

public final class Gps {
    public static boolean isAvailable(Context context){
        String locationProviders = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")){
            return false;
        }
        else {
            return true;
        }
    }
}
