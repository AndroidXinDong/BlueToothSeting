package com.usr.usrsimplebleassistent;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MDevice;
import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.firmware.bleconnect.BleUtil;
import com.usr.usrsimplebleassistent.firmware.util.GetFileSizeFromPath;
import com.usr.usrsimplebleassistent.firmware.util.GetPathFromUri;
import com.usr.usrsimplebleassistent.firmware.permission.MPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * 固件选择连接蓝牙Activity
 *
 * @author 李雷红 2017/12/21  version 1.0
 */
public class FirmwareActivity extends MyBaseActivity implements View.OnClickListener {


    private static final String TAG = "FirmwareActivity";
    @BindView(R.id.firmware_info_title)
    LinearLayout firmwareInfoTitle;
    @BindView(R.id.firmware_firware_info)
    TextView firmwareFirwareInfo;
    @BindView(R.id.firmware_search)
    Button firmwareSearch;
    @BindView(R.id.firmware_recycleviewspp)
    RecyclerView firmwareRecycleviewspp;
    //请求sd卡权限Code码
    private static final int REQUECT_CODE_SDCARD = 10001;
    //打开sd卡文件系统
    private static final int OPEN_SD_FILESYSTEMT = 20001;
    //获得的文件路径
    private String filePath;
    //获得的文件
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);
        bindToolBar();
        BleUtil.checkBleSupportAndInitialize(this);
        setOnClick();
        initSPP();
    }

    private void setOnClick() {
        firmwareSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firmware_search:
                searchBleDev();
                break;
        }
    }

    /**
     * 创建menu菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_firmware, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //打开文件浏览器
            case R.id.menu_firmware_filesystem:
                //如果没有权限,申请权限
                if (isGrantExternalRW(this)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*.bin");   //打开文件类型
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, OPEN_SD_FILESYSTEMT);
                }
                break;
            //检测服务器上面的固件，并进行更新
            case R.id.menu_firmware_checkupdate:
                Toast.makeText(this, "正在研发中...", Toast.LENGTH_LONG).show();
                break;
            //清除本地固件的文件
            case R.id.menu_firmware_clear:
                Toast.makeText(this, "正在研发中...", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        return MPermissions.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD);
    }

    /**
     * 权限回调函数
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUECT_CODE_SDCARD) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission Denied
                Toast.makeText(this, "打开SD卡权限才能更好的使用应用", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == OPEN_SD_FILESYSTEMT) {
                disponseSelFile(data);
            }
        }
    }

    /**
     * 处理选中的文件
     *
     * @param data
     */
    private void disponseSelFile(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();//得到uri，
        filePath = GetPathFromUri.getPath(this, uri);
        if (!filePath.endsWith(".bin")) {
            Toast.makeText(this, "您选中的不是固件", Toast.LENGTH_LONG).show();
            filePath = null;
            return;
        }
        file = new File(filePath);
        firmwareInfoTitle.setVisibility(View.VISIBLE);
        firmwareFirwareInfo.setText("固件名称: " + file.getName() + "\n固件大小: " + GetFileSizeFromPath.getFileSize(filePath));
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBroadcastReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mGattUpdateReceiver != null) {
            unregisterReceiver(mGattUpdateReceiver);
        }
    }

    //----------------------------------蓝牙-------------------------------------

    private BluetoothAdapter mBtAdapter;
    private MDevice mDevice = new MDevice();
    private List<MDevice> deviceList = new ArrayList<>();
    private DevicesAdapter devicesAdapter;
    private MaterialDialog progressDialog;
    private BluetoothDevice mBluetoothDevice;
    private Handler hander;
    boolean isShowingDialog = false;
    private String currentDevAddress;
    private String currentDevName;
    //如果还在搜索设备中，关掉了搜索功能，让其不显示“搜索完成”提示
    private boolean isManualEnd;
    //----------------------------------spp搜索-------------------------------------

    /**
     * 设置SPP初始化动作
     */
    private void initSPP() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //给recyclerView   设置布局样式
        LinearLayoutManager llm = new LinearLayoutManager(this);
        firmwareRecycleviewspp.setLayoutManager(llm);
        //初始化搜索到的设备列表
        devicesAdapter = new DevicesAdapter(deviceList, this);
        firmwareRecycleviewspp.setAdapter(devicesAdapter);
        hander = new Handler();
        setSPPOnClick();

    }

    private void setSPPOnClick() {
        devicesAdapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            public void onItemClick(View itemView, int position) {
                //显示动画
                showProgressDialog();
                // 准备连接设备，关闭服务查找
                if (mBtAdapter.isDiscovering()) {
                    isManualEnd = true;
                    mBtAdapter.cancelDiscovery();
                }
                // 得到蓝牙对象
                mBluetoothDevice = deviceList.get(position).getDevice();
                //两秒后关闭连接动画
//                hander.postDelayed(dismssDialogRunnable, 2000);
                connectDevice(mBluetoothDevice);
            }
        });
    }


    /**
     * 进度动画
     */
    private void showProgressDialog() {
        progressDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }


    /**
     * 搜索蓝牙设备
     */
    private void searchBleDev() {
        isManualEnd = false;
        deviceList.clear();
        // 关闭再进行的服务查找
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //并重新开始
        mBtAdapter.startDiscovery();
    }


    // 查找到设备和搜索完成action监听器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //得到信号强度
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                //获取设备类
                MDevice mDev = new MDevice(device, rssi);
                mDevice.setDevice(device);
                mDevice.setRssi(rssi);
                if (deviceList.contains(mDev))
                    return;
                deviceList.add(mDev);
                devicesAdapter.notifyDataSetChanged();
                firmwareSearch.setText("devices：" + deviceList.size());
                // 搜索完成action
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (isManualEnd)
                    Toast.makeText(FirmwareActivity.this, "搜索完成", Toast.LENGTH_SHORT).show();
                firmwareSearch.setText("搜索蓝牙设备");
            }
        }
    };

    private void initBroadcastReceiver() {
        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        // 注册查找结束action接收器
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    //-------------------------------------连接----------------------------------------

    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
        if (com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService.getConnectionState() != com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService.STATE_DISCONNECTED)
            com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService.disconnect();
        com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService.connect(currentDevAddress, currentDevName, this);
    }


    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("--------------------->连接成功");
                //搜索服务
                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                hander.removeCallbacks(dismssDialogRunnable);
                progressDialog.dismiss();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                progressDialog.dismiss();
                //connect break (连接断开)
                showDialog(R.string.conn_disconnected_home);
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

    private void disconnectDevice() {
        isShowingDialog = false;
        BluetoothLeService.disconnect();
    }

    /**
     * Getting the
     * GATT Services
     * 获得服务
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);

        Intent intent = new Intent(this, FirmwareUpdActivity.class);
        intent.putExtra("dev_name", currentDevName);
        intent.putExtra("dev_mac", currentDevAddress);
        intent.putExtra("firmware", filePath);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    /**
     * Prepare GATTServices data.
     *
     * @param
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
        ((MyApplication) getApplication()).setServices(list);
    }
}
