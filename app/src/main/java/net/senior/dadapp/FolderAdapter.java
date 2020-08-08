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

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements Filterable {
    List<String> FoldersModels;
    List<String> filteredList;
    Context context;
    OnImgClicked onImgClicked;

    public void setOnImgClicked(OnImgClicked onImgClicked) {
        this.onImgClicked = onImgClicked;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(FoldersModels);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : FoldersModels) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            filteredList.clear();
//            filteredList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public FolderAdapter(List<String> FoldersModels) {
        this.FoldersModels = FoldersModels;
        this.filteredList = FoldersModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (filteredList != null) {
            holder.name.setText(filteredList.get(position));
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImgClicked.onImgClicked(position);
                }
            });
        }
    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    public interface OnImgClicked {
        void onImgClicked(int pos);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
        }

    }
}
