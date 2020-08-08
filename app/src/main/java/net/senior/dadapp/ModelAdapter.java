package net.senior.dadapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder>  {
    List<String> models;
    OnImgClicked onImgClicked;

    public void setOnImgClicked(OnImgClicked onImgClicked) {
        this.onImgClicked = onImgClicked;
    }



    public ModelAdapter(List<String> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            holder.img.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  onImgClicked.onImgClicked(position);
                                              }
                                          });
            Glide.with(holder.itemView).load(models.get(position)).into(holder.img);

    }


    @Override
    public int getItemCount() {
        return models.size();
    }


    public interface OnImgClicked {
        void onImgClicked(int pos);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }

    }
}
