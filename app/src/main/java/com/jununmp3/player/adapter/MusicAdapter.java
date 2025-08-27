package com.jununmp3.player.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jununmp3.player.R;
import com.jununmp3.player.model.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    
    private List<Music> musicList;
    private OnMusicClickListener listener;
    private int selectedPosition = -1;
    
    public interface OnMusicClickListener {
        void onMusicClick(Music music, int position);
    }
    
    public MusicAdapter(List<Music> musicList, OnMusicClickListener listener) {
        this.musicList = musicList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.bind(music, position);
    }
    
    @Override
    public int getItemCount() {
        return musicList.size();
    }
    
    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }
    
    public void updateMusicList(List<Music> newMusicList) {
        this.musicList.clear();
        this.musicList.addAll(newMusicList);
        notifyDataSetChanged();
    }
    
    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageAlbumArt;
        private TextView textSongTitle;
        private TextView textArtist;
        private TextView textDuration;
        private ImageView imageMore;
        
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageAlbumArt = itemView.findViewById(R.id.imageAlbumArt);
            textSongTitle = itemView.findViewById(R.id.textSongTitle);
            textArtist = itemView.findViewById(R.id.textArtist);
            textDuration = itemView.findViewById(R.id.textDuration);
            imageMore = itemView.findViewById(R.id.imageMore);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMusicClick(musicList.get(position), position);
                    setSelectedPosition(position);
                }
            });
            
            imageMore.setOnClickListener(v -> {
                // TODO: Show popup menu with options (Add to playlist, Delete, etc.)
            });
        }
        
        public void bind(Music music, int position) {
            textSongTitle.setText(music.getTitle());
            textArtist.setText(music.getArtist());
            textDuration.setText(music.getFormattedDuration());
            
            // Load album art using Glide or similar library
            // For now, we'll use a placeholder
            imageAlbumArt.setImageResource(R.drawable.music_placeholder);
            
            // Highlight selected item
            if (position == selectedPosition) {
                itemView.setBackgroundColor(itemView.getContext().getColor(R.color.selected_item_background));
            } else {
                itemView.setBackgroundColor(itemView.getContext().getColor(R.color.card_background));
            }
        }
    }
}
