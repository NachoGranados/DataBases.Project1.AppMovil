package com.example.cinetec.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinetec.R;
import com.example.cinetec.models.Screening;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ScreeningAdapter extends RecyclerView.Adapter<ScreeningAdapter.ScreeningViewHolder> implements View.OnClickListener {

    Context context;
    List<Screening> screeningList;
    String imageURL;
    private View.OnClickListener onClickListener;

    public ScreeningAdapter(Context context, List<Screening> screeningList, String imageURL) {

        this.context = context;
        this.screeningList = screeningList;
        this.imageURL = imageURL;

    }

    @NonNull
    @Override
    public ScreeningAdapter.ScreeningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.screening_item, parent, false);

        view.setOnClickListener(this);

        return new ScreeningAdapter.ScreeningViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ScreeningAdapter.ScreeningViewHolder holder, int position) {

        Screening screening = screeningList.get(position);

        holder.screeningMovieOriginalName.setText(screening.getMovieOriginalName());
        holder.screeningCinemaNumber.setText("Cinema: " + Integer.toString(screening.getCinemaNumber()));
        holder.screeningHour.setText("Hour: " + Integer.toString(screening.getHour()));
        holder.screeningCapacity.setText("Capacity: " + Integer.toString(screening.getCapacity()));

        Picasso.with(context).load(imageURL).into(holder.screeningImage);

    }

    @Override
    public int getItemCount() {

        return screeningList.size();

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.onClickListener = onClickListener;

    }

    @Override
    public void onClick(View view) {

        if(onClickListener != null) {

            onClickListener.onClick(view);

        }

    }

    public class ScreeningViewHolder extends RecyclerView.ViewHolder {

        TextView screeningMovieOriginalName;
        TextView screeningCinemaNumber;
        TextView screeningHour;
        TextView screeningCapacity;
        ImageView screeningImage;

        public ScreeningViewHolder(@NonNull View itemView) {

            super(itemView);

            screeningMovieOriginalName = itemView.findViewById(R.id.screening_movie_original_name);
            screeningCinemaNumber = itemView.findViewById(R.id.screening_cinema_number);
            screeningHour = itemView.findViewById(R.id.screening_hour);
            screeningCapacity = itemView.findViewById(R.id.screening_capacity);
            screeningImage = itemView.findViewById(R.id.imageViewScreening);

        }

    }

}
