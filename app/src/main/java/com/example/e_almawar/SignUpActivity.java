package com.example.e_almawar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 9001;

    // Firebase & Google Sign-In Components
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    // UI Components
    private EditText inputEmail, inputNama, inputPassword;
    private Button btnSignUp, btnSignUpGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase components
        initFirebaseComponents();

        // Initialize UI components
        initUIComponents();

        // Setup click listeners
        setupClickListeners();
    }

    private void initFirebaseComponents() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initUIComponents() {
        inputEmail = findViewById(R.id.inputEmail);
        inputNama = findViewById(R.id.inputNama);
        inputPassword = findViewById(R.id.inputPassword);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUpGoogle = findViewById(R.id.btnSignUpGoogle);
        TextView btnLogin = findViewById(R.id.btnLogin);

        // Back button listener
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        // Email Sign Up
        btnSignUp.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String nama = inputNama.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            // Validate input
            if (validateInput(email, nama, password)) {
                showLoadingFragment();
                performEmailSignUp(email, nama, password);
            }
        });

        // Google Sign Up
        btnSignUpGoogle.setOnClickListener(v -> {
            showLoadingFragment();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Login Navigation
        findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(SignUpActivity.this, MainActivity.class))
        );
    }

    private boolean validateInput(String email, String nama, String password) {
        boolean isValid = true;

        // Email validation
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email tidak boleh kosong");
            inputEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Format email tidak valid");
            inputEmail.requestFocus();
            isValid = false;
        }

        // Name validation
        if (TextUtils.isEmpty(nama)) {
            inputNama.setError("Nama tidak boleh kosong");
            inputNama.requestFocus();
            isValid = false;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password tidak boleh kosong");
            inputPassword.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            inputPassword.setError("Password minimal 6 karakter");
            inputPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void performEmailSignUp(String email, String nama, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideLoadingFragment();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUserProfile(user, nama, email);
                        }
                    } else {
                        handleSignUpError(task.getException());
                    }
                });
    }

    private void updateUserProfile(FirebaseUser user, String nama, String email) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nama)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(profileTask -> {
                    if (profileTask.isSuccessful()) {
                        saveUserToFirestore(user.getUid(), email, nama);
                    } else {
                        Log.e(TAG, "Profile update failed", profileTask.getException());
                        Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String uid, String email, String nama) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("nama", nama);
        userMap.put("role", "siswa");
        userMap.put("registrationTimestamp", System.currentTimeMillis());

        db.collection("users").document(uid)
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    saveUserToSharedPreferences(nama, email);
                    navigateToHomeScreen();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore save failed", e);
                    Toast.makeText(this, "Gagal menyimpan data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void saveUserToSharedPreferences(String nama, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", nama);
        editor.putString("user_email", email);
        editor.putLong("last_login", System.currentTimeMillis());
        editor.apply();
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(SignUpActivity.this, SiswaHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleSignUpError(Exception exception) {
        if (exception != null) {
            String errorMessage = exception.getMessage();
            Log.e(TAG, "SignUp Error", exception);

            if (exception instanceof FirebaseAuthWeakPasswordException) {
                inputPassword.setError("Password terlalu lemah");
                inputPassword.requestFocus();
                Toast.makeText(this, "Password terlalu lemah. Gunakan password yang lebih kuat.",
                        Toast.LENGTH_LONG).show();
            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                inputEmail.setError("Email tidak valid");
                inputEmail.requestFocus();
                Toast.makeText(this, "Email tidak valid. Silakan periksa kembali.",
                        Toast.LENGTH_LONG).show();
            } else if (exception instanceof FirebaseAuthUserCollisionException) {
                inputEmail.setError("Email sudah terdaftar");
                inputEmail.requestFocus();
                Toast.makeText(this, "Email sudah terdaftar. Gunakan email lain.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Pendaftaran gagal: " + errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
                hideLoadingFragment();
                Toast.makeText(this, "Google Sign-In Gagal: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    hideLoadingFragment();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveGoogleUserToFirestore(user);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SignUpActivity.this,
                                "Authentication Failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveGoogleUserToFirestore(FirebaseUser user) {
        String uid = user.getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("nama", user.getDisplayName());
        userMap.put("role", "siswa");
        userMap.put("registrationTimestamp", System.currentTimeMillis());

        // Simpan URL foto profil jika tersedia
        if (user.getPhotoUrl() != null) {
            userMap.put("profileUrl", user.getPhotoUrl().toString());
        }

        db.collection("users").document(uid)
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    saveUserToSharedPreferences(
                            user.getDisplayName(),
                            user.getEmail()
                    );
                    navigateToHomeScreen();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore save failed", e);
                    Toast.makeText(this,
                            "Gagal menyimpan data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void showLoadingFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new LoadingFragment(), "LOADING_FRAGMENT");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void hideLoadingFragment() {
        Fragment loadingFragment = getSupportFragmentManager().findFragmentByTag("LOADING_FRAGMENT");
        if (loadingFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(loadingFragment);
            transaction.commit();
        }
    }
}
