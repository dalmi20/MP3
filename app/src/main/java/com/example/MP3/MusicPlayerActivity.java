package com.example.MP3;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicPlayerActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    ImageView favoriteBtn;
    AtomicBoolean isFavorite;
    PlayerService musicService;

    public  static   TextView titleTv,currentTimeTv,totalTimeTv;
    public  static SeekBar seekBar;
    public  static ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    public  static ArrayList<AudioModel> quranList;
    public  static AudioModel currentSong;
    public  static  MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        favoriteBtn = findViewById(R.id.favoriteBtn);
        databaseHelper = new DatabaseHelper(this);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        titleTv.setSelected(true);

        quranList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        isFavorite = new AtomicBoolean(databaseHelper.isFavorite(currentSong.getTitle()));
        updateFavoriteButtonState(isFavorite.get());

        favoriteBtn.setOnClickListener(v -> {
            boolean isFavoriteNow = !isFavorite.get();

            if (isFavoriteNow) {
                databaseHelper.addFavorite(currentSong);
            } else {
                databaseHelper.removeFavorite(currentSong.getTitle());
            }

            isFavorite.set(isFavoriteNow);
            updateFavoriteButtonState(isFavoriteNow);
        });
    }

  public static void setResourcesWithMusic(){
        currentSong = quranList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());

        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());

        playMusic();
        return;


    }


    private  static void playMusic(){

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void playNextSong(){

        if(MyMediaPlayer.currentIndex== quranList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private static void playPreviousSong(){
        if(MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private static void pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }


    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
    private void updateFavoriteButtonState(boolean isFavorite) {
        if (isFavorite) {
            favoriteBtn.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            favoriteBtn.setImageResource(R.drawable.baseline_favorite_border_24);
        }
    }
    ServiceConnection playerServiceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName service, IBinder iBinder) {
            PlayerService.MyBinder binder = (PlayerService.MyBinder) iBinder;
            musicService = binder.currentService();
            musicService.showNotification();


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int item_id= item.getItemId();
        if(item_id==R.id.download){
            Intent intent = new Intent(MusicPlayerActivity.this, Download.class);
            startActivity(intent);
        } else if (item_id==R.id.favoris) {
            Intent intent = new Intent(MusicPlayerActivity.this,Favoris.class);
            startActivity(intent);
        }
        return  true;
    }
}