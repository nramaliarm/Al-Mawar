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
import com.example.e_almawar.viewmodel.Facility;

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.ViewHolder> {

    private Context context;
    private List<Facility> facilityList;

    private OnItemClickListener listener;

    // Interface untuk listener klik
    public interface OnItemClickListener {
        void onItemClick(Facility facility);
    }

    // Setter untuk OnItemClickListener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FacilityAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FacilityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_facility, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityAdapter.ViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.tvNama.setText(facility.getTitle());

        Glide.with(context)
                .load(facility.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .placeholder(R.drawable.image_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivFacility);

        // Menambahkan listener untuk klik pada gambar
        holder.ivFacility.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(facility); // Memanggil listener klik
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView ivFacility;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_fasilitas); // pastikan ID sesuai dengan item_facility.xml
            ivFacility = itemView.findViewById(R.id.ivFacilityImage); // pastikan ID sesuai juga
        }
    }
}
