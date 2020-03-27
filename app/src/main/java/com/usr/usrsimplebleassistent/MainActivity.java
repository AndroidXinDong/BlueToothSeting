package com.usr.usrsimplebleassistent;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MDevice;
import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.bean.Message;
import com.usr.usrsimplebleassistent.firmware.bleconnect.BleUtil;
import com.usr.usrsimplebleassistent.fragments.BleFragment;
import com.usr.usrsimplebleassistent.fragments.DataFragment;
import com.usr.usrsimplebleassistent.fragments.SetFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import me.drakeet.materialdialog.MaterialDialog;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends MyBaseActivity  {

    /*
     * onCreate 入口
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManager = getSupportFragmentManager();
        //必须调用，其在setContentView后面调用
        bindToolBar();
        initFragment();
    }

    private void initFragment() {
        if (mBleFragment == null) {
            mBleFragment = new BleFragment();
        }
        addFragment(mBleFragment);
    }


    private BleFragment mBleFragment;
    private DataFragment mDataFragment;
    private SetFragment mSetFragment;
    private FragmentManager mManager;
    private Fragment currentFragment;
    public void click(View view) {
        switch (view.getId()) {
            case R.id.radiobutton1:
                if (mBleFragment == null) {
                    mBleFragment = new BleFragment();
                }
                addFragment(mBleFragment);
                break;
            case R.id.radiobutton2:
                if (mDataFragment == null) {
                    mDataFragment = new DataFragment();
                }
                addFragment(mDataFragment);
                break;
            case R.id.radiobutton3:
                if (mSetFragment == null) {
                    mSetFragment = new SetFragment();
                }
                addFragment(mSetFragment);
                break;

        }
    }

    /**
     * fragment的切换 不通过replace的方式将fragment进行替换，禁止fragment页面重新加载
     */
    private void addFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        if (currentFragment == null) {
            fragmentTransaction.add(R.id.ll_tihuan, fragment).commit();
            currentFragment = fragment;
        }
        if (currentFragment != fragment) {
            // 先判断是否被add过
            if (!fragment.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                fragmentTransaction.hide(currentFragment)
                        .add(R.id.ll_tihuan, fragment).commit();
            } else {
                // 隐藏当前的fragment，显示下一个
                fragmentTransaction.hide(currentFragment).show(fragment)
                        .commit();
            }
            currentFragment = fragment;
        }
    }
}
