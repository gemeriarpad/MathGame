package com.example.mathgame;

public class highscore {
    public String username;
    public int score;

    public highscore(String username, int score) {
        this.username = username;
        this.score = score;
    }
    public highscore(){

    }

    public CharSequence getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
