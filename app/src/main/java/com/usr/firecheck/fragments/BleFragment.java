package com.usr.firecheck.fragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.usr.firecheck.BlueToothLeService.BluetoothLeService;
import com.usr.firecheck.R;
import com.usr.firecheck.Utils.Constants;
import com.usr.firecheck.Utils.DataUtils;
import com.usr.firecheck.Utils.GattAttributes;
import com.usr.firecheck.Utils.MaterialDialog;
import com.usr.firecheck.Utils.SharedPreference;
import com.usr.firecheck.Utils.Utils;
import com.usr.firecheck.adapter.DevicesAdapter;
import com.usr.firecheck.application.MyApplication;
import com.usr.firecheck.bean.MDevice;
import com.usr.firecheck.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * create
 * on 2020-03-27 14:59
 * by xinDong
 **/
public class BleFragment extends Fragment implements View.OnClickListener {
    private static BluetoothAdapter mBluetoothAdapter;
    private Handler hander = new Handler();
    private MaterialDialog progressDialog;
    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;
    @BindView(R.id.rcy_ble)
    RecyclerView recyclerView;
    private String currentDevAddress;
    private String currentDevName;
    private MyApplication myApplication;
    @BindView(R.id.ll_ble)
    LinearLayout ll_ble;
    @BindView(R.id.btn_set)
    Button btn_set;
    @BindView(R.id.et_machine)
    TextView et_machine;
    @BindView(R.id.et_bleName)
    TextView et_bleName;
    @BindView(R.id.et_blePass)
    TextView et_blePass;
    @BindView(R.id.et_machineDate)
    TextView et_machineDate;
    @BindView(R.id.tv_version)
    TextView tv_version;

