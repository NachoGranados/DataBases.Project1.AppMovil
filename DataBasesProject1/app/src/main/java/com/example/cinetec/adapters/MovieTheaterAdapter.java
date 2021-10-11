package com.example.cinetec.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinetec.R;
import com.example.cinetec.models.MovieTheater;
import java.util.List;

public class MovieTheaterAdapter extends RecyclerView.Adapter<MovieTheaterAdapter.MovieTheaterViewHolder> implements View.OnClickListener {

    Context context;
    List<MovieTheater> movieTheaterList;
    private View.OnClickListener onClickListener;

    public MovieTheaterAdapter(Context context, List<MovieTheater> movieTheaterList) {

        this.context = context;
        this.movieTheaterList = movieTheaterList;

    }

    @NonNull
    @Override
    public MovieTheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_theater_item, parent, false);

        view.setOnClickListener(this);

        return new MovieTheaterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieTheaterViewHolder holder, int position) {

        MovieTheater movieTheater = movieTheaterList.get(position);

        holder.movieTheaterTitle.setText(movieTheater.getName());
        holder.movieTheaterLocation.setText(movieTheater.getLocation());

    }

    @Override
    public int getItemCount() {

        return movieTheaterList.size();

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

    public class MovieTheaterViewHolder extends RecyclerView.ViewHolder {

        TextView movieTheaterTitle;
        TextView movieTheaterLocation;

        public MovieTheaterViewHolder(@NonNull View itemView) {

            super(itemView);

            movieTheaterTitle = itemView.findViewById(R.id.movie_teather_name);
            movieTheaterLocation = itemView.findViewById(R.id.movie_theather_location);

        }

    }

}
