package com.library.gallery.status;

import android.app.Activity;
import android.os.Build;

/**
 * @author ydy
 */
public class StatusBar {

    /**
     * apply
     *
     * @param activity   activity
     * @param isDarkFont isDarkFont
     */
    public static void apply(Activity activity, boolean isDarkFont) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IStatusBar statusBar = null;
            if (OSUtil.isMeiZu()) {
                // is MeiZu
                statusBar = new MeizuStatusBar();
            } else if (OSUtil.isXiaoMi()) {
                // is XiaoMi
                statusBar = new MiuiStatusBar();
            } else if (OSUtil.isOSM()) {
                // is OS M
                statusBar = new OSMStatusBar();
            } else {
                // 都不是
            }
            if (statusBar != null) {
                statusBar.setStatusBarLightMode(activity, isDarkFont);
            }
        }
    }
}