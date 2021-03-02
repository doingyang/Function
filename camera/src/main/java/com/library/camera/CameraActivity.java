package com.library.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.library.camera.listener.ClickListener;
import com.library.camera.listener.ErrorListener;
import com.library.camera.listener.CameraListener;
import com.library.camera.util.FileUtil;
import com.library.camera.widget.CameraView;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";

    public static final int FEATURES_ONLY_CAPTURE = CameraView.BUTTON_STATE_ONLY_CAPTURE;
    public static final int FEATURES_ONLY_RECORDER = CameraView.BUTTON_STATE_ONLY_RECORDER;
    public static final int FEATURES_BOTH = CameraView.BUTTON_STATE_BOTH;

    public static final int CAMERA_RESULT_CODE_CAPTURE = 1001;
    public static final int CAMERA_RESULT_CODE_RECORD = 1002;
    public static final int CAMERA_RESULT_CODE_ERROR = 1003;

    private CameraView cameraView;

    private String videoSavePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "Camera";
    private int features = CameraView.BUTTON_STATE_BOTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);

        cameraView = (CameraView) findViewById(R.id.cameraview);

        getIntentData();
        initConfig();
        initEvents();
    }

    public static void startCameraActivityForResult(Activity context, int requestCode, String videoSavePath, int features) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra("videoSavePath", videoSavePath);
        intent.putExtra("features", features);
        context.startActivityForResult(intent, requestCode);
    }

    private void getIntentData() {
        if (null != getIntent()) {
            videoSavePath = getIntent().getStringExtra("videoSavePath");
            features = getIntent().getIntExtra("features", CameraView.BUTTON_STATE_BOTH);
        }
    }

    private String getTip(int features) {
        switch (features) {
            case CameraView.BUTTON_STATE_ONLY_CAPTURE:
                return getResources().getString(R.string.tip_photo);
            case CameraView.BUTTON_STATE_ONLY_RECORDER:
                return getResources().getString(R.string.tip_video);
            case CameraView.BUTTON_STATE_BOTH:
                return getResources().getString(R.string.tip_photo_video);
            default:
                return "";
        }
    }

    private void initConfig() {
        // 设置视频保存路径
        cameraView.setSaveVideoPath(videoSavePath);
        // 设置功能（拍照、录像、两者）
        cameraView.setFeatures(features);
        // 设置按钮提示
        cameraView.setTip(getTip(features));
        // 设置录制质量
        cameraView.setMediaQuality(CameraView.MEDIA_QUALITY_MIDDLE);
    }

    private void initEvents() {
        // 启动Camera错误回调监听
        cameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                // 错误监听
                Log.i(TAG, "camera error");
                Intent intent = new Intent();
                setResult(CAMERA_RESULT_CODE_ERROR, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                // 录音权限?
                Log.i(TAG, "audio permission error");
                Intent intent = new Intent();
                setResult(CAMERA_RESULT_CODE_ERROR, intent);
                finish();
            }
        });
        // 拍照录像回调监听
        cameraView.setJCameraLisenter(new CameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                // 获取图片bitmap
                String path = FileUtil.saveBitmap("Camera", bitmap);
                Intent intent = new Intent();
                intent.putExtra("path", path);
                setResult(CAMERA_RESULT_CODE_CAPTURE, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                // 获取视频路径
                String path = FileUtil.saveBitmap("Camera", firstFrame);
                Log.i(TAG, "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                intent.putExtra("path", url);
                setResult(CAMERA_RESULT_CODE_RECORD, intent);
                finish();
            }
        });
        // 左按钮（退出）点击事件
        cameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
        // 右按钮（暂无）点击事件
        cameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }
}