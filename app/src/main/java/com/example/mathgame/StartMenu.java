package com.example.mathgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
public class StartMenu extends AppCompatActivity {
    private MediaPlayer buttonClickSound, backgroundMusic;
    private Button muteSound,muteMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundMusic = MediaPlayer.create(this, R.raw.backgronudmusic);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f, 0.5f);
        buttonClickSound = MediaPlayer.create(this, R.raw.buttonclick);

        muteSound = findViewById(R.id.muteSoundButton);
        muteMusic = findViewById(R.id.muteMusicButton);

        checkMusicAndSounds();

        muteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.music = !global.music;
                checkMusicAndSounds();
                buttonClickSound.start();
            }
        });
        muteSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.sounds = !global.sounds;
                checkMusicAndSounds();
                buttonClickSound.start();
            }
        });
    }

    // start
    public void onButton1Click(View view) {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
        buttonClickSound.start();
        backgroundMusic.pause();
    }

    // how to
    public void onButton2Click(View view) {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
        buttonClickSound.start();
    }

    public void onButton3Click(View view) {
        showHighscoreDialog();
        buttonClickSound.start();
    }

    // external link
    public void onButton4Click(View view) {
        buttonClickSound.start();
        Log.d("Button4Click", "Button 4 clicked");

        String urlToOpen = "https://www.google.com";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showHighscoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Highscore");
        builder.setMessage("A jelenlegi highscore: 1000"); // Itt a valós adatot helyettesítsd be

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkMusicAndSounds(){
        if(global.sounds){
            buttonClickSound.setVolume(0.5f,0.5f);
            muteSound.setText("Sounds ON");
        }else{
            buttonClickSound.setVolume(0f,0f);
            muteSound.setText("Sounds OFF");
        }

        if(global.music){
            if(!backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
            muteMusic.setText("Music ON");
        }else{
            backgroundMusic.pause();
            muteMusic.setText("Music OFF");
        }
    }
}
