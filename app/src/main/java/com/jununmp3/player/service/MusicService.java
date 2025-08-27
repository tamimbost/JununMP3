package com.jununmp3.player.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jununmp3.player.MainActivity;
import com.jununmp3.player.R;
import com.jununmp3.player.model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {
    
    private static final String CHANNEL_ID = "music_playback";
    private static final int NOTIFICATION_ID = 1;
    
    private MediaPlayer mediaPlayer;
    private List<Music> playlist = new ArrayList<>();
    private List<Music> originalPlaylist = new ArrayList<>();
    private int currentPosition = -1;
    private boolean isPlaying = false;
    private boolean shuffleEnabled = false;
    private boolean repeatEnabled = false;
    
    private MediaSessionCompat mediaSession;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateProgressRunnable;
    
    private final IBinder binder = new MusicBinder();
    
    public interface ProgressUpdateListener {
        void onProgressUpdate(int currentPosition, int duration);
    }
    
    private ProgressUpdateListener progressListener;
    
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaPlayer();
        createNotificationChannel();
        initializeMediaSession();
        startProgressUpdater();
    }
    
    private void initializeMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        
        mediaPlayer.setOnCompletionListener(mp -> {
            if (repeatEnabled) {
                seekTo(0);
                playMusic();
            } else {
                nextSong();
            }
        });
        
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Error playing music", Toast.LENGTH_SHORT).show();
            return true;
        });
    }
    
    private void initializeMediaSession() {
        mediaSession = new MediaSessionCompat(this, "MusicService");
        mediaSession.setActive(true);
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows currently playing music");
            channel.setShowBadge(false);
            channel.setSound(null, null);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private void startProgressUpdater() {
        updateProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    if (progressListener != null) {
                        progressListener.onProgressUpdate(
                            mediaPlayer.getCurrentPosition(),
                            mediaPlayer.getDuration()
                        );
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleNotificationActions(intent);
        return START_STICKY;
    }
    
    private void handleNotificationActions(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "PLAY":
                    resumeMusic();
                    break;
                case "PAUSE":
                    pauseMusic();
                    break;
                case "NEXT":
                    nextSong();
                    break;
                case "PREVIOUS":
                    previousSong();
                    break;
            }
        }
    }
    
    public void setPlaylist(List<Music> musicList, int startPosition) {
        this.originalPlaylist = new ArrayList<>(musicList);
        this.playlist = new ArrayList<>(musicList);
        this.currentPosition = startPosition;
        
        if (shuffleEnabled) {
            shufflePlaylist();
        }
    }
    
    public void playMusic() {
        if (currentPosition >= 0 && currentPosition < playlist.size()) {
            try {
                Music currentMusic = playlist.get(currentPosition);
                
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currentMusic.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                
                isPlaying = true;
                startForeground(NOTIFICATION_ID, createNotification());
                startProgressUpdater();
                handler.post(updateProgressRunnable);
                
            } catch (Exception e) {
                Toast.makeText(this, "Error playing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    public void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            handler.removeCallbacks(updateProgressRunnable);
            updateNotification();
        }
    }
    
    public void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            handler.post(updateProgressRunnable);
            updateNotification();
        }
    }
    
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPlaying = false;
            handler.removeCallbacks(updateProgressRunnable);
            stopForeground(true);
        }
    }
    
    public void nextSong() {
        if (playlist.isEmpty()) return;
        
        currentPosition++;
        if (currentPosition >= playlist.size()) {
            currentPosition = 0;
        }
        playMusic();
    }
    
    public void previousSong() {
        if (playlist.isEmpty()) return;
        
        currentPosition--;
        if (currentPosition < 0) {
            currentPosition = playlist.size() - 1;
        }
        playMusic();
    }
    
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }
    
    public void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
        
        if (shuffleEnabled) {
            shufflePlaylist();
        } else {
            // Restore original order
            Music currentSong = getCurrentMusic();
            playlist = new ArrayList<>(originalPlaylist);
            currentPosition = playlist.indexOf(currentSong);
        }
    }
    
    public void toggleRepeat() {
        repeatEnabled = !repeatEnabled;
    }
    
    private void shufflePlaylist() {
        Music currentSong = getCurrentMusic();
        Collections.shuffle(playlist, new Random());
        
        // Move current song to front
        if (currentSong != null) {
            playlist.remove(currentSong);
            playlist.add(0, currentSong);
            currentPosition = 0;
        }
    }
    
    private Notification createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Music currentMusic = getCurrentMusic();
        String title = currentMusic != null ? currentMusic.getTitle() : "Unknown";
        String artist = currentMusic != null ? currentMusic.getArtist() : "Unknown Artist";
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isPlaying)
            .setShowWhen(false);
        
        // Add media controls
        builder.addAction(createNotificationAction("PREVIOUS", "Previous", R.drawable.ic_skip_previous))
               .addAction(createNotificationAction(
                   isPlaying ? "PAUSE" : "PLAY", 
                   isPlaying ? "Pause" : "Play", 
                   isPlaying ? R.drawable.ic_pause : R.drawable.ic_play))
               .addAction(createNotificationAction("NEXT", "Next", R.drawable.ic_skip_next));
        
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(mediaSession.getSessionToken()));
        
        return builder.build();
    }
    
    private NotificationCompat.Action createNotificationAction(String action, String title, int icon) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(
            this, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Action(icon, title, pendingIntent);
    }
    
    private void updateNotification() {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            NotificationManagerCompat.from(this)
                .notify(NOTIFICATION_ID, createNotification());
        }
    }
    
    // Getters
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public boolean isShuffleEnabled() {
        return shuffleEnabled;
    }
    
    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }
    
    public Music getCurrentMusic() {
        if (currentPosition >= 0 && currentPosition < playlist.size()) {
            return playlist.get(currentPosition);
        }
        return null;
    }
    
    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }
    
    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }
    
    public void setProgressUpdateListener(ProgressUpdateListener listener) {
        this.progressListener = listener;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        if (mediaSession != null) {
            mediaSession.release();
        }
        
        handler.removeCallbacks(updateProgressRunnable);
    }
}
