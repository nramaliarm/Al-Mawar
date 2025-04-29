package com.example.e_almawar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;  // Unique request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

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
        ImageView btnBack = findViewById(R.id.btnBack);
        TextView btnDaftar = findViewById(R.id.btnDaftar);
        TextView txtLupaPassword = findViewById(R.id.txtLupaPassword);
        Button btnLoginGoogle = findViewById(R.id.btnLoginGoogle);  // Tombol untuk login dengan Google

        // Inisialisasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Gantilah dengan ID klien Firebase-mu
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isLoggedIn", true);
                                                editor.putString("user_name", nama);
                                                editor.putString("user_email", emailUser);
                                                editor.apply();

                                                startActivity(new Intent(MainActivity.this, SiswaHomeActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Akun ini bukan untuk siswa!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Data pengguna tidak ditemukan di Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "Gagal mengambil data pengguna: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(MainActivity.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Login dengan Google
        btnLoginGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN); // Memulai Google Sign-In dan akan menampilkan pop-up untuk memilih akun
        });

        btnDaftar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class)); // Navigasi ke sign-up
        });

        txtLupaPassword.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LupaPasswordActivity.class)); // Navigasi ke lupa password
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
                            Toast.makeText(MainActivity.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
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

                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String nama = documentSnapshot.getString("nama");
                                        String emailUser = documentSnapshot.getString("email");
                                        String role = documentSnapshot.getString("role");

                                        if (role != null && role.equals("siswa")) {
                                            SharedPreferences.Editor editor = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit();
                                            editor.putBoolean("isLoggedIn", true);
                                            editor.putString("user_name", nama);
                                            editor.putString("user_email", emailUser);
                                            editor.apply();

                                            startActivity(new Intent(MainActivity.this, SiswaHomeActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Akun ini bukan untuk siswa!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Data pengguna tidak ditemukan di Firestore", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
