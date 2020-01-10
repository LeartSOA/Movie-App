package com.example.movieapp.ui.Upcoming;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpcomingFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Movie> movieList;
    private RequestQueue queue;


    Context context ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_upcoming, container, false);




        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        ((theactivity) getActivity()).hideFab();
        queue = Volley.newRequestQueue(context);


        recyclerView = (RecyclerView) root.findViewById(R.id.upcomingRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        movieList = new ArrayList<>();



        adapter = new MovieRecyclerAdapter(context, movieList);
        recyclerView.setAdapter(adapter);
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

    }

    public List<Movie> getMovieList() {
        movieList.clear();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String formated_date = formatter.format(date);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.discover_path+ Constants.api_key+
                "&primary_release_date.gte="+formated_date+"&sort_by=popularity.desc", null,
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
        queue.add(searchjsonObjectRequest);

        return movieList;
    }


}