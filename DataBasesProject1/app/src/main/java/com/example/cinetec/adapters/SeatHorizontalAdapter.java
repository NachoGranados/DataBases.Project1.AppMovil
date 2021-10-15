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

public class SeatHorizontalAdapter extends RecyclerView.Adapter<SeatHorizontalAdapter.SeatHorizontalViewHolder> {
    Context context;
    List<Seat> seatList;
    SeatSelectionActivity seatSelectionActivity;
    private View.OnClickListener onClickListener;

    public SeatHorizontalAdapter(Context context, List<Seat> seatList, SeatSelectionActivity seatSelectionActivity) {

        this.context = context;
        this.seatList = seatList;
        this.seatSelectionActivity = seatSelectionActivity;

    }

    @NonNull
    @Override
    public SeatHorizontalAdapter.SeatHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.seat_item, parent, false);

        return new SeatHorizontalAdapter.SeatHorizontalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SeatHorizontalAdapter.SeatHorizontalViewHolder holder, int position) {

        Seat seat = seatList.get(position);

        holder.rowNumber.setText("Row: " + Integer.toString(seat.getRowNum()));
        holder.columnNumber.setText("Column: " + Integer.toString(seat.getColumnNum()));
        holder.state.setText("State: " + seat.getState());

        if(seat.getState().equals("free")) {

            holder.seatImage.setImageResource(R.drawable.white_seat);

        } else if(seat.getState().equals("sold")) {

            holder.seatImage.setImageResource(R.drawable.green_seat);

        } else if(seat.getState().equals("restricted")) {

            holder.seatImage.setImageResource(R.drawable.red_seat);

        }

        holder.cardViewSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(seat.getState().equals("free")) {

                    seatSelectionActivity.addSelectedSeat(seat);

                    holder.seatImage.setImageResource(R.drawable.yellow_seat);

                    seat.setState("selected");

                } else if(seat.getState().equals("sold")) {

                    Toast.makeText(context, "Seat sold. Try another one", Toast.LENGTH_SHORT).show();

                } else if(seat.getState().equals("restricted")) {

                    Toast.makeText(context, "Seat not allowed. Try another one", Toast.LENGTH_SHORT).show();

                } else if(seat.getState().equals("selected")) {

                    seat.setState("free");

                    seatSelectionActivity.deleteSelectedSeat(seat);

                    holder.seatImage.setImageResource(R.drawable.white_seat);

                }

            }
        });

    }

    @Override
    public int getItemCount() {

        return seatList.size();

    }

    public class SeatHorizontalViewHolder extends RecyclerView.ViewHolder {

        TextView rowNumber;
        TextView columnNumber;
        TextView state;
        ImageView seatImage;
        CardView cardViewSeat;

        public SeatHorizontalViewHolder(@NonNull View itemView) {

            super(itemView);

            rowNumber = itemView.findViewById(R.id.seat_row_number);
            columnNumber = itemView.findViewById(R.id.seat_column_number);
            state = itemView.findViewById(R.id.seat_state);
            seatImage = itemView.findViewById(R.id.imageViewSeat);
            cardViewSeat = itemView.findViewById(R.id.cardViewSeat);

        }

    }

}