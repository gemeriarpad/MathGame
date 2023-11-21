package com.example.mathgame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class Help extends AppCompatActivity {

    private MediaPlayer buttonClickSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Button backButton = findViewById(R.id.buttonBackToMenu);
        buttonClickSound = MediaPlayer.create(this, R.raw.buttonclick);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickSound.start();
                finish();
                overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
            }
        });
    }
    @Override
    public void onBackPressed() {
        buttonClickSound.start();
        finish();
        overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
        super.onBackPressed();
    }


}