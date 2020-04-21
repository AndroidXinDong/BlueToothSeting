package com.usr.usrsimplebleassistent;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MessageEvent;
import com.usr.usrsimplebleassistent.fragments.BleFragment;
import com.usr.usrsimplebleassistent.fragments.DataFragment;
import com.usr.usrsimplebleassistent.fragments.SetFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends MyBaseActivity implements View.OnClickListener {
    private MyApplication mMyApplication;
    @BindView(R.id.radiobutton1)
    RadioButton radiobutton1;
    @BindView(R.id.radiobutton2)
    RadioButton radiobutton2;
    @BindView(R.id.radiobutton3)
    RadioButton radiobutton3;
    private int isChoose = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMyApplication = (MyApplication) this.getApplication();
        mManager = getSupportFragmentManager();
        //必须调用，其在setContentView后面调用
        bindToolBar();
        radiobutton1.setOnClickListener(this);
        radiobutton2.setOnClickListener(this);
        radiobutton3.setOnClickListener(this);
        initFragment();
        EventBus.getDefault().register(this);
        registerReceiver(mReceiver, Utils.makeGattUpdateIntentFilter());
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

    @Override
    public void onClick(View view) {
        boolean connect = mMyApplication.isConnect();
        switch (view.getId()) {
            case R.id.radiobutton1:
                isChoose = 0;
                if (mBleFragment == null) {
                    mBleFragment = new BleFragment();
                }
                addFragment(mBleFragment);
                if (connect) {
                    EventBus.getDefault().post(new MessageEvent("stop", false));
                }
                break;
            case R.id.radiobutton2:
                if (connect) {
                    if (mDataFragment == null) {
                        mDataFragment = new DataFragment();
                    }
                    addFragment(mDataFragment);
                    if (isChoose == 0) {
                        EventBus.getDefault().post(new MessageEvent("start", false));
                        isChoose++;
                    }
                } else {
                    radiobutton2.setChecked(false);
                    radiobutton1.setChecked(true);
                    Toast.makeText(mMyApplication, "请连接蓝牙之后再操作", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.radiobutton3:
                if (connect) {
                    isChoose = 0;
                    EventBus.getDefault().post(new MessageEvent("stop", false));
                    if (mSetFragment == null) {
                        mSetFragment = new SetFragment();
                    }
                    addFragment(mSetFragment);
                } else {
                    radiobutton1.setChecked(true);
                    radiobutton3.setChecked(false);
                    Toast.makeText(mMyApplication, "请连接蓝牙之后再操作", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    /**
     * fragment的切换 不通过replace的方式将fragment进行替换，禁止fragment页面重新加载
     */
    private void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mManager.beginTransaction();
        if (currentFragment == null) {
            fragmentTransaction.add(R.id.ll_tihuan, fragment).commit();
            currentFragment = fragment;
        }
        if (currentFragment != fragment) {
            // 先判断是否被add过
            if (!fragment.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                fragmentTransaction.hide(currentFragment).add(R.id.ll_tihuan, fragment).commit();
            } else {
                // 隐藏当前的fragment，显示下一个
                fragmentTransaction.hide(currentFragment).show(fragment).commit();
            }
            currentFragment = fragment;
        }
    }

    private String TAG = "Tag";
    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                EventBus.getDefault().post(new MessageEvent("" + Utils.ByteArraytoHex(array), false));
            }
            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS)) {

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Services Discovered from GATT Server
                int sdkInt = Build.VERSION.SDK_INT;
                if (sdkInt >= 21) {
                    //设置最大发包、收包的长度为512个字节
                    boolean b = BluetoothLeService.requestMtu(512);
                    if (b) {
                        Toast.makeText(MainActivity.this, getString(R.string.transmittal_length, "512"), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(MainActivity.this, getString(R.string.transmittal_length, "20"), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.transmittal_length, "20"), Toast.LENGTH_LONG).show();
                }
            }

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessageEvent(MessageEvent event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }
}
