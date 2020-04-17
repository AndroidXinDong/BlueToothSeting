package com.usr.usrsimplebleassistent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.Utils.C2JUtils;
import com.usr.usrsimplebleassistent.Utils.DataUtils;
import com.usr.usrsimplebleassistent.Utils.DbUtil;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.application.MyApplication;
import com.usr.usrsimplebleassistent.bean.MessageEvent;
import com.usr.usrsimplebleassistent.bean.SaveBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

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
    @BindView(R.id.iBtn_reference)
    ImageButton iBtnReference;
    @BindView(R.id.rBtn_set_red2)
    RadioButton rBtnSetRed2;
    @BindView(R.id.rBtn_set_red3)
    RadioButton rBtnSetRed3;
    @BindView(R.id.sBtn_switch)
    Switch sBtnSwitch;
    @BindView(R.id.tv_translate)
    TextView tvTranslate;
    @BindView(R.id.et_translate)
    EditText etTranslate;
    @BindView(R.id.btn_translate_du)
    Button btnTranslateDu;
    @BindView(R.id.btn_translate_write)
    Button btnTranslateWrite;
    @BindView(R.id.tv_currentElectric)
    TextView tvCurrentElectric;
    @BindView(R.id.et_currentElectric)
    EditText etCurrentElectric;
    @BindView(R.id.btn_dianliu_du)
    Button btnDianliuDu;
    @BindView(R.id.btn_dianliu_write)
    Button btnDianliuWrite;
    @BindView(R.id.btn_save)
    Button btnSave;
    private boolean jdqOne = false;
    private boolean jdqTwo = false;
    private String zlOne = "00";
    private String zlTwo = "00";
    private String TAG = "Tag";
    private String type = "01";
    private MyApplication mApplication;
    private CountDownTimer mTimer;
    private DbUtil mDbUtil;

    public SetFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        mApplication = (MyApplication) getActivity().getApplication();
        ButterKnife.bind(SetFragment.this, view);
        EventBus.getDefault().register(SetFragment.this);
        mDbUtil = new DbUtil(getContext());
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
        mTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 启动倒计时
