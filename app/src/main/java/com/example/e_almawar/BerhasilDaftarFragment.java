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

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BerhasilDaftarFragment extends Fragment {

    private TextView tvNamaSiswa;
    private ImageView ivFotoProfil;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_berhasil_daftar, container, false);

        tvNamaSiswa = view.findViewById(R.id.tv_greeting);
        ivFotoProfil = view.findViewById(R.id.iv_profile);
        Button lokasiButton = view.findViewById(R.id.btn_location);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            ambilDataUser(user.getUid()); // gunakan data dari "users"
        } else {
            Log.e("BerhasilDaftarFragment", "User belum login!");
        }

        lokasiButton.setOnClickListener(v -> {
            String mapsUrl = "https://maps.app.goo.gl/EVwcGq8uixrfSUQj9";

            try {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl)));
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Tidak dapat membuka Google Maps", Toast.LENGTH_SHORT).show();
                String alamat = "Jl. Pondok Pesantren No.10, Lakomato, Kec. Kolaka";
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(alamat));
                startActivity(new Intent(Intent.ACTION_VIEW, geoUri));
            }
        });

        return view;
    }

    private void ambilDataUser(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        String imageUrl = documentSnapshot.getString("profileUrl");

                        if (name != null && !name.isEmpty()) {
                            tvNamaSiswa.setText("Halo, " + name + "!");
                        } else {
                            tvNamaSiswa.setText("Halo, Siswa!");
                        }

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .error(R.drawable.ic_profile)
                                    .circleCrop()
                                    .into(ivFotoProfil);
                        } else {
                            ivFotoProfil.setImageResource(R.drawable.ic_profile);
                        }
                    } else {
                        Log.e("BerhasilDaftarFragment", "Dokumen user tidak ditemukan.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("BerhasilDaftarFragment", "Gagal mengambil data user: " + e.getMessage());
                });
    }
}
