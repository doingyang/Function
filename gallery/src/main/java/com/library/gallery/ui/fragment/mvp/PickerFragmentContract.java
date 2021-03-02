package com.library.gallery.ui.fragment.mvp;

import android.content.Context;

import com.library.gallery.base.BasePresenter;
import com.library.gallery.base.BaseView;
import com.library.gallery.bean.ImageFolder;

import java.util.List;

/**
 * @author ydy
 */
public interface PickerFragmentContract {

    interface View extends BaseView {
        /**
         * showAllImage
         * @param datas datas
         */
        void showAllImage(List<ImageFolder> datas);
    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * loadAllImage
         * @param context context
         */
        public abstract void loadAllImage(Context context);
    }
}
