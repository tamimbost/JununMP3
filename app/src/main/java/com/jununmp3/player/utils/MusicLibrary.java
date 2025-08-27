package com.jununmp3.player.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.jununmp3.player.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicLibrary {
    
    public static List<Music> getAllMusic(Context context) {
        List<Music> musicList = new ArrayList<>();
        
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DISPLAY_NAME
        };
        
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, null, sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                
                do {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    String album = cursor.getString(albumColumn);
                    long duration = cursor.getLong(durationColumn);
                    String path = cursor.getString(pathColumn);
                    long albumId = cursor.getLong(albumIdColumn);
                    long size = cursor.getLong(sizeColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    
                    // Get album art URI
                    String albumArt = getAlbumArtUri(albumId);
                    
                    // Handle null values
                    if (title == null) title = "Unknown Title";
                    if (artist == null) artist = "Unknown Artist";
                    if (album == null) album = "Unknown Album";
                    if (displayName == null) displayName = title;
                    
                    // Only add if duration > 0 (valid audio file)
                    if (duration > 0 && path != null) {
                        Music music = new Music(id, title, artist, album, duration, 
                                              path, albumArt, size, displayName);
                        musicList.add(music);
                    }
                    
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return musicList;
    }
    
    public static List<Music> getMusicByArtist(Context context, String artistName) {
        List<Music> musicList = new ArrayList<>();
        
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DISPLAY_NAME
        };
        
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                          MediaStore.Audio.Media.ARTIST + " = ?";
        String[] selectionArgs = {artistName};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, selectionArgs, sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                
                do {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    String album = cursor.getString(albumColumn);
                    long duration = cursor.getLong(durationColumn);
                    String path = cursor.getString(pathColumn);
                    long albumId = cursor.getLong(albumIdColumn);
                    long size = cursor.getLong(sizeColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    
                    String albumArt = getAlbumArtUri(albumId);
                    
                    if (title == null) title = "Unknown Title";
                    if (artist == null) artist = "Unknown Artist";
                    if (album == null) album = "Unknown Album";
                    if (displayName == null) displayName = title;
                    
                    if (duration > 0 && path != null) {
                        Music music = new Music(id, title, artist, album, duration, 
                                              path, albumArt, size, displayName);
                        musicList.add(music);
                    }
                    
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return musicList;
    }
    
    public static List<Music> getMusicByAlbum(Context context, String albumName) {
        List<Music> musicList = new ArrayList<>();
        
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DISPLAY_NAME
        };
        
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                          MediaStore.Audio.Media.ALBUM + " = ?";
        String[] selectionArgs = {albumName};
        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC, " + 
                          MediaStore.Audio.Media.TITLE + " ASC";
        
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, selectionArgs, sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                
                do {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    String album = cursor.getString(albumColumn);
                    long duration = cursor.getLong(durationColumn);
                    String path = cursor.getString(pathColumn);
                    long albumId = cursor.getLong(albumIdColumn);
                    long size = cursor.getLong(sizeColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    
                    String albumArt = getAlbumArtUri(albumId);
                    
                    if (title == null) title = "Unknown Title";
                    if (artist == null) artist = "Unknown Artist";
                    if (album == null) album = "Unknown Album";
                    if (displayName == null) displayName = title;
                    
                    if (duration > 0 && path != null) {
                        Music music = new Music(id, title, artist, album, duration, 
                                              path, albumArt, size, displayName);
                        musicList.add(music);
                    }
                    
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return musicList;
    }
    
    public static List<String> getAllArtists(Context context) {
        List<String> artists = new ArrayList<>();
        
        String[] projection = {MediaStore.Audio.Artists.ARTIST};
        String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";
        
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, null, null, sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                
                do {
                    String artist = cursor.getString(artistColumn);
                    if (artist != null && !artist.equals("<unknown>")) {
                        artists.add(artist);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return artists;
    }
    
    public static List<String> getAllAlbums(Context context) {
        List<String> albums = new ArrayList<>();
        
        String[] projection = {MediaStore.Audio.Albums.ALBUM};
        String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";
        
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, null, null, sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                
                do {
                    String album = cursor.getString(albumColumn);
                    if (album != null && !album.equals("<unknown>")) {
                        albums.add(album);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return albums;
    }
    
    private static String getAlbumArtUri(long albumId) {
        return "content://media/external/audio/albumart/" + albumId;
    }
    
    public static List<Music> searchMusic(Context context, String query) {
        List<Music> musicList = new ArrayList<>();
        
        String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DISPLAY_NAME
        };
        
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND (" +
                          MediaStore.Audio.Media.TITLE + " LIKE ? OR " +
                          MediaStore.Audio.Media.ARTIST + " LIKE ? OR " +
                          MediaStore.Audio.Media.ALBUM + " LIKE ?)";
        String searchTerm = "%" + query + "%";
        String[] selectionArgs = {searchTerm, searchTerm, searchTerm};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        
        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, selectionArgs, sortOrder)) {
            
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                
                do {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    String album = cursor.getString(albumColumn);
                    long duration = cursor.getLong(durationColumn);
                    String path = cursor.getString(pathColumn);
                    long albumId = cursor.getLong(albumIdColumn);
                    long size = cursor.getLong(sizeColumn);
                    String displayName = cursor.getString(displayNameColumn);
                    
                    String albumArt = getAlbumArtUri(albumId);
                    
                    if (title == null) title = "Unknown Title";
                    if (artist == null) artist = "Unknown Artist";
                    if (album == null) album = "Unknown Album";
                    if (displayName == null) displayName = title;
                    
                    if (duration > 0 && path != null) {
                        Music music = new Music(id, title, artist, album, duration, 
                                              path, albumArt, size, displayName);
                        musicList.add(music);
                    }
                    
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return musicList;
    }
}
