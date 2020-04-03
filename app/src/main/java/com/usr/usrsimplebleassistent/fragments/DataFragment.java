package com.usr.usrsimplebleassistent.fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.GattDetailActivity;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.DataUtils;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * create
 * on 2020-03-27 14:59
 * by xinDong
 **/
public class DataFragment extends Fragment {
    private MyApplication myApplication;
    private String TAG = "Tag";
    @BindView(R.id.tv_purple)
    TextView tv_purple;
    @BindView(R.id.tv_light)
    TextView tv_light;
    @BindView(R.id.tv_red)
    TextView tv_red;
    private String sendMoreParameterCMD;
    Handler handler = new Handler();
    private Runnable runable;
    private List<TextView> mList = new ArrayList<>();
    public DataFragment() {
    }

    public static DataFragment getInstance() {
        DataFragment fragment = new DataFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        ButterKnife.bind(DataFragment.this, view);
        EventBus.getDefault().register(DataFragment.this);
        mList.add(tv_purple);
        mList.add(tv_light);
        mList.add(tv_red);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sendMoreParameterCMD = DataUtils.sendMoreParameterCMD("03");
        runable = new Runnable() {
            @Override
            public void run() {

                EventBus.getDefault().post(new MessageEvent(sendMoreParameterCMD));

                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runable);
    }

    @OnClick(R.id.tv_purple)
    public void getTest() {
        EventBus.getDefault().post(new MessageEvent(sendMoreParameterCMD));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getReceiverData(MessageEvent event) {
        String message = event.getMessage();
        if (message.length()>50){
            String substring = message.substring(4, 6);
            if (substring.equals(DataUtils.CMD_MOREDATA_CODE)){
                List<String> list = DataUtils.getReadMoreFloatResponse(message);
                for (int i = 0; i < list.size(); i++) {
                    mList.get(i).setText(list.get(i));
                }
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(DataFragment.this);
        handler.removeCallbacks(runable);
    }

}
