package com.hsh.project101;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;
import java.util.ArrayList;

@EBean
public class MusicRvAdapter extends RecyclerView.Adapter<MusicRvAdapter.ViewHolder> {

    @RootContext
    Context context;

    ArrayList<MusicData> list;
    MediaPlayer player = new MediaPlayer();

    public void setList(ArrayList<MusicData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_music, parent, false);
        return new MusicRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicData data = list.get(position);

        Picasso.with(context).load(data.getImgUrl()).into(holder.iv);
        holder.tvName.setText(data.getName());
        holder.tvAlbum.setText(data.getAlbumName());
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusicResetUI(data.getM4aUrl(), holder);
                notifyDataSetChanged();
            }
        });
    }

    @Background
    void playMusicResetUI(String url, ViewHolder holder) {
        try {
            player.reset();
            player.setDataSource(context, Uri.parse(url));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tvAlbum;
        TextView tvName;
        Button btnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tvAlbum = itemView.findViewById(R.id.tv_album);
            tvName = itemView.findViewById(R.id.tv_name);
            btnPlay = itemView.findViewById(R.id.btn_play);
        }
    }
}
