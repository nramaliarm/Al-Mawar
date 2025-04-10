package com.example.e_almawar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SiswaSchoolFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvGreeting;  // TextView untuk menampilkan nama pengguna

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout fragment_siswa_school.xml
        return inflater.inflate(R.layout.fragment_siswa_school, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ambil reference TextView untuk menyapa pengguna
        tvGreeting = view.findViewById(R.id.tv_greeting); // Pastikan ID ini sesuai dengan layout

        // Cek apakah nama pengguna sudah ada di SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);

        if (userName == null) {
            // Jika belum ada, ambil data dari Firebase dan simpan di SharedPreferences
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                // Ambil data pengguna dari Firebase Realtime Database
                mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Mendapatkan nama pengguna dari Firebase
                            String name = dataSnapshot.child("nama").getValue(String.class);
                            if (name != null) {
                                // Menampilkan nama pengguna di TextView greeting
                                tvGreeting.setText("Halo, " + name + "!");

                                // Simpan nama pengguna di SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user_name", name);
                                editor.apply();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Menangani error jika ada
                        tvGreeting.setText("Terjadi kesalahan, coba lagi nanti.");
                    }
                });
            }
        } else {
            // Jika nama pengguna sudah ada di SharedPreferences, langsung tampilkan
            tvGreeting.setText("Halo, " + userName + "!");
        }

        // Mengatur klik untuk ikon-ikon lainnya (misalnya Sejarah, Tujuan, dll.)
        ImageView iconSejarah = view.findViewById(R.id.icon_sejarah);
        ImageView iconTujuan = view.findViewById(R.id.icon_tujuan);
        ImageView iconVisimisi = view.findViewById(R.id.icon_visimisi);
        ImageView iconSarpras = view.findViewById(R.id.icon_sarpras);
        ImageView iconEkstrakulikuler = view.findViewById(R.id.icon_ekstrakulikuler);

        iconSejarah.setOnClickListener(v -> {
            SiswaSejarahFragment siswaSejarahFragment = new SiswaSejarahFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, siswaSejarahFragment) // ID container layout
                    .addToBackStack(null) // biar bisa di-back
                    .commit();
        });

        iconTujuan.setOnClickListener(v -> {
            SiswaTujuanFragment siswaTujuanFragment = new SiswaTujuanFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, siswaTujuanFragment) // ID container layout
                    .addToBackStack(null) // biar bisa di-back
                    .commit();
        });

        iconVisimisi.setOnClickListener(v -> {
            SiswaVisimisiFragment siswaVisimisiFragment = new SiswaVisimisiFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, siswaVisimisiFragment) // ID container layout
                    .addToBackStack(null) // biar bisa di-back
                    .commit();
        });

        iconSarpras.setOnClickListener(v -> {
            SiswaSarprasFragment siswaSarprasFragmentt = new SiswaSarprasFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, siswaSarprasFragmentt) // ID container layout
                    .addToBackStack(null) // biar bisa di-back
                    .commit();
        });

        iconEkstrakulikuler.setOnClickListener(v -> {
            SiswaEkstrakulikulerFragment siswaEkstrakulikulerFragment = new SiswaEkstrakulikulerFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, siswaEkstrakulikulerFragment) // ID container layout
                    .addToBackStack(null) // biar bisa di-back
                    .commit();
        });
    }
}
