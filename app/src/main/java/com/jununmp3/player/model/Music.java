package com.jununmp3.player.model;

import java.io.Serializable;

public class Music implements Serializable {
    private long id;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private String path;
    private String albumArt;
    private long size;
    private String displayName;
    
    public Music() {}
    
    public Music(long id, String title, String artist, String album, 
                 long duration, String path, String albumArt, long size, String displayName) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.path = path;
        this.albumArt = albumArt;
        this.size = size;
        this.displayName = displayName;
    }
    
    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public long getDuration() { return duration; }
    public String getPath() { return path; }
    public String getAlbumArt() { return albumArt; }
    public long getSize() { return size; }
    public String getDisplayName() { return displayName; }
    
    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbum(String album) { this.album = album; }
    public void setDuration(long duration) { this.duration = duration; }
    public void setPath(String path) { this.path = path; }
    public void setAlbumArt(String albumArt) { this.albumArt = albumArt; }
    public void setSize(long size) { this.size = size; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public String getFormattedSize() {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Music music = (Music) obj;
        return id == music.id && path.equals(music.path);
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id) + path.hashCode();
    }
    
    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                '}';
    }
}
