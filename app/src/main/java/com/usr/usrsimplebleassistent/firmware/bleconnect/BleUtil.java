package com.usr.usrsimplebleassistent.firmware.bleconnect;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.R;

/**
 * 检查蓝牙是否可用
 *
 * @author 李雷红 2017/12/22  version 1.0
 */
public class BleUtil {
    private static BluetoothAdapter mBluetoothAdapter;

    /**
     * 检查蓝牙是否可用
     */
    public static void checkBleSupportAndInitialize(Activity activity) {
        // Use this check to determine whether BLE is supported on the device.
        if (!activity.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(activity,
                    R.string.device_ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }
}
