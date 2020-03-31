package com.usr.usrsimplebleassistent.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create
 * on 2020-03-27 14:59
 * by xinDong
 **/
public class SetFragment extends Fragment {
    @BindView(R.id.bgcanshu)
    TextView bgcanshu;
    private String TAG = "Tag";

    public SetFragment() {
    }

    public static SetFragment getInstance(){
        SetFragment fragment = new SetFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set,container,false);
        ButterKnife.bind(SetFragment.this,view);
        EventBus.getDefault().register(SetFragment.this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowEventMessage(MessageEvent event){
        Log.i(TAG, "onShowEventMessage: "+event.getMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SetFragment.this);
    }
}
