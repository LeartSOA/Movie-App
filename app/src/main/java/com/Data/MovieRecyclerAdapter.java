package com.Data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Model.Movie;

import com.example.movieapp.Details;
import com.example.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private int id;

    public MovieRecyclerAdapter(Context context, List<Movie> movies){
        this.context = context;
        movieList = movies;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);


        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        id = movie.getId();

        String posterLink = movie.getImgLink();

        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        holder.rating.setText(String.valueOf(movie.getRating()));
        holder.description.setText(movie.getOverview());

        

        String link = "https://image.tmdb.org/t/p/w92"+posterLink;

        Picasso.with(context)
                .load(link)
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(holder.img);


    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;
        TextView year;
        TextView rating;
        TextView description;

        public ViewHolder(@NonNull final View itemView, final Context ctx) {
            super(itemView);
            context = ctx;

            title = (TextView) itemView.findViewById(R.id.movie_title_watchlist);
            img = (ImageView) itemView.findViewById(R.id.imgWatchlistrow);
            year = (TextView) itemView.findViewById(R.id.year_watchlist);
            rating = (TextView) itemView.findViewById(R.id.rating_watchlist);
            description = (TextView) itemView.findViewById(R.id.runtime_watchlist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int realid = getAdapterPosition();
                    Movie mov = movieList.get(realid);
                    int theid = mov.getId();
                    Intent intent =  new Intent(context, Details.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", theid);
                    intent.putExtra("name", mov.getTitle());
                    context.startActivity(intent);
                }
            });
        }
    }
}
