package com.usr.usrsimplebleassistent;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    @BindView(R.id.btn_option)
    Button btnOption;
    @BindView(R.id.lv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.et_write)
    EditText etWrite;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.rl_write)
    RelativeLayout rlWrite;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;

    private final List<Message> list = new ArrayList<>();
    private MessagesAdapter adapter;
    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private MyApplication myApplication;
    private String properties;
    private boolean isHexSend;


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
                            Log.i(TAG, "收到消息: " + Utils.ByteArraytoHex(array));
                            Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE, formatMsgContent(array));
                            notifyAdapter(msg);
                        }
                    }
                }
            }
            // 数据发送成功
            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS)) {
                list.get(list.size() - 1).setDone(true);
                adapter.notifyItemChanged(list.size() - 1);
            }
            //connect break (连接断开)
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                showDialog(getString(R.string.conn_disconnected));
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatt_detail);
        ButterKnife.bind(this);
        bindToolBar();
        myApplication = (MyApplication) getApplication();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvMsg.setLayoutManager(llm);
        adapter = new MessagesAdapter(this, list);
        rvMsg.setAdapter(adapter);
        initCharacteristics();
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    private void initCharacteristics() {
        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
        String uuid = characteristic.getUuid().toString();
        if (uuid.equals(GattAttributes.USR_SERVICE)) {
            List<BluetoothGattCharacteristic> characteristics = ((MyApplication) getApplication()).getCharacteristics();
            for (BluetoothGattCharacteristic c : characteristics) {
                if (Utils.getPorperties(this, c).equals("Notify")) {
                    notifyCharacteristic = c;
                    continue;
                }
                if (Utils.getPorperties(this, c).equals("Write")) {
                    writeCharacteristic = c;
                    continue;
                }
            }
            properties = "Notify & Write";
        } else {
            properties = Utils.getPorperties(this, characteristic);
            notifyCharacteristic = characteristic;
            writeCharacteristic = characteristic;
        }
    }

    @OnClick(R.id.btn_option)
    public void onOptionClick() {
        notifyOption();
    }

    @OnClick(R.id.btn_send)
    public void onSendClick() {
        writeOption();
    }

    private void notifyOption() {
        prepareBroadcastDataNotify(notifyCharacteristic);
        Message msg = new Message(Message.MESSAGE_TYPE.SEND, Option.NOTIFY);
        notifyAdapter(msg);
    }

    /**
     * 向BLE蓝牙发送数据
     */
    private void writeOption() {
        String text = etWrite.getText().toString();
        if (TextUtils.isEmpty(text)) {
            AnimateUtils.shake(etWrite);
            return;
        }
        if (isHexSend) {
            text = text.replace(" ", "");
            if (!Utils.isRightHexStr(text)) {
                AnimateUtils.shake(etWrite);
                return;
            }
            byte[] array = Utils.hexStringToByteArray(text);
            writeCharacteristic(writeCharacteristic, array);
        } else {
            // AT+指令
            if (Utils.isAtCmd(text))
                text = text + "\r\n";
            try {
                byte[] array = text.getBytes("US-ASCII");
                writeCharacteristic(writeCharacteristic, array);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }

        }

        Message msg = new Message(Message.MESSAGE_TYPE.SEND, text);
        notifyAdapter(msg);
    }


    private void notifyAdapter(Message msg) {
        list.add(msg);
        adapter.notifyLastItem();
        rvMsg.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    private String formatMsgContent(byte[] data) {
        String s = Utils.ByteArraytoHex(data);
        String s1 = Utils.byteToASCII(data);
        return "HEX:" + s + "  (ASSCII:" + s1 + ")";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        String text = etWrite.getText().toString();
        switch (item.getItemId()) {
            case R.id.menu_hex_send:
                isHexSend = true;
                if (!TextUtils.isEmpty(text)) {
                    if (Utils.isAtCmd(text))
                        text = text + "\r\n";
                    try {
                        etWrite.setText(Utils.ByteArraytoHex(text.getBytes("US-ASCII")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        etWrite.setText("");
                    }
                }
                break;
            case R.id.menu_asscii_send:
                isHexSend = false;
                etWrite.setText("");
                break;
            case R.id.menu_clear_display:
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }
        return false;
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

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic, bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(String info) {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }
}
