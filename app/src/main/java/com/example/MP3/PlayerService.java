package com.example.MP3;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;

public class PlayerService extends Service {
    private MyBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSession;
    private Runnable runnable;
    private AudioManager audioManager;

    @Override
    public IBinder onBind(Intent intent) {
        mediaSession = new MediaSessionCompat(getApplicationContext(), "My Music");
        return myBinder;
    }

    public class MyBinder extends Binder {
        public PlayerService currentService() {
            return PlayerService.this;
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification() {
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),  "channel1")
                .setContentTitle(MusicPlayerActivity.currentSong.getTitle())
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", null)
                .addAction(R.drawable.ic_baseline_play_circle_outline_24, "Play", null)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", null)
                .build();

        startForeground(13,notification);
    }
}