package com.Data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Model.MovieWatchlist;
import com.example.movieapp.Details;
import com.example.movieapp.Posts;
import com.example.movieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.List;

public class WatchlistRecyclerAdapter extends RecyclerView.Adapter<WatchlistRecyclerAdapter.ViewHolder> {

    private DatabaseReference dbref;
    private Context context;
    private List<MovieWatchlist> movieList;
    private int id;

    public WatchlistRecyclerAdapter(Context context, List<MovieWatchlist> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_row, parent , false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MovieWatchlist movie = movieList.get(position);
        String rating_text = "Rating: "+String.valueOf(movie.getRating());
        String runtime_text = "Runtime: " + movie.getRuntime();
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        holder.rating.setText(rating_text);
        holder.runtime.setText(runtime_text);
        Log.d("movieID",String.valueOf( movie.getId()));
        if (!(movieList.size() >= 0)){
            Toast.makeText(context, "No Movies on Your Watchlist", Toast.LENGTH_SHORT).show();
        }

        String link = "https://image.tmdb.org/t/p/w92"+movie.getImgLink();
        Picasso.with(context)
                .load(link)
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(holder.img);



    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView img;
        TextView year;
        TextView rating;
        TextView runtime;
        Button seen;
        Button remove;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            title = (TextView) itemView.findViewById(R.id.movie_title_watchlist);
            img = (ImageView) itemView.findViewById(R.id.imgWatchlistrow);
            year = (TextView) itemView.findViewById(R.id.year_watchlist);
            rating = (TextView) itemView.findViewById(R.id.rating_watchlist);
            runtime = (TextView) itemView.findViewById(R.id.runtime_watchlist);
            seen = (Button) itemView.findViewById(R.id.watchedWatchlist);
            remove = (Button) itemView.findViewById(R.id.remove);
            seen.setOnClickListener(this);
            remove.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieWatchlist mov = movieList.get(getAdapterPosition());
                    Intent intent =  new Intent(context, Details.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id", mov.getId());
                    Log.d("movid" , String.valueOf(mov.getId()));
                    intent.putExtra("name", mov.getTitle());
                    context.startActivity(intent);


                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.watchedWatchlist:
                {
                    int position = getAdapterPosition();
                    MovieWatchlist movie = movieList.get(position);
                    int movieid = movie.getId();
                    String movietitle = movie.getTitle();
                    String current_user = FirebaseAuth.getInstance().getUid();
                    Intent intent = new Intent(context, Posts.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("title", movietitle);
                    Delete(movietitle, current_user);
                    context.startActivity(intent);

                }
                break;
                case R.id.remove:
                {
                    int position = getAdapterPosition();
                    MovieWatchlist movie = movieList.get(position);
                    int movieid = movie.getId();
                    String movietitle = movie.getTitle();
                    String current_user = FirebaseAuth.getInstance().getUid();
                    Delete(movietitle, current_user);

                }
                break;

            }
        }

        public void Delete(String movietitle, String current_user){
            dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user)
                    .child("watchlist").child(movietitle);
            dbref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                    movieList.remove(getAdapterPosition());
                    notifyDataSetChanged();

                }
            });
        }
    }
}
