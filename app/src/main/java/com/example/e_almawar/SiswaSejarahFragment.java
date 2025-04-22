package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SiswaSejarahFragment extends Fragment {

    private TextView textViewSejarah;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_sejarah, container, false);
        textViewSejarah = view.findViewById(R.id.textView5);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()

                .child("sejarah").child("sejarahID").child("sejarah_sekolah");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String sejarah = snapshot.getValue(String.class);
                    textViewSejarah.setText(sejarah);
                } else {
                    textViewSejarah.setText("Data tidak ditemukan.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                textViewSejarah.setText("Gagal memuat data: " + error.getMessage());
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sejarah").document("sejarahID")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String sejarah = documentSnapshot.getString("sejarah_sekolah");
                            textViewSejarah.setText(sejarah);
                        }
                    }
                })
                .addOnFailureListener(e -> textViewSejarah.setText("Gagal memuat data: " + e.getMessage()));

        return view;
    }
}
