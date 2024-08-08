/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.airtel.buildingconnectivitymmi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airtel.buildingconnectivitymmi.R;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomInfoAdaptor extends RecyclerView.Adapter<CustomInfoAdaptor.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static CustomAdapterCheckboxListener mListener;
    private final String[] arrNeRehabAcitivity;
    private Context mContext;


    public interface CustomAdapterCheckboxListener {
        void selectedNEActivity(String neActivity);
    }

    /**
     * Initialize the setNeUserMap of the Adapter.
     *
     * @param mContext
     * @param arrNeRehabAcitivity Map<Integer, NeUser> containing the data to populate views to be used by RecyclerView.
     */
    public CustomInfoAdaptor(Context mContext, String[] arrNeRehabAcitivity) {
        this.mContext = mContext;
        this.arrNeRehabAcitivity = arrNeRehabAcitivity;
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvActivity;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.

            tvActivity = v.findViewById(R.id.recycleView);
        }

        public TextView getTvActivity() {
            return tvActivity;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_info_window, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        /*NeRehabAcitivity obj = arrNeRehabAcitivity[position];
        viewHolder.getCbActivity().setChecked(obj.getChecked());
        if (obj.getChecked() && !arrSelected.contains(obj)) {
            arrSelected.add(obj);
        } else if (!obj.getChecked()) {
            arrSelected.remove(obj);
        }*/

        //viewHolder.getTvActivity().setText(arrNeRehabAcitivity[position].getNeRehabActivity());

        viewHolder.getTvActivity().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arrNeRehabAcitivity.length;
    }

    public static void bindListener(CustomAdapterCheckboxListener listener) {
        mListener = listener;
    }
}
