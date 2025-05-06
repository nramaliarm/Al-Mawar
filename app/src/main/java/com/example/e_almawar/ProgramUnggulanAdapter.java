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
import com.example.e_almawar.viewmodel.ProgramUnggulan;

import java.util.List;

public class ProgramUnggulanAdapter extends RecyclerView.Adapter<ProgramUnggulanAdapter.ViewHolder> {

    private Context context;
    private List<ProgramUnggulan> programList;

    public ProgramUnggulanAdapter(Context context, List<ProgramUnggulan> programList) {
        this.context = context;
        this.programList = programList;
    }

    @NonNull
    @Override
    public ProgramUnggulanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_program_unggulan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramUnggulanAdapter.ViewHolder holder, int position) {
        ProgramUnggulan program = programList.get(position);
        holder.tvNama.setText(program.getTitle());

        Glide.with(context)
                .load(program.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivProgram);
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        ImageView ivProgram;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama_program);       // Sesuaikan ID dengan layout item_program_unggulan.xml
            ivProgram = itemView.findViewById(R.id.ivProgramImage);     // Sesuaikan juga
        }
    }
}
