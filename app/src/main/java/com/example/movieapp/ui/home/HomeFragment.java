package com.example.movieapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Data.MovieRecyclerAdapter;
import com.Model.Movie;
import com.Util.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.R;
import com.example.movieapp.theactivity;
import com.example.movieapp.ui.toprated.GalleryViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Movie> movieList;
    private RequestQueue queue;
    private FirebaseDatabase database;


    Context context ;

    private int n = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        ((theactivity) getActivity()).hideFab();
        queue = Volley.newRequestQueue(context);


        database = FirebaseDatabase.getInstance();

        DatabaseReference ref = database.getReference("users");




        recyclerView = (RecyclerView) root.findViewById(R.id.homeRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        movieList = new ArrayList<>();



        adapter = new MovieRecyclerAdapter(context, movieList);
        recyclerView.setAdapter(adapter);
        //movieList = getMovieList();
        //adapter.notifyDataSetChanged();
        getMovieList();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =(SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String search = query.replaceAll(" ", "+");
                //movieList = searchMovies(search);
                //adapter.notifyDataSetChanged();
                searchMovies(search);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;


            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();

        getMovieList();
        Log.d("ItemsonmovieList Resume", String.valueOf(movieList.size()));
        for(int i=0;i<movieList.size();i++){
            Movie movie = movieList.get(i);
            Log.d("Titles in on Resume", movie.getTitle());
        }
        Log.d("Home ", "Fragments is resumed");
        if(adapter == null){
            Log.d("On Resume", "Adapter is null");
        }
        else{

            Log.d("On Resume", "Adapter is not null");
        }
        //adapter.notifyDataSetChanged();
    }


    public List<Movie> getMovieList() {
        movieList.clear();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.base_url + Constants.discover + Constants.api_key, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject movieObj = results.getJSONObject(i);

                                Movie movie = new Movie();
                                movie.setTitle(movieObj.getString("title"));
                                movie.setImgLink(movieObj.getString("poster_path"));
                                movie.setRating(movieObj.getDouble("vote_average"));
                                movie.setYear(movieObj.getString("release_date"));
                                movie.setOverview(movieObj.getString("overview"));
                                movie.setId(movieObj.getInt("id"));

                                Log.d("release date", movie.getTitle());

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

        Log.d("ItemCunt getMovies",String.valueOf(movieList.size()));

        return movieList;
    }


    public List<Movie> searchMovies(String search) {
        movieList.clear();

        JsonObjectRequest searchjsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.search + search + "&" + Constants.api_key, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject movieObj = results.getJSONObject(i);

                                Movie movie = new Movie();
                                movie.setTitle(movieObj.getString("title"));
                                movie.setImgLink(movieObj.getString("poster_path"));
                                movie.setRating(movieObj.getDouble("vote_average"));
                                movie.setYear(movieObj.getString("release_date"));
                                movie.setOverview(movieObj.getString("overview"));
                                movie.setId(movieObj.getInt("id"));

                                Log.d("movie title", movieObj.getString("original_title"));

                                movieList.add(movie);

                            }
                            Log.d("items inside", String.valueOf(movieList.size()));
                            adapter.notifyDataSetChanged();
                            if(adapter.getItemCount() == 0){
                                Toast.makeText(getContext(), "No results for your search", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(searchjsonObjectRequest);
        Log.d("ItemCunt searchMovies",String.valueOf(movieList.size()));
        //adapter.notifyDataSetChanged();
        return movieList;
    }

}
