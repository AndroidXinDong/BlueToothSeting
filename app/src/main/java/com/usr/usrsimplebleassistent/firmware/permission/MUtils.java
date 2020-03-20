package com.usr.usrsimplebleassistent.firmware.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 6.0权限申请Util
 *
 * @author 李雷红 2017/12/22  version 1.0
 */

final public class MUtils {
    private MUtils() {
    }

    /**
     * 判断是否是6.0权限以上
     *
     * @return
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 权限数组转List
     *
     * @param activity
     * @param permission
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }


    /**
     * object对象转Activity 或Fragment
     *
     * @param object
     * @return
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof Activity) {
            return (Activity) object;
        }
        return null;
    }
}
