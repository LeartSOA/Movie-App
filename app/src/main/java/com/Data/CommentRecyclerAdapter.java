package com.Data;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Model.CommentsModel;
import com.example.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<CommentsModel> commentsModels;


    public CommentRecyclerAdapter(Context context, List<CommentsModel> commentsModels) {
        this.context = context;
        this.commentsModels = commentsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent , false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentsModel comments = commentsModels.get(position);

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(comments.getDate())).getTime());

        holder.descriptionV.setText(comments.getDescription());
        holder.nameV.setText(comments.getName());
        holder.dateV.setText(formattedDate);

        Picasso.with(context)
                .load(comments.getImgLink())
                .placeholder(R.drawable.theuser)
                .into(holder.profileimg);
    }

    @Override
    public int getItemCount() {
        return commentsModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileimg;
        TextView nameV, descriptionV, dateV;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            nameV = (TextView) itemView.findViewById(R.id.nameComment);
            descriptionV = (TextView) itemView.findViewById(R.id.DescriptionComment);
            dateV = (TextView) itemView.findViewById(R.id.dateComment);
            profileimg  = (CircleImageView) itemView.findViewById(R.id.userImgComment);
        }
    }
}
