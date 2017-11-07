#<font color="#FF1493">Notification使用详解</font>

##<font color="#FF1493">1、获取NotificationManager</font>
	NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

##<font color="#FF1493">2、创建Notification</font>
	Notification notification=new NotificationCompat.Builder(context)
				.setContentTitle("This is content title")
				.setContentText("This is content text")
				.setWhen(System.currentTimeMillis())
				.setSmallIcon(R.drawable.small_icon)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.large_icon))
				.build();


##<font color="#FF1493">3、发送通知</font>
	//notify接收两个参数，第一个参数是id，要保证为每个通知所指定的id都是不同的；
	manager.notify(1,notification);

##<font color="#FF1493">4、PendingIntent:延迟执行的Intent；PendingIntent提供了几个静态方法用于获取PendingIntent的实例，可以根据需求来选择是使用getActivity()、getBoradcast()、getService()等</font>
	Intent intent=new Intent(this,NotificationActivity.class);
	PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
	notification.setContentIntent(pi);//为Notification添加PendingIntent;

##<font color="#FF1493">5、取消通知</font>
	//setAutoCancel()方法传入true，表示当点击了这个通知时，通知自动取消掉；
	1、Notification notification=new NotificationCompat.Builder(context)
					...	
					.setAutoCancel(true)
					.builde();

---------------------------------------------------------------------

	//在cancel()方法中传入1，表示取消上面传入的那个通知id；
	2、NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	...
	manager.cancel(1);


##<font color="#FF1493">6、通知时播放音频、震动、灯光闪烁等</font>

	1、播放音频(手机/system/media/audio/ringtones目录下的音频文件)
	Notification notification=new NotificationCompat.Builder(context)
					...	
					.setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
					.build();
	
---------------------------------------------------------------------
	2、手机震动(需要添加权限：android.permission.VIBRATE)	
	Notification notification=new NotificationCompat.Builder(context)
					...	
					.setVibrate(new long[]{0,1000,1000,1000})
					.build();

-----------------------------------------------------------------------
	3、LED灯光闪烁:setLights()方法接收三个参数，第一个参数用于指定LED灯的颜色，第二个参数用于指定LED灯亮起的时长，第三个参数用于指定LED灯暗去的时长，也是以毫秒为单位;
	Notification notification=new NotificationCompat.Builder(context)
					...	
					.setLights(Color.GREEN,1000,1000)
					.build();

---------------------------------------------------------------------
	4、系统默认的通知效果
	Notification notification=new NotificationCompat.Builder(context)
					...	
					.setDefaults(NotificationCompat.DEFAULT_ALL)
					.build();

##<font color="#FF1493">7、通知的高级功能</font>
	
	1、显示长文字：如果在通知当中要显示一段文字，会发现通知内容是无法显示完整的，多余的部分会用省略号来代替。如果真的非常需要在通知当中显示一段长文字，可以通过setStyle()方法。
	Notification notification=new NotificationCompat.Builder(context)
					...	
					.setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications,send and sync data,and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
					.build();
	在setStyle()方法中创建一个NotificationCompat.BigTextStyle对象，这个对象就是用于封装长文字信息的，调用它的bigText()方法并将文字内容传入就可以了。

--------------------------------------------------------------------------
	2、显示大图片
	Notification notification=new NotificationCompat.Builder(context)
					...	
					.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.big_image)))
					.build();

##<font color="#FF1493">8、自定义通知内容</font>
		//当播放歌曲的时候，在状态显示正在播放，点击的时候，可以进入音乐播放页面

		//NotificationManager的创建
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

       /* Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("音乐")
                .setContentText("正在播放:"+getName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);*/


        //自定义的Notification;
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.custom_notification);
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
	
	
	
