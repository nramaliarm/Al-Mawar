package com.example.e_almawar;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_almawar.viewmodel.Guru;

public class GuruDetailDialog {

    public static void show(Context context, Guru guru) {
        if (guru == null) {
            // Log jika guru null dan keluar dari method
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_detail_guru, null);

        // Referensi view dari layout dialog_detail_guru.xml
        ImageView ivFoto = view.findViewById(R.id.iv_detail_foto);
        TextView tvNama = view.findViewById(R.id.tv_detail_nama);
        TextView tvJabatan = view.findViewById(R.id.tv_detail_jabatan);
        TextView tvJenisKelamin = view.findViewById(R.id.tv_detail_jenis_kelamin);
        TextView tvJurusan = view.findViewById(R.id.tv_detail_jurusan);
        TextView tvAlamat = view.findViewById(R.id.tv_detail_alamat);
        TextView tvTempatTanggalLahir = view.findViewById(R.id.tv_detail_tempat_tanggal_lahir);
        TextView tvPendidikan = view.findViewById(R.id.tv_detail_pendidikan);
        TextView tvStatus = view.findViewById(R.id.tv_detail_status);
        TextView tvTahunMasuk = view.findViewById(R.id.tv_detail_tahun_masuk);
        TextView tvNip = view.findViewById(R.id.tv_detail_nip);
        TextView tvNoHp = view.findViewById(R.id.tv_detail_nohp);
        TextView tvAgama = view.findViewById(R.id.tv_detail_agama);

        // Set data ke tampilan
        Glide.with(context)
                .load(guru.getFotoURL())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.foto_background)
                .into(ivFoto);

        tvNama.setText(guru.getNamaLengkap());
        tvJabatan.setText(guru.getJabatan());
        tvJenisKelamin.setText(guru.getJenisKelamin());
        tvJurusan.setText(guru.getJurusan());
        tvAlamat.setText(guru.getAlamat());
        tvTempatTanggalLahir.setText(guru.getTempatLahir() + ", " + guru.getTanggalLahir());
        tvPendidikan.setText(guru.getPendidikanTerakhir());
        tvStatus.setText(guru.getStatus());
        tvTahunMasuk.setText(guru.getTahunMasuk());
        tvNip.setText(guru.getNip());
        tvNoHp.setText(guru.getNoHP());
        tvAgama.setText(guru.getAgama());

        builder.setView(view);
        builder.setPositiveButton("Tutup", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_rounded);


        dialog.show();
    }
}

