package com.example.cinetec.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinetec.R;
import com.example.cinetec.SeatSelectionActivity;
import com.example.cinetec.models.Seat;

import java.util.List;

public class ConfirmationSeatAdapter extends RecyclerView.Adapter<ConfirmationSeatAdapter.ConfirmationSeatViewHolder> {

    Context context;
    List<Seat> seatList;

    public ConfirmationSeatAdapter(Context context, List<Seat> seatList) {

        this.context = context;
        this.seatList = seatList;

    }

    @NonNull
    @Override
    public ConfirmationSeatAdapter.ConfirmationSeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.seat_item, parent, false);

        return new ConfirmationSeatAdapter.ConfirmationSeatViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmationSeatAdapter.ConfirmationSeatViewHolder holder, int position) {

        Seat seat = seatList.get(position);

        holder.rowNumber.setText("Row: " + Integer.toString(seat.getRowNum()));
        holder.columnNumber.setText("Column: " + Integer.toString(seat.getColumnNum()));
        holder.state.setText("");

        if(seat.getState().equals("free")) {

            holder.seatImage.setImageResource(R.drawable.white_seat);

        } else if(seat.getState().equals("sold")) {

            holder.seatImage.setImageResource(R.drawable.green_seat);

        } else if(seat.getState().equals("restricted")) {

            holder.seatImage.setImageResource(R.drawable.red_seat);

        }

    }

    @Override
    public int getItemCount() {

        return seatList.size();

    }

    public class ConfirmationSeatViewHolder extends RecyclerView.ViewHolder {

        TextView rowNumber;
        TextView columnNumber;
        TextView state;
        ImageView seatImage;
        CardView cardViewSeat;

        public ConfirmationSeatViewHolder(@NonNull View itemView) {

            super(itemView);

            rowNumber = itemView.findViewById(R.id.seat_row_number);
            columnNumber = itemView.findViewById(R.id.seat_column_number);
            state = itemView.findViewById(R.id.seat_state);
            seatImage = itemView.findViewById(R.id.imageViewSeat);
            cardViewSeat = itemView.findViewById(R.id.cardViewSeat);

        }

    }

}
