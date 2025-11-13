package com.example.pomoz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomoz.R;
import com.example.pomoz.model_classes.Action;

import java.util.ArrayList;
import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> implements Filterable {

    private List<Action> originalList;
    private List<Action> filteredList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Action action);
    }

    public ActionAdapter(List<Action> list, OnItemClickListener listener) {
        this.originalList = list;
        this.filteredList = new ArrayList<>(list);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.action, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Action a = filteredList.get(position);
        holder.nazwa.setText(a.getName());
        holder.icon.setImageResource(a.getImgResID());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(a));
    }

    @Override
    public int getItemCount() { return filteredList.size(); }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<Action> result = new ArrayList<>();
                if (query.isEmpty()) {
                    result.addAll(originalList);
                } else {
                    for (Action a : originalList) {
                        if (a.getName().toLowerCase().contains(query)) {
                            result.add(a);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = result;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<Action>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nazwa;
        ImageView icon;
        ViewHolder(View itemView) {
            super(itemView);
            nazwa = itemView.findViewById(R.id.nazwa_czynnosc);
            icon = itemView.findViewById(R.id.icon_czynnosc);
        }
    }
}
