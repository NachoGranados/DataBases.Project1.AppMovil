package com.example.cinetec.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinetec.R;
import com.example.cinetec.models.Movie;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements View.OnClickListener {

    Context context;
    List<Movie> movieList;
    private View.OnClickListener onClickListener;

    public MovieAdapter(Context context, List<Movie> movieList) {

        this.context = context;
        this.movieList = movieList;

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);

        view.setOnClickListener(this);

        return new MovieAdapter.MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        Movie movie = movieList.get(position);

        holder.movieOriginalName.setText(movie.getOriginalName());
        holder.movieGendre.setText(movie.getGendre());
        holder.movieLenght.setText(Integer.toString(movie.getLenght()));

    }

    @Override
    public int getItemCount() {

        return movieList.size();

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

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView movieOriginalName;
        TextView movieGendre;
        TextView movieLenght;

        public MovieViewHolder(@NonNull View itemView) {

            super(itemView);

            movieOriginalName = itemView.findViewById(R.id.movie_original_name);
            movieGendre = itemView.findViewById(R.id.movie_gendre);
            movieLenght = itemView.findViewById(R.id.movie_lenght);

        }

    }

}
