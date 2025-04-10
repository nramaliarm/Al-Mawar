package com.example.e_almawar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Inisialisasi Firebase Auth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi elemen UI
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputNama = findViewById(R.id.inputNama);
        EditText inputPassword = findViewById(R.id.inputPassword);
        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView btnLogin = findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String nama = inputNama.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();

                                // Buat data pengguna untuk disimpan ke Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", email);
                                userMap.put("nama", nama);
                                userMap.put("role", "siswa"); // Bisa digunakan untuk cek peran nanti

                                db.collection("users").document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignUpActivity.this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, LoginSiswaActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignUpActivity.this, "Gagal menyimpan ke Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("FirestoreError", e.getMessage());
                                        });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("AuthError", task.getException().getMessage());
                            }
                        });
            }
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginSiswaActivity.class));
        });

        btnBack.setOnClickListener(v -> finish());  // Kembali ke halaman sebelumnya
    }
}
