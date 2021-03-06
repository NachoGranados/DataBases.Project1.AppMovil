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
import com.example.cinetec.models.Movie;
import com.squareup.picasso.Picasso;

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
        holder.movieGendre.setText("Gendre: " + movie.getGendre());
        holder.movieLenght.setText("Lenght: " + Integer.toString(movie.getLenght()) + " min");

        Picasso.with(context).load(movie.getImageUrl()).into(holder.movieImage);

        //Picasso.with(context).load("https://static.metacritic.com/images/products/movies/4/895501b7c879f8b8b236524db91ee9ab.jpg").into(holder.movieImage);

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
        ImageView movieImage;

        public MovieViewHolder(@NonNull View itemView) {

            super(itemView);

            movieOriginalName = itemView.findViewById(R.id.movie_original_name);
            movieGendre = itemView.findViewById(R.id.movie_gendre);
            movieLenght = itemView.findViewById(R.id.movie_lenght);
            movieImage = itemView.findViewById(R.id.imageViewMovieMovie);

        }

    }

}
