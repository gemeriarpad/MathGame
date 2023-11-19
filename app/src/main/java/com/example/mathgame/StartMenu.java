package com.example.mathgame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;

public class StartMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Függvény az első gombra kattintás kezeléséhez
    public void onButton1Click(View view) {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

    // Függvény a második gombra kattintás kezeléséhez
    public void onButton2Click(View view) {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    public void onButton3Click(View view) {
        // Felugró ablak létrehozása a highscore megjelenítéséhez
        showHighscoreDialog();
    }

    // Függvény a negyedik gombra kattintás kezeléséhez
    public void onButton4Click(View view) {

        Log.d("Button4Click", "Button 4 clicked");

        // Az URL, amit megnyitunk a böngészőben
        String urlToOpen = "https://www.google.com";

        // Intent létrehozása a böngésző megnyitásához
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen));

        // Ellenőrzés, hogy van-e olyan alkalmazás, ami meg tudja nyitni az URL-t
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showHighscoreDialog() {
        // Egy AlertDialog létrehozása
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Highscore");
        builder.setMessage("A jelenlegi highscore: 1000"); // Itt a valós adatot helyettesítsd be

        // Pozitív gomb hozzáadása (opcionális)
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Ebben a példában csak bezárjuk az ablakot
                dialog.dismiss();
            }
        });

        // AlertDialog megjelenítése
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
