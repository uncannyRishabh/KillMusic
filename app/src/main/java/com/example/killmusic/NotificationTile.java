package com.example.killmusic;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

public class NotificationTile extends TileService {
    private int time=0,clicks=0;
    private Handler handler = new Handler();
    private CountDownTimer cdt;
    public Runnable r;
    private boolean isExecuting = false;

    public void setExecuting(boolean executing){
        this.isExecuting = executing;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Tile tile = getQsTile();
        tile.setLabel("Kill Music");
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
        Toast.makeText(this, "Tile added successfully ^o^", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Toast.makeText(this, "Tile Removed ;(", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick() {
        super.onClick();
        clicks+=1;

        Log.d("TAG", "onClick: isExecuting: "+isExecuting);

        Tile tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        if (time>=0 && time<60){
            time+=5;
            if(!isExecuting) {
                handler.removeCallbacks(r);
                setExecuting(true);
                startTimer();
                r = new Runnable() {
                    @Override
                    public void run() {
                        setExecuting(false);
                        setClicks(0);
                        setTime(0);
                        tile.setState(Tile.STATE_INACTIVE);
                        tile.setLabel("Kill Music");
                        tile.updateTile();
                        KillMusic();
                    }
                };
                handler.postDelayed(r, 1000*time);
            }
            else {
                handler.removeCallbacks(r);
                Log.d("TAG", "onClick: Callback Removed");
                cdt.cancel();
                startTimer();
                setExecuting(true);
                r = new Runnable() {
                    @Override
                    public void run() {
                        setExecuting(false);
                        setClicks(0);
                        setTime(0);
                        tile.setState(Tile.STATE_INACTIVE);
                        tile.setLabel("Kill Music");
                        tile.updateTile();
                        KillMusic();
                    }
                };
                handler.postDelayed(r,1000*60*time);
            }
            tile.setLabel(""+time);
            tile.updateTile();
        }
        if(clicks>12){
            clicks=0;
            time=0;
            handler.removeCallbacks(r);
        }
        Log.d("TAG", "onClick: "+time+" clicks "+clicks);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        tile.setLabel(""+time);
        Log.d("TAG", "onStartListening: TIME CHECK >>"+time);
        if(time==0) tile.setLabel("Kill Music");
        if(time>0) tile.setState(Tile.STATE_ACTIVE);
        else tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
        Log.d("TAG", "onStartListening: time "+time+" clicks "+clicks);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Tile tile = getQsTile();
        tile.setLabel(""+time);
        if(time==0) tile.setLabel("Kill Music");
        if(time>0) tile.setState(Tile.STATE_ACTIVE);
        else tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
        Log.d("TAG", "onStopListening: time "+time+" clicks "+clicks);
    }

    private void KillMusic() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.isMusicActive()) {
            Log.d("DEBUG", "onClick: MUSIC STOPPED");

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            AudioFocusRequest request  = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(audioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    i -> {
                                    })
                            .build();
            mAudioManager.requestAudioFocus(request);
        }
    }

    private void startTimer() {
        cdt = new CountDownTimer(1000*60*time, 1000) {
            final Tile tile = getQsTile();
            public void onTick(long millisUntilFinished) {
                int total_sec = (int) (millisUntilFinished / 1000);
                int minutes = total_sec / 60;
                int seconds = total_sec % 60;
//                Log.d("TIMER ", "onTick: total>> "+total_sec+" mins>> "+minutes+"seconds>> "+seconds);
                tile.setLabel(""+minutes+":"+seconds);
                tile.updateTile();
            }
            public void onFinish() {
                tile.setLabel("Kill Music");
                tile.updateTile();
            }
        }.start();
    }
}