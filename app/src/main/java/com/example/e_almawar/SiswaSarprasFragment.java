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
import com.example.e_almawar.viewmodel.Facility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SiswaSarprasFragment extends Fragment {

    private RecyclerView recyclerView;
    private FacilityAdapter adapter;
    private List<Facility> facilityList;
    private FirebaseFirestore db;

    private TextView tvGreeting;
    private ImageView ivProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_sarpras, container, false);

        recyclerView = view.findViewById(R.id.recyclerFacilities);
        int spanCount = calculateSpanCount();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));


        tvGreeting = view.findViewById(R.id.tv_greeting);
        ivProfile = view.findViewById(R.id.iv_profile);

        facilityList = new ArrayList<>();
        adapter = new FacilityAdapter(getContext(), facilityList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadUserData(); // Ambil nama dan foto profil
        loadFacilities(); // Ambil data sarana prasarana

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
                        Log.e("SiswaSarprasFragment", "Gagal memuat data pengguna: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadFacilities() {
        db.collection("sarana_prasarana")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilityList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Facility facility = doc.toObject(Facility.class);
                        facilityList.add(facility);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("SiswaSarprasFragment", "Gagal mengambil data sarpras: " + e.getMessage());
                    Toast.makeText(getContext(), "Gagal memuat data sarana prasarana", Toast.LENGTH_SHORT).show();
                });
    }

    private int calculateSpanCount() {
        float screenWidthDp = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density;

        if (screenWidthDp >= 720) {
            return 4; // Tablet atau layar lebar
        } else if (screenWidthDp >= 600) {
            return 3; // Layar sedang
        } else {
            return 2; // Layar kecil (smartphone)
        }
    }

}
