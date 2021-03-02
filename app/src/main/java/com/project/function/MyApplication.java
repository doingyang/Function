package com.project.function;

import android.support.multidex.MultiDexApplication;

import com.library.gallery.RxPicker;
import com.library.gallery.imageloader.GlideImageLoader;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        RxPicker.init(new GlideImageLoader());
    }
}