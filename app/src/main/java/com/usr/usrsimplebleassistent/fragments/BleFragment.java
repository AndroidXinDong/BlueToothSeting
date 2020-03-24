package com.usr.usrsimplebleassistent.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.bean.MDevice;
import com.usr.usrsimplebleassistent.views.RevealBackgroundView;
import com.usr.usrsimplebleassistent.views.RevealSearchView;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class BleFragment extends Fragment  {
    private Context mContext;
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private OnRunningAppRefreshListener onRunningAppRefreshListener;
    private View rootView;
    private RecyclerView recyclerView;
    private String currentDevAddress;
    private String currentDevName;
    private DevicesAdapter adapter;

    private final List<MDevice> list = new ArrayList<>();
    private static BluetoothAdapter mBluetoothAdapter;
    private Handler hander;
    boolean isShowingDialog = false;
    private MaterialDialog alarmDialog;
    private MaterialDialog progressDialog;
    private RevealSearchView revealSearchView;
    private RevealBackgroundView revealBackgroundView;
    private TextView tvSearchDeviceCount;
    private RelativeLayout rlSearchInfo;
    private boolean scaning;
    private FloatingActionButton fabSearch;
    private FloatingActionButton searchDevice;
    private Button stopSearching;
    private String mode;
    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null)
                progressDialog.dismiss();
            disconnectDevice();
        }
    };
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    };

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onRunningAppRefreshListener = (OnRunningAppRefreshListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRunningAppRefreshListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mPackageManager =  mContext.getPackageManager();
        rootView = inflater.inflate(
                R.layout.ble_fragment, container, false);
        hander = new Handler();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("-------------->onRefresh");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onRefresh() {
        System.out.println("-------------->onRefresh");
        // Prepare list view and initiate scanning
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        startScan();

    }

    private void startScan() {
        scanPrevious21Version();
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        //10秒后停止扫描
        //hander.postDelayed(stopScanRunnable,10000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     * 发现设备时回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MDevice mDev = new MDevice(device, rssi);
                    if (list.contains(mDev))
                        return;
                    list.add(mDev);
                    tvSearchDeviceCount.setText(getString(R.string.search_device_count, list.size()));
                }
            });
        }
    };


    private void stopScan() {
        revealSearchView.setVisibility(View.GONE);
        //停止雷达动画
        revealSearchView.stopAnimate();
        //涟漪动画回缩
        revealBackgroundView.endFromEdge();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        hander.removeCallbacks(stopScanRunnable);
    }



    private void initShow() {
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(llm);

        adapter = new DevicesAdapter(list, mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                adapter.setDelayStartAnimation(false);
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        checkBleSupportAndInitialize();

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("-------------->onActivityCreated");

    }

    public void onStart() {
        super.onStart();
        System.out.println("RunningAppFragment-------------->onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RunningAppFragment-------------->onResume");
        //如果有连接先关闭连接
        disconnectDevice();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("RunningAppFragment-------------->onPause");
    }

    @Override
    public void onStop() {
        System.out.println("RunningAppFragment-------------->onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("RunningAppFragment-------------->onDestroyView");
    }

    @Override
    public void onDestroy() {
        System.out.println("-------------->onDestroyView");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        System.out.println("-------------->onDestroyView");
        super.onDetach();
    }

    private void disconnectDevice() {
        isShowingDialog = false;
        BluetoothLeService.disconnect();
    }

    private void showProgressDialog() {
        progressDialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }

    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED)
            BluetoothLeService.disconnect();

        BluetoothLeService.connect(currentDevAddress, currentDevName, getActivity());
    }

    /**
     * 获得蓝牙适配器
     */
    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(mContext,
                    R.string.device_ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }


    public interface OnRunningAppRefreshListener {
        public void onRunningAppRefreshed();
    }


}
