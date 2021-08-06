package com.naitech.medicalLocator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycler_view_adapter extends RecyclerView.Adapter<recycler_view_adapter.holder> {

    Context context;


    ArrayList<String> hospitals = new ArrayList<>();

    public recycler_view_adapter(Context ct, ArrayList<String> hosp) {
        this.context = ct;
        this.hospitals = hosp;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_info,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.hosp_name.setText(hospitals.get(position));
        holder.distance.setText("0km");
    }

    @Override
    public int getItemCount() {
        //Log.d("count", "The size of the array is "+ hospitals.size());
        return hospitals.size();
    }

    public class holder extends RecyclerView.ViewHolder{
        TextView hosp_name;
        TextView distance;
        public holder(@NonNull View itemView) {
            super(itemView);
            hosp_name = itemView.findViewById(R.id.hospital_name);
            distance = itemView.findViewById(R.id.hospital_distance);
        }

        //write code to add information on the view
    }
}
