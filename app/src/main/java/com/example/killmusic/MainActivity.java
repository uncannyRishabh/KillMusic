package com.example.killmusic;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button killer;
    TextInputLayout timer;
    SeekBar seekbar;
    androidx.appcompat.widget.Toolbar ToolbarLayout;
    private int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ToolbarLayout = findViewById(R.id.customToolbar);

        Log.d("TAG", "onCreate: "+getStatusBarHeight());

        timer = findViewById(R.id.til);
        seekbar = findViewById(R.id.seekBar);
        killer = findViewById(R.id.killer);
        killer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Objects.equals(timer.getEditText().getText().toString(),"")) {
                    Log.d("TAG", "onCreate: time from editText"+time);
                    time = Integer.parseInt(timer.getEditText().getText().toString());
                }
                Toast.makeText(MainActivity.this, "Stopping After "+time+" minutes", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KillMusic();
                        time=0;
                        finishAffinity();
                    }
                },1000*60*time);
            }
        });

        seekbar.setHapticFeedbackEnabled(true);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY
                        ,HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                time=5*progress;
                String s = ""+time;
                timer.getEditText().setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void KillMusic() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.isMusicActive()) {
            Log.d("DEBUG", "onClick: MUSIC STOPPED");
            AudioFocusRequest request = null;
            AudioAttributes audioAttributes = null;
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            request =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(audioAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    i -> {
                                    })
                            .build();
            mAudioManager.requestAudioFocus(request);
        }
    }

    private int getStatusBarHeight() {
        int height;
        Resources myResources = getResources();
        int idStatusBarHeight = myResources.getIdentifier( "status_bar_height", "dimen", "android");
        if (idStatusBarHeight > 0) {
            height = getResources().getDimensionPixelSize(idStatusBarHeight);
            Toast.makeText(this, "Status Bar Height = " + height, Toast.LENGTH_LONG).show();
        } else {
            height = 0;
            Toast.makeText(this, "Resources NOT found", Toast.LENGTH_LONG).show();
        }
        return height;
    }
}