package com.example.e_almawar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_almawar.R;
import com.example.e_almawar.viewmodel.Facility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private Context context;
    private List<Facility> facilityList;

    public FacilityAdapter(Context context, List<Facility> facilityList) {
        this.context = context;
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.tvName.setText(facility.getTitle());

        // Load image from URL using Picasso
        Picasso.get()
                .load(facility.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivFacility);
    }

    @Override
    public int getItemCount() {
        return facilityList.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFacility;
        TextView tvName;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFacility = itemView.findViewById(R.id.iv_facility);
            tvName = itemView.findViewById(R.id.tv_facility_name);
        }
    }
}
