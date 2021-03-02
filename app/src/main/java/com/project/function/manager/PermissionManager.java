package com.project.function.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.permissionx.guolindev.PermissionCollection;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

/**
 * PermissionManager：动态权限统一管理
 * <p>
 * 1、onExplainRequestReason()方法，用于监听那些被用户拒绝，而又可以再次去申请的权限，在这个方法中解释申请这些权限的原因；
 * 2、onForwardToSettings()方法，用于监听那些被用户永久拒绝的权限，这里提醒用户手动去应用程序设置当中打开权限；
 * 3、explainReasonBeforeRequest()方法，用于向用户解释权限申请原因；
 * 在使用explainReasonBeforeRequest()方法时，需要注意：
 * 第一，单独使用explainReasonBeforeRequest()方法是无效的，必须配合onExplainRequestReason()方法一起使用才能起作用。这个很好理解，因为没有配置onExplainRequestReason()方法，我们怎么向用户解释权限申请原因呢？
 * 第二，在使用explainReasonBeforeRequest()方法时，如果onExplainRequestReason()方法中编写了权限过滤的逻辑，最终的运行结果可能和你期望的会不一致。
 * 第三，在onExplainRequestReason()方法中提供了一个额外的beforeRequest参数，用于标识当前上下文是在权限请求之前还是之后，借助这个参数在onExplainRequestReason()方法中执行不同的逻辑
 */
public class PermissionManager {

    private PermissionManager() {
    }

    private static class InstanceHolder {
        public static PermissionManager INSTANCE = new PermissionManager();
    }

    public static PermissionManager getInstance() {
        return PermissionManager.InstanceHolder.INSTANCE;
    }

    public final String CAMERA = Manifest.permission.CAMERA;
    public final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public final String CALL_PHONE = Manifest.permission.CALL_PHONE;
    public final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    private final String[] permission_storage = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private final String[] permission_scan = {CAMERA};
    private final String[] permission_camera = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private final String[] permission_audio = {RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private final String[] permission_video = {CAMERA, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private final String[] permission_av = {CAMERA, RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private final String[] permission_location = {ACCESS_FINE_LOCATION};
    private final String[] permission_phone = {CALL_PHONE, READ_PHONE_STATE};
    private final String[] permission_all = {CAMERA, RECORD_AUDIO, CALL_PHONE, READ_PHONE_STATE, ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    /**
     * 功能枚举（App中涉及到动态权限的功能）
     */
    public enum Function {
        STORAGE,
        SCAN,
        CAMERA,
        AUDIO,
        VIDEO,
        AV,
        LOCATION,
        PHONE,
        ALL
    }

    /**
     * 根据组功能枚举获取对应相关动态权限
     *
     * @param function 功能枚举
     * @return 功能对应相关动态权限
     */
    private String[] getPermissionByFunction(Function function) {
        switch (function) {
            case STORAGE:
                return permission_storage;
            case SCAN:
                return permission_scan;
            case CAMERA:
                return permission_camera;
            case AUDIO:
                return permission_audio;
            case VIDEO:
                return permission_video;
            case AV:
                return permission_av;
            case LOCATION:
                return permission_location;
            case PHONE:
                return permission_phone;
            default:
                return permission_all;
        }
    }

    /**
     * 权限申请结果回调接口
     */
    public abstract static class PermissionCallback {

        public abstract void granted();

        public void refused() {
        }
    }

    /**
     * 是否Android6.0+
     */
    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查单个权限是否获取
     *
     * @param context    上下文
     * @param permission 权限
     * @return 是否获取
     */
    public boolean isGranted(Context context, String permission) {
        return !isMarshmallow() || (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 检查多个权限是否全部获取
     *
     * @param context     上下文
     * @param permissions 权限数组
     * @return 是否全部获取
     */
    public boolean isAllGranted(Context context, String[] permissions) {
        if (null != permissions && permissions.length > 0) {
            int count = 0;
            for (String str : permissions) {
                if (isGranted(context, str)) {
                    count++;
                }
            }
            return count == permissions.length;
        }
        return true;
    }

    /**
     * 检查权功能相关权限是否全部获取
     *
     * @param context  上下文
     * @param function 功能枚举
     * @return 是否全部获取
     */
    public boolean isAllGranted(Context context, Function function) {
        String[] permissions = getPermissionByFunction(function);
        return isAllGranted(context, permissions);
    }

    //----------------------------------------------------------------------------------------------

    private PermissionCollection permissionCollection = null;

    /**
     * Activity中申请权限
     *
     * @param fragmentActivity FragmentActivity
     */
    public PermissionManager init(FragmentActivity fragmentActivity) {
        this.permissionCollection = PermissionX.init(fragmentActivity);
        return this;
    }

    /**
     * Fragment中申请权限
     *
     * @param fragment Fragment
     */
    public PermissionManager init(Fragment fragment) {
        this.permissionCollection = PermissionX.init(fragment);
        return this;
    }

    /**
     * 单个权限申请
     *
     * @param permission 权限
     * @param callback   权限申请结果回调
     */
    public void requestPermission(String permission, PermissionCallback callback) {
        requestPermission(new String[]{permission}, callback);
    }

    /**
     * 多个权限申请
     *
     * @param permissions 权限数组
     * @param callback    权限申请结果回调
     */
    public void requestPermission(String[] permissions, PermissionCallback callback) {
        startRequestPermissions(this.permissionCollection, permissions, callback);
    }

    /**
     * 按功能申请权限
     *
     * @param function 功能枚举
     * @param callback 权限申请结果回调
     */
    public void requestPermission(Function function, PermissionCallback callback) {
        requestPermission(getPermissionByFunction(function), callback);
    }

    /**
     * 申请权限
     *
     * @param permissionCollection permissionCollection
     * @param permissions          权限
     * @param callback             权限申请结果回调
     */
    private void startRequestPermissions(PermissionCollection permissionCollection, String[] permissions, PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCollection
                    .permissions(permissions)
                    .setDialogTintColor(Color.parseColor("#2481FC"), Color.parseColor("#2481FC"))
                    /*.explainReasonBeforeRequest()
                    .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                        @Override
                        public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                            // 被用户拒绝，而又可以再次去申请的权限，beforeRequest参数，用于标识当前上下文是在权限请求之前还是之后
                            if (beforeRequest) {
                                scope.showRequestReasonDialog(deniedList, "为了保证程序正常工作，请您同意以下权限申请", "确定", "取消");
                            } else {
                                List<String> filteredList = new ArrayList<>();
                                if (null != deniedList && deniedList.size() > 0 && deniedList.contains(Manifest.permission.CAMERA)) {
                                    filteredList.add(Manifest.permission.CAMERA);
                                }
                                scope.showRequestReasonDialog(filteredList, "摄像机权限是程序必须依赖的权限", "确定", "取消");
                            }
                        }
                    })
                    .onExplainRequestReason(new ExplainReasonCallback() {
                        @Override
                        public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                            // 被用户拒绝，而又可以再次去申请的权限
                            scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "确定", "取消");
                        }
                    })*/
                    .onForwardToSettings(new ForwardToSettingsCallback() {
                        @Override
                        public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                            // 被用户拒绝且不再询问的权限
                            scope.showForwardToSettingsDialog(deniedList, "为了保证程序正常工作，请您在应用程序设置中手动开启以下权限", "确定", "取消");
                        }
                    })
                    .request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                            if (allGranted) {
                                callback.granted();
                            } else {
                                callback.refused();
                                // 请允许相关权限
                            }
                        }
                    });
        } else {
            callback.granted();
        }
    }
}