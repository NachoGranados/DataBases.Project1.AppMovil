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
import com.example.cinetec.models.Movie;
import com.example.cinetec.models.Seat;
import java.util.List;

//public class SeatHorizontalAdapter extends RecyclerView.Adapter<SeatHorizontalAdapter.SeatHorizontalViewHolder> implements View.OnClickListener {
public class SeatHorizontalAdapter extends RecyclerView.Adapter<SeatHorizontalAdapter.SeatHorizontalViewHolder> {
    Context context;
    List<Seat> seatList;
    private View.OnClickListener onClickListener;

    public SeatHorizontalAdapter(Context context, List<Seat> seatList) {

        this.context = context;
        this.seatList = seatList;

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

            holder.seatImage.setImageResource(R.drawable.green_seat);

        } else if(seat.getState().equals("sold")) {

            holder.seatImage.setImageResource(R.drawable.red_seat);

        }

        holder.cardViewSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String selectedMovieTheater = movieTheaterList.get(recyclerView.getChildAdapterPosition(view)).getName();

                //openMovieSelectionActivity(selectedMovieTheater);

                //String selectedScreeningId = Integer.toString(seatList.get(recyclerView.getChildAdapterPosition(view)).getId());

                Toast.makeText(context, "Row: " + seat.getRowNum() + " / Column: " + seat.getColumnNum(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {

        return seatList.size();

    }

    /*
    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.onClickListener = onClickListener;

    }

    @Override
    public void onClick(View view) {

        if(onClickListener != null) {

            onClickListener.onClick(view);

        }

    }

     */

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