package com.example.e_almawar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SiswaHomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvGreeting, visiTextView;
    private ImageView ivProfile; // Tambahkan ini

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        visiTextView = view.findViewById(R.id.visi); // Inisialisasi TextView untuk Visi
        tvGreeting = view.findViewById(R.id.tv_greeting);
        ivProfile = view.findViewById(R.id.iv_profile); // Inisialisasi ImageView

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("nama");
                            String imageUrl = documentSnapshot.getString("profileUrl"); // Ambil URL gambar

                            if (name != null && !name.isEmpty()) {
                                tvGreeting.setText("Halo, " + name + "!");
                            } else {
                                tvGreeting.setText("Halo, Siswa!");
                            }

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_profile) // default/fallback image
                                        .error(R.drawable.ic_profile)
                                        .circleCrop()
                                        .into(ivProfile);
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

        // Tombol Lokasi
        Button lokasiButton = view.findViewById(R.id.btn_location);
        lokasiButton.setOnClickListener(v -> {
            String mapsUrl = "https://maps.app.goo.gl/EVwcGq8uixrfSUQj9";

            try {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                    startActivity(browserIntent);
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Tidak dapat membuka Google Maps", Toast.LENGTH_SHORT).show();
                String alamat = "Jl. Pondok Pesantren No.10, Lakomato, Kec. Kolaka";
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(alamat));
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                startActivity(fallbackIntent);
            }
        });

        // Ambil data visi dari Firestore (hapus bagian misi)
        db.collection("visi_misi").document("visi_misiID")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String visi = documentSnapshot.getString("visi");

                        if (visi != null) {
                            visiTextView.setText(visi);
                        } else {
                            visiTextView.setText("Belum ada data visi.");
                        }
                    } else {
                        Toast.makeText(getContext(), "Data visi tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", e.getMessage());
                    Toast.makeText(getContext(), "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        return view;
    }
}
