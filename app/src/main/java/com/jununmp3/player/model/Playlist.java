package com.jununmp3.player.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    private long id;
    private String name;
    private String description;
    private List<Music> musicList;
    private long dateCreated;
    private long dateModified;
    
    public Playlist() {
        this.musicList = new ArrayList<>();
        this.dateCreated = System.currentTimeMillis();
        this.dateModified = System.currentTimeMillis();
    }
    
    public Playlist(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.musicList = new ArrayList<>();
        this.dateCreated = System.currentTimeMillis();
        this.dateModified = System.currentTimeMillis();
    }
    
    public Playlist(long id, String name, String description, List<Music> musicList, 
                   long dateCreated, long dateModified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.musicList = musicList != null ? musicList : new ArrayList<>();
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }
    
    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<Music> getMusicList() { return musicList; }
    public long getDateCreated() { return dateCreated; }
    public long getDateModified() { return dateModified; }
    
    // Setters
    public void setId(long id) { this.id = id; }
    public void setName(String name) { 
        this.name = name; 
        updateModifiedDate();
    }
    public void setDescription(String description) { 
        this.description = description; 
        updateModifiedDate();
    }
    public void setMusicList(List<Music> musicList) { 
        this.musicList = musicList != null ? musicList : new ArrayList<>(); 
        updateModifiedDate();
    }
    public void setDateCreated(long dateCreated) { this.dateCreated = dateCreated; }
    public void setDateModified(long dateModified) { this.dateModified = dateModified; }
    
    // Utility methods
    public void addMusic(Music music) {
        if (!musicList.contains(music)) {
            musicList.add(music);
            updateModifiedDate();
        }
    }
    
    public void removeMusic(Music music) {
        if (musicList.remove(music)) {
            updateModifiedDate();
        }
    }
    
    public void removeMusicAt(int position) {
        if (position >= 0 && position < musicList.size()) {
            musicList.remove(position);
            updateModifiedDate();
        }
    }
    
    public boolean containsMusic(Music music) {
        return musicList.contains(music);
    }
    
    public int getMusicCount() {
        return musicList.size();
    }
    
    public long getTotalDuration() {
        long totalDuration = 0;
        for (Music music : musicList) {
            totalDuration += music.getDuration();
        }
        return totalDuration;
    }
    
    public String getFormattedDuration() {
        long totalMs = getTotalDuration();
        long totalSeconds = totalMs / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    private void updateModifiedDate() {
        this.dateModified = System.currentTimeMillis();
    }
    
    public void clearPlaylist() {
        musicList.clear();
        updateModifiedDate();
    }
    
    public boolean isEmpty() {
        return musicList.isEmpty();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Playlist playlist = (Playlist) obj;
        return id == playlist.id;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", musicCount=" + getMusicCount() +
                ", duration='" + getFormattedDuration() + '\'' +
                '}';
    }
}
