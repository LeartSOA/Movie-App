package com.example.movieapp.ui.Watchlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Data.MovieRecyclerAdapter;
import com.Data.WatchlistRecyclerAdapter;
import com.Model.MovieWatchlist;
import com.Util.Constants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class WatchlistFragment extends Fragment {

    private List<MovieWatchlist> movieWatchlists;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference dbref;
    private RequestQueue queue;
    private String[] titles;
    int f = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        final ArrayList<String> title = new ArrayList<String>();
        queue = Volley.newRequestQueue(getContext());
        recyclerView = (RecyclerView) root.findViewById(R.id.watchlistRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        movieWatchlists = new ArrayList<>();
        adapter = new WatchlistRecyclerAdapter( getContext() , movieWatchlists );
        recyclerView.setAdapter(adapter);

        String current_user = FirebaseAuth.getInstance().getUid();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user).child("watchlist");
        dbref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                int i = 0;

                for(final DataSnapshot data : dataSnapshot.getChildren()){
                    title.add(data.getKey());

                }
                //movieWatchlists.clear();
                for(int n=0; n<title.size(); n++){
                    f = n;

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.cast + dataSnapshot.child(title.get(n)).getValue()+"?" + Constants.api_key,
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                MovieWatchlist movie = new MovieWatchlist();
                                movie.setId(Integer.parseInt((String) dataSnapshot.child(title.get(f)).getValue()));
                                Log.d("Value of f", String.valueOf(f));
                                movie.setRating(response.getDouble("vote_average"));
                                movie.setRuntime(String.valueOf(response.getInt("runtime")));
                                movie.setImgLink(response.getString("poster_path"));
                                movie.setTitle(response.getString("title"));
                                movie.setYear(response.getString("release_date"));

                                Log.d("title watchlist", response.getString("title"));

                                boolean does_exist = exists(response.getString("title"));

                                if(!does_exist){
                                    movieWatchlists.add(movie);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }

                    });
                    queue.add(jsonObjectRequest);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

    private Boolean exists(String title){

        for(int i=0; i<movieWatchlists.size(); i++){
            if(movieWatchlists.get(i).getTitle().equals(title)){
                return true;
            }
        }

        return false;

    }


}