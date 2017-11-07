package com.security.fragment.util;

import android.app.Activity;
import java.util.Stack;

/**
 * 统一应用程序中所有的Activity的栈管理
 * 涉及到activity的添加、删除指定、删除当前、删除所有、返回栈大小的方法
 */
public class ActivityManager {

    private static volatile ActivityManager instance;
    //保存Activity的栈,栈的结构特点是后进先出;
    private Stack<Activity> activityStack = new Stack<>();

    /**
     * 私有化构造方法
     */
    private ActivityManager() {
    }

    /**
     *单例模式
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                if (instance == null) {
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity进入栈中；
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            activityStack.add(activity);
        }
    }

    /**
     * 在栈中清除Activity;
     * @param activity
     */
    public void removeActivity(Activity activity) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (activityStack.get(i).getClass().equals(activity.getClass())) {
                activity.finish();
                activityStack.remove(i);
                break;
            }
        }
    }

    /**
     * 移除当前的Activity;
     */
    public void removeCurrentActivity() {
        //Activity activity = activityStack.get(activityStack.size() - 1);
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 移除所有的Activity;
     */
    public void removeAll() {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            activityStack.get(i).finish();
            activityStack.remove(i);
        }
    }


    /**
     * 返回栈中Activity的数量;
     * @return
     */
    public int activitySize() {
        if (activityStack != null) {
            return activityStack.size();
        }
        return 0;
    }

}
