package com.Data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Model.Movie;
import com.Model.MoviePosts;
import com.example.movieapp.Comments;
import com.example.movieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<MoviePosts> moviePosts;
    private DatabaseReference dbref, likeref;
    long numofLikes, numOfComments;
    private Boolean liked;
    private int q=0;

    public PostsRecyclerAdapter(Context context, List<MoviePosts> moviePosts) {
        this.context = context;
        this.moviePosts = moviePosts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);

        return new ViewHolder(view, context);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final MoviePosts posts = moviePosts.get(position);



        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(posts.getDate())).getTime());
        holder.date_added.setText(formattedDate);
        holder.description.setText(posts.getDesc());
        holder.numOfComments.setText(posts.getNumOfComments());

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Posts").child(posts.getMovie_title()).child("posts");

        holder.likebutton.setTag("like");


        Log.d("post_id value","Value : "+ posts.getPost_id());




        final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(posts.getMovie_title()).child("posts").child(posts.getPost_id()).child("Likes");
        DatabaseReference constant = likeRef;

        constant.child("constant").setValue(true);
        final DatabaseReference getLikes = likeRef;

            getLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        holder.likebutton.setBackgroundResource(R.drawable.like);
                        holder.numOfLikes.setText(String.valueOf(dataSnapshot.getChildrenCount() - 1));

                    } else {
                        holder.likebutton.setBackgroundResource(R.drawable.heart);
                        holder.numOfLikes.setText(String.valueOf(dataSnapshot.getChildrenCount() - 1));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        holder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                liked = true;

                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(liked.equals(true)){
                            if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                holder.likebutton.setBackgroundResource(R.drawable.heart);
                                holder.numOfLikes.setText(String.valueOf(dataSnapshot.getChildrenCount()-2));
                                liked = false;
                                Log.d("Inside the method", "addlike");
                            }
                            else{
                                likeRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                liked = false;
                                holder.likebutton.setBackgroundResource(R.drawable.like);
                                holder.numOfLikes.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                                Log.d("Inside the method", "removelike");

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //holder.setLikesCount(posts.getPost_id(), posts.getMovie_title());

        holder.commentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Comments.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("title", posts.getMovie_title());
                intent.putExtra("uid", posts.getId());
                intent.putExtra("adapterPosition", position);
                context.startActivity(intent);
            }
        });


        if (posts.getImglink().equals("null")) {
            holder.post_img.getLayoutParams().height = 0;
        } else {
            holder.post_img.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Picasso.with(context).load(posts.getImglink()).into(holder.post_img);
        }

        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getId());
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profile_picture = dataSnapshot.child("imageLink").getValue().toString();
                holder.name.setText(dataSnapshot.child("name").getValue().toString());
                Picasso.with(context)
                        .load(profile_picture)
                        .placeholder(R.drawable.theuser)
                        .into(holder.profile_pic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("number of likes outside", String.valueOf(numofLikes));
        //posts.setNumOfLikes(String.valueOf(numofLikes));


    }


    @Override
    public int getItemCount() {
        return moviePosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profile_pic;
        private TextView name, date_added, description, numOfLikes, numOfComments;
        private ImageView post_img;
        private Button likebutton, commentbutton;
        private String countlike;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            name = (TextView) itemView.findViewById(R.id.namePost);
            date_added = (TextView) itemView.findViewById(R.id.dateAdded);
            description = (TextView) itemView.findViewById(R.id.opinion);
            numOfComments = (TextView) itemView.findViewById(R.id.commentsNum);
            numOfLikes = (TextView) itemView.findViewById(R.id.numberofLikes);
            profile_pic = (CircleImageView) itemView.findViewById(R.id.postusrImg);
            post_img = (ImageView) itemView.findViewById(R.id.PostImg);
            likebutton = (Button) itemView.findViewById(R.id.like);
            commentbutton = (Button) itemView.findViewById(R.id.comments);


        }


        public void setLikesCount(final String post_id, final String movie_title) {

            DatabaseReference likes = FirebaseDatabase.getInstance().getReference().child("Posts").child(movie_title).child("posts").child(post_id).child("Likes");

            likes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        if(data.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            likebutton.setBackgroundResource(R.drawable.like);
                            Log.d("Inside the method", "like");
                            countlike = String.valueOf(dataSnapshot.getChildrenCount()-1);
                            numOfLikes.setText(countlike);

                        }
                        else{
                            likebutton.setBackgroundResource(R.drawable.heart);
                            countlike = String.valueOf(dataSnapshot.getChildrenCount()-1);
                            Log.d("Inside the method", "dislike");
                            numOfLikes.setText(countlike);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }







}
