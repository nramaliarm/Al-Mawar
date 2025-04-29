package com.example.e_almawar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.e_almawar.viewmodel.Ekskul;

import java.util.List;

public class EkskulAdapter extends RecyclerView.Adapter<EkskulAdapter.ViewHolder> {

    private Context context;
    private List<Ekskul> ekskulList;

    public EkskulAdapter(Context context, List<Ekskul> ekskulList) {
        this.context = context;
        this.ekskulList = ekskulList;
    }

    @NonNull
    @Override
    public EkskulAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ekskul, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EkskulAdapter.ViewHolder holder, int position) {
        Ekskul ekskul = ekskulList.get(position);
        holder.tvNama.setText(ekskul.getTitle());

        Glide.with(context)
                .load(ekskul.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24))) // Ubah 24 jadi radius sesuai keinginan
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivEkskul);
    }

    @Override
    public int getItemCount() {
        return ekskulList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvDeskripsi;
        ImageView ivEkskul;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_ekskul);
            ivEkskul = itemView.findViewById(R.id.ivEkskulImage);
        }
    }
}
