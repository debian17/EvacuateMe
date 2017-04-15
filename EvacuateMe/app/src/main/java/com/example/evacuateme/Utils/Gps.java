package com.example.evacuateme.Utils;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by Андрей Кравченко on 15-Apr-17.
 */

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
