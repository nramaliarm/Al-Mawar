package com.example.e_almawar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.DynamicLink;
import android.net.Uri;

public class LupaPasswordActivity extends AppCompatActivity {
    private EditText inputEmail;
    private Button btnSubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        // Inisialisasi Firebase Auth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi View
        inputEmail = findViewById(R.id.inputEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Tombol Kembali
        btnBack.setOnClickListener(v -> finish());

        // Tombol Submit untuk mengirimkan link reset password
        btnSubmit.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(LupaPasswordActivity.this, "Masukkan email terlebih dahulu!", Toast.LENGTH_SHORT).show();
            } else {
                // Mengecek apakah email ada di Firestore
                checkEmailInFirestore(email);
            }
        });
    }

    private void checkEmailInFirestore(String email) {
        // Mengecek apakah email ada di Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Jika email ditemukan, lanjutkan untuk mengirimkan link reset password
                        sendResetPasswordEmail(email);
                    } else {
                        // Jika email tidak ditemukan, tampilkan pesan error
                        Toast.makeText(LupaPasswordActivity.this, "Email tidak ditemukan atau tidak terdaftar", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Menangani error jika gagal melakukan query
                    Toast.makeText(LupaPasswordActivity.this, "Gagal memeriksa email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendResetPasswordEmail(String email) {
        // Membuat link dinamis untuk reset password
        FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse("https://yourapp.com/resetpassword?email=" + email))
                .setDomainUriPrefix("https://ealmawar.page.link") // Gantilah dengan domain Anda
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.example.e_almawar").build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Reset Password")
                        .setDescription("Klik untuk mereset password Anda.")
                        .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Mendapatkan link dinamis yang sudah dipersingkat
                        Uri shortLink = task.getResult().getShortLink();
                        // Kirimkan link tersebut ke email pengguna
                        sendEmailWithLink(email, shortLink.toString());
                    } else {
                        // Menangani error
                        Toast.makeText(LupaPasswordActivity.this, "Gagal membuat link: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailWithLink(String email, String link) {
        // Kirimkan link yang telah dipersingkat ke email pengguna (gunakan metode pengiriman email sesuai kebutuhan)
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Jika berhasil, beri tahu pengguna
                        Toast.makeText(LupaPasswordActivity.this, "Instruksi reset password telah dikirim ke " + email, Toast.LENGTH_LONG).show();
                    } else {
                        // Jika gagal, beri tahu pengguna
                        Toast.makeText(LupaPasswordActivity.this, "Terjadi kesalahan: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
