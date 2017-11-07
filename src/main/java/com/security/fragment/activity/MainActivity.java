package com.security.fragment.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.security.fragment.R;
import com.security.fragment.fragment.BaseFragment;
import com.security.fragment.fragment.BlankFragment;
import com.security.fragment.fragment.CarFragment;
import com.security.fragment.fragment.MusicFragment;
import com.security.fragment.fragment.SearchFragment;
import com.security.fragment.fragment.SettingFragment;
import com.security.fragment.util.BaseActivity;

public class MainActivity extends BaseActivity {

    private AHBottomNavigation bottomNavigation;
    private SearchFragment searchFragment;
    private MusicFragment musicFragment;
    private CarFragment carFragment;
    private SettingFragment settingFragment;
    private BlankFragment blankFragment;
    private BaseFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("搜索", R.drawable.ic_bottom_navigation);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("音乐", R.drawable.ic_bottom_music);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("车辆", R.drawable.ic_bottom_car);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("管理", R.drawable.ic_bottom_setting);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("玩具", R.drawable.ic_bottom_toys);
        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        // 默认背景颜色
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.bg_bottom_navigation));
        // 切换时颜色的转变
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.accent_bottom_navigation));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.inactive_bottom_navigation));
        // 强制着色
        bottomNavigation.setForceTint(true);
        // 强制展示标题
        //bottomNavigation.setForceTitlesDisplay(true);
        // 使用圆圈效果
        bottomNavigation.setColored(false);
        // Set current item programmatically
        bottomNavigation.setCurrentItem(0);
    }

    private void initData() {
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance("搜索");
        }
        if (!searchFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_content, searchFragment).commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction().show(searchFragment).commitAllowingStateLoss();
        }
        currentFragment = searchFragment;
    }

    private void initListener() {
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {// 导航
                    clickSearchLayout();
                } else if (position == 1) {// 音乐
                    clickMusicLayout();
                } else if (position == 2) {// OBD
                    clickCarLayout();
                } else if (position == 3) {
                    clickSettingLayout();
                } else if (position == 4) {
                    clickToysLayout();
                }
                return true;
            }
        });
    }

    private void clickSearchLayout() {
        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance("搜索");
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), searchFragment);
    }

    private void clickMusicLayout() {
        if (musicFragment == null) {
            musicFragment = MusicFragment.newInstance("音乐");
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), musicFragment);
    }

    private void clickCarLayout() {
        if (carFragment == null) {
            carFragment = CarFragment.newInstance("车辆");
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), carFragment);
    }

    private void clickSettingLayout() {
        if (settingFragment == null) {
            settingFragment = SettingFragment.newInstance("设置");
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), settingFragment);
    }

    private void clickToysLayout() {
        if (blankFragment == null) {
            blankFragment = BlankFragment.newInstance("玩乐");
        }
        addOrShowFragment(getSupportFragmentManager().beginTransaction(), blankFragment);
    }

    /**
     * 添加或者显示 fragment
     *
     * @param transaction
     * @param fragment
     */
    protected void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (currentFragment == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(R.id.fl_content, fragment).commitAllowingStateLoss();
        } else {
            transaction.hide(currentFragment).show(fragment).commitAllowingStateLoss();
        }

        currentFragment = (BaseFragment)fragment;
    }


}
