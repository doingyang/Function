package com.library.gallery.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.library.gallery.bean.ImageItem;
import com.library.gallery.ui.activity.PreviewActivity;
import com.library.gallery.utils.DensityUtil;
import com.library.gallery.manager.RxPickerManager;
import com.library.gallery.widget.imageview.TouchImageView;

import java.util.List;

/**
 * 预览图片ViewPager适配器
 *
 * @author ydy
 */
public class PreviewAdapter extends PagerAdapter {

    private List<ImageItem> data;
    private PreviewActivity activity;

    public PreviewAdapter(PreviewActivity activity, List<ImageItem> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        AppCompatImageView imageView = new AppCompatImageView(container.getContext());//原生ImageView
        //可缩放ImageView
        TouchImageView imageView = new TouchImageView(container.getContext());
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        imageView.setLayoutParams(layoutParams);
        ImageItem imageItem = data.get(position);
        container.addView(imageView);
        int deviceWidth = DensityUtil.getDeviceWidth(container.getContext());
        RxPickerManager
                .getInstance()
                .display(imageView, imageItem.getPath(), deviceWidth, deviceWidth);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
