package com.qgnix.live.utils;

import android.app.Application;

import com.lovense.sdklibrary.Lovense;
import com.lovense.sdklibrary.LovenseToy;
import com.lovense.sdklibrary.callBack.LovenseError;
import com.lovense.sdklibrary.callBack.OnSearchToyListener;

/**
 * 跳蛋util
 */
public class VibratingEggUtil {

    public static void init(Application application) {
        Lovense.getInstance(application).setDeveloperToken("hgSnK1y5y1mw83VunmFjSqrF6AV2mjNDMygLmdS5OvzDtY4i2p0K5N/sys0/Ay3L");
    }


    public static void scanToys(Application application) {
        Lovense.getInstance(application).searchToys(new OnSearchToyListener() {
            @Override
            public void onSearchToy(LovenseToy lovenseToy) {
            } // Find toys

            @Override
            public void finishSearch() {

            }  // Scan finish

            @Override
            public void onError(LovenseError msg) {
            } // error

        });
    }


}
