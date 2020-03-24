package com.usr.usrsimplebleassistent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 固件升级Activity
 *
 * @author 李雷红 2017/12/21  version 1.0
 */
public class FirmwareUpdActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "FirmwareUpdActivity";

    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    @BindView(R.id.view_filter)
    View filterView;
    @BindView(R.id.view_shadow)
    View viewShadow;
    @BindView(R.id.lv_services)
    ListView lvServices;
    @BindView(R.id.iv_ble)
    ImageView ivBle;
    @BindView(R.id.tv_service_name)
    TextView tvServiceName;
    @BindView(R.id.tv_service_mac)
    TextView tvServiceMac;
    @BindView(R.id.tv_service_firm)
    TextView tvServiceFirm;
    @BindView(R.id.tv_service_count)
    TextView tvServiceCount;
    @BindView(R.id.iv_firmware_update)
    ImageView ivFirmwareUpdate;
    private ProgressBar proBar;
    private String dev_name;
    private String dev_mac;
    private String firmwarePath;
    private MyApplication myApplication;
    private final List<MService> serviceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_upd);
        bindToolBar();
        setIntentContent();
        setAnimator(savedInstanceState);
        setOnClick();

    }

    /**
     * 设置点击事件
     */
    private void setOnClick() {
        ivFirmwareUpdate.setOnClickListener(this);
    }

    /**
     * 设置动画效果
     */
    private void setAnimator(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            filterView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    filterView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startAnimation();
                    return true;
                }
            });
        }
    }

    /**
     * 设置上个界面传递过来的值
     */
    private void setIntentContent() {
        Intent intent = getIntent();
        dev_name = intent.getStringExtra("dev_name");
        dev_mac = intent.getStringExtra("dev_mac");
        firmwarePath = intent.getStringExtra("firmware");
        myApplication = (MyApplication) getApplication();
        List<MService> services = myApplication.getServices();
        serviceList.addAll(services);
        tvServiceName.setText("NAME:" + dev_name);
        tvServiceMac.setText("MAC:" + dev_mac);
        tvServiceFirm.setText("FIRMWARE:" + firmwarePath.substring(firmwarePath.lastIndexOf("/") + 1));
        tvServiceCount.setText("SERVICES:" + String.valueOf(serviceList.size()));

        setOATBle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_firmware_update:
                Toast.makeText(this, firmwarePath + "", Toast.LENGTH_SHORT).show();
                oadUpdata113();
                break;
        }
    }


    //-----------------------------------------------------------------------
    private BluetoothGattService mOadService;
    private BluetoothGattCharacteristic imageNotifyCharacter2;
    private BluetoothGattCharacteristic imageBlockRequestCharacter2;
    private BluetoothGatt mBluetoothGatt2;

    /**
     * 设置蓝牙空中升级的监听
     */
    private void setOATBle() {
        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
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
        }
    };


    /**
     * 固件升级
     */
    public void oadUpdata113() {
        List<BluetoothGattService> supportedGattServices = BluetoothLeService.getSupportedGattServices();
        for (int i = 0; i < supportedGattServices.size(); i++) {
            BluetoothGattService bluetoothGattService = supportedGattServices.get(i);
            if (bluetoothGattService.getUuid().equals(GattAttributes.USR_SERVICE)) {
                mOadService = bluetoothGattService;
            }
        }
        if (mOadService == null) {
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = mOadService.getCharacteristics();
        if (characteristics.size() == 2) {
            imageNotifyCharacter2 = characteristics.get(0);
            imageBlockRequestCharacter2 = characteristics.get(1);
        }
        mBluetoothGatt2 = BluetoothLeService.mBluetoothGatt;
        int EACH_PACKAGE_SIZE = 16;      //每次发送16个字节;
        InputStream is = null;
        int fileSize = 0;            //文件的大小
        byte[] fileByteArray = null;      //把文件读取到数组中
        try {
            //获取文件
            File file = new File(firmwarePath);
            is = new FileInputStream(file);
            fileSize = is.available();
            //把文件读取到数组中
            fileByteArray = new byte[fileSize];
            is.read(fileByteArray, 0, fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断有多少个包
        int packnum = fileSize / EACH_PACKAGE_SIZE;
        int lastNum = fileSize % EACH_PACKAGE_SIZE;        //检测是否有最后一个不满足16字节的包
        int lastPack = packnum;
        //总包数
        if (lastNum != 0) {
            lastPack++;
        }

        int FILE_HEADER_SIZE = 8;
        /**要发送的升级通知消息，FILE_HEADER_SIZE这里设置的是8，其实应该就发8个字节，但是demo中又来了个+2+2，索性就按照他的来*/
        byte[] prepareBuffer = new byte[FILE_HEADER_SIZE + 2 + 2];
        /**设置要发送的内容，从文件数组下标4将内容复制到prepareBuffer数组中01 00 00 7C 42 42 42 42一定包含这样类型的字节信息，其实就是通知蓝牙设备发送的镜像版本01
         * ，大小7C（嵌入式那边7C*4就是文件长度，我猜就是通过这个我们最后才不用发送结束信号，设备那边就能自动判断是否发送结束,有兴趣的可以打印出来要发送的文件的前12个字节看看）。*/
        System.arraycopy(fileByteArray, 4, prepareBuffer, 0, FILE_HEADER_SIZE);
        //首先配置信息发送到蓝牙设备
        imageNotifyCharacter2.setValue(prepareBuffer);
        mBluetoothGatt2.writeCharacteristic(imageNotifyCharacter2);

        //然后发送文件
        for (int i = 0; i < lastPack; i++) {
       /* *最终要发送的包还要加2个头字节，表示发送的包的索引，索引从0开始，索引从0开始，索引从0开始，重要的话说三遍，这是折磨我很久的坑，但是不知道是不是都是需要从0开始，
       我之前从1开始一直不行，低位在前，高位在后,比如第257个包，temp[0]就是1，temp[1]是1，都是16进制表示，还原到2进制就是0000 0001 0000 0001，这就是头2个字节组合后代表的数字257*/
            byte[] temp = new byte[EACH_PACKAGE_SIZE + 2 + 1];
            //低位
            temp[0] = (byte) (i & 0xff);
            //高位
            temp[1] = (byte) (i >> 8 & 0xff);

            Log.d("doInBackground", "正在发包" + i + "/" + new Gson().toJson(temp));
            //复制数组
            System.arraycopy(fileByteArray, i * EACH_PACKAGE_SIZE, temp, 2, EACH_PACKAGE_SIZE);
            //校验
            temp[temp.length - 1] = checkSum(temp);

            imageBlockRequestCharacter2.setValue(temp);
            mBluetoothGatt2.writeCharacteristic(imageBlockRequestCharacter2);         //写入成功启动
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * 校验和
     *
     * @param temp
     * @return
     */
    private byte checkSum(byte[] temp) {
        int i, sum = temp.length;
        for (i = 0; i < 8; i++)
            sum += temp[i];//将每个数相加
        if (sum > 0xff) {
            sum = ~sum;
            sum += 1;
        }
        return (byte) (sum & 0xff);
    }


    /**
     * 设置启动动画
     */
    private void startAnimation() {
        rlTop.setAlpha(0.0f);
        rlTop.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ObjectAnimator animator1 = ObjectAnimator.ofInt(rlTop, "backgroundColor",
                Color.parseColor("#0277bd"), Color.parseColor("#009688"));
        animator1.setDuration(700);
        animator1.setStartDelay(100);
        animator1.setEvaluator(new ArgbEvaluator());
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewShadow.setVisibility(View.VISIBLE);
                rlTop.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setAlpha(0.0f);
            toolbar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ObjectAnimator animator0 = ObjectAnimator.ofInt(toolbar, "backgroundColor",
                    Color.parseColor("#0277bd"), Color.parseColor("#009688"));
            animator0.setDuration(700);
            animator0.setStartDelay(100);
            animator0.setEvaluator(new ArgbEvaluator());
            animator0.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toolbar.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            });
            animator0.start();
            AnimateUtils.alpha(toolbar, 1.0f, 400, 0);
        }


        animator1.start();
        AnimateUtils.alpha(rlTop, 1.0f, 400, 0);
        startIntroAnimator();
    }

    private void startIntroAnimator() {
        ivBle.setVisibility(View.VISIBLE);
        tvServiceName.setVisibility(View.VISIBLE);
        tvServiceMac.setVisibility(View.VISIBLE);
        tvServiceFirm.setVisibility(View.VISIBLE);
        tvServiceCount.setVisibility(View.VISIBLE);
        ivBle.setTranslationX(-Utils.dpToPx(100));
        ivBle.setRotation(-360f);
        ivBle.setAlpha(0f);
        tvServiceName.setTranslationX(Utils.dpToPx(300));
        tvServiceName.setAlpha(0f);
        tvServiceMac.setTranslationX(Utils.dpToPx(300));
        tvServiceMac.setAlpha(0f);
        tvServiceFirm.setTranslationX(Utils.dpToPx(300));
        tvServiceFirm.setAlpha(0f);
        tvServiceCount.setTranslationX(Utils.dpToPx(300));
        tvServiceCount.setAlpha(0f);

        lvServices.setTranslationY(Utils.dpToPx(300));


        AnimateUtils.translationX(ivBle, 0, 400, 400);
        AnimateUtils.rotation(ivBle, 0f, 400, 400, null);
        AnimateUtils.alpha(ivBle, 1.0f, 400, 400);
        AnimateUtils.translationX(tvServiceName, 0, 400, 400);
        AnimateUtils.alpha(tvServiceName, 1.0f, 400, 400);
        AnimateUtils.translationX(tvServiceMac, 0, 400, 500);
        AnimateUtils.alpha(tvServiceMac, 1.0f, 400, 500);
        AnimateUtils.translationX(tvServiceFirm, 0, 400, 600);
        AnimateUtils.alpha(tvServiceFirm, 1.0f, 400, 600);
        AnimateUtils.translationX(tvServiceCount, 0, 400, 600);
        AnimateUtils.alpha(tvServiceCount, 1.0f, 400, 600);


        AnimateUtils.alpha(lvServices, 1.0f, 200, 400);
        AnimateUtils.translationY(lvServices, 0, 400, 400);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
