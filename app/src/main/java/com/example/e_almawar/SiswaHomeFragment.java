package com.example.e_almawar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaHomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvGreeting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_home, container, false);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi TextView
        tvGreeting = view.findViewById(R.id.tv_greeting);

        // Cek user yang sedang login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Ambil nama dari Firestore
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("nama");
                            if (name != null && !name.isEmpty()) {
                                tvGreeting.setText("Halo, " + name + "!");
                            } else {
                                tvGreeting.setText("Halo, Siswa!");
                            }
                        } else {
                            Log.e("SiswaHomeFragment", "Dokumen tidak ditemukan di Firestore.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SiswaHomeFragment", "Gagal mengambil data Firestore: " + e.getMessage());
                    });
        } else {
            Log.e("SiswaHomeFragment", "User tidak ditemukan!");
        }

        // Tombol Daftar
        Button daftarButton = view.findViewById(R.id.button_daftar);
        daftarButton.setOnClickListener(v -> {
            Log.d("SiswaHomeFragment", "Tombol Daftar ditekan!");

            SiswaHomeActivity siswaHomeActivity = (SiswaHomeActivity) getActivity();
            if (siswaHomeActivity != null) {
                Log.d("SiswaHomeFragment", "Activity ditemukan, mengganti fragment...");
                siswaHomeActivity.replaceFragment(new SiswaFormulirFragment());

                BottomNavigationView bottomNavigationView = siswaHomeActivity.findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.formulir);
            } else {
                Log.e("SiswaHomeFragment", "Gagal mendapatkan referensi Activity!");
            }
        });

        return view;
    }
}
