package com.usr.usrsimplebleassistent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import butterknife.OnClick;

/**
 * create
 * on 2020-03-27 14:59
 * by xinDong
 **/
public class SetFragment extends Fragment {
    @BindView(R.id.rBtn_set_ziwai)
    RadioButton rBtnSetZiwai;
    @BindView(R.id.rBtn_set_kejian)
    RadioButton rBtnSetKejian;
    @BindView(R.id.rBtn_set_red)
    RadioButton rBtnSetRed;
    @BindView(R.id.rBtn_set_harderware)
    RadioButton rBtnSetHarderware;
    @BindView(R.id.bgcanshu)
    TextView bgcanshu;
    @BindView(R.id.tv_y_bg)
    TextView tvYBg;
    @BindView(R.id.tv_y_lmd)
    TextView tvYLmd;
    @BindView(R.id.tv_y_ldbd)
    TextView tvYLdbd;
    @BindView(R.id.tv_y_mdbd)
    TextView tvYMdbd;
    @BindView(R.id.et_b_bg)
    EditText etBBg;
    @BindView(R.id.et_b_lmd)
    EditText etBLmd;
    @BindView(R.id.et_b_ldbd)
    EditText etBLdbd;
    @BindView(R.id.et_b_mdbd)
    EditText etBMdbd;
    @BindView(R.id.btn_bg)
    Button btnBg;
    @BindView(R.id.btn_lmd)
    Button btnLmd;
    @BindView(R.id.btn_ldbd)
    Button btnLdbd;
    @BindView(R.id.btn_mdbd)
    Button btnMdbd;
    @BindView(R.id.ll_set)
    LinearLayout llSet;
    @BindView(R.id.cb_one)
    CheckBox cbOne;
    @BindView(R.id.cb_two)
    CheckBox cbTwo;
    @BindView(R.id.btn_jdqset)
    Button btnJdqset;
    @BindView(R.id.ll_jidianqi)
    LinearLayout llJidianqi;
    private boolean jdqOne = false;
    private boolean jdqTwo = false;
    private String TAG = "Tag";

    public SetFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        ButterKnife.bind(SetFragment.this, view);
        EventBus.getDefault().register(SetFragment.this);
        initCb();
        return view;
    }

    private void initCb() {
        cbOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               jdqOne = isChecked;
            }
        });
        cbTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                jdqTwo = isChecked;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowEventMessage(MessageEvent event) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(SetFragment.this);
    }

    @OnClick({R.id.rBtn_set_ziwai, R.id.rBtn_set_kejian, R.id.rBtn_set_red, R.id.rBtn_set_harderware,
            R.id.btn_bg, R.id.btn_lmd, R.id.btn_ldbd, R.id.btn_mdbd, R.id.btn_jdqset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rBtn_set_ziwai:
                hideJDQ();
                break;
            case R.id.rBtn_set_kejian:
                hideJDQ();
                break;
            case R.id.rBtn_set_red:
                hideJDQ();
                break;
            case R.id.rBtn_set_harderware:
                llSet.setVisibility(View.GONE);
                llJidianqi.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_bg:
                break;
            case R.id.btn_lmd:
                break;
            case R.id.btn_ldbd:
                break;
            case R.id.btn_mdbd:
                break;
            case R.id.btn_jdqset:

                break;
        }
    }

    private void hideJDQ() {
        llSet.setVisibility(View.VISIBLE);
        llJidianqi.setVisibility(View.GONE);
    }

}
