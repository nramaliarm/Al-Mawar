package com.example.e_almawar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Memeriksa status login pengguna
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Jika sudah login, arahkan ke halaman utama siswa
        if (isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, SiswaHomeActivity.class);
            startActivity(intent);
            finish();  // Tutup MainActivity agar tidak bisa kembali ke halaman ini
        }

        // Tombol untuk login sebagai admin
        Button btnAdmin = findViewById(R.id.btnAdmin);
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke halaman login admin
                Intent intent = new Intent(MainActivity.this, LoginAdminActivity.class);
                startActivity(intent);
            }
        });

        // Tombol untuk login sebagai siswa
        Button btnSiswa = findViewById(R.id.btnSiswa);
        btnSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke halaman login siswa
                Intent intent = new Intent(MainActivity.this, LoginSiswaActivity.class);
                startActivity(intent);
            }
        });
    }
}
