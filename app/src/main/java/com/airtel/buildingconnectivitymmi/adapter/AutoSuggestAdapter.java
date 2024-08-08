package com.airtel.buildingconnectivitymmi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airtel.buildingconnectivitymmi.R;
import com.mmi.services.api.autosuggest.model.ELocation;

import java.util.ArrayList;

/**
 * Created by CEINFO on 26-02-2019.
 */

public class AutoSuggestAdapter extends RecyclerView.Adapter<AutoSuggestAdapter.MyViewholder> {
    private ArrayList<ELocation> list;
    private PlaceName placeName;

    public AutoSuggestAdapter(ArrayList<ELocation> list, PlaceName placeName) {
        this.list = list;
        this.placeName = placeName;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.auto_suggest_adapter_row, parent, false);
        return new MyViewholder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, final int position) {
        holder.viewName.setText(list.get(position).placeName);
        holder.viewSubName.setText(list.get(position).placeAddress);
        holder.viewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                placeName.nameOfPlace(list.get(position).placeName);
                placeName.nameOfPlace(list.get(position).placeAddress);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface PlaceName {
        void nameOfPlace(String name);
    }

    class MyViewholder extends RecyclerView.ViewHolder {

        TextView viewName, viewSubName;
        ConstraintLayout viewParent;

        public MyViewholder(View itemView) {
            super(itemView);
            viewName = itemView.findViewById(R.id.textView);
            viewSubName = itemView.findViewById(R.id.subTextView);
            viewParent = itemView.findViewById(R.id.clParent);
        }
    }
}
