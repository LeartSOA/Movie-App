package com.example.movieapp.ui.toprated;

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
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class TopRatedFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Movie> movieList;
    private RequestQueue queue;
    private Context context;




    private int n = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_top_rated, container, false);

        context = getActivity().getApplicationContext();

        context = getActivity().getApplicationContext();
        ((theactivity) getActivity()).hideFab();
        queue = Volley.newRequestQueue(context);



        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        movieList = new ArrayList<>();


        movieList = getMovieList();
        adapter = new MovieRecyclerAdapter(context, movieList);
        recyclerView.setAdapter(adapter);
        movieList = getMovieList();
        adapter.notifyDataSetChanged();



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

        movieList = getMovieList();

    }

    public List<Movie> getMovieList() {
        movieList.clear();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.discover_path+Constants.api_key+"&sort_by=vote_average.desc&vote_count.gte=10000",
                null,
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

                                Log.d("release date", movieObj.getString("release_date"));

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
        return movieList;
    }

}




