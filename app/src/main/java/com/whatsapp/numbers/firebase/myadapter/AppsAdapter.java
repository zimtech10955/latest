package com.whatsapp.numbers.firebase.myadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.whatsapp.numbers.R;
import com.whatsapp.numbers.firebase.StartActivity;
import com.whatsapp.numbers.firebase.model.Apps;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsViewHolder>{
    private Context context;
    private List<Apps> list;

    public AppsAdapter(Context context, List<Apps> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppsViewHolder(LayoutInflater.from(context).inflate(R.layout.apps_item_row , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppsViewHolder holder, int position) {
        holder.bindIcon(list.get(position).getIcon());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AppsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public AppsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        void bindIcon(String icon){
            ImageView mImage = itemView.findViewById(R.id.item_view_icon);
            Glide.with(context).load(icon).into(mImage);

            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StartActivity.openAppRating(context, list.get(getAdapterPosition()).getUrl());
                }
            });
        }

        @Override
        public void onClick(View v) {
            StartActivity.openAppRating(context, list.get(getAdapterPosition()).getUrl());

        }
    }
}
