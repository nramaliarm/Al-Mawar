package com.example.e_almawar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaAkunFragment extends Fragment {

    private Button btnLogout, btnEditProfile, btnTogglePassword;
    private TextView tvUserName, tvEmail, tvPassword;
    private ImageView ivProfile;
    private String userPassword = "";
    private boolean isPasswordVisible = false;

    private static final String TAG = "SiswaAkunFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_akun, container, false);

        // onCreateView
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnTogglePassword = view.findViewById(R.id.btnTogglePassword);
        tvUserName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPassword = view.findViewById(R.id.tvPassword);
        ivProfile = view.findViewById(R.id.ivProfile);

        ivProfile.setImageResource(R.drawable.default_profile);

        loadUserData();

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        btnEditProfile.setOnClickListener(v -> {
            EditProfileSiswaFragment editProfileFragment = new EditProfileSiswaFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnTogglePassword.setOnClickListener(v -> {
            Log.d(TAG, "Tombol toggle password diklik");
            togglePasswordVisibility();
        });


        return view;
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            updateUIFromFirestore(documentSnapshot);
                        } else {
                            loadFromSharedPreferences();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error mengambil data dari Firestore: " + e.getMessage());
                        loadFromSharedPreferences();
                    });
        } else {
            loadFromSharedPreferences();
        }
    }

    private void updateUIFromFirestore(DocumentSnapshot documentSnapshot) {
        String userName = documentSnapshot.getString("nama");
        String userEmail = documentSnapshot.getString("email");
        String profileImageUrl = documentSnapshot.getString("profileUrl");

        // AMBIL PASSWORD DARI SHARED PREFERENCES
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        userPassword = sharedPreferences.getString("user_password", "");

        if (!TextUtils.isEmpty(userEmail)) {
            tvEmail.setText(userEmail);
        }

        if (!TextUtils.isEmpty(userName)) {
            tvUserName.setText(userName);
        }

        saveToSharedPreferences(userName, userEmail, userPassword, profileImageUrl);

        setHiddenPassword();
        loadImage(profileImageUrl);
    }


    private void loadFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Tidak Tersedia");
        String userEmail = sharedPreferences.getString("user_email", "Tidak Tersedia");
        String profileImageUrl = sharedPreferences.getString("profile_image_url", "");
        userPassword = sharedPreferences.getString("user_password", "");

        tvUserName.setText(userName);
        tvEmail.setText(userEmail);

        setHiddenPassword();
        loadImage(profileImageUrl);
    }

    private void saveToSharedPreferences(String name, String email, String password, String imageUrl) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.putString("profile_image_url", imageUrl);
        editor.apply();
    }

    private void setHiddenPassword() {
        if (!TextUtils.isEmpty(userPassword)) {
            StringBuilder hidden = new StringBuilder();
            for (int i = 0; i < userPassword.length(); i++) {
                hidden.append("*");
            }
            tvPassword.setText(hidden.toString());
            isPasswordVisible = false;
            btnTogglePassword.setBackgroundResource(R.drawable.ic_visible);
        }
    }

    private void togglePasswordVisibility() {
        if (TextUtils.isEmpty(userPassword)) {
            Log.w(TAG, "Password kosong atau belum dimuat.");
            return;
        }

        if (isPasswordVisible) {
            setHiddenPassword();
        } else {
            tvPassword.setText(userPassword);
            btnTogglePassword.setBackgroundResource(R.drawable.ic_invisible);
            isPasswordVisible = true;
        }
    }

    private void loadImage(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Log.d(TAG, "Memuat foto profil dari: " + imageUrl);
            Glide.with(getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.default_profile);
        }
    }

    private void showLogoutDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();

                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("Tidak", null)
                .show();

        // Atur latar belakang agar pakai bg_dialog_rounded.xml
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(getContext(), R.drawable.bg_dialog_rounded)
            );
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String profileImageUrl = documentSnapshot.getString("profileUrl");
                            loadImage(profileImageUrl);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Gagal memuat profil di onResume: " + e.getMessage()));
        }
    }
}
