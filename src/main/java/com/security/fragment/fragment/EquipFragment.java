package com.security.fragment.fragment;

import android.os.Bundle;

/**
 * Created by Feng on 2017/4/15 0015.
 */

public class EquipFragment extends BaseFragment {

    public static EquipFragment newInstance(String title) {
        EquipFragment fragment = new EquipFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

}
