package com.security.fragment.fragment;

import android.os.Bundle;

/**
 * Created by Feng on 2017/4/15 0015.
 */
public class ThinkingFragment extends BaseFragment {

    public static ThinkingFragment newInstance(String title) {
        ThinkingFragment fragment = new ThinkingFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }
}
