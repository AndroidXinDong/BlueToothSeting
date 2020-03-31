package com.usr.usrsimplebleassistent;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.MessagesAdapter;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.Message;
import com.usr.usrsimplebleassistent.bean.Option;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

public class GattDetailActivity extends MyBaseActivity {
    private final String TAG = "Tag";
    @BindView(R.id.et_write)
    EditText etWrite;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.rl_write)
    RelativeLayout rlWrite;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private MyApplication myApplication;


    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
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
                            Log.i(TAG, "Gatt: "+Utils.byteToASCII(array));
                        }
                    }
                }
            }
            // 数据发送成功
            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS)) {
                Log.i(TAG, "onReceive: 数据发送成功");
            }
            //connect break (连接断开)
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                AnimateUtils.showDialog(getString(R.string.conn_disconnected),GattDetailActivity.this);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatt_detail);
        bindToolBar();
        myApplication = (MyApplication) getApplication();
        initCharacteristics();
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    private void initCharacteristics() {
        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
        String uuid = characteristic.getUuid().toString();
        if (uuid.equals(GattAttributes.USR_SERVICE)) {
            List<BluetoothGattCharacteristic> characteristics = myApplication.getCharacteristics();
            for (BluetoothGattCharacteristic c : characteristics) {
                String porpertie = Utils.getPorperties(this, c);
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

    @Override
    protected void onResume() {
        super.onResume();
        notifyOption();
    }

    @OnClick(R.id.btn_send)
    public void onSendClick() {
        writeOption("123456");
    }

    private void notifyOption() {
        prepareBroadcastDataNotify(notifyCharacteristic);
    }

    /**
     * 向BLE蓝牙发送数据
     */
    private void writeOption(String hexString) {
            if (!Utils.isRightHexStr(hexString)) {
                AnimateUtils.shake(etWrite);
                return;
            }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }
}