    @BindView(R.id.ble_state)
    TextView ble_state;
    private BluetoothAdapter mBtAdapter;
    private final List<MDevice> list = new ArrayList<>();
    private DevicesAdapter adapter;
    private BluetoothGattCharacteristic notifyCharacteristic = null;
    private BluetoothGattCharacteristic writeCharacteristic = null;
    private Handler msgHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String obj = (String) msg.obj;
            switch (msg.what) {
                case 20:
                    String readIDResponse = DataUtils.getReadIDResponse(obj);
                    et_machine.setText(readIDResponse);
                    break;
                case 21:
                    String s = "版本：" + DataUtils.getReadVersionResponse(obj);
                    SharedPreference.saveString(getContext(), "version", s);
                    tv_version.setText(s);
                    break;
                case 23:
                    writeOption(DataUtils.sendReadIDCMD());
                    break;
                case 22:
                    writeOption(DataUtils.sendReadVersionCMD());
                    break;

            }
        }
    };
    private String TAG = "Tag";

    public BleFragment() {
    }

    //停止扫描
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }

        }
    };
    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null)
                progressDialog.dismiss();
            disconnectDevice();
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        int state = BluetoothLeService.getConnectionState();
        if (state == 0) {
            startScan();
            recyclerView.setVisibility(View.VISIBLE);
            ll_ble.setVisibility(View.GONE);
        } else {
            String version = SharedPreference.getString(getActivity(), "version", null);
            tv_version.setText(version);
            recyclerView.setVisibility(View.GONE);
            ll_ble.setVisibility(View.VISIBLE);
            et_bleName.setText(currentDevName);
            et_machineDate.setText(Utils.GetDate());
        }
    }

    @OnClick(R.id.btn_set)
    public void setCmd() {
        boolean currentModel = myApplication.isCurrentModel();
        if (currentModel) {
            String trim = et_machine.getText().toString().trim();
            int length = trim.length();
            if (length == 14) {
                byte[] bytes = DataUtils.sendWriteIDCMD(trim);
                writeOption(bytes);
            } else {
                Toast.makeText(myApplication, "请输入14位标准长度ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(myApplication, "当前为测量模式，请切换维护模式操作", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.tv_version)
    public void getVersion() {
        byte[] bytes = DataUtils.sendReadVersionCMD();
        writeOption(bytes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble, container, false);
        ButterKnife.bind(BleFragment.this, view);
        myApplication = (MyApplication) getActivity().getApplication();
        //检查蓝牙
        ll_ble.setVisibility(View.GONE);
        checkBleSupportAndInitialize();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        initEvent();//初始化事件
        initbleFragment();
        EventBus.getDefault().register(BleFragment.this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //搜索按钮点击事件
            case R.id.fab_search:
                //如果有连接先关闭连接
                disconnectDevice();
                onRefresh();
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initBroadcast();//初始化广播
        initService();//初始化服务
    }

    /**
     * 初始化服务
     */
    private void initService() {
        Intent gattServiceIntent = new Intent(getActivity().getApplicationContext(), BluetoothLeService.class);
        getActivity().startService(gattServiceIntent);
    }

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        //注册广播接收者，接收消息
        getActivity().registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //搜索按钮点击事件
        fabSearch.setOnClickListener(this);
    }

    /**
     * ble 停止扫描
     */
    private void stopScan() {
        mBtAdapter.cancelDiscovery();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        hander.removeCallbacks(stopScanRunnable);
    }

    /**
     * 初始化blefragment
     */
    private void initbleFragment() {
        //给recyclerView   设置布局样式
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        //获取并设置设备适配器
        adapter = new DevicesAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        //recyclerView  添加条目效果
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                adapter.setDelayStartAnimation(false);
                return false;
            }

            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        //adapter 点击事件
        adapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            public void onItemClick(View itemView, int position) {
                stopScan();
                showProgressDialog();
                connectDevice(list.get(position).getDevice());
            }
        });
    }

    /**
     * 显示连接动画
     */
    private void showProgressDialog() {
        progressDialog = new MaterialDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progressbar_item, null);
        progressDialog.setView(view).show();
    }

    /**
     * /准备列表视图并开始扫描
     */
    public void onRefresh() {
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        startScan();//开始扫描
    }

    /**
     * 开始扫描入口
     */
    private void startScan() {
        // 开始之前先停掉之前的
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            hander.removeCallbacks(stopScanRunnable);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        //10秒后停止扫描
        hander.postDelayed(stopScanRunnable, 10000);

    }

    /**
     * 发现设备时 处理方法
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    MDevice mDev = new MDevice(device, rssi);
                    if (list.contains(mDev))
                        return;
                    list.add(mDev);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    /**
     * 检查蓝牙是否可用
     */
    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.device_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(getActivity(), R.string.device_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

    }

    /**
     * ble 连接
     */
    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
