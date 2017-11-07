package com.security.fragment.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.security.fragment.IMusicService;
import com.security.fragment.R;
import com.security.fragment.activity.MusicActivity;
import com.security.fragment.bean.Mp3Info;
import com.security.fragment.util.ActivityManager;
import com.security.fragment.util.MediaUtil;
import com.security.fragment.util.SharedPreferenceUtil;
import com.security.fragment.util.ThreadPoolFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 音乐播放器服务：
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public static final String MUSIC_PREPARED ="com.security.fragment.music_prepared" ;
    public static final String MUSIC_PAUSED ="com.security.fragment.music_paused" ;
    public static final String MUSIC_RESUMED ="com.security.fragment.music_resumed" ;

    /*******************全部循环播放**********************/
    public static final int MUSIC_PLAYMODE_REPEATE_ALL =0;
    /*******************单曲循环播放**********************/
    public static final int MUSIC_PLAYMODE_REPEATE_ONE =1;
    /*******************随机播放**********************/
    public static final int MUSIC_PLAYMODE_REPEATE_SHUFFLE =2;


    /**
     * 上一曲的广播的Action;
     */
    public static final String MUSIC_PRE="music_pre";
    /**
     * 下一曲的广播的Action;
     */
    public static final String MUSIC_NEXT="music_next";
    /**
     * 暂停播放/继续播放的广播的Action;
     */
    public static final String MUSIC_PLAY_PAUSE="music_play_pause";

    /**
     * 停止播放并关闭App的广播的Action;
     */
    public static final String MUSIC_CLOSE="music_close";


    private List<Mp3Info> datas = new ArrayList<>();
    private Mp3Info mp3;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;
    private int playMode;//当前歌曲的播放模式;
    private int position;//当前歌曲的position;
    private MyBroadcastReceiver receiver;
    private RemoteViews remoteViews;
    private AudioManager audioManager;//获取音频管理

    @Override
    public void onCreate() {
        super.onCreate();
        //开启子线程获取手机的音乐文件；
        ThreadPoolFactory.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                datas = MediaUtil.getMp3Info(MusicService.this);
            }
        });
        playMode= (int)SharedPreferenceUtil.get(this,"play_mode",0);
        //注册广播接收者;
        registerReceiver();
        //获取音频管理
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        audioManager.requestAudioFocus(new MyAudioFocusChangeListener(), AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    /**
     * 音频焦点改变的监听;
     */
    private class MyAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener{

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange){
                //focusChange参数告诉你音频焦点时如何改变的，并且可以使用以下的值之一；
                case AudioManager.AUDIOFOCUS_GAIN://你已经得到了音频焦点
                    Log.i("Tag","获取焦点");
                    start();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS://你已经失去了音频焦点很长时间了。你必须停止所有的音频播放.因为你应该不希望长时间等待焦点返回，这将是你尽可能清楚你得资源的一个好地方。例如，你应该释放MediaPlayer.
                    Log.i("Tag","长时间失去焦点");
                    if(isPlaying()){
                        pause();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://你暂时失去了音频焦点,但很快将重新得到焦点。你必须停止多有的音频播放，但是你可以保持你得资源，因为你可能很快会重新获得焦点。
                    Log.i("Tag","暂时失去焦点,但是很快可以获取焦点");
                    if (isPlaying()){
                        pause();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://你暂时失去了音频焦点，但你可以小声地继续播放音频（低音量）而不是完全扼杀音频。
                    Log.i("Tag","失去焦点，降低音量");
                    if(isPlaying()){
                        mediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 注册广播接收者；
     */
    private void registerReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(MUSIC_PRE);
        filter.addAction(MUSIC_NEXT);
        filter.addAction(MUSIC_PLAY_PAUSE);
        filter.addAction(MUSIC_CLOSE);
        registerReceiver(receiver,filter);
    }

    /**
     * 自定义的广播接收者;
     */
    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MUSIC_PRE)){
                MusicService.this.pre();
            }else if(intent.getAction().equals(MUSIC_NEXT)){
                MusicService.this.next();
            }else if(intent.getAction().equals(MUSIC_PLAY_PAUSE)){
                if(isPlaying()){
                    MusicService.this.pause();
                }else{
                    MusicService.this.start();
                }

            }else if(intent.getAction().equals(MUSIC_CLOSE)){
                //释放MediaPlayer资源，取消通知栏,关闭所有的Activity,关闭服务
                MusicService.this.stop();
                ActivityManager.getInstance().removeAll();
                //杀死当前的进程；
                android.os.Process.killProcess(android.os.Process.myPid());
                //关闭虚拟机，释放所有内存:参数0代表正常退出
                System.exit(0);
            }
        }
    }

    private IMusicService.Stub stub= new IMusicService.Stub() {

        private MusicService service=MusicService.this;
        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


    /***********************Service中用到的方法***************************/
    /**
     * 根据位置打开对应的音频文件
     * @param position
     */
    private void openAudio(int position){
        this.position=position;
        if(datas==null||datas.size()==0) return;
        mp3 = datas.get(position);
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            //设置文件的路径;
            mediaPlayer.setDataSource(mp3.getUrl());
            //异步准备;
            mediaPlayer.prepareAsync();

            if(playMode==MUSIC_PLAYMODE_REPEATE_ONE){//单曲循环
                //单曲循环播放,不会触发播放完成的回调;
                mediaPlayer.setLooping(true);
            }else{
                //不循环播放;
                mediaPlayer.setLooping(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音乐
     */
    private void start(){
        if(mediaPlayer!=null){
            mediaPlayer.start();
        }
        updateNotification();
        //更改主界面的play_pause的图标;
        Intent intent=new Intent(MUSIC_RESUMED);
        sendBroadcast(intent);

    }

    private void updateNotification() {
        //当播放歌曲的时候，在状态显示正在播放，点击的时候，可以进入音乐播放页面
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       /* Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("音乐")
                .setContentText("正在播放:"+getName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);*/
        //自定义的Notification;

        remoteViews = new RemoteViews(this.getPackageName(), R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.widget_title,mp3.getTitle());
        remoteViews.setTextViewText(R.id.widget_artist,mp3.getArtist());
        if(isPlaying()){
            remoteViews.setImageViewResource(R.id.widget_play,R.drawable.widget_btn_pause_normal);
        }else{
            remoteViews.setImageViewResource(R.id.widget_play,R.drawable.widget_btn_play_normal);
        }

        //进入音乐播放界面的Activity;
        Intent intent = new Intent(this,MusicActivity.class);
        intent.putExtra("notification",true);//标识来自状态拦
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //上一曲PendingIntent;
        Intent pre_intent=new Intent(MUSIC_PRE);
        PendingIntent pre_pendingIntent=PendingIntent.getBroadcast(this,2,pre_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_prev,pre_pendingIntent);

        //下一曲PendingIntent;
        Intent next_intent=new Intent(MUSIC_NEXT);
        PendingIntent next_pendingIntent=PendingIntent.getBroadcast(this,3,next_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_next,next_pendingIntent);

        //暂停播放、继续播放PendingIntent;
        Intent play_pause_intent=new Intent(MUSIC_PLAY_PAUSE);
        PendingIntent play_pause_pendingIntent=PendingIntent.getBroadcast(this,4,play_pause_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_play,play_pause_pendingIntent);

        //点击关闭图标的PendingIntent;
        Intent close_intent=new Intent(MUSIC_CLOSE);
        PendingIntent close_pendingIntent=PendingIntent.getBroadcast(this,5,close_intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_close,close_pendingIntent);

        Notification notification=new NotificationCompat.Builder(this)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setSmallIcon(R.mipmap.nav_icon)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .build();
        manager.notify(1,notification);
    }

    /**
     * 播暂停音乐
     */
    private void pause(){
        if(mediaPlayer!=null){
            mediaPlayer.pause();
        }
        if(manager!=null){
            manager.cancel(1);
        }
        //更新通知栏的状态;
        updateNotification();
        //更新音乐播放器界面的play_pause的图标;
        Intent intent=new Intent(MUSIC_PAUSED);
        sendBroadcast(intent);

    }

    /**
     * 停止
     */
    private void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
            if(manager!=null){
                manager.cancel(1);
            }
            stopSelf();
        }
    }

    private void seekTo(int position) {
        if(mediaPlayer!=null){
            mediaPlayer.seekTo(position);
        }
    }


    /**
     * 得到当前的播放进度
     * @return
     */
    private int getCurrentPosition(){
        if(mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    /**
     * 得到当前音频的总时长
     * @return
     */
    private int getDuration(){
        if(mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 得到艺术家
     * @return
     */
    private String getArtist(){
        if(mp3!=null){
            return mp3.getArtist();
        }
        return "";
    }

    /**
     * 得到歌曲名字
     * @return
     */
    private String getName(){
        if(mp3!=null){
            return mp3.getTitle();
        }
        return "";
    }


    /**
     * 得到歌曲播放的路径
     * @return
     */
    private String getAudioPath(){
        if(mp3!=null){
            return mp3.getUrl();
        }
        return "";
    }

    /**
     * 播放下一个视频
     */
    private void next(){
        setNextPositon();
        //准备资源，播放下一曲;
        openAudio(position);
    }


    /**
     * 设置下一曲的位置;
     */
    private void setNextPositon() {
        if(playMode==MUSIC_PLAYMODE_REPEATE_ALL){
            position++;
            if(position>datas.size()-1){
                position=0;
            }
        }else if(playMode==MUSIC_PLAYMODE_REPEATE_ONE){
            position++;
            if(position>datas.size()-1){
                position=0;
            }
        }else if(playMode==MUSIC_PLAYMODE_REPEATE_SHUFFLE){
            position=new Random().nextInt(25);
        }
    }



    /**
     * 播放上一个视频
     */
    private void pre(){
        setPrePositon();
        openAudio(position);
    }

    /**
     * 设置下一曲的位置;
     */
    private void setPrePositon() {
        if(playMode==MUSIC_PLAYMODE_REPEATE_ALL){
            position--;
            if(position<0){
                position=datas.size()-1;
            }
        }else if(playMode==MUSIC_PLAYMODE_REPEATE_ONE){
            position--;
            if(position<0){
                position=datas.size()-1;
            }
        }else if(playMode==MUSIC_PLAYMODE_REPEATE_SHUFFLE){
            position=new Random().nextInt(datas.size());
        }
    }

    /**
     * 设置播放模式
     * @param playmode
     */
    private void setPlayMode(int playmode){
        this.playMode=playmode;
        SharedPreferenceUtil.put(this,"play_mode",playmode);
        //如果播放模式为单曲循环,设置当前歌曲播放完成后不会触发播放完成的回调;
        if(playMode==MUSIC_PLAYMODE_REPEATE_ONE){
            //单曲循环播放,不会触发播放完成的回调;
            mediaPlayer.setLooping(true);
        }else{
            //不循环播放;
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 得到播放模式
     * @return
     */
    private int getPlayMode(){
        return playMode;
    }

    private boolean isPlaying(){
        if(mediaPlayer!=null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /***********************MediaPlayer的各种监听器***************************/
    @Override    //资源准备好的监听;
    public void onPrepared(MediaPlayer mp) {
        start();
        //资源准备好，发送广播；
        Intent intent=new Intent(MUSIC_PREPARED);
        sendBroadcast(intent);
    }

    @Override   //歌曲播放完毕的监听;
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override    //资源出错的监听;
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "资源出错", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.cancel(1);
        unregisterReceiver(receiver);
    }
}
