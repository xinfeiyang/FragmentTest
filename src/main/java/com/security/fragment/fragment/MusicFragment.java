package com.security.fragment.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.security.fragment.R;
import com.security.fragment.adapter.MusicAdapter;
import com.security.fragment.application.MyApplication;
import com.security.fragment.bean.Mp3Info;
import com.security.fragment.util.MediaUtil;
import com.security.fragment.util.ThreadPoolFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2017/4/14 0014.
 */
public class MusicFragment extends BaseFragment {

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                progressBar.setVisibility(View.GONE);
                adapter = new MusicAdapter(getActivity(),datas);
                recyclerView.setAdapter(adapter);
            }
        }
    };

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Mp3Info> datas=new ArrayList<>();
    private MusicAdapter adapter;

    public static MusicFragment newInstance(String title) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_music,container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager manager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        ThreadPoolFactory.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                datas= MediaUtil.getMp3Info(getActivity());
                Log.i("size",datas.size()+":xxxxxxxxxxxx");
                handler.sendEmptyMessage(0);
            }
        });
    }
}
