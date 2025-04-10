package com.example.e_almawar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

public class FlashscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_EAlMawar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashscreen);
        test();
        // Timer 3 Detik
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FlashscreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Supaya ga bisa balik ke flashscreen
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Animasi transisi
            }
        }, 6000); // 3 Detik
    }

    private void test() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }
}
