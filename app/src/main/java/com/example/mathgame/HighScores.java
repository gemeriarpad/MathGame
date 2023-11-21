package com.example.mathgame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private DatabaseReference databaseReference;
    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.btnBack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Replace "your_data_node" with the actual path to your data
        databaseReference = FirebaseDatabase.getInstance().getReference("highscores");

        // Initialize your adapter with an empty list (you can update it later)
        adapter = new DataAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        fetchDataFromFirebase();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void fetchDataFromFirebase() {
        databaseReference.orderByChild("score").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<highscore> dataList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    highscore data = snapshot.getValue(highscore.class);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                Collections.reverse(dataList);
                // Update the adapter with the new data
                adapter = new DataAdapter(dataList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data", databaseError.toException());
            }
        });
    }
}