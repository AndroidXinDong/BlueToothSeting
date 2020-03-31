package com.usr.usrsimplebleassistent.fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.GattDetailActivity;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * create
 * on 2020-03-27 14:59
 * by xinDong
 **/
public class DataFragment extends Fragment {
    private MyApplication myApplication;
    private String TAG = "Tag";
    public DataFragment() {
    }

    public static DataFragment getInstance(){
        DataFragment fragment = new DataFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data,container,false);
        ButterKnife.bind(DataFragment.this,view);
        EventBus.getDefault().register(DataFragment.this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "dataResume: ");
    }

    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private void initCharacteristics() {
        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
        String uuid = characteristic.getUuid().toString();
        if (uuid.equals(GattAttributes.USR_SERVICE)) {
            List<BluetoothGattCharacteristic> characteristics = myApplication.getCharacteristics();
            for (BluetoothGattCharacteristic c : characteristics) {
                String porpertie = Utils.getPorperties(getActivity(), c);
                if (porpertie.equals("Notify")) {
                    notifyCharacteristic = c;
                    continue;
                }
                if (porpertie.equals("Write")) {
                    writeCharacteristic = c;
                    continue;
                }
            }
        } else {
            notifyCharacteristic = characteristic;
            writeCharacteristic = characteristic;
        }
    }

    private void notifyOption() {
        prepareBroadcastDataNotify(notifyCharacteristic);
    }

    /**
     * 向BLE蓝牙发送数据
     */
    private void writeOption(String hexString) {
        byte[] array = Utils.hexStringToByteArray(hexString);
        writeCharacteristic(writeCharacteristic, array);
    }
    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }

    }
    // Writing the hexValue to the characteristics
    private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getReceiverData(MessageEvent event){

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DataFragment.this);
    }
}
