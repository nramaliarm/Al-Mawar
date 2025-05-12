package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SiswaTujuanFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView textView;
    private TextView tvNamaSiswa;
    private ImageView ivProfile;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_tujuan, container, false);

        textView = view.findViewById(R.id.list_poin);
        ivProfile = view.findViewById(R.id.iv_profile);
        tvNamaSiswa = view.findViewById(R.id.tv_greeting);

        mAuth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth
        db = FirebaseFirestore.getInstance();

        loadSchoolGoals();
        loadUserData();  // Added method to load user data

        return view;
    }

    private void loadSchoolGoals() {
        db.collection("TujuanSekolah").document("TujuanID")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (!documentSnapshot.exists()) {
                            textView.setText(getString(R.string.document_not_found));
                            return;
                        }

                        Object rawTujuan = documentSnapshot.get("tujuan_sekolah");
                        List<String> goalsList = new ArrayList<>();

                        if (rawTujuan instanceof Map) {
                            Map<String, Object> tujuanMap = (Map<String, Object>) rawTujuan;
                            for (int i = 0; i < tujuanMap.size(); i++) {
                                Object value = tujuanMap.get(String.valueOf(i));
                                if (value != null) {
                                    goalsList.add(value.toString());
                                }
                            }
                        } else if (rawTujuan instanceof List) {
                            goalsList = (List<String>) rawTujuan;
                        } else {
                            textView.setText(getString(R.string.invalid_data_format));
                            return;
                        }

                        displayNumberedGoals(removeDuplicateGoals(goalsList));
                    } catch (Exception e) {
                        textView.setText(getString(R.string.processing_error));
                    }
                })
                .addOnFailureListener(e -> {
                    textView.setText(getString(R.string.data_load_failed));
                });
    }

    private List<String> removeDuplicateGoals(List<String> originalList) {
        List<String> cleanedList = new ArrayList<>();
        originalList.forEach(goal -> {
            if (!cleanedList.contains(goal.trim())) {
                cleanedList.add(goal.trim());
            }
        });
        return cleanedList;
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("nama");
                            String imageUrl = documentSnapshot.getString("profileUrl");

                            if (name != null && !name.isEmpty()) {
                                tvNamaSiswa.setText(getString(R.string.greeting_message, name));
                            }

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.drawable.ic_profile)
                                        .circleCrop()
                                        .into(ivProfile);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserData", "Error loading user data: " + e.getMessage());
                    });
        }
    }

    private void displayNumberedGoals(List<String> goalsList) {
        StringBuilder builder = new StringBuilder();

        // Menambahkan setiap tujuan tanpa nomor
        for (String goal : goalsList) {
            builder.append(goal);  // Tidak perlu penomoran
            builder.append("\n\n");  // Tambahkan jarak antar item
        }

        textView.setText(builder.toString());
    }


}
