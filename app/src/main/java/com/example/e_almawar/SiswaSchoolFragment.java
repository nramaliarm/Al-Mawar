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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
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
    }

    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
