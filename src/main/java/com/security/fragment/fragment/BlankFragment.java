package com.security.fragment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.security.fragment.R;
import com.security.fragment.application.MyApplication;

/**
 * Created by Feng on 2017/4/14 0014.
 */
public class BlankFragment extends BaseFragment implements View.OnClickListener {

    public static BlankFragment newInstance(String title) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_blank,container,false);
        Button btn= (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        MyApplication.getInstance().showWifiSettingDialog();
    }
}
