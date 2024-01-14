package com.example.mathgame;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class Game extends AppCompatActivity {
    private global GlobalVariables;
    private MediaPlayer backgroundMusic, buttonClickSound, correctAnswer, incorrectAnswer;
    private static final long COUNTDOWN_TIME = 4000;
    private long REMAINING_TIME = 15000;

    private Handler remainingTimer;
    private CountDownTimer countDownTimer;
    private TextView countdownTextView, currentTaskTextView, currentTaskResultTextView, scoreTextView, solvedTasks, nextTaskTextView, levelCounterTextView, remainingTimeValue;
    private SpannableStringBuilder solvedTasksText = new SpannableStringBuilder();

    private int result, currentTaskId, currentLevel = 1;
    private HashMap<Integer, int[]> nextTasks = new HashMap<>();

    private HashMap<Integer, String> operations = new HashMap<>();
    DatabaseReference HighScoreDB;
    Button Clear, stopMenu, muteSound, muteMusic, continueBtn, backToMainMenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        operations.put(0, "+");
        operations.put(1, "-");
        operations.put(2, "*");
        operations.put(3, "/");

        stopMenu = findViewById(R.id.stopGameButton);
        muteSound = findViewById(R.id.muteSoundButton);
        muteMusic = findViewById(R.id.muteMusicButton);


        //music
        backgroundMusic = MediaPlayer.create(this, R.raw.backgronudmusic);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f, 0.5f);

        //sounds
        buttonClickSound = MediaPlayer.create(this, R.raw.buttonclick);
        correctAnswer = MediaPlayer.create(this, R.raw.correctanswer);
        incorrectAnswer = MediaPlayer.create(this, R.raw.incorrectanswer);
        checkMusicAndSounds();

        Clear = findViewById(R.id.numC);

        countdownTextView = findViewById(R.id.countdownTextView);
        startCountdown();

        currentTaskTextView = findViewById(R.id.currentTask);
        currentTaskResultTextView = findViewById(R.id.currentTaskResult);
        scoreTextView = findViewById(R.id.scoreCounterTextView);
        solvedTasks = findViewById(R.id.solvedTasks);
        nextTaskTextView = findViewById(R.id.nextTasks);
        levelCounterTextView = findViewById(R.id.levelCounterTextView);
        remainingTimeValue = findViewById(R.id.remainingTimeValue);

        levelCounterTextView.setText(Integer.toString(currentLevel));

        for (int i = 0; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("num" + i, "id", getPackageName());
            Button numButton = findViewById(buttonId);
            numButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (countdownTextView.getText() == "") {
                        onNumberButtonClick(((Button) v).getText().toString());
                    } else {
                        buttonClickSound.start();
                    }
                }
            });
            Clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClickSound.start();
                    currentTaskResultTextView.setText("");
                }
            });
        }

        // Popup menu
        stopMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickSound.start();
