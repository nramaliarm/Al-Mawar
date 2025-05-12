package com.example.e_almawar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_almawar.viewmodel.Guru;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataGuruFragment extends Fragment {

    private RecyclerView recyclerView;
    private GuruAdapter adapter;
    private List<Guru> guruList;
    private FirebaseFirestore db;

    private TextView tvGreeting;
    private ImageView ivProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_guru, container, false);

        recyclerView = view.findViewById(R.id.recyclerGuru);
        int spanCount = calculateSpanCount();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        tvGreeting = view.findViewById(R.id.tv_greeting);
        ivProfile = view.findViewById(R.id.iv_profile);

        guruList = new ArrayList<>();
        adapter = new GuruAdapter(getContext(), guruList);
        recyclerView.setAdapter(adapter);

        // ✅ Set click listener untuk item guru
        adapter.setOnItemClickListener(guru -> {
            if (guru != null) {
                GuruDetailDialog.show(requireContext(), guru);
            } else {
                Log.e("DataGuruFragment", "Guru data is null");
            }
        });

        db = FirebaseFirestore.getInstance();

        loadUserData();
        loadPrograms();

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("nama");
                            String profileUrl = documentSnapshot.getString("profileUrl");

                            if (name != null) {
                                tvGreeting.setText("Halo, " + name + "!");
                            }

                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(profileUrl)
                                        .placeholder(R.drawable.ic_profile)
                                        .error(R.drawable.ic_profile)
                                        .circleCrop()
                                        .into(ivProfile);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DataGuruFragment", "Gagal memuat data pengguna: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadPrograms() {
        db.collection("guru")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        guruList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Guru guru = doc.toObject(Guru.class);
                            if (guru != null) {
                                guruList.add(guru);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("DataGuruFragment", "Document snapshots are null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DataGuruFragment", "Gagal mengambil data guru: " + e.getMessage());
                    Toast.makeText(getContext(), "Gagal memuat data guru", Toast.LENGTH_SHORT).show();
                });
    }

    private int calculateSpanCount() {
        float screenWidthDp = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density;

        if (screenWidthDp >= 720) {
            return 4;
        } else if (screenWidthDp >= 600) {
            return 3;
        } else {
            return 2;
        }
    }
}
