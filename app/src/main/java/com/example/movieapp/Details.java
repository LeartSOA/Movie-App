package com.example.movieapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.Util.Constants;
import com.Util.YoutubeConfig;
import com.Util.YoutubeLink;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

public class Details extends YouTubeBaseActivity {
    private String title_intent;
    private AlertDialog dialog;
    private YouTubePlayer.OnInitializedListener mYoutubeListener;
    private AlertDialog.Builder builder;
    private Context context=this;
    private ImageView imgview;
    private TextView titleV, yearV, directorV, castV, descV, genresV, runtime, ratingV;
    private String videoLink="";
    private String imageLink;
    private Button butoni, watched;
    private DatabaseReference dbref, newdbref;
    private FirebaseAuth mAuth;
    private int id;
    String nameextra;
    private FloatingActionButton fab;
    RequestQueue queue;
    YoutubeLink yt = new YoutubeLink();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupUI();
        queue = Volley.newRequestQueue(this);
        Bundle extra = getIntent().getExtras();
        id = extra.getInt("id");
        nameextra = extra.getString("name");
        fab = (FloatingActionButton) findViewById(R.id.addtoWatchlist);



        restofData();
        butoni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playViedo(id);
            }
        });


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.cast+String.valueOf(id)+
                "?"+Constants.api_key+Constants.appendix, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    String gen="";

                    imageLink = "https://image.tmdb.org/t/p/w92"+response.getString("poster_path");
                    Picasso.with(context).load(imageLink).placeholder(android.R.drawable.ic_btn_speak_now).into(imgview);
                    titleV.setText(response.getString("title"));
                    ratingV.setText("Rating: "+String.valueOf(response.getDouble("vote_average"))+"/10");
                    runtime.setText("Runtime: "+String.valueOf(response.getInt("runtime"))+" mins");

                    title_intent = response.getString("title");

                    JSONArray genres = response.getJSONArray("genres");
                    for(int i=0; i<genres.length();i++){

                        JSONObject genresObj = genres.getJSONObject(i);

                        if(i==0){
                            gen="Genres : "+genresObj.getString("name");
                        }
                        else{
                            gen= gen+", "+genresObj.getString("name");
                        }

                    }
                    genresV.setText(gen);
                    JSONObject credits = response.getJSONObject("credits");
                    JSONArray cast = credits.getJSONArray("cast");
                    String thecast = "";
                    for (int i=0; i<cast.length();i++){
                        JSONObject obj = cast.getJSONObject(i);
                        Log.d("name", obj.getString("name"));
                        if(i < 3){
                            if(i==0){
                                thecast = "Cast: "+obj.getString("name");
                            }
                            else{
                                thecast = thecast +", "+obj.getString("name");
                            }
                        }

                    }
                    castV.setText(thecast);
                    JSONArray crew = credits.getJSONArray("crew");
                    for(int i=0; i<crew.length();i++){
                        JSONObject crewObj = crew.getJSONObject(i);
                        if(crewObj.getString("job").equals("Director")){
                            directorV.setText("Director: "+crewObj.getString("name"));
                        }

                    }


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


        queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new  Intent(getApplicationContext(), Posts.class);
                intent.putExtra("title", title_intent);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fab button
                String current_user = FirebaseAuth.getInstance().getUid();
                dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user);
                dbref.child("watchlist").child(nameextra).setValue(String.valueOf(id))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        });



    }



    public void playViedo(int idlink){
        String thelink= String.valueOf(idlink);
        builder =  new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.youtube_player, null);
        final YouTubePlayerView youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.youtID);
        //Log.d("requesLink",Constants.cast + thelink + "/videos?" + Constants.api_key);
        JsonObjectRequest json = new JsonObjectRequest(Request.Method.GET, Constants.cast + thelink + "/videos?" + Constants.api_key, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray results = response.getJSONArray("results");

                            for(int i=0;i<results.length();i++){
                                final JSONObject trailerObject = results.getJSONObject(i);
                                if(i == 0) {
                                    builder.setView(view);
                                    dialog = builder.create();
                                    Log.d("VideoLink", videoLink+"char");
                                    dialog.show();

                                    mYoutubeListener = new YouTubePlayer.OnInitializedListener() {
                                        @Override
                                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                            try {
                                                Log.d("Youtube", "Succes initializing");
                                                youTubePlayer.setShowFullscreenButton(false);
                                                Log.d("link", trailerObject.getString("key"));
                                                youTubePlayer.loadVideo(trailerObject.getString("key"));
                                            }
                                            catch (Exception e){
                                                e.printStackTrace();
                                                Log.d("Error Exception", "the Exception message:"+e.getMessage());
                                                dialog.dismiss();
                                            }

                                        }

                                        @Override
                                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                                            Log.d("Youtube" , "Failed to initialize");
                                            dialog.dismiss();
                                        }
                                    };

                                    youTubePlayerView.initialize(YoutubeConfig.api_key, mYoutubeListener);

                                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {

                                        }
                                    });

                                }
                                Log.d("Video Name", trailerObject.getString("key"));
                                Log.d("inside the loop", videoLink);
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d("outside the loop", videoLink);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(json);



    }

    public void restofData(){
        String idlink = String.valueOf(id);
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, Constants.base_url + "movie/" + idlink + "?" + Constants.api_key, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    yearV.setText("Release Date: "+response.getString("release_date"));
                    descV.setText("Overview: "+ response.getString("overview"));
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
        queue.add(jsonObject);
    }

    public void setupUI() {
        imgview = (ImageView) findViewById(R.id.imgD);
        titleV = (TextView) findViewById(R.id.titleD);
        yearV = (TextView) findViewById(R.id.yearD);
        ratingV = (TextView) findViewById(R.id.ratingD);
        directorV = (TextView) findViewById(R.id.directorD);
        castV = (TextView) findViewById(R.id.castD);
        descV = (TextView) findViewById(R.id.overviewD);
        runtime = (TextView) findViewById(R.id.runtime);
        genresV = (TextView) findViewById(R.id.genresD);
        butoni = (Button) findViewById(R.id.trailerID);
        watched = (Button) findViewById(R.id.watchedButton);

    }



}
