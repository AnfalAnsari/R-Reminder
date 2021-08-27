package com.example.scrollview;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Alarm extends Activity {
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      onPause();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();

    }

}
