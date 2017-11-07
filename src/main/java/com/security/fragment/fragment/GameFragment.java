package com.security.fragment.fragment;

import android.os.Bundle;

/**
 * Created by Feng on 2017/4/15 0015.
 */

public class GameFragment extends BaseFragment {

    public static GameFragment newInstance(String title) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

}
