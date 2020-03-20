package com.usr.usrsimplebleassistent.firmware.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.fragment.app.Fragment;

import java.util.List;


/**
 * 6.0权限申请
 *
 * @author 李雷红 2017/12/22  version 1.0
 */
public class MPermissions {

    /**
     * 请求权限 Activity
     *
     * @param object
     * @param requestCode
     * @param permissions
     */
    public static void requestPermissions(Activity object, int requestCode, String... permissions) {
        _requestPermissions(object, requestCode, permissions);
    }

    /**
     * 请求权限 Fragment
     *
     * @param object
     * @param requestCode
     * @param permissions
     */
    public static void requestPermissions(Fragment object, int requestCode, String... permissions) {
        _requestPermissions(object, requestCode, permissions);
    }



    /**
     * shouldShowRequestPermissionRationale
     * 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
     * 注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，
     * 此方法将返回 false。如果设备规范禁止应用具有该权限，此方法也会返回 false。
     *
     * @param activity
     * @param permission
     * @param requestCode
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                permission) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    permission)) {
//                Toast.makeText(activity, R.string.no_permission_go_set, Toast.LENGTH_SHORT).show();
//            }
            _requestPermissions(activity, requestCode, permission);
            return false;
        }
        return true;
    }

    /**
     * 请求多个权限
     *
     * @param object
     * @param requestCode
     * @param permissions
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    private static void _requestPermissions(Object object, int requestCode, String... permissions) {
        if (!MUtils.isOverMarshmallow()) {
            return;
        }
        List<String> deniedPermissions = MUtils.findDeniedPermissions(MUtils.getActivity(object), permissions);
        if (deniedPermissions.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(object.getClass().getName() + " is not supported!");
            }
        }
    }
}
