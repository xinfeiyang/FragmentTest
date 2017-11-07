package com.security.fragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.security.fragment.R;
import com.security.fragment.activity.MusicActivity;
import com.security.fragment.bean.Mp3Info;
import com.security.fragment.util.MediaUtil;

import java.util.List;

/**
 * Created by Feng on 2017/4/20 0020.
 */
public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<Mp3Info> datas;
    public MusicAdapter(Context context, List<Mp3Info> datas) {
        this.context=context;
        this.datas=datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= View.inflate(context, R.layout.item_music,null);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        MusicViewHolder holder= (MusicViewHolder) viewHolder;
        Mp3Info mp3=datas.get(position);
        holder.tv_name.setText(mp3.getTitle());
        holder.tv_time.setText(MediaUtil.formatTime(mp3.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context,mp3.getSize()));
        holder.iv_icon.setImageResource(R.drawable.video_default_icon);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MusicActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(datas!=null&&datas.size()>0){
            return datas.size();
        }
        return 0;
    }

    private static class MusicViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private final ImageView iv_icon;
        private final TextView tv_name;
        private final TextView tv_size;
        private final TextView tv_time;

        public MusicViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
