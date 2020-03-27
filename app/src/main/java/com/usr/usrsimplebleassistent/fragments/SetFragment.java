package com.usr.usrsimplebleassistent.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usr.usrsimplebleassistent.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

/**
 * create
 * on 2020-03-27 14:59
 * by xinDong
 **/
public class SetFragment extends Fragment {

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
        return view;
    }
}
