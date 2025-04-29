package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaSejarahFragment extends Fragment {

    private TextView textViewSejarah;
    private TextView tvNamaSiswa;
    private ImageView ivProfile;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_siswa_sejarah, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi view
        textViewSejarah = view.findViewById(R.id.textView5);
        tvNamaSiswa = view.findViewById(R.id.tv_greeting); // Pastikan ID ini ada di XML
        ivProfile = view.findViewById(R.id.iv_profile);       // Pastikan ID ini ada di XML

        // Inisialisasi Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Ambil data sejarah dari Firestore
        db.collection("sejarah").document("F3KMGYAUe263MP7Uir2Z")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String sejarah = documentSnapshot.getString("deskripsi");
                        textViewSejarah.setText(sejarah);
                    } else {
                        textViewSejarah.setText("Data tidak ditemukan.");
                    }
                })
                .addOnFailureListener(e -> textViewSejarah.setText("Gagal memuat data: " + e.getMessage()));

        // Ambil data pengguna dari Firestore
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
                    });
        }
    }
}