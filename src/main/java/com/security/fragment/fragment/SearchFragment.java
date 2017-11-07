package com.security.fragment.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.security.fragment.R;
import com.security.fragment.adapter.TabAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/4/14 0014.
 */
public class SearchFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static SearchFragment newInstance(String title) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,container,false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        List<BaseFragment> datas=new ArrayList<>();
        datas.add(ScienceFragment.newInstance("科学"));
        datas.add(GameFragment.newInstance("游戏"));
        datas.add(EquipFragment.newInstance("装备"));
        datas.add(ThinkingFragment.newInstance("思考"));

        TabAdapter adapter=new TabAdapter(getActivity().getSupportFragmentManager(),datas);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
