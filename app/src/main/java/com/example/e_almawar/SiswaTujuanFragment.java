package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SiswaTujuanFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_tujuan, container, false);


        TextView textView = view.findViewById(R.id.list_poin);

        String[] listTujuan = {
                "Melaksanakan ibadah wajib dan sunnah dengan kesadaran mandiri.",
                "Mengamalkan akhlak karimah berbasis FASTER (Fatanah, Amanah, Sabar, Tabliqh, Empati, dan Respek) dalam berinteraksi.",
                "Mampu membaca Al-Quran dengan tartil.",
                "Mampu menghafal Al-Quran Juz 30, 1, 2, dan 3.",
                "Mampu mencetak lulusan yang kompetitif sesuai bakat dan bidangnya.",
                "Memiliki kemampuan literasi dan numerik (matematika dasar).",
                "Aktif berkomunikasi dalam bahasa asing (Arab dan Inggris).",
                "Mampu mengoperasikan Microsoft Office (Word, Excel, dan PowerPoint).",
                "Memiliki kemampuan PTT (PAI, Tahsin, dan Tahfidz) sesuai tingkatannya."
        };

        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (String item : listTujuan) {
            SpannableString spannable = new SpannableString(item + "\n"); // Tanpa manual bullet
            spannable.setSpan(new BulletSpan(40), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(spannable);
        }

        textView.setText(builder);

        return view;
    }
}