package com.security.fragment.fragment;

import android.os.Bundle;

/**
 * Created by Feng on 2017/4/15 0015.
 */

public class ScienceFragment extends BaseFragment {

    public static ScienceFragment newInstance(String title) {
        ScienceFragment fragment = new ScienceFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }
}
