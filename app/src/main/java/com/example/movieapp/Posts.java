package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.Data.PostsRecyclerAdapter;
import com.Model.MoviePosts;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Posts extends AppCompatActivity {
    private Button addButton;
    private String title_intent;
    private DatabaseReference dbref;
    private List<MoviePosts> moviePosts;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Context context = this;
    private String thename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.containsKey("title")){
                if(extras.getBoolean("title", false)){
                    Log.d("Not null", "True");
                }
                else{
                    Log.d("Not null", "False");
                }
                title_intent = extras.getString("title");
                this.setTitle(title_intent);

            }
        }

        moviePosts = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.postsrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new PostsRecyclerAdapter(getApplicationContext(), moviePosts);
        recyclerView.setAdapter(adapter);


        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewPost.class);
                intent.putExtra("title", title_intent);
                startActivity(intent);

            }
        });
        Log.d("TitleIntent", title_intent);
        dbref = FirebaseDatabase.getInstance().getReference().child("Posts").child(title_intent).child("posts");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    final MoviePosts mp = new MoviePosts();


                    //mp.setName(getUsername(data.child("user_id").getValue().toString()));
                    DatabaseReference username = FirebaseDatabase.getInstance().getReference().child("Users").child(data.child("user_id").getValue().toString());
                    username.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mp.setName(dataSnapshot.child("name").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mp.setDate(data.child("timestamp").getValue().toString());
                    mp.setMovie_title(title_intent);
                    mp.setDesc(data.child("description").getValue().toString());
                    mp.setNumOfLikes(String.valueOf(data.child("Likes").getChildrenCount()));
                    mp.setNumOfComments(String.valueOf(data.child("Comments").getChildrenCount()));
                    mp.setId(data.child("user_id").getValue().toString());
                    mp.setImglink(data.child("img_download").getValue().toString());
                    mp.setPost_id(data.getKey());


                    moviePosts.add(mp);

                }
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount() == 0){
                    Toast.makeText(context, "No posts about this movie", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();

    }
}
