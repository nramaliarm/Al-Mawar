package com.example.e_almawar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.e_almawar.viewmodel.Guru;

import java.util.List;

public class GuruAdapter extends RecyclerView.Adapter<GuruAdapter.ViewHolder> {

    private Context context;
    private List<Guru> guruList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Guru guru);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public GuruAdapter(Context context, List<Guru> guruList) {
        this.context = context;
        this.guruList = guruList;
    }

    @NonNull
    @Override
    public GuruAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_guru, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuruAdapter.ViewHolder holder, int position) {
        Guru guru = guruList.get(position);
        holder.tvNama.setText(guru.getNamaLengkap());

        Glide.with(context)
                .load(guru.getFotoURL())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .placeholder(R.drawable.foto_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivGuru);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(guru);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guruList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView ivGuru;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_guru);
            ivGuru = itemView.findViewById(R.id.ivGuruImage);
        }
    }
}
