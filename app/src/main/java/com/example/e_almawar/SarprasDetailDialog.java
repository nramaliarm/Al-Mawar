package com.example.e_almawar;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_almawar.viewmodel.Facility;


public class SarprasDetailDialog {

    public static void show(Context context, Facility sarpras) {
        if (sarpras == null) {
            // Log jika sarpras null dan keluar dari method
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_detail_sarpras, null);

        // Referensi view dari layout dialog_detail_sarpras.xml
        ImageView ivFoto = view.findViewById(R.id.iv_detail_foto);
        TextView tvNama = view.findViewById(R.id.tv_detail_nama);

        // Set data ke tampilan
        Glide.with(context)
                .load(sarpras.getImageUrl())
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.image_background) // Placeholder jika gambar belum dimuat
                .into(ivFoto);

        tvNama.setText(sarpras.getTitle());

        builder.setView(view);
        builder.setPositiveButton("Tutup", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_rounded); // Gambar background dialog

        dialog.show();
    }
}