//                Log.i(TAG, "onTick: "+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                // 倒计时结束之后发送关闭维护模式命令
                String cmd = DataUtils.sendWriteModelCmd("00");
                EventBus.getDefault().post(new MessageEvent(cmd, true));
                sBtnSwitch.setChecked(false);
            }
        };
        sBtnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String modelCmd = null;
                if (isChecked) {
                    modelCmd = "01";
                } else {
                    modelCmd = "00";
                    mTimer.cancel();
                }
                String cmd = DataUtils.sendWriteModelCmd(modelCmd);
                EventBus.getDefault().post(new MessageEvent(cmd, true));
            }
        });
        setParameter();
    }

    @OnClick({R.id.rBtn_set_ziwai, R.id.rBtn_set_kejian, R.id.rBtn_set_red, R.id.rBtn_set_harderware,
            R.id.btn_bg, R.id.btn_lmd, R.id.btn_ldbd, R.id.btn_mdbd, R.id.btn_jdqset, R.id.iBtn_reference
            , R.id.rBtn_set_red2, R.id.rBtn_set_red3, R.id.btn_translate_du, R.id.btn_translate_write,
            R.id.btn_dianliu_du, R.id.btn_dianliu_write, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rBtn_set_ziwai:
                type = "01";
                setParameter();
                break;
            case R.id.rBtn_set_kejian:
                type = "02";
                setParameter();
                break;
            case R.id.rBtn_set_red:
                type = "03";
                setParameter();
                break;
            case R.id.rBtn_set_red2:
                type = "04";
                setParameter();
                break;
            case R.id.rBtn_set_red3:
                type = "05";
                setParameter();
                break;
            case R.id.rBtn_set_harderware:
                llSet.setVisibility(View.GONE);
                llJidianqi.setVisibility(View.VISIBLE);
                String readModelCmd = DataUtils.sendReadModelCmd();
                EventBus.getDefault().post(new MessageEvent(readModelCmd, true));
                break;
            case R.id.btn_bg:
                sendMessage(etBBg, "01");
                break;
            case R.id.btn_lmd:
                sendMessage(etBLmd, "02");
                break;
            case R.id.btn_ldbd:
                sendMessage(etBLdbd, "03");
                break;
            case R.id.btn_mdbd:
                sendMessage(etBMdbd, "04");
                break;
            case R.id.btn_jdqset:
                if (jdqOne) {
                    zlOne = "01";
                } else {
                    zlOne = "00";
                }
                if (jdqTwo) {
                    zlTwo = "01";
                } else {
                    zlTwo = "00";
                }
                String s = DataUtils.sendWriteJIDIANQICMD(zlOne + zlTwo);
                EventBus.getDefault().post(new MessageEvent(s, true));
                break;
            case R.id.iBtn_reference:
                String s1 = DataUtils.sendReadJIDIANQICMD();
                EventBus.getDefault().post(new MessageEvent(s1, true));
                break;
            case R.id.btn_translate_du:
                String readTranslate = DataUtils.sendReadTranslate();
                EventBus.getDefault().post(new MessageEvent(readTranslate, true));
                break;
            case R.id.btn_translate_write:
                String trim = etTranslate.getText().toString().trim();
                String value = C2JUtils.intToMinByteArray(Integer.toHexString(Float.floatToIntBits(Float.parseFloat(trim))).toUpperCase()).replaceAll(" ", "");
                String writeTranslate = DataUtils.sendWriteTranslate(value.replaceAll(" ", ""));
                EventBus.getDefault().post(new MessageEvent(writeTranslate, true));
                break;
            case R.id.btn_dianliu_du:
                String ss = DataUtils.sendReadCurrentElectric();
                EventBus.getDefault().post(new MessageEvent(ss, true));
                break;
            case R.id.btn_dianliu_write:
                String trim1 = etCurrentElectric.getText().toString().trim();
                String value1 = C2JUtils.intToMinByteArray(Integer.toHexString(Float.floatToIntBits(Float.parseFloat(trim1))).toUpperCase()).replaceAll(" ", "");
                String currentElectric = DataUtils.sendWriteCurrentElectric(value1);
                EventBus.getDefault().post(new MessageEvent(currentElectric, true));
                break;
            case R.id.btn_save:
                saveParameter();
                break;
        }
    }

    /**
     * 保存标定参数
     */
    private void saveParameter() {
        String t = "0.0";
        String t1 = etBBg.getText().toString().trim();
        String bg = t1.isEmpty() ? t : t1;
        String t2 = etBLmd.getText().toString().trim();
        String lmd = t2.isEmpty() ? t : t2;
        String t3 = etBLdbd.getText().toString().trim();
        String ldbd = t3.isEmpty() ? t : t3;
        String t4 = etBMdbd.getText().toString().trim();
        String mdbd = t4.isEmpty() ? t : t4;
        long id = Integer.parseInt(type, 16);
        String date = Utils.GetTimeandDateUpdate();
        SaveBean saveBean = new SaveBean();
        saveBean.setId(id);
        saveBean.setType(type);
        saveBean.setBg(bg);
        saveBean.setLmd(lmd);
        saveBean.setLdbd(ldbd);
        saveBean.setMdbd(mdbd);
        saveBean.setDate(date);
        boolean b = mDbUtil.insertTargetParameter(saveBean);
        if (b) {
            Toast.makeText(mApplication, "保存成功", Toast.LENGTH_SHORT).show();
        }else {
            boolean b1 = mDbUtil.updateTargetParameter(saveBean);
            if (b1){
                Toast.makeText(mApplication, "保存数据更新完成", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 设置标定参数，发送请求数据 隐藏继电器
     */
    private void setParameter() {
        etBBg.setText(null);
        etBLmd.setText(null);
        etBLdbd.setText(null);
        etBMdbd.setText(null);
        hideJDQ();
        String s = DataUtils.sendReadParameter(type);
        EventBus.getDefault().post(new MessageEvent(s, true));
        SaveBean bean = mDbUtil.loadTargetParameter(Integer.parseInt(type, 16));
        try {
            String bg1 = bean.getBg();
            String lmd1 = bean.getLmd();
            String ldbd1 = bean.getLdbd();
            String mdbd1 = bean.getMdbd();
            etBBg.setText(bg1);
            etBLmd.setText(lmd1);
            etBLdbd.setText(ldbd1);
            etBMdbd.setText(mdbd1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送校验指令
     *
     * @param et
     * @param parameter
     */
    private void sendMessage(EditText et, String parameter) {
        String trim = et.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            // 传感器类型 参数类型 float
            String intToMinByteArray = C2JUtils.intToMinByteArray(Integer.toHexString(Float.floatToIntBits(Float.parseFloat(trim))).toUpperCase()).replaceAll(" ", "");
            String writeParameter = DataUtils.sendWriteParameter(type.concat(parameter).concat(intToMinByteArray));
            EventBus.getDefault().post(new MessageEvent(writeParameter, true));
        } else {
            Toast.makeText(getContext(), "请输入标定值", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 隐藏继电器
     */
    private void hideJDQ() {
        llSet.setVisibility(View.VISIBLE);
        llJidianqi.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowEventMessage(MessageEvent event) {
        String message = event.getMessage();
        try {
            handleMessage(message);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * EventBus 接收消息处理方法
     *
     * @param message
     */
    private void handleMessage(String message) {
        if (message.contains("7D7B") && message.contains("7D7D")) {
            String response = message.substring(6, 8);// 响应码
            String substring = message.substring(4, 6);
            if (substring.equals(DataUtils.CMD_JIDIANQI_CODE)) {
                llSet.setVisibility(View.GONE);
                llJidianqi.setVisibility(View.VISIBLE);
                if (DataUtils.EXTEND_WRITE_RESPONSE_CODE.equals(response)) { // 写的回应
                    boolean b = DataUtils.getWriteJIDIANQIResponse(message);
                    if (b) {
                        Toast.makeText(getContext(), "继电器写入成功", Toast.LENGTH_SHORT).show();
                    }
                } else if (DataUtils.EXTEND_READ_RESPONSE_CODE.equals(response)) { // 读的回应
                    String jidianqiResponse = DataUtils.getReadJIDIANQIResponse(message);
                    String s1 = jidianqiResponse.substring(0, 2);
                    String s2 = jidianqiResponse.substring(2, 4);

                    EventBus.getDefault().post(new MessageEvent(s1 + s2, false));
                    if ("01".equals(s1)) {
                        cbOne.setChecked(true);
                    } else {
                        cbOne.setChecked(false);
                    }
                    if ("01".equals(s2)) {
                        cbTwo.setChecked(true);
                    } else {
                        cbTwo.setChecked(false);
                    }
                }
            } else if (substring.equals(DataUtils.CMD_PARAMETER_SET_CODE)) { // 参数标定
                hideJDQ();
                if (DataUtils.EXTEND_WRITE_RESPONSE_CODE.equals(response)) { // 写的回应
                    boolean parameterResponse = DataUtils.getWriteParameterResponse(message);
                    if (parameterResponse) {
                        Toast.makeText(getContext(), "数值设置成功", Toast.LENGTH_SHORT).show();
                    }
                } else if (DataUtils.EXTEND_READ_RESPONSE_CODE.equals(response)) { // 读的回应
                    HashMap<String, String> hashMap = DataUtils.getReadParameterResponse(message);
                    int type = Integer.parseInt(hashMap.get("type"), 16);
                    switch (type) {
                        case 1:
                            rBtnSetZiwai.setChecked(true);
                            break;
                        case 2:
                            rBtnSetKejian.setChecked(true);
                            break;
                        case 3:
                            rBtnSetRed.setChecked(true);
                            break;
                        case 4:
                            rBtnSetRed2.setChecked(true);
                            break;
                        case 5:
                            rBtnSetRed3.setChecked(true);
                            break;
                    }
                    String bg = hashMap.get("bg");
                    String lmd = hashMap.get("lmd");
                    String ldbd = hashMap.get("ldbd");
                    String mdbd = hashMap.get("mdbd");
                    tvYBg.setText(bg);
                    tvYLmd.setText(lmd);
                    tvYLdbd.setText(ldbd);
                    tvYMdbd.setText(mdbd);

                }
            } else if (substring.equals(DataUtils.CMD_MODEL_CODE)) {
                if (DataUtils.EXTEND_WRITE_RESPONSE_CODE.equals(response)) { // 写的回应
                    boolean modelResponse = DataUtils.getCmdWriteModelResponse(message);
                    if (modelResponse) {
                        boolean checked = sBtnSwitch.isChecked();
                        if (checked) {
                            mApplication.setCurrentModel(true);
                            mTimer.start();
                        } else {
                            mApplication.setCurrentModel(false);
                        }
                        Toast.makeText(getContext(), "设备模式已切换", Toast.LENGTH_SHORT).show();
                    }
                } else if (DataUtils.EXTEND_READ_RESPONSE_CODE.equals(response)) { // 读的回应
                    boolean modelResponse = DataUtils.getCmdReadModelResponse(message);
                    if (modelResponse) {
                        // 维护模式
                        sBtnSwitch.setChecked(true);
                        mApplication.setCurrentModel(true);
                    } else {
                        // 测量模式
                        sBtnSwitch.setChecked(false);
                        mApplication.setCurrentModel(false);
                    }
                }
            } else if (substring.equals(DataUtils.CMD_ELECTRICTRANSLATE_CODE)) { // 电流转换
                if (DataUtils.EXTEND_WRITE_RESPONSE_CODE.equals(response)) { // 写的回应
                    boolean writeTranslateResponse = DataUtils.getWriteTranslateResponse(message);
                    if (writeTranslateResponse) {
                        Toast.makeText(mApplication, "电流转换量输入完成", Toast.LENGTH_SHORT).show();
                    }
                } else if (DataUtils.EXTEND_READ_RESPONSE_CODE.equals(response)) { // 读的回应
                    String readTranslateResponse = DataUtils.getReadTranslateResponse(message);
                    tvTranslate.setText(readTranslateResponse);
                }
            } else if (substring.equals(DataUtils.CMD_CURRENTELECTIC_CODE)) { // 电流写入
                if (DataUtils.EXTEND_WRITE_RESPONSE_CODE.equals(response)) { // 写的回应
                    boolean writeCurrentResponse = DataUtils.getWriteCurrentResponse(message);
                    if (writeCurrentResponse) {
                        Toast.makeText(mApplication, "电流写入完成", Toast.LENGTH_SHORT).show();
                    }
                } else if (DataUtils.EXTEND_READ_RESPONSE_CODE.equals(response)) { // 读的回应
                    String readCurrentResponse = DataUtils.getReadCurrentResponse(message);
                    tvCurrentElectric.setText(readCurrentResponse);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        EventBus.getDefault().unregister(SetFragment.this);
    }

}
