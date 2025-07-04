package com.example.e_almawar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.e_almawar.viewmodel.Ekskul;

import java.util.List;

public class EkskulAdapter extends RecyclerView.Adapter<EkskulAdapter.ViewHolder> {

    private Context context;
    private List<Ekskul> ekskulList;

    private OnItemClickListener listener;

    // Interface untuk listener klik
    public interface OnItemClickListener {
        void onItemClick(Ekskul ekskul);
    }

    // Setter untuk OnItemClickListener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public EkskulAdapter(Context context, List<Ekskul> ekskulList) {
        this.context = context;
        this.ekskulList = ekskulList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ekskul, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ekskul ekskul = ekskulList.get(position);
        holder.tvNama.setText(ekskul.getTitle());

        // Memuat gambar dengan Glide dan memberikan efek rounded corner
        Glide.with(context)
                .load(ekskul.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24))) // Radius bisa diubah sesuai kebutuhan
                .placeholder(R.drawable.image_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivEkskul);

        // Menambahkan listener untuk klik pada gambar
        holder.ivEkskul.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(ekskul); // Memanggil listener klik
            }
        });
    }

    @Override
    public int getItemCount() {
        return ekskulList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView ivEkskul;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_ekskul); // pastikan ID sesuai dengan layout item_ekskul.xml
            ivEkskul = itemView.findViewById(R.id.ivEkskulImage); // pastikan ID sesuai juga
        }
    }
}
