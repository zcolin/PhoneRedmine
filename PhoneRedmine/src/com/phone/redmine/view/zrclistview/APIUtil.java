package com.phone.redmine.view.zrclistview;

import android.os.Build;

public class APIUtil {
    public static boolean isSupport(int apiNo){
        return Build.VERSION.SDK_INT >= apiNo;
    }
}
