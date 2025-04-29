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

public class SiswaHomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvGreeting;
    private ImageView ivProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvGreeting = view.findViewById(R.id.tv_greeting);
        ivProfile = view.findViewById(R.id.iv_profile);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("nama");
                            String imageUrl = documentSnapshot.getString("profileUrl");

                            if (name != null && !name.isEmpty()) {
                                tvGreeting.setText("Halo, " + name + "!");
                            } else {
                                tvGreeting.setText("Halo, Siswa!");
                            }

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_profile)
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

        // Tombol DAFTAR
        Button daftarButton = view.findViewById(R.id.button_daftar);
        daftarButton.setOnClickListener(v -> {
            Log.d("SiswaHomeFragment", "Tombol Daftar ditekan!");

            SiswaHomeActivity siswaHomeActivity = (SiswaHomeActivity) getActivity();
            if (siswaHomeActivity != null) {
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
            // Menggunakan URL Google Maps yang Anda berikan
            String mapsUrl = "https://maps.app.goo.gl/EVwcGq8uixrfSUQj9";

            try {
                // Intent untuk Google Maps app
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                mapIntent.setPackage("com.google.android.apps.maps");

                // Fallback ke browser jika Maps app tidak terinstall
                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Buka di browser jika Maps app tidak tersedia
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                    startActivity(browserIntent);
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Tidak dapat membuka Google Maps", Toast.LENGTH_SHORT).show();
                // Fallback ke koordinat dasar jika semua gagal
                String alamat = "Jl. Pondok Pesantren No.10, Lakomato, Kec. Kolaka";
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(alamat));
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                startActivity(fallbackIntent);
            }
        });

        return view;
    }
}