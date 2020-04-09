package com.usr.usrsimplebleassistent.application;

import android.app.Application;
import android.bluetooth.BluetoothGattCharacteristic;

import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.views.CrashHandler;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private boolean clearflag;
    private boolean isConnect = false;
    private boolean currentModel = false;
    public boolean isClearflag() {
        return clearflag;
    }

    public void setClearflag(boolean clearflag) {
        this.clearflag = clearflag;
    }

    public enum SERVICE_TYPE {
        TYPE_USR_DEBUG, TYPE_NUMBER, TYPE_STR, TYPE_OTHER;
    }

    private final List<MService> services = new ArrayList<>();
    private final List<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

    private BluetoothGattCharacteristic characteristic;

    public List<MService> getServices() {
        return services;
    }

    public static SERVICE_TYPE serviceType;

    public void setServices(List<MService> services) {
        this.services.clear();
        this.services.addAll(services);
    }


    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<BluetoothGattCharacteristic> characteristics) {
        this.characteristics.clear();
        this.characteristics.addAll(characteristics);
    }


    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 添加异常打印反馈回执
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public boolean isCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(boolean currentModel) {
        this.currentModel = currentModel;
    }
}
