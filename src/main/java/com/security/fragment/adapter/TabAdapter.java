package com.security.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.security.fragment.fragment.BaseFragment;

import java.util.List;

/**
 * Created by Feng on 2017/4/15 0015.
 */

public class TabAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> datas;
    private String tabTitles[] = new String[]{"科技", "游戏", "装备","想法"};

    public TabAdapter(FragmentManager fm, List<BaseFragment> datas) {
        super(fm);
        this.datas=datas;
    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
