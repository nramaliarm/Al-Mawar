package com.example.e_almawar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.SetOptions;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;  // Unique request code for Google Sign-In

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
        Button btnSignUpGoogle = findViewById(R.id.btnSignUpGoogle);  // Tombol Sign-Up dengan Google
        TextView btnLogin = findViewById(R.id.btnLogin);

        // Inisialisasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Login dengan Email dan Password
        btnSignUp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String nama = inputNama.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();

                                // Update profil pengguna dengan nama
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nama)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            // Buat data pengguna untuk disimpan ke Firestore
                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("email", email);
                                            userMap.put("nama", nama);
                                            userMap.put("role", "siswa");

                                            // Menyimpan data pengguna ke Firestore
                                            db.collection("users").document(uid)
                                                    .set(userMap, SetOptions.merge())
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d("Firestore", "Data pengguna berhasil disimpan.");
                                                        startActivity(new Intent(SignUpActivity.this, SiswaHomeActivity.class));
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e("Firestore", "Gagal menyimpan data pengguna: " + e.getMessage());
                                                        Toast.makeText(SignUpActivity.this, "Pendaftaran gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        });
                            } else {
                                Log.e("SignUpActivity", "Firebase authentication failed.");
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Sign Up with Google
        btnSignUpGoogle.setOnClickListener(v -> {
            // Memastikan sign-out dulu sebelum memulai Google Sign-In
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> {
                        // Memulai proses Google Sign-In
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    });
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class)); // Navigasi ke login
        });

        btnBack.setOnClickListener(v -> finish());  // Kembali ke halaman sebelumnya
    }

    // Menangani hasil dari sign-in intent (Google Sign-In)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            GoogleSignInAccount account = task.getResult();
                            firebaseAuthWithGoogle(account.getIdToken());  // Menggunakan idToken untuk autentikasi Firebase
                        } else {
                            Toast.makeText(SignUpActivity.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        // Buat data pengguna untuk disimpan ke Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", user.getEmail());
                        userMap.put("nama", user.getDisplayName());
                        userMap.put("role", "siswa");

                        // Simpan URL foto profil langsung ke Firestore
                        String profileUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                        userMap.put("profileUrl", profileUrl);  // Menyimpan URL foto profil

                        // Simpan data pengguna ke Firestore
                        db.collection("users").document(uid)
                                .set(userMap, SetOptions.merge()) // Menambahkan atau memperbarui data pengguna di Firestore
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignUpActivity.this, "Pendaftaran Google Berhasil!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, SiswaHomeActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUpActivity.this, "Gagal menyimpan ke Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("FirestoreError", e.getMessage());
                                });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}