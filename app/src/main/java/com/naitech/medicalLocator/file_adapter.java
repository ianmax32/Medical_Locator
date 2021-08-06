package com.naitech.medicalLocator;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.naitech.medicalLocator.POJOs.MedicalVisits;

import java.util.ArrayList;

public class file_adapter extends RecyclerView.Adapter<file_adapter.holder> {
    private Context context;
    private RecyclerViewClickListener listener;
    private ArrayList<MedicalVisits> visits = new ArrayList<>();

    public file_adapter(Context context, ArrayList<MedicalVisits> visits, RecyclerViewClickListener listen) {
        this.context = context;
        this.visits = visits;
        this.listener= listen;
    }

    @NonNull
    @Override
    public file_adapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_checkup,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull file_adapter.holder holder, int position) {
        holder.bp.setText("Blood Pressure: "+visits.get(position).getBp());
        holder.date.setText("Date of Visit: "+ visits.get(position).getDate().substring(0,10));
        holder.visit.setText("Symptoms: "+ visits.get(position).getSymptoms().substring(0,20)+" ...");
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public class holder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{
        TextView date;
        TextView bp;
        TextView visit;
        public holder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            bp= itemView.findViewById(R.id.bp);
            visit= itemView.findViewById(R.id.symptoms);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Vibrator vb = (Vibrator)v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(100);
            //menu.add(this.getAdapterPosition(),10,0,"DELETE").;
            menu.add(this.getAdapterPosition(),10,0,"SHARE").setIcon(R.drawable.ic_menu_share);

        }

        @Override
        public void onClick(View v) {
            listener.onClick(v,getAdapterPosition());
        }
    }
}
