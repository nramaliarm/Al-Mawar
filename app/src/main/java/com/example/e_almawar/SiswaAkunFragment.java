package com.example.e_almawar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SiswaAkunFragment extends Fragment {

    Button btnLogout, btnEditProfile;
    TextView tvUserName, tvEmail;
    ImageView ivProfile;
    private static final String TAG = "SiswaAkunFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_siswa_akun, container, false);

        // Link views to their XML counterparts
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvUserName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivProfile = view.findViewById(R.id.ivProfile);

        // Tampilkan gambar default sementara
        ivProfile.setImageResource(R.drawable.default_profile);

        // Get current user from Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            // Fetch user data from Firebase
            mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    // Get user data from database
                    String userName = task.getResult().child("nama").getValue(String.class);
                    String userEmail = task.getResult().child("email").getValue(String.class);
                    String profileImageUrl = task.getResult().child("profileImage").getValue(String.class);

                    // Update UI with user data
                    if (userName != null && !userName.isEmpty()) {
                        tvUserName.setText(userName);
                    }

                    if (userEmail != null && !userEmail.isEmpty()) {
                        tvEmail.setText(userEmail);
                    }

                    // Update SharedPreferences with latest data
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_name", userName);
                    editor.putString("user_email", userEmail);
                    editor.putString("profile_image_url", profileImageUrl); // Simpan URL gambar di SharedPreferences
                    editor.apply();

                    // Handle profile image
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Log.d(TAG, "Memuat foto profil dari: " + profileImageUrl);
                        // Load profile image using Glide dengan penanganan error
                        Glide.with(getContext())
                                .load(profileImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE) // Hindari cache
                                .skipMemoryCache(true) // Skip memory cache
                                .circleCrop()
                                .placeholder(R.drawable.default_profile) // Gambar default saat loading
                                .error(R.drawable.default_profile) // Gambar default jika error
                                .into(ivProfile);
                    } else {
                        // Jika tidak ada gambar, tampilkan gambar default
                        ivProfile.setImageResource(R.drawable.default_profile);
                    }
                } else {
                    // Fallback to SharedPreferences if Firebase data not available
                    loadFromSharedPreferences();
                }
            }).addOnFailureListener(e -> {
                // If Firebase fails, use SharedPreferences data
                Log.e(TAG, "Error mengambil data dari Firebase: " + e.getMessage());
                loadFromSharedPreferences();
            });
        } else {
            // If user not authenticated, use SharedPreferences data
            loadFromSharedPreferences();
        }

        // Handle Logout button click
        btnLogout.setOnClickListener(v -> {
            // Show confirmation dialog for logging out
            new AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Apakah Anda yakin ingin keluar?")
                    .setCancelable(false)
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Clear the user data stored in SharedPreferences
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Log out from Firebase
                        FirebaseAuth.getInstance().signOut();

                        // Redirect to Login page after logout
                        Intent intent = new Intent(getActivity(), LoginSiswaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        });

        // Handle Edit Profile button click
        btnEditProfile.setOnClickListener(v -> {
            // Navigate to EditProfileSiswaFragment
            EditProfileSiswaFragment editProfileFragment = new EditProfileSiswaFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Helper method to load data from SharedPreferences
    private void loadFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Tidak Tersedia");
        String userEmail = sharedPreferences.getString("user_email", "Tidak Tersedia");
        String profileImageUrl = sharedPreferences.getString("profile_image_url", "");

        tvUserName.setText(userName);
        tvEmail.setText(userEmail);

        // Coba muat gambar dari SharedPreferences jika tersedia
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Log.d(TAG, "Memuat foto profil dari SharedPreferences: " + profileImageUrl);
            Glide.with(getContext())
                    .load(profileImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .circleCrop()
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(ivProfile);
        } else {
            // Jika tidak ada gambar, tampilkan gambar default
            ivProfile.setImageResource(R.drawable.default_profile);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data ketika fragment menjadi aktif kembali
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String profileImageUrl = task.getResult().child("profileImage").getValue(String.class);

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Log.d(TAG, "onResume: Memperbarui foto profil");
                        Glide.with(getContext())
                                .load(profileImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .circleCrop()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .into(ivProfile);
                    }
                }
            });
        }
    }
}