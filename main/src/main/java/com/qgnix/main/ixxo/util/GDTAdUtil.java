package com.qgnix.main.ixxo.util;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Ray on 2017/9/20.
 */

public class GDTAdUtil {
    Activity context;

    public GDTAdUtil(Activity context) {
        this.context = context;
    }
    public void addListener(View v){
    };

    private void getIAD() {

    }

    public void showAD() {
        try {

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showAsPopup() {

    }

    public void closeAsPopup() {

    }


    ViewGroup bannerContainer;

    public void initBanner(ViewGroup container) {

    }
    public void doRefreshBanner() {

    }

    public void doCloseBanner() {
        if(bannerContainer==null){
            return;
        }
        bannerContainer.removeAllViews();
    }
}
