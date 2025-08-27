package com.jununmp3.player;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jununmp3.player.adapter.MusicAdapter;
import com.jununmp3.player.model.Music;
import com.jununmp3.player.service.MusicService;
import com.jununmp3.player.utils.MusicLibrary;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MusicAdapter.OnMusicClickListener {

    private RecyclerView recyclerViewMusic;
    private MusicAdapter musicAdapter;
    private List<Music> musicList = new ArrayList<>();
    
    // Player UI components
    private ImageView imageAlbumArt;
    private TextView textSongTitle, textArtist, textCurrentTime, textTotalTime;
    private SeekBar seekBar;
    private ImageButton buttonPlayPause, buttonNext, buttonPrevious, buttonShuffle, buttonRepeat;
    
    // Service
    private MusicService musicService;
    private boolean serviceBound = false;
    
    // Permission launcher
    private ActivityResultLauncher<String[]> permissionLauncher;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            serviceBound = true;
            updateUI();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupPermissionLauncher();
        requestPermissions();
    }

    private void initViews() {
        recyclerViewMusic = findViewById(R.id.recyclerViewMusic);
        imageAlbumArt = findViewById(R.id.imageAlbumArt);
        textSongTitle = findViewById(R.id.textSongTitle);
        textArtist = findViewById(R.id.textArtist);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalTime = findViewById(R.id.textTotalTime);
        seekBar = findViewById(R.id.seekBar);
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonShuffle = findViewById(R.id.buttonShuffle);
        buttonRepeat = findViewById(R.id.buttonRepeat);
    }

    private void setupRecyclerView() {
        musicAdapter = new MusicAdapter(musicList, this);
        recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMusic.setAdapter(musicAdapter);
    }

    private void setupClickListeners() {
        buttonPlayPause.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                if (musicService.isPlaying()) {
                    musicService.pauseMusic();
                } else {
                    musicService.resumeMusic();
                }
                updatePlayPauseButton();
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.nextSong();
                updateUI();
            }
        });

        buttonPrevious.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.previousSong();
                updateUI();
            }
        });

        buttonShuffle.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.toggleShuffle();
                updateShuffleButton();
            }
        });

        buttonRepeat.setOnClickListener(v -> {
            if (serviceBound && musicService != null) {
                musicService.toggleRepeat();
                updateRepeatButton();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && serviceBound && musicService != null) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean allPermissionsGranted = true;
                for (Boolean granted : result.values()) {
                    if (!granted) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
                
                if (allPermissionsGranted) {
                    loadMusicLibrary();
                    startMusicService();
                } else {
                    Toast.makeText(this, "Permissions are required to access music files", 
                        Toast.LENGTH_LONG).show();
                }
            }
        );
    }

    private void requestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            loadMusicLibrary();
            startMusicService();
        }
    }

    private void loadMusicLibrary() {
        musicList.clear();
        musicList.addAll(MusicLibrary.getAllMusic(this));
        musicAdapter.notifyDataSetChanged();
    }

    private void startMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onMusicClick(Music music, int position) {
        if (serviceBound && musicService != null) {
            musicService.setPlaylist(musicList, position);
            musicService.playMusic();
            updateUI();
        }
    }

    private void updateUI() {
        if (serviceBound && musicService != null) {
            Music currentMusic = musicService.getCurrentMusic();
            if (currentMusic != null) {
                textSongTitle.setText(currentMusic.getTitle());
                textArtist.setText(currentMusic.getArtist());
                seekBar.setMax((int) currentMusic.getDuration());
                textTotalTime.setText(formatTime(currentMusic.getDuration()));
                
                // Load album art if available
                // imageAlbumArt can be updated with Glide or similar library
            }
            
            updatePlayPauseButton();
            updateShuffleButton();
            updateRepeatButton();
        }
    }

    private void updatePlayPauseButton() {
        if (serviceBound && musicService != null) {
            if (musicService.isPlaying()) {
                buttonPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                buttonPlayPause.setImageResource(R.drawable.ic_play);
            }
        }
    }

    private void updateShuffleButton() {
        if (serviceBound && musicService != null) {
            if (musicService.isShuffleEnabled()) {
                buttonShuffle.setColorFilter(getColor(R.color.accent));
            } else {
                buttonShuffle.setColorFilter(getColor(R.color.text_secondary));
            }
        }
    }

    private void updateRepeatButton() {
        if (serviceBound && musicService != null) {
            if (musicService.isRepeatEnabled()) {
                buttonRepeat.setColorFilter(getColor(R.color.accent));
            } else {
                buttonRepeat.setColorFilter(getColor(R.color.text_secondary));
            }
        }
    }

    private String formatTime(long timeMs) {
        long minutes = (timeMs / 1000) / 60;
        long seconds = (timeMs / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}
