package com.example.e_almawar;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
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
                            textView.setText("Dokumen tidak ditemukan.");
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
                            textView.setText("Format data tidak valid.");
                            return;
                        }

                        displayNumberedGoals(removeDuplicateGoals(goalsList));
                    } catch (Exception e) {
                        textView.setText("Gagal memproses data.");
                        Log.e("TujuanSekolah", "Error parsing data: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    textView.setText("Gagal mengambil data.");
                    Log.e("TujuanSekolah", "Load failed: " + e.getMessage());
                });
    }

    private List<String> removeDuplicateGoals(List<String> originalList) {
        List<String> cleanedList = new ArrayList<>();
        for (String goal : originalList) {
            if (!cleanedList.contains(goal.trim())) {
                cleanedList.add(goal.trim());
            }
        }
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
                                tvNamaSiswa.setText("Halo, " + name + "!");
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
        String indent = "    "; // 4 spaces for alignment

        for (int i = 0; i < goalsList.size(); i++) {
            String goal = goalsList.get(i);
            // Format with proper numbering and indentation
            builder.append(String.format("%d.%s\n\n", i + 1, goal));
        }

        textView.setText(builder.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }
}
