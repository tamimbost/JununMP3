package com.jununmp3.player.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jununmp3.player.model.Playlist;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    
    private static final String PREFS_NAME = "junun_playlists";
    private static final String PLAYLISTS_KEY = "playlists";
    private static PlaylistManager instance;
    private Context context;
    private List<Playlist> playlists;
    private Gson gson;
    
    private PlaylistManager(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.playlists = new ArrayList<>();
        loadPlaylists();
    }
    
    public static synchronized PlaylistManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlaylistManager(context);
        }
        return instance;
    }
    
    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlists);
    }
    
    public Playlist getPlaylist(long id) {
        for (Playlist playlist : playlists) {
            if (playlist.getId() == id) {
                return playlist;
            }
        }
        return null;
    }
    
    public Playlist getPlaylistByName(String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
    
    public boolean createPlaylist(String name, String description) {
        if (getPlaylistByName(name) != null) {
            return false; // Playlist with this name already exists
        }
        
        long id = generateNewId();
        Playlist newPlaylist = new Playlist(id, name, description);
        playlists.add(newPlaylist);
        savePlaylists();
        return true;
    }
    
    public boolean deletePlaylist(long id) {
        Playlist playlistToRemove = null;
        for (Playlist playlist : playlists) {
            if (playlist.getId() == id) {
                playlistToRemove = playlist;
                break;
            }
        }
        
        if (playlistToRemove != null) {
            playlists.remove(playlistToRemove);
            savePlaylists();
            return true;
        }
        return false;
    }
    
    public boolean renamePlaylist(long id, String newName) {
        if (getPlaylistByName(newName) != null) {
            return false; // Playlist with this name already exists
        }
        
        Playlist playlist = getPlaylist(id);
        if (playlist != null) {
            playlist.setName(newName);
            savePlaylists();
            return true;
        }
        return false;
    }
    
    public boolean updatePlaylistDescription(long id, String description) {
        Playlist playlist = getPlaylist(id);
        if (playlist != null) {
            playlist.setDescription(description);
            savePlaylists();
            return true;
        }
        return false;
    }
    
    public void updatePlaylist(Playlist playlist) {
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getId() == playlist.getId()) {
                playlists.set(i, playlist);
                savePlaylists();
                break;
            }
        }
    }
    
    private long generateNewId() {
        long maxId = 0;
        for (Playlist playlist : playlists) {
            if (playlist.getId() > maxId) {
                maxId = playlist.getId();
            }
        }
        return maxId + 1;
    }
    
    private void loadPlaylists() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String playlistsJson = prefs.getString(PLAYLISTS_KEY, "");
        
        if (!playlistsJson.isEmpty()) {
            try {
                Type listType = new TypeToken<List<Playlist>>(){}.getType();
                List<Playlist> loadedPlaylists = gson.fromJson(playlistsJson, listType);
                if (loadedPlaylists != null) {
                    this.playlists = loadedPlaylists;
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.playlists = new ArrayList<>();
            }
        }
        
        // Create default playlists if none exist
        if (playlists.isEmpty()) {
            createDefaultPlaylists();
        }
    }
    
    private void savePlaylists() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String playlistsJson = gson.toJson(playlists);
        prefs.edit().putString(PLAYLISTS_KEY, playlistsJson).apply();
    }
    
    private void createDefaultPlaylists() {
        createPlaylist("Favorites", "Your favorite songs");
        createPlaylist("Recently Played", "Songs you've played recently");
        createPlaylist("Most Played", "Your most played songs");
    }
    
    public List<Playlist> searchPlaylists(String query) {
        List<Playlist> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (Playlist playlist : playlists) {
            if (playlist.getName().toLowerCase().contains(lowerQuery) ||
                (playlist.getDescription() != null && 
                 playlist.getDescription().toLowerCase().contains(lowerQuery))) {
                results.add(playlist);
            }
        }
        
        return results;
    }
    
    public int getPlaylistCount() {
        return playlists.size();
    }
    
    public boolean isPlaylistEmpty(long id) {
        Playlist playlist = getPlaylist(id);
        return playlist == null || playlist.isEmpty();
    }
    
    public void clearAllPlaylists() {
        playlists.clear();
        savePlaylists();
    }
    
    public void exportPlaylists() {
        // TODO: Implement playlist export functionality
    }
    
    public void importPlaylists() {
        // TODO: Implement playlist import functionality
    }
}