//        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED) BluetoothLeService.disconnect();
        BluetoothLeService.connect(currentDevAddress, currentDevName, getActivity());
    }


    /**
     * ble 取消连接
     */
    private void disconnectDevice() {
        BluetoothLeService.disconnect();
    }

    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                handlerReceiver(context, intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("RestrictedApi")
    private void handlerReceiver(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
            String response = Utils.ByteArraytoHex(array);
            if (response.contains("7D7B") && response.contains("7D7D")) {
                String cmd = response.substring(4, 6);
                String ex = response.substring(6, 8);
                if (cmd.equals(DataUtils.CMD_ID_CODE)) {
                    if (ex.equals(DataUtils.EXTEND_WRITE_RESPONSE_CODE)) {
                        boolean writeResponse = DataUtils.getWriteResponse(response);
                        if (writeResponse) {
                            Toast.makeText(context, "设备ID设置完成", Toast.LENGTH_SHORT).show();
                        }
                    } else if (ex.equals(DataUtils.EXTEND_READ_RESPONSE_CODE)) {
                        Message msg = new Message();
                        msg.obj = response;
                        msg.what = 20;
                        msgHandler.sendMessage(msg);
                    }
                } else if (cmd.equals(DataUtils.CMD_VERSION_CODE)) {
                    if (ex.equals(DataUtils.EXTEND_WRITE_RESPONSE_CODE)) {

                    } else if (ex.equals(DataUtils.EXTEND_READ_RESPONSE_CODE)) {
                        Message msg = new Message();
                        msg.obj = response;
                        msg.what = 21;
                        msgHandler.sendMessage(msg);
                    }
                }
            }

        }
        // Status received when connected to GATT Server
        //连接成功
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            //搜索服务
            progressDialog.dismiss();
            BluetoothLeService.discoverServices();
            ble_state.setText("已连接");
            fabSearch.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            ll_ble.setVisibility(View.VISIBLE);
            et_bleName.setText(currentDevName);
            et_machineDate.setText(Utils.GetDate());
            myApplication.setConnect(true);
            msgHandler.sendEmptyMessageDelayed(22, 2000);
            msgHandler.sendEmptyMessageDelayed(23, 3000);
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            hander.removeCallbacks(dismssDialogRunnable);
            progressDialog.dismiss();
            for (int i = 0; i < 3; i++) {
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            }
        } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
            boolean connect = myApplication.isConnect();
            dissAndReconnect();
            if (!connect) {
                BluetoothLeService.close();
                BluetoothLeService.connect(BluetoothLeService.getmBluetoothDeviceAddress());
            }

        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            dissAndReconnect();
            BluetoothLeService.close();
            BluetoothLeService.connect(BluetoothLeService.getmBluetoothDeviceAddress());
        }
    }

    @SuppressLint("RestrictedApi")
    private void dissAndReconnect() {
        ble_state.setText("未连接");
        fabSearch.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        ll_ble.setVisibility(View.GONE);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        //connect break (连接断开)
        myApplication.setConnect(false);

    }


    /**
     * Getting the GATT Services
     * 获得服务
     *
     * @param gattServices
     */

    public void prepareGattServices(List<BluetoothGattService> gattServices) {
        try {
            if (gattServices == null)
                return;
            BluetoothGattService service = null;
            for (BluetoothGattService gattService : gattServices) {
                String uuid = gattService.getUuid().toString();
                if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE) || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE))
                    continue;
                service = gattService;
            }
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            Thread.sleep(1000);
            for (BluetoothGattCharacteristic c : characteristics) {
                String sc = c.getUuid().toString();
                if (sc.equals(GattAttributes.USR_NOTIFYCHARACTER)) {
                    notifyCharacteristic = c;
                    continue;
                }
                if (sc.equals(GattAttributes.USR_WRITECHARACTER)) {
                    writeCharacteristic = c;
                    continue;
                }
            }
            Thread.sleep(1000);
            BluetoothLeService.setCharacteristicNotification(notifyCharacteristic, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGattUpdateReceiver != null) {
            getActivity().unregisterReceiver(mGattUpdateReceiver);
        }
    }


    /**
     * 向BLE蓝牙发送数据
     */
    public void writeOption(byte[] hexString) {
        if (writeCharacteristic != null) {
            writeCharacteristic(writeCharacteristic, hexString);
        } else {
            BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_WRITECHARACTER),-1,-1);
            prepareGattServices(BluetoothLeService.getSupportedGattServices());
            writeCharacteristic(writeCharacteristic, hexString);
            Log.i(TAG, "创建writeCharacteristic");
        }

    }

    // Writing the hexValue to the characteristics
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(BleFragment.this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBleReceiverData(MessageEvent event) {
        try {
            String message = event.getMessage();
            Boolean send = event.getSend();
            boolean currentModel = myApplication.isCurrentModel();
            if (send) {
                String model = message.substring(4, 6);
                String s = message.substring(6, 8);
                boolean equals = DataUtils.EXTEND_READ_CODE.equals(s);
                if (currentModel || equals || model.equals(DataUtils.CMD_MODEL_CODE)) {
                    if (writeCharacteristic != null) {
                        writeCharacteristic(writeCharacteristic, Utils.hexStringToByteArray(message));
                    } else {
                        BluetoothLeService.closeAndDisconnect();
//                        Toast.makeText(myApplication, "writeCharacteristic为空，写入失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(myApplication, "当前为测量模式，请切换维护模式操作", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }


    }
}
