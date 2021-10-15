package com.example.cinetec.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinetec.R;
import com.example.cinetec.models.Seat;
import com.example.cinetec.models.SeatList;

import java.util.List;

public class SeatVerticalAdapter extends RecyclerView.Adapter<SeatVerticalAdapter.SeatVerticalViewHolder> {

    Context context;
    List<SeatList> seatListList;

    public SeatVerticalAdapter(Context context, List<SeatList> seatListList) {

        this.context = context;
        this.seatListList = seatListList;

    }

    @NonNull
    @Override
    public SeatVerticalAdapter.SeatVerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.seat_list_item, parent, false);

        return new SeatVerticalAdapter.SeatVerticalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SeatVerticalAdapter.SeatVerticalViewHolder holder, int position) {

        List<Seat> seatList = seatListList.get(position).getSeatList();

        SeatHorizontalAdapter seatHorizontalAdapter = new SeatHorizontalAdapter(context, seatList);

        holder.recyclerViewHorizontal.setHasFixedSize(true);
        holder.recyclerViewHorizontal.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewHorizontal.setAdapter(seatHorizontalAdapter);

    }

    @Override
    public int getItemCount() {

        return seatListList.size();

    }

    public class SeatVerticalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerViewHorizontal;

        public SeatVerticalViewHolder(@NonNull View itemView) {

            super(itemView);

            recyclerViewHorizontal = itemView.findViewById(R.id.recyclerViewSeatHorizontal);

        }

    }

}
