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
public class DataFragment extends Fragment {

    public DataFragment() {
    }

    public static DataFragment getInstance(){
        DataFragment fragment = new DataFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data,container,false);
        ButterKnife.bind(DataFragment.this,view);
        return view;
    }
}
