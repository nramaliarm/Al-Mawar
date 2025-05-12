package com.example.e_almawar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            );
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white, getTheme()));
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white, getTheme()));
        }


        // Cek login status dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(MainActivity.this, SiswaHomeActivity.class));
            finish();
            return;
        }

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi UI
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnDaftar = findViewById(R.id.btnDaftar);
        TextView txtLupaPassword = findViewById(R.id.txtLupaPassword);

        // Login dengan Email dan Password
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Harap isi email dan password!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String nama = documentSnapshot.getString("nama");
                                            String emailUser = documentSnapshot.getString("email");
                                            String role = documentSnapshot.getString("role");

                                            if (role != null && role.equals("siswa")) {
                                                // Save the email and password in SharedPreferences
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isLoggedIn", true);
                                                editor.putString("user_name", nama);
                                                editor.putString("user_email", emailUser);
                                                editor.putString("user_password", password); // Save the password
                                                editor.apply();

                                                startActivity(new Intent(MainActivity.this, SiswaHomeActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Akun ini bukan untuk siswa!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Email not registered in Firestore
                                            Toast.makeText(MainActivity.this, "Email Belum Terdaftar", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "Gagal mengambil data pengguna: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Handle different error codes
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Incorrect password
                                    Toast.makeText(MainActivity.this, "Email atau Password Salah", Toast.LENGTH_SHORT).show();
                                } else if (e instanceof FirebaseAuthInvalidUserException) {
                                    // Email not registered
                                    Toast.makeText(MainActivity.this, "Email Belum Terdaftar", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Other errors
                                    Toast.makeText(MainActivity.this, "Login Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

        });

        btnDaftar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class)); // Navigasi ke sign-up
        });

        txtLupaPassword.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LupaPasswordActivity.class)); // Navigasi ke lupa password
        });
    }
}
