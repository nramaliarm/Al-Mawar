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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SiswaVisimisiFragment extends Fragment {

    private TextView visiTextView, misiTextView, greetingTextView;
    private ImageView ivProfile;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_visimisi, container, false);

        greetingTextView = view.findViewById(R.id.tv_greeting);
        visiTextView = view.findViewById(R.id.visi);
        misiTextView = view.findViewById(R.id.misi);
        ivProfile = view.findViewById(R.id.iv_profile); // Tambahkan ImageView ke layout XML
        firestore = FirebaseFirestore.getInstance();

        // Ambil data pengguna (nama dan profileUrl)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            firestore.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nama = documentSnapshot.getString("nama");
                            String profileUrl = documentSnapshot.getString("profileUrl");

                            if (nama != null) {
                                greetingTextView.setText("Halo, " + nama + "!");
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
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Gagal mengambil nama siswa", Toast.LENGTH_SHORT).show()
                    );
        }

        // Ambil data visi dan misi dari Firestore
        firestore.collection("visi_misi").document("visi_misiID")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String visi = documentSnapshot.getString("visi");
                        List<String> misiList = (List<String>) documentSnapshot.get("misi");

                        if (visi != null) {
                            visiTextView.setText("VISI\n\n" + visi);
                        }

                        if (misiList != null && !misiList.isEmpty()) {
                            StringBuilder misiBuilder = new StringBuilder();
                            misiBuilder.append("MISI\n\n");
                            for (int i = 0; i < misiList.size(); i++) {
                                misiBuilder.append((i + 1)).append(". ").append(misiList.get(i)).append("\n");
                            }
                            misiTextView.setText(misiBuilder.toString());
                        } else {
                            misiTextView.setText("MISI\n\nBelum ada data misi.");
                        }
                    } else {
                        Toast.makeText(getContext(), "Data visi dan misi tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", e.getMessage());
                    Toast.makeText(getContext(), "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        return view;
    }
}
