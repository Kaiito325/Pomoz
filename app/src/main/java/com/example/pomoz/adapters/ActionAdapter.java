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

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> implements Filterable {

    private List<Action> fullList; // pełna lista pobrana z API
    private List<Action> filteredList; // lista filtrowana
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Action action);
    }

    public ActionAdapter(List<Action> lista, OnItemClickListener listener) {
        this.fullList = new ArrayList<>(lista);
        this.filteredList = new ArrayList<>(lista);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder holder, int position) {
        Action action = filteredList.get(position);
        holder.bind(action);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.action_name);
        }

        public void bind(Action action) {
            nameText.setText(action.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(action));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Action> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Action a : fullList) {
                        if (a.getName().toLowerCase().contains(filterPattern)) {
                            filtered.add(a);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateList(List<Action> newList) {
        fullList.clear();
        fullList.addAll(newList);
        getFilter().filter(""); // od razu pokazuje wszystkie elementy
    }
}
