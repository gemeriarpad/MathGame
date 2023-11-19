package com.example.mathgame;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game extends AppCompatActivity {

    private static final long COUNTDOWN_TIME = 4000;
    private CountDownTimer countDownTimer;
    private TextView countdownTextView;
    private TextView currentTaskTextView;
    private TextView currentTaskResultTextView;
    private TextView scoreTextView;
    private TextView solvedTasks;
    private TextView nextTaskTextView;
    private SpannableStringBuilder solvedTasksText = new SpannableStringBuilder();

    private int result;
    private HashMap<Integer, int[]> nextTasks = new HashMap<>();

    private int currentTaskId;
    private int currentLevel = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Button Clear = findViewById(R.id.numC);

        Button stopMenu = findViewById(R.id.stopGameButton);
        Button muteSound = findViewById(R.id.muteSoundButton);
        Button muteMusic = findViewById(R.id.muteMusicButton);

        countdownTextView = findViewById(R.id.countdownTextView);
        startCountdown();

        currentTaskTextView = findViewById(R.id.currentTask);
        currentTaskResultTextView = findViewById(R.id.currentTaskResult);
        scoreTextView = findViewById(R.id.scoreCounterTextView);
        solvedTasks = findViewById(R.id.solvedTasks);
        nextTaskTextView = findViewById(R.id.nextTasks);

        for (int i = 0; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("num" + i, "id", getPackageName());
            Button numButton = findViewById(buttonId);
            numButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberButtonClick(((Button) v).getText().toString());
                }
            });
            Clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   currentTaskResultTextView.setText("");
                }
            });
        }
    }

    private void onNumberButtonClick(String number) {
        currentTaskResultTextView.append(number);
        if(Integer.toString(result).length() == currentTaskResultTextView.getText().length()){
            if (result == Integer.parseInt(currentTaskResultTextView.getText().toString())) {
                //Correct answer
                scoreTextView.setText(Integer.toString(Integer.parseInt(scoreTextView.getText().toString()) + 100));
                solvedTasksText.append("\n" + currentTaskTextView.getText() + currentTaskResultTextView.getText());
                setSpannableTextColor(solvedTasksText, Color.argb(255, 0, 255, 0));

                currentTaskId++;
                nextTask();
            } else {
                //Incorrect answer
                scoreTextView.setText(Integer.toString(Integer.parseInt(scoreTextView.getText().toString()) - 50));
                solvedTasksText.append("\n" + currentTaskTextView.getText() + currentTaskResultTextView.getText());
                setSpannableTextColor(solvedTasksText, Color.argb(255, 255, 0, 0));

                currentTaskId++;
                nextTask();
            }

            solvedTasks.setText(solvedTasksText);
        }
    }


    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                long secondsUntilFinished = millisUntilFinished / 1000;
                countdownTextView.setText(String.valueOf(secondsUntilFinished));
            }
            //After the countdown
            @Override
            public void onFinish() {
                countdownTextView.setText("");
                generateTasks(5);
                currentTaskId = 0;
                nextTask();
            }
        }.start();
    }

    private static int generateRandomNumber(int minValue, int maxValue) {
        // Random objektum létrehozása
        Random random = new Random();

        // Két érték közötti random szám generálása és visszaadása
        return random.nextInt((maxValue - minValue) + 1) + minValue;
    }

    private void generateTasks(int count){
        for(int i = 0; i < count; i++){
            int firstNumber = generateRandomNumber(1,10);
            int secondNumber = generateRandomNumber(1,10);
            nextTasks.put(i,new int[] {firstNumber, secondNumber});
        }
    }

    private void nextTask(){
        if (!nextTasks.containsKey(currentTaskId)){
            //End of the level
            currentTaskResultTextView.setText("");
            currentTaskTextView.setText("");
            Toast.makeText(this, "Level " + Integer.toString(currentLevel) + "completed", Toast.LENGTH_SHORT).show();

            //On pupup screen to continue
            currentLevel++;
            generateTasks(10);
            currentTaskId = 0;
            nextTask();
            solvedTasksText.clear();
        }else{
            int firstNumber = nextTasks.get(currentTaskId)[0];
            int secondNumber = nextTasks.get(currentTaskId)[1];
            result =  firstNumber + secondNumber;
            currentTaskResultTextView.setText("");
            currentTaskTextView.setText(firstNumber + " + " + secondNumber + " = ");
            nextTaskTextView.setText("");
            for(int i = currentTaskId + 1; i < nextTasks.size();i++){
                nextTaskTextView.append("\n" + nextTasks.get(i)[0] + " + " + nextTasks.get(i)[1] + " = ");
            }
        }
    }

// End of the counter
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // For correct and incorrect colors
    private void setSpannableTextColor(SpannableStringBuilder builder, int color) {
        builder.setSpan(new ForegroundColorSpan(color), builder.length() - (currentTaskTextView.getText().length() + currentTaskResultTextView.getText().length()), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}