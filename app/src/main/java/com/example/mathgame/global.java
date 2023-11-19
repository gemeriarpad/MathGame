package com.example.mathgame;

import android.content.Context;
import android.media.MediaPlayer;

public class global {
    public MediaPlayer backgroundMusic, buttonClickSound, correctAnswer, incorrectAnswer;
    public static boolean music = true;
    public static boolean sounds = true;

    public void musicSwitch(){
        music = !music;
    }

    public void soundsSwitch(){
        sounds = !sounds;
    }

    public global(Context context){
        backgroundMusic = MediaPlayer.create(context, R.raw.backgronudmusic);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f, 0.5f);
    }
}
