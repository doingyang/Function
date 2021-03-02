package com.project.function;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.library.camera.CameraActivity;
import com.library.gallery.RxPicker;
import com.library.gallery.bean.ImageItem;
import com.project.function.manager.PermissionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TAKE_PICTURE = 1;
    public static final int TAKE_VIDEO = 2;
    public static final int TAKE_PICTURE_VIDEO = 3;

    private Button btnTakePicture;
    private Button btnTakeVideo;
    private Button btnPictureVideo;
    private Button btnSelectPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_picture:
                takePictureVideo(TAKE_PICTURE, CameraActivity.FEATURES_ONLY_CAPTURE);
                break;
            case R.id.btn_take_video:
                takePictureVideo(TAKE_VIDEO, CameraActivity.FEATURES_ONLY_RECORDER);
                break;
            case R.id.btn_picture_video:
                takePictureVideo(TAKE_PICTURE_VIDEO, CameraActivity.FEATURES_BOTH);
                break;
            case R.id.btn_select_picture:
                selectPicture();
                break;
        }
    }

    private void initView() {
        btnTakePicture = findViewById(R.id.btn_take_picture);
        btnTakeVideo = findViewById(R.id.btn_take_video);
        btnPictureVideo = findViewById(R.id.btn_picture_video);
        btnSelectPicture = findViewById(R.id.btn_select_picture);
        //
        btnTakePicture.setOnClickListener(this);
        btnTakeVideo.setOnClickListener(this);
        btnPictureVideo.setOnClickListener(this);
        btnSelectPicture.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == CameraActivity.CAMERA_RESULT_CODE_CAPTURE) {
            // 拍照
            String imagePath = intent.getStringExtra("path");
            Log.i("TAG", "picture：" + imagePath);
            // TODO:
        } else if (resultCode == CameraActivity.CAMERA_RESULT_CODE_RECORD) {
            // 拍视频
            String videoPath = intent.getStringExtra("path");
            Log.i("TAG", "video：" + videoPath);
        } else if (resultCode == CameraActivity.CAMERA_RESULT_CODE_ERROR) {
            // 拍照、拍视频error
        }
    }

    private void takePictureVideo(int requestCode, int features) {
        String videoFilePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/videos";
        PermissionManager.getInstance().init(this).requestPermission(PermissionManager.Function.VIDEO, new PermissionManager.PermissionCallback() {
            @Override
            public void granted() {
                CameraActivity.startCameraActivityForResult(MainActivity.this, requestCode, videoFilePath, features);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void selectPicture() {
        List<String> oldSelectImageList = new ArrayList<>();
        RxPicker.of()
                .single(false)
                .camera(true)
                .limit(1, 3)
                .check(oldSelectImageList)
                .start(this)
                .subscribe(new Consumer<List<ImageItem>>() {
                    @Override
                    public void accept(@NonNull List<ImageItem> imageItems) throws Exception {
                        oldSelectImageList.clear();
                        for (ImageItem imageItem : imageItems) {
                            String path = imageItem.getPath();
                            File file = new File(path);
                            if (file.exists()) {
                                oldSelectImageList.add(path);
                            }
                        }
                    }
                });
    }
}