//               Stop the timer
                if (remainingTimer != null) {
                    remainingTimer.removeCallbacksAndMessages(null);
                }

                final Dialog dialog = new Dialog(Game.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);

                dialog.setContentView(R.layout.popup_meu);
                dialog.show();
                continueBtn = dialog.findViewById(R.id.btnContinue);
                backToMainMenuBtn = dialog.findViewById(R.id.btnBackToMenu);

                continueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        buttonClickSound.start();
                    }
                });

                backToMainMenuBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonClickSound.start();
                        finish();
                        overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
                    }
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        startRemainingTime();
                    }
                });
            }

        });

        muteMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.music = !global.music;
                checkMusicAndSounds();
            }
        });
        muteSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                global.sounds = !global.sounds;
                checkMusicAndSounds();
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

    private void onNumberButtonClick(String number) {
        currentTaskResultTextView.append(number);
        buttonClickSound.start();
        if (Integer.toString(result).length() == currentTaskResultTextView.getText().length()) {
            if (result == Integer.parseInt(currentTaskResultTextView.getText().toString())) {
                //Correct answer
                scoreTextView.setText(Integer.toString(Integer.parseInt(scoreTextView.getText().toString()) + 100));
                solvedTasksText.append("\n" + currentTaskTextView.getText() + currentTaskResultTextView.getText());
                setSpannableTextColor(solvedTasksText, Color.argb(255, 0, 255, 0));
                REMAINING_TIME += 1000;
                correctAnswer.start();

                currentTaskId++;
                nextTask();
            } else {
                //Incorrect answer
                scoreTextView.setText(Integer.toString(Integer.parseInt(scoreTextView.getText().toString()) - 50));
                solvedTasksText.append("\n" + currentTaskTextView.getText() + currentTaskResultTextView.getText());
                setSpannableTextColor(solvedTasksText, Color.argb(255, 255, 0, 0));
                REMAINING_TIME -= 500;
                incorrectAnswer.start();

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
                generateTasks(10);
                currentTaskId = 0;
                nextTask();
            }
        }.start();
    }


    private void startRemainingTime() {
        remainingTimer = new Handler();
        remainingTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                long secondsRemaining = REMAINING_TIME / 1000;
                long milisecondsRemaining = REMAINING_TIME % 1000;
                remainingTimeValue.setText(String.valueOf(secondsRemaining) + "." + String.valueOf(milisecondsRemaining / 100));
                remainingTimer.postDelayed(this, 100);
                REMAINING_TIME -= 100;

                // Check if the countdown is finished
                if (REMAINING_TIME < 0) {
                    remainingTimer.removeCallbacks(this);
                    remainingTimeValue.setText("0");
                    showHighscoreDialog();
                }
            }
        }, 0);
    }

    private static int generateRandomNumber(int minValue, int maxValue) {
        Random random = new Random();
        return random.nextInt((maxValue - minValue) + 1) + minValue;
    }

    private void generateTasks(int count) {
        REMAINING_TIME = 30000 - (100 * currentLevel);
        for (int i = 0; i < count; i++) {
            int firstNumber = generateRandomNumber(1, 10 + (1 * currentLevel));
            int secondNumber = generateRandomNumber(1, 10 + (1 * currentLevel));
            switch (currentLevel) {
                case 1: { // +
                    nextTasks.put(i, new int[]{firstNumber, secondNumber, 0});
                    break;
                }
                case 2: { // -
                    nextTasks.put(i, new int[]{firstNumber, secondNumber, 1});
                    break;
                }
                case 3: { // *
                    if (firstNumber > 10) {
                        firstNumber = generateRandomNumber(0, 10);
                    }
                    if (secondNumber > 10) {
                        secondNumber = generateRandomNumber(0, 10);
                    }
                    nextTasks.put(i, new int[]{firstNumber, secondNumber, 2});
                    break;
                }
                case 4: { // /
                    if (firstNumber > 10) {
                        firstNumber = generateRandomNumber(0, 10);
                    }
                    if (secondNumber > 10) {
                        secondNumber = generateRandomNumber(0, 10);
                    }
                    int tempResult = firstNumber * secondNumber;
                    nextTasks.put(i, new int[]{tempResult, secondNumber, 3});
                    break;
                }
                case 5: { // + -
                    nextTasks.put(i, new int[]{firstNumber, secondNumber, generateRandomNumber(0, 1)});
                    break;
                }
                case 6: { // * /
                    int randomNum = generateRandomNumber(0, 3);
                    if (randomNum >= 2) {
                        if (firstNumber > 10) {
                            firstNumber = generateRandomNumber(0, 10);
                        }
                        if (secondNumber > 10) {
                            secondNumber = generateRandomNumber(0, 10);
                        }
                    }
                    if (randomNum == 3) {
                        int tempResult = firstNumber * secondNumber;
                        nextTasks.put(i, new int[]{tempResult, secondNumber, 3});
                    } else {
                        nextTasks.put(i, new int[]{firstNumber, secondNumber, randomNum});
                    }
                    break;
                }
                default: { // + - * /
                    int randomNum = generateRandomNumber(0, 3);
                    if (randomNum >= 2) {
                        if (firstNumber > 10) {
                            firstNumber = generateRandomNumber(0, 10);
                        }
                        if (secondNumber > 10) {
                            secondNumber = generateRandomNumber(0, 10);
                        }
                    }
                    if (randomNum == 3) {
                        int tempResult = firstNumber * secondNumber;
                        nextTasks.put(i, new int[]{tempResult, secondNumber, 3});
                    } else {
                        nextTasks.put(i, new int[]{firstNumber, secondNumber, randomNum});
                    }
                    break;
                }
            }
            ;
        }
        startRemainingTime();
    }

    private void nextTask() {
        if (!nextTasks.containsKey(currentTaskId)) {
            //End of the level
            currentTaskResultTextView.setText("");
            currentTaskTextView.setText("");
            int bonusPoint = (int) Math.ceil(REMAINING_TIME / 50);
            scoreTextView.setText(Integer.toString(Integer.parseInt(scoreTextView.getText().toString()) + bonusPoint));
            Toast.makeText(this, "Level " + Integer.toString(currentLevel) + "completed \n U made " + bonusPoint + " in bonus points!", Toast.LENGTH_SHORT).show();
            remainingTimer.removeCallbacksAndMessages(null);

            //On pupup screen to continue
            currentLevel++;
            levelCounterTextView.setText(Integer.toString(currentLevel));
            generateTasks(10);
            currentTaskId = 0;
            nextTask();
            solvedTasksText.clear();
        } else {
            int firstNumber = nextTasks.get(currentTaskId)[0];
            int secondNumber = nextTasks.get(currentTaskId)[1];
            int operationId = nextTasks.get(currentTaskId)[2];
            String operationString = "";
            currentTaskResultTextView.setText("");

            switch (operationId) {
                case 0: {
                    operationString = "+";
                    result = firstNumber + secondNumber;
                    break;
                }
                case 1: {
                    operationString = "-";
                    if (secondNumber > firstNumber) {
                        int temp = firstNumber;
                        firstNumber = secondNumber;
                        secondNumber = temp;
                    }
                    result = firstNumber - secondNumber;
                    break;
                }
                case 2: {
                    operationString = "*";
                    if (firstNumber > 10) {
                        firstNumber = generateRandomNumber(0, 10);
                    }
                    if (secondNumber > 10) {
                        secondNumber = generateRandomNumber(0, 10);
                    }
                    nextTasks.put(currentTaskId, new int[]{firstNumber, secondNumber, 2});
                    result = firstNumber * secondNumber;
                    break;
                }
                case 3: {
                    operationString = "/";
                    result = firstNumber / secondNumber;
                    break;
                }
                default: {
                    break;
                }
            }
            currentTaskTextView.setText(firstNumber + " " + operationString + " " + secondNumber + " = ");
            //
            nextTaskTextView.setText("");
            for (int i = currentTaskId + 1; i < nextTasks.size(); i++) {
                switch (nextTasks.get(i)[2]) {
                    case 0: {
                        operationString = "+";
                        nextTaskTextView.append("\n" + nextTasks.get(i)[0] + " " + operationString + " " + nextTasks.get(i)[1] + " = ");
                        break;
                    }
                    case 1: {
                        operationString = "-";
                        int tempFirstNum = nextTasks.get(i)[0];
                        int tempSecondNum = nextTasks.get(i)[1];
                        if (tempFirstNum < tempSecondNum) {
                            int temp = tempFirstNum;
                            tempFirstNum = tempSecondNum;
                            tempSecondNum = temp;
                        }
                        nextTaskTextView.append("\n" + tempFirstNum + " " + operationString + " " + tempSecondNum + " = ");
                        break;
                    }
                    case 2: {
                        operationString = "*";
                        nextTaskTextView.append("\n" + nextTasks.get(i)[0] + " " + operationString + " " + nextTasks.get(i)[1] + " = ");
                        break;
                    }
                    case 3: {
                        operationString = "/";
                        nextTaskTextView.append("\n" + nextTasks.get(i)[0] + " " + operationString + " " + nextTasks.get(i)[1] + " = ");
                        break;
                    }
                    default: {
                        nextTaskTextView.append("\n" + nextTasks.get(i)[0] + " " + operationString + " " + nextTasks.get(i)[1] + " = ");
                        break;
                    }
                }
                //
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

    private void showHighscoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your gained score: " + scoreTextView.getText());
//        builder.setMessage();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        EditText editText = dialogView.findViewById(R.id.editText);

        builder.setView(dialogView);
        builder.setPositiveButton("Submit my score", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, editText.getText().toString());
//                Write to database
                if (editText.getText().toString().length() != 0) {
                    HighScoreDB = FirebaseDatabase.getInstance().getReference("highscores");
                    highscore newScore = new highscore(editText.getText().toString(), Integer.parseInt(scoreTextView.getText().toString()));
                    String userId = HighScoreDB.push().getKey();
                    HighScoreDB.child(userId).setValue(newScore);
                    buttonClickSound.start();
                    finish();
                    overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
                } else {
                    Toast.makeText(Game.this, "U didnt entered your name...", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

        });
        builder.setNegativeButton("Try again...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                resetLeftLayout();
            }
        });

        dialog.show();
    }

    private void checkMusicAndSounds() {
        if (global.sounds) {
            buttonClickSound.setVolume(0.5f, 0.5f);
            correctAnswer.setVolume(0.5f, 0.5f);
            incorrectAnswer.setVolume(0.5f, 0.5f);
            muteSound.setText("Sounds ON");
        } else {
            buttonClickSound.setVolume(0f, 0f);
            correctAnswer.setVolume(0f, 0f);
            incorrectAnswer.setVolume(0f, 0f);
            muteSound.setText("Sounds OFF");
        }

        if (global.music) {
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.start();
            }
            muteMusic.setText("Music ON");
        } else {
            backgroundMusic.pause();
            muteMusic.setText("Music OFF");
        }
    }

    private void resetLeftLayout() {
        currentLevel = 1;
        levelCounterTextView.setText(Integer.toString(currentLevel));
        scoreTextView.setText("0");
        currentTaskTextView.setText("");
        currentTaskResultTextView.setText("");
        solvedTasksText.clear();
        solvedTasks.setText("");
        nextTaskTextView.setText("");
        startCountdown();
    }
}