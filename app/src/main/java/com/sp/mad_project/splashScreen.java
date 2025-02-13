package com.sp.mad_project;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class splashScreen extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private float volume = 1.0f; // Full volume

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize and start the music
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_music);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer.start();

        // Schedule the fade-out and transition after 4 seconds
        new Handler().postDelayed(() -> fadeOutMusicAndTransition(), 4000);
    }

    private void fadeOutMusicAndTransition() {
        // Gradually reduce the volume of the music
        final Handler fadeHandler = new Handler();
        final int fadeDuration = 2000; // 2 seconds for the fade-out
        final int fadeStep = 100; // Step interval in milliseconds
        final float fadeStepVolume = volume / (fadeDuration / fadeStep);

        fadeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (volume > 0.0f) {
                    volume -= fadeStepVolume;
                    mediaPlayer.setVolume(Math.max(volume, 0.0f), Math.max(volume, 0.0f));
                    fadeHandler.postDelayed(this, fadeStep);
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    transitionToHomePage();
                }
            }
        }, fadeStep);
    }

    private void transitionToHomePage() {
        Intent intent = new Intent(splashScreen.this, register.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
