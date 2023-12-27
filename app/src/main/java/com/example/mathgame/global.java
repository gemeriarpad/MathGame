package com.example.mathgame;

public class global {
    public static boolean music = true;
    public static boolean sounds = true;
//    continue level
    private int level = 1;
    private int score = 0;
    private int solvedTasks = 0;

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        // Add custom logic if needed
        this.level = level;
    }
}
