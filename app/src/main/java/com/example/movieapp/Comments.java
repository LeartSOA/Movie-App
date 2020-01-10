package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Data.CommentRecyclerAdapter;
import com.Model.CommentsModel;
import com.google.android.gms.common.data.DataBuffer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comments extends AppCompatActivity {

    private CircleImageView userprofieIMg;
    private EditText commentEdit;
    private Button addComment;
    private ProgressDialog progressDialog;
    private DatabaseReference dbref ;
    private Context context = this;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CommentsModel> commentsModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        commentsModels = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.commentsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommentRecyclerAdapter(context, commentsModels);
        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(context);

        Bundle results = getIntent().getExtras();
        String movie_title = results.getString("title");
        String uid_intent = results.getString("uid");
        final int post_id = results.getInt("adapterPosition");

        dbref = FirebaseDatabase.getInstance().getReference().child("Posts").child(movie_title).child("posts");
        final DatabaseReference profile = FirebaseDatabase.getInstance().getReference().child("Users").child(uid_intent);

        profile_pic();


        userprofieIMg = (CircleImageView) findViewById(R.id.profileCommentImage);
        commentEdit = (EditText) findViewById(R.id.editComment);
        addComment = (Button) findViewById(R.id.addCommentButton);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentEdit.getText().toString() != null) {
                    progressDialog.setMessage("Posting Comment");
                    progressDialog.show();
                    profile.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String imageLink = dataSnapshot.child("imageLink").getValue().toString();
                            final String name_user = dataSnapshot.child("name").getValue().toString();

                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<String> lista = new ArrayList<>();
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        lista.add(data.getKey());
                                    }

                                    DatabaseReference newdb = dbref.child(lista.get(post_id)).child("Comments").push();
                                    Map values = new HashMap();
                                    values.put("name", name_user);
                                    values.put("profile_pic", imageLink);
                                    values.put("commentDesc", commentEdit.getText().toString());
                                    values.put("timestamp", java.lang.System.currentTimeMillis());
                                    values.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                                    newdb.setValue(values);
                                    progressDialog.dismiss();
                                    commentEdit.getText().clear();
                                    adapter.notifyDataSetChanged();


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        if(progressDialog.isShowing()){
            progressDialog.dismiss();
            Toast.makeText(context, "Posting Comment failed", Toast.LENGTH_SHORT).show();
        }

        final DatabaseReference getComments = dbref;

        getComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    keys.add(data.getKey());
                }
                DatabaseReference closer = getComments.child(keys.get(post_id)).child("Comments");
                closer.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentsModels.clear();
                        for (DataSnapshot thedata : dataSnapshot.getChildren()){
                            CommentsModel comments = new CommentsModel();
                            comments.setDate(thedata.child("timestamp").getValue().toString());
                            comments.setName(thedata.child("name").getValue().toString());
                            comments.setDescription(thedata.child("commentDesc").getValue().toString());
                            comments.setUid(thedata.child("user_id").getValue().toString());
                            comments.setImgLink(thedata.child("profile_pic").getValue().toString());
                            commentsModels.add(comments);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    private void profile_pic() {
        final DatabaseReference profilepic = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        profilepic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.with(context).load(dataSnapshot.child("imageLink").getValue().toString()).placeholder(R.drawable.theuser).into(userprofieIMg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
