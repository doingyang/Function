package com.library.gallery.status;

import android.os.Build;

/**
 * @author ydy
 */
public class OSUtil {

    private static final String XIAO_MI = "Xiaomi";
    private static final String HUA_WEI = "HUAWEI";
    private static final String MEI_ZU = "Meizu";
    private static final String VIVO = "vivo";
    private static final String SAMSUNG = "samsung";
    private static final String OPPO = "OPPO";

    public static boolean isXiaoMi() {
        return Build.MANUFACTURER.equalsIgnoreCase(XIAO_MI);
    }

    public static boolean isHuaWei() {
        return Build.MANUFACTURER.equalsIgnoreCase(HUA_WEI);
    }

    public static boolean isMeiZu() {
        return Build.MANUFACTURER.equalsIgnoreCase(MEI_ZU);
    }

    public static boolean isSamSung() {
        return Build.MANUFACTURER.equalsIgnoreCase(SAMSUNG);
    }

    public static boolean isOPPO() {
        return Build.MANUFACTURER.equalsIgnoreCase(OPPO);
    }

    public static boolean isOSM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}