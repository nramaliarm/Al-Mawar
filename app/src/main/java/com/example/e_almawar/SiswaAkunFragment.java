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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaAkunFragment extends Fragment {

    private Button btnLogout, btnEditProfile;
    private TextView tvUserName, tvEmail;
    private ImageView ivProfile;
    private static final String TAG = "SiswaAkunFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_akun, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvUserName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
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

        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            tvEmail.setText(userEmail);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", userName);
        editor.putString("user_email", userEmail);
        editor.putString("profile_image_url", profileImageUrl);
        editor.apply();

        loadImage(profileImageUrl);
    }

    private void loadFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", "Tidak Tersedia");
        String userEmail = sharedPreferences.getString("user_email", "Tidak Tersedia");
        String profileImageUrl = sharedPreferences.getString("profile_image_url", "");

        tvUserName.setText(userName);
        tvEmail.setText(userEmail);

        loadImage(profileImageUrl);
    }

    private void loadImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
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
        new AlertDialog.Builder(getContext())
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
