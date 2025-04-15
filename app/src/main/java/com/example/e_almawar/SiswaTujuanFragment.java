package com.example.e_almawar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SiswaTujuanFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView tvGreeting, textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_tujuan, container, false);

        // Inisialisasi TextView
        tvGreeting = view.findViewById(R.id.tv_greeting);
        textView = view.findViewById(R.id.list_poin);

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Tampilkan nama user dari SharedPreferences
        tampilkanNamaPengguna();

        // Load data tujuan sekolah dari Firestore
        loadTujuanSekolah();

        return view;
    }

    private void tampilkanNamaPengguna() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);

        if (userName != null) {
            tvGreeting.setText("Halo, " + userName + "!");
        } else {
            tvGreeting.setText("Halo!");
        }
    }

    private void loadTujuanSekolah() {
        db.collection("TujuanSekolah").document("TujuanID")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Ambil tujuan sekolah dari Firestore
                            List<String> tujuanList = (List<String>) documentSnapshot.get("tujuan_sekolah");

                            if (tujuanList != null && !tujuanList.isEmpty()) {
                                tampilkanDenganNomor(tujuanList);
                            } else {
                                textView.setText("Data tujuan kosong.");
                            }
                        } else {
                            textView.setText("Dokumen tidak ditemukan.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView.setText("Gagal mengambil data: " + e.getMessage());
                    }
                });
    }

    private void tampilkanDenganNomor(List<String> listTujuan) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // Menentukan jarak nomor dan teks penjelasan
        int nomorWidth = 40; // Menyesuaikan lebar indentasi untuk nomor

        for (int i = 0; i < listTujuan.size(); i++) {
            String nomor = (i + 1) + ". "; // Nomor tanpa kurung
            String isi = listTujuan.get(i).trim();

            // Gabungkan nomor dan isi dengan jarak yang konsisten
            SpannableStringBuilder itemBuilder = new SpannableStringBuilder(nomor + isi + "\n\n");
            itemBuilder.setSpan(new LeadingMarginSpan.Standard(nomorWidth, nomorWidth),
                    0, itemBuilder.length(), 0);

            builder.append(itemBuilder);
        }

        textView.setText(builder);

        // Mengatur justification (rata kiri-kanan) untuk Android 8+ ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }
}
