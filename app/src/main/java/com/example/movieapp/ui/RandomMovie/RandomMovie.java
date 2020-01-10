package com.example.movieapp.ui.RandomMovie;

import android.content.Context;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.Data.MovieRecyclerAdapter;
import com.Model.Movie;
import com.Util.Constants;
import com.Util.MovieLength;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMovie extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Movie> movieList;
    private Button butoni;
    private RequestQueue queue;
    private String runtime="";

    MovieLength ml = new MovieLength();


    private Context context;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_what_to_watch, container, false);

        context = getActivity().getApplicationContext();

        queue = Volley.newRequestQueue(context);

        movieList = new ArrayList<>();
        butoni = (Button) root.findViewById(R.id.generate_buttoni);
        recyclerView = (RecyclerView) root.findViewById(R.id.randomRecycler);
        adapter = new MovieRecyclerAdapter(context, movieList);
        recyclerView.setAdapter(adapter);
        for(int m=0;m<6; m++) {
            int l=m+1;
            String page = String.valueOf(l);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.discover_path + Constants.api_key +
                    "&sort_by=vote_average.desc&vote_count.gte=10000&page="+page,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray results = response.getJSONArray("results");

                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject movieObj = results.getJSONObject(i);

                                    final Movie movie = new Movie();
                                    movie.setTitle(movieObj.getString("title"));
                                    movie.setImgLink(movieObj.getString("poster_path"));
                                    movie.setRating(movieObj.getDouble("vote_average"));
                                    movie.setYear("Release Date: " + movieObj.getString("release_date"));
                                    movie.setId(movieObj.getInt("id"));
                                    String id = String.valueOf(movieObj.getInt("id"));

                                    JsonObjectRequest jobj = new JsonObjectRequest(Request.Method.GET, Constants.cast + id +"?"+Constants.api_key, null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                runtime = String.valueOf(response.getInt("runtime"));
                                                movie.setOverview("Runtime: "+runtime+" mins");
                                                Log.d("inside runtime", String.valueOf(response.getInt("runtime")));
                                            }
                                            catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    });

                                    queue.add(jobj);

                                    movieList.add(movie);
                                }
                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(jsonObjectRequest);
        }



        butoni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                Log.d("movieList Size", String.valueOf(movieList.size()));
                recyclerView.smoothScrollToPosition(random.nextInt(movieList.size()));

            }
        });



        return root;
    }

}