package com.example.e_almawar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaSchoolFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvGreeting;
    private ImageView ivProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_siswa_school, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvGreeting = view.findViewById(R.id.tv_greeting);
        ivProfile = view.findViewById(R.id.iv_profile);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("nama");
                            String imageUrl = documentSnapshot.getString("profileUrl");

                            if (name != null) {
                                tvGreeting.setText("Halo, " + name + "!");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_name", name);
                                editor.apply();
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
                    .addOnFailureListener(e -> tvGreeting.setText("Terjadi kesalahan, coba lagi nanti."));
        } else if (userName != null) {
            tvGreeting.setText("Halo, " + userName + "!");
        }

        // Navigasi antar ikon
        ImageView iconSejarah = view.findViewById(R.id.icon_sejarah);
        ImageView iconTujuan = view.findViewById(R.id.icon_tujuan);
        ImageView iconVisimisi = view.findViewById(R.id.icon_visimisi);
        ImageView iconSarpras = view.findViewById(R.id.icon_sarpras);
        ImageView iconEkstrakulikuler = view.findViewById(R.id.icon_ekstrakulikuler);

        iconSejarah.setOnClickListener(v -> replaceFragment(new SiswaSejarahFragment()));
        iconTujuan.setOnClickListener(v -> replaceFragment(new SiswaTujuanFragment()));
        iconVisimisi.setOnClickListener(v -> replaceFragment(new SiswaVisimisiFragment()));
        iconSarpras.setOnClickListener(v -> replaceFragment(new SiswaSarprasFragment()));
        iconEkstrakulikuler.setOnClickListener(v -> replaceFragment(new SiswaEkstrakulikulerFragment()));

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
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) // Ganti fragment dengan ID fragment_container
                .addToBackStack(null) // Menambah fragment ke back stack
                .commit(); // Menjalankan transaksi
    }
}
