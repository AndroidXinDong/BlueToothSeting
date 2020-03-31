package com.usr.usrsimplebleassistent.fragments;

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
import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.GattDetailActivity;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.DataUtils;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MDevice;
import com.usr.usrsimplebleassistent.bean.MService;

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
import me.drakeet.materialdialog.MaterialDialog;

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
    private MaterialDialog alarmDialog;
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

    @BindView(R.id.ble_state)
    TextView ble_state;
    public BleFragment() {
    }

    public static BleFragment getInstance(){
        BleFragment fragment = new BleFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }



    //停止扫描
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null){
                mBluetoothAdapter.startLeScan(mLeScanCallback);
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
    private BluetoothAdapter mBtAdapter;
    private final List<MDevice> list = new ArrayList<>();
    private DevicesAdapter adapter;


    @Override
    public void onResume() {
        super.onResume();
        int state = BluetoothLeService.getConnectionState();
        if (state==0){
            startScan();
        }
    }
    @OnClick(R.id.btn_set)
    public void setCmd(){
        byte[] bytes = DataUtils.sendCrCCMD("20", "66", "");
        writeOption(bytes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble,container,false);
        ButterKnife.bind(BleFragment.this,view);
        myApplication = (MyApplication)getActivity(). getApplication();
        //检查蓝牙
        ll_ble.setVisibility(View.GONE);
        checkBleSupportAndInitialize();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        initEvent();//初始化事件
        initbleFragment();
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
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
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
        //10秒后停止扫描
        hander.postDelayed(stopScanRunnable, 20000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
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
        @SuppressLint("RestrictedApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        if (myApplication != null) {
                            byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                            Log.i("Tag", "BLE: "+Utils.ByteArraytoHex(array));
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
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Services Discovered from GATT Server
                hander.removeCallbacks(dismssDialogRunnable);
                progressDialog.dismiss();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                ble_state.setText("未连接");
                fabSearch.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                ll_ble.setVisibility(View.GONE);
                progressDialog.dismiss();
                //connect break (连接断开)
                showDialog(getString(R.string.conn_disconnected_home));
            }
        }
    };

    private void showDialog(String info) {
        if (alarmDialog != null)
            return;
        alarmDialog = new MaterialDialog(getActivity());
        alarmDialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDialog.dismiss();
                        alarmDialog = null;
                    }
                });
        alarmDialog.show();
    }

    /**
     * Getting the GATT Services
     * 获得服务
     *
     * @param gattServices
     */
    private final List<BluetoothGattCharacteristic> cList = new ArrayList<>();

    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);
        List<MService> services = myApplication.getServices();

        if (services.size() > 0) {
            MService mService = services.get(0);
            BluetoothGattService service = mService.getService();
            myApplication.setCharacteristics(service.getCharacteristics());
            //这里为了方便暂时直接用Application serviceType 来标记当前的服务，应该是和上面的代码合并
            MyApplication.serviceType = MyApplication.SERVICE_TYPE.TYPE_USR_DEBUG;
            List<BluetoothGattCharacteristic> characteristics = myApplication.getCharacteristics();
            cList.addAll(characteristics);
            BluetoothGattCharacteristic usrVirtualCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(GattAttributes.USR_SERVICE), -1, -1);
            cList.add(usrVirtualCharacteristic);
            if (cList.size()>=2){
                myApplication.setCharacteristic(cList.get(2));
                initCharacteristics();
                prepareBroadcastDataNotify(notifyCharacteristic);
            }else {
                progressDialog.dismiss();
                Toast.makeText(myApplication, "未能获得通信服务", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(myApplication, "未能获得通信服务", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
    private void prepareData(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        List<MService> list = new ArrayList<>();
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE) || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE))
                continue;
            String name = GattAttributes.lookup(gattService.getUuid().toString(), "UnkonwService");
            MService mService = new MService(name, gattService);
            list.add(mService);
        }

        myApplication.setServices(list);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGattUpdateReceiver != null) {
           getActivity().unregisterReceiver(mGattUpdateReceiver);
        }
    }
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    /**
     * 向BLE蓝牙发送数据
     */
    private void writeOption(byte[] hexString) {

//        byte[] array = Utils.hexStringToByteArray(hexString);
        writeCharacteristic(writeCharacteristic, hexString);
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
}
