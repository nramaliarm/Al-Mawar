package com.example.e_almawar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Ensure that this ID is correct
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize UI elements
        EditText inputEmail = findViewById(R.id.inputEmail);
        EditText inputPassword = findViewById(R.id.inputPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnLoginGoogle = findViewById(R.id.btnLoginGoogle);  // Google Sign-In button
        TextView btnDaftar = findViewById(R.id.btnDaftar);
        TextView txtLupaPassword = findViewById(R.id.txtLupaPassword);

        // Check login status from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(MainActivity.this, SiswaHomeActivity.class));
            finish();
            return;
        }

        // Email and Password Login
        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Harap isi email dan password!", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoadingFragment();  // Show loading fragment while logging in
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        hideLoadingFragment();  // Hide loading fragment after completion
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
                                                // Save user data to SharedPreferences
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isLoggedIn", true);
                                                editor.putString("user_name", nama);
                                                editor.putString("user_email", emailUser);
                                                editor.putString("user_password", password); // Save password
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

        // Google Sign-In
        btnLoginGoogle.setOnClickListener(v -> {
            showLoadingFragment();  // Show loading fragment while signing in with Google
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Sign Up button action
        btnDaftar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class)); // Navigate to sign-up
        });

        // Forgot password button action
        txtLupaPassword.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LupaPasswordActivity.class)); // Navigate to forgot password
        });
    }

    // Handle the result of Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(task -> {
                        hideLoadingFragment(); // Hide loading fragment after sign-in attempt
                        if (task.isSuccessful()) {
                            GoogleSignInAccount account = task.getResult();
                            firebaseAuthWithGoogle(account.getIdToken());  // Authenticate with Firebase using the Google ID token
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Log.e("GoogleSignIn", "Google Sign-In failed: " + errorMessage);
                            Toast.makeText(MainActivity.this, "Google Sign-In failed: " + errorMessage, Toast.LENGTH_SHORT).show();
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

                        // Save user data to Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", user.getEmail());
                        userMap.put("nama", user.getDisplayName());
                        userMap.put("role", "siswa");

                        String profileUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                        userMap.put("profileUrl", profileUrl);

                        db.collection("users").document(uid)
                                .set(userMap, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Data pengguna berhasil disimpan.");
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putString("user_name", user.getDisplayName());
                                    editor.putString("user_email", user.getEmail());
                                    editor.apply();

                                    startActivity(new Intent(MainActivity.this, SiswaHomeActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this, "Gagal menyimpan ke Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("FirestoreError", e.getMessage());
                                });
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e("FirebaseAuth", "Authentication failed: " + errorMessage);
                        Toast.makeText(MainActivity.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Show loading fragment while signing in
    private void showLoadingFragment() {
        // Implement your loading fragment logic here
    }

    private void hideLoadingFragment() {
        // Implement your loading fragment hide logic here
    }
}
