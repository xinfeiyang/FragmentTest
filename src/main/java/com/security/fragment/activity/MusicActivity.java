package com.security.fragment.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.security.fragment.IMusicService;
import com.security.fragment.R;
import com.security.fragment.service.MusicService;
import com.security.fragment.util.BaseActivity;
import com.security.fragment.util.MediaUtil;
import com.security.fragment.util.SharedPreferenceUtil;
import com.security.fragment.view.CustomRelativeLayout;
import com.security.fragment.view.CustomSettingView;
import com.security.fragment.view.LyricView;

import java.io.File;

public class MusicActivity extends BaseActivity {

    private int position;
    private TextView tv_title;
    private TextView tv_position;
    private TextView tv_duration;
    private SeekBar sb_progress;
    private MusicPreparedReceiver receiver;

    private static final int PROGRESS = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        lyricView.setCurrentTimeMillis(currentPosition);
                        sb_progress.setProgress(currentPosition);
                        handler.removeMessages(PROGRESS);
                        tv_position.setText(MediaUtil.formatTime(currentPosition));
                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    break;
            }
        }
    };

    private ImageView iv_pause_play;
    private boolean notification;
    private int playMode;//歌曲的播放模式;
    private ImageView iv_playmode;
    private ImageView iv_prev;
    private ImageView iv_next;
    private ViewStub setting_layout;
    private ImageView iv_action_setting;
    private CustomRelativeLayout customRelativeLayout;
    private LyricView lyricView;//歌词文件;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ininView();
        Log.i("Tag",this.getCacheDir()+":"+this.getExternalCacheDir());
        initData();
        initListener();
    }


    /**
     * 初始化数据;
     */
    private void initData() {
        //获取从歌曲播放列表传递过来的position;
        position = getIntent().getIntExtra("position", 0);
        //标识是否是从通知栏进入的;
        notification = getIntent().getBooleanExtra("notification", false);
        //获取当前的播放模式;
        playMode = (int) SharedPreferenceUtil.get(this, "play_mode", MusicService.MUSIC_PLAYMODE_REPEATE_ALL);

        //开启、绑定服务;
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        //注册广播接收者;
        receiver = new MusicPreparedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.MUSIC_PREPARED);
        filter.addAction(MusicService.MUSIC_PAUSED);
        filter.addAction(MusicService.MUSIC_RESUMED);
        registerReceiver(receiver, filter);

        //根据播放模式,设定相应的播放模式的图标;
        if (playMode == MusicService.MUSIC_PLAYMODE_REPEATE_ALL) {
            iv_playmode.setBackgroundResource(R.drawable.m_light_repeat_all);
        } else if (playMode == MusicService.MUSIC_PLAYMODE_REPEATE_ONE) {
            iv_playmode.setBackgroundResource(R.drawable.m_light_repeat_one);
        } else if (playMode == MusicService.MUSIC_PLAYMODE_REPEATE_SHUFFLE) {
            iv_playmode.setBackgroundResource(R.drawable.m_light_shuffle_on);
        }
    }

    /**
     * 初始化View;
     */
    private void ininView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_position = (TextView) findViewById(R.id.currentPosition);
        tv_duration = (TextView) findViewById(R.id.duration);
        sb_progress = (SeekBar) findViewById(R.id.seekBar);
        iv_pause_play = (ImageView) findViewById(R.id.play_pasue);
        iv_playmode = (ImageView) findViewById(R.id.playMode);
        iv_prev = (ImageView) findViewById(R.id.prev);
        iv_next = (ImageView) findViewById(R.id.next);
        setting_layout = (ViewStub) findViewById(R.id.main_setting_layout);
        iv_action_setting = (ImageView) findViewById(R.id.action_setting);
        lyricView = (LyricView) findViewById(R.id.lyric_view);
    }

    /**
     * 初始化各种监听；
     */
    private void initListener() {

        lyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(long progress, String content) {
                try {
                    service.seekTo((int) progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //歌词的字体大小、颜色的配置;
        iv_action_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customRelativeLayout==null){
                    customRelativeLayout = (CustomRelativeLayout) setting_layout.inflate();
                    initCustomSettingView();
                }
                customRelativeLayout.show();
            }
        });

        //上一曲;
        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (service != null) {
                        service.pre();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //下一曲;
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (service != null) {
                        service.next();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //更换播放模式;
        iv_playmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (playMode == MusicService.MUSIC_PLAYMODE_REPEATE_ALL) {
                        playMode = MusicService.MUSIC_PLAYMODE_REPEATE_ONE;
                        Toast.makeText(MusicActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                        iv_playmode.setBackgroundResource(R.drawable.m_light_repeat_one);
                    } else if (playMode == MusicService.MUSIC_PLAYMODE_REPEATE_ONE) {
                        playMode = MusicService.MUSIC_PLAYMODE_REPEATE_SHUFFLE;
                        Toast.makeText(MusicActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                        iv_playmode.setBackgroundResource(R.drawable.m_light_shuffle_on);
                    } else if (playMode == MusicService.MUSIC_PLAYMODE_REPEATE_SHUFFLE) {
                        playMode = MusicService.MUSIC_PLAYMODE_REPEATE_ALL;
                        Toast.makeText(MusicActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
                        iv_playmode.setBackgroundResource(R.drawable.m_light_repeat_all);
                    }
                    //设置播放模式;
                    service.setPlayMode(playMode);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //暂停播放、继续播放;
        iv_pause_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (service.isPlaying()) {
                        service.pause();
                        iv_pause_play.setBackgroundResource(R.drawable.m_play_selector);
                    } else {
                        service.start();
                        iv_pause_play.setBackgroundResource(R.drawable.m_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        //歌曲播放进度;
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        service.seekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessage(PROGRESS);
            }
        });
    }

    /**
     * 初始化自定义的SettingView;
     */
    private void initCustomSettingView() {
        CustomSettingView customSettingView = (CustomSettingView) customRelativeLayout.getChildAt(0);
        customSettingView.setOnTextSizeChangeListener(new TextSizeChangeListener());
        customSettingView.setOnColorItemChangeListener(new ColorItemClickListener());
        customSettingView.setOnDismissBtnClickListener(new DismissBtnClickListener());
        customSettingView.setOnLineSpaceChangeListener(new LineSpaceChangeListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Tag", "MusicActivity开始destroy");
        //取消广播接收者的注册;
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    /**************************各种自定义的监听器**************************/

    /**
     * 行间距的监听
     */
    private class LineSpaceChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                lyricView.setLineSpace(12.0f + 3 * progress / 100.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            SharedPreferenceUtil.put(MusicActivity.this,LyricView.KEY_LINE_SPACE,12.0f + 3 * seekBar.getProgress() / 100.0f);
        }
    }

    /**
     * 高亮字体的颜色;
     */
    private class ColorItemClickListener implements CustomSettingView.OnColorItemChangeListener {

        @Override
        public void onColorChanged(int color) {
            lyricView.setHighLightTextColor(color);
            SharedPreferenceUtil.put(MusicActivity.this,LyricView.KEY_HIGHLIGHT_COLOR,color);
            if(customRelativeLayout != null) {
                customRelativeLayout.dismiss();
            }
        }
    }

    /**
     * 设置歌词字体的大小;
     */
    private class TextSizeChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                lyricView.setTextSize(15.0f + 3 * progress / 100.0f);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //将文本大小保存进SharedPreference;
            SharedPreferenceUtil.put(MusicActivity.this,LyricView.KEY_TEXT_SIZE,15.0f + 3 * seekBar.getProgress() / 100.0f);
        }
    }


    /**
     * 关闭弹出的框;
     */
    private class DismissBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(customRelativeLayout != null) {
                customRelativeLayout.dismiss();
            }
        }
    }

    /*************************广播接收者******************/
    private class MusicPreparedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicService.MUSIC_PREPARED)) {
                setData();
            } else if (intent.getAction().equals(MusicService.MUSIC_PAUSED)) {
                //音乐暂停时，设置音乐播放时play_pause的图标；
                iv_pause_play.setBackgroundResource(R.drawable.m_play_selector);
            }else if(intent.getAction().equals(MusicService.MUSIC_RESUMED)){
                iv_pause_play.setBackgroundResource(R.drawable.m_pause_selector);
            }

        }
    }

    /**
     * 根据歌曲准备好的广播,为当前的播放进度、总时长、歌曲名称等赋值;
     */
    private void setData() {
        try {
            String title = service.getName();
            long duration = service.getDuration();
            //设置播放时的图标；
            iv_pause_play.setBackgroundResource(R.drawable.m_pause_selector);
            tv_title.setText(title);
            tv_duration.setText(MediaUtil.formatTime(duration));
            sb_progress.setMax((int) duration);

            lyricView.reset("暂无歌词....");
            File file=new File(MusicActivity.this.getExternalCacheDir(),"旭日阳刚 - 春天里.lrc");
            if(file.exists()){
                Log.i("Tag",file.getName()+":"+file.getName().contains(service.getName().substring(2,5)));
                if(file.getName().contains(service.getName().substring(2,5))){
                    lyricView.setLyricFile(file,"GBK");
                    lyricView.setPlayable(true);
                }
            }else{
                //是否绘制Player;
               lyricView.setPlayable(false);
            }
            handler.sendEmptyMessage(PROGRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*********************开启、绑定服务***************************/
    private IMusicService service;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = IMusicService.Stub.asInterface(binder);
            if (service != null) {
                try {
                    Log.i("Tag","Service开启连接");
                    if (!notification) {
                        service.openAudio(position);
                    } else {
                        setData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service != null) {
                Log.i("Tag","Service断开连接");
                try {
                    service.stop();
                    unbindService(conn);
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
