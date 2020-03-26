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
import android.widget.ImageButton;
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
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic indicateCharacteristic;
    private MyApplication myApplication;
    private String properties;
    private List<Option> options = new ArrayList<>();
    private Option currentOption;
    private boolean isHexSend;
    private boolean nofityEnable;
    private boolean isDebugMode;


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
                            BluetoothGattCharacteristic requiredCharacteristic = myApplication.getCharacteristic();
                            String uuidRequired = requiredCharacteristic.getUuid().toString();
                            String receivedUUID = intent.getStringExtra(Constants.EXTRA_BYTE_UUID_VALUE);
                            byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                            Log.i(TAG, "收到消息: "+Utils.ByteArraytoHex(array));
                            if (isDebugMode){
                                Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,formatMsgContent(array));
                                notifyAdapter(msg);
                            }else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
                                Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,formatMsgContent(array,MyApplication.serviceType));
                                notifyAdapter(msg);
                            }
                        }
                    }
                }
            }

            //write characteristics succcess
            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS)){
                list.get(list.size()-1).setDone(true);
                adapter.notifyItemChanged(list.size()-1);
            }
            //connect break (连接断开)
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)){
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
        initProperties();
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt>=21){
            //设置最大发包、收包的长度为512个字节
            if(BluetoothLeService.requestMtu(512)){
                Toast.makeText(this,getString(R.string.transmittal_length,"512"),Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }
    }

    private void initCharacteristics(){
        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
        if (characteristic.getUuid().toString().equals(GattAttributes.USR_SERVICE)){
            isDebugMode = true;
            List<BluetoothGattCharacteristic> characteristics = ((MyApplication)getApplication()).getCharacteristics();
            for (BluetoothGattCharacteristic c :characteristics){
                if (Utils.getPorperties(this,c).equals("Notify")){
                    notifyCharacteristic = c;
                    continue;
                }

                if (Utils.getPorperties(this,c).equals("Write")){
                    writeCharacteristic = c;
                    continue;
                }
            }
            properties = "Notify & Write";

        }else {
            properties = Utils.getPorperties(this, characteristic);
            notifyCharacteristic = characteristic;
            readCharacteristic = characteristic;
            writeCharacteristic = characteristic;
            indicateCharacteristic = characteristic;
        }
    }

    private void initProperties() {
        if (TextUtils.isEmpty(properties))
            return;
        String[] property = properties.split("&");
        if (property.length == 1) {
            Option option = new Option(properties.trim(),Option.OPTIONS_MAP.get(properties.trim()));
            setOption(option);
        } else {
            for (int i=0;i<property.length;i++){
                String p = property[i];
                Option option = new Option();
                option.setName(p.trim());
                option.setPropertyType(Option.OPTIONS_MAP.get(p.trim()));
                options.add(option);
                if (i==0){
                  setOption(option);
                }
            }
        }
    }

    private void setOption(Option option){
        currentOption = option;
        switch (option.getPropertyType()){
            case PROPERTY_NOTIFY:
                if (!nofityEnable)
                    btnOption.setText(Option.NOTIFY);
                else
                    btnOption.setText(Option.STOP_NOTIFY);
                break;

        }
    }

    @OnClick(R.id.btn_option)
    public void onOptionClick() {
        Option.OPTION_PROPERTY propertyType = currentOption.getPropertyType();
        switch (propertyType){
            case PROPERTY_NOTIFY:
                notifyOption();
                break;
            case PROPERTY_INDICATE:
                break;
            case PROPERTY_READ:
                readOption();
                break;
            case PROPERTY_WRITE:
                break;
        }
    }

    @OnClick(R.id.btn_send)
    public void onSendClick(){
        writeOption();
    }

    private void notifyOption(){
           nofityEnable = true;
           btnOption.setText(Option.STOP_NOTIFY);
           prepareBroadcastDataNotify(notifyCharacteristic);
           Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.NOTIFY);
           notifyAdapter(msg);
       }

    private void readOption(){
        Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.READ);
        notifyAdapter(msg);
        prepareBroadcastDataRead(readCharacteristic);
    }

    /**
     * 向BLE蓝牙发送数据
     */
    private void writeOption(){
        String text = etWrite.getText().toString();
        if (TextUtils.isEmpty(text)){
            AnimateUtils.shake(etWrite);
            return;
        }
        if (isHexSend){
            text = text.replace(" ","");
            if (!Utils.isRightHexStr(text)){
                AnimateUtils.shake(etWrite);
                return;
            }
            byte[] array = Utils.hexStringToByteArray(text);
            writeCharacteristic(writeCharacteristic, array);
        }else {
            // AT+指令
            if(Utils.isAtCmd(text))
                text = text + "\r\n";
            try {
                byte[] array = text.getBytes("US-ASCII");
                writeCharacteristic(writeCharacteristic,array);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }

        }

        Message msg = new Message(Message.MESSAGE_TYPE.SEND,text);
        notifyAdapter(msg);
    }


    private void notifyAdapter(Message msg){
        list.add(msg);
        adapter.notifyLastItem();
        rvMsg.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    private String formatMsgContent(byte[] data){
        String s = Utils.ByteArraytoHex(data);
        Log.i(TAG, "formatMsgContent1: "+s);
        String s1 = Utils.byteToASCII(data);
        return "HEX:"+ s +"  (ASSCII:"+ s1 +")";
    }

    private String formatMsgContent(byte[] data,MyApplication.SERVICE_TYPE type){
        Log.i(TAG, "formatMsgContent2: "+new String(data));
        String res = "ASSCII:"+Utils.byteToASCII(data);
        switch (type){
            case TYPE_STR:
                res += "  (ASSCII:"+Utils.byteToASCII(data)+")";
                break;
            case TYPE_USR_DEBUG:
                res += "  (ASSCII:"+Utils.byteToASCII(data)+")";
                break;
            case TYPE_NUMBER:
                res+= "  (int:"+Utils.ByteArrToIntStr(data)+")";
                break;
            case TYPE_OTHER:
                res += " (HEX:"+Utils.ByteArraytoHex(data)+")";
                break;
        }
        return  res;
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
        switch (item.getItemId()){
            case R.id.menu_hex_send:
                isHexSend = true;
                if (!TextUtils.isEmpty(text)){
                    if(Utils.isAtCmd(text))
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
     * Preparing Broadcast receiver to broadcast read characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataRead(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            BluetoothLeService.readCharacteristic(characteristic);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }

    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void stopBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, false);
        }
    }



    /**
     * Stopping Broadcast receiver to broadcast indicate characteristics
     *
     * @param characteristic
     */
    void stopBroadcastDataIndicate(BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BluetoothLeService.setCharacteristicIndication(characteristic, false);
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

    private void showDialog(String info){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok,new View.OnClickListener(){
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
