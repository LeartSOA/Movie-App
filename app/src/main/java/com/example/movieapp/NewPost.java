package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class NewPost extends AppCompatActivity {

    private EditText descriptionET;
    private StorageReference img_post;
    private DatabaseReference dbref;
    private Button post_button;
    private Uri imgUri;
    private ImageView imgView;
    private String title_get;
    private ProgressDialog progressDialog;
    private int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Bundle extra = getIntent().getExtras();
        title_get = extra.getString("title");

        progressDialog = new ProgressDialog(this);


        dbref = FirebaseDatabase.getInstance().getReference().child("Posts").child(title_get).child("posts");
        img_post = FirebaseStorage.getInstance().getReference().child("Post Images").child(title_get);

        descriptionET = (EditText) findViewById(R.id.postDesc);
        post_button = (Button) findViewById(R.id.post_button);
        imgView = (ImageView) findViewById(R.id.post_img);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryintent, "Select a photo"), PICK_IMAGE);
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = descriptionET.getText().toString();
                final String Likes = "0";
                final String comments = "0";
                progressDialog.setMessage("Posting");
                progressDialog.show();
                if(!description.isEmpty() && imgUri != null){
                    final StorageReference filepath = img_post.child("Posts_Images").child(imgUri.getLastPathSegment());
                    filepath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    Uri downloaduri = uri;


                                    DatabaseReference newPost = dbref.push();
                                    Map newValues = new HashMap();
                                    newValues.put("description", description);
                                    newValues.put("Likes", Likes);
                                    newValues.put("Comments", comments);
                                    newValues.put("img_download", downloaduri.toString());
                                    newValues.put("user_id", user_id);
                                    newValues.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));

                                    newPost.setValue(newValues);
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Posted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Posts.class);
                                    intent.putExtra("title", title_get);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                }
                else if(imgUri == null){

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference newPost = dbref.push();
                    Map newValues = new HashMap();
                    newValues.put("description", description);
                    newValues.put("Likes", Likes);
                    newValues.put("Comments", comments);
                    newValues.put("img_download", "null");
                    newValues.put("user_id", user_id);
                    newValues.put("timestamp", String.valueOf(java.lang.System.currentTimeMillis()));

                    newPost.setValue(newValues);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Posted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Posts.class);
                    intent.putExtra("title", title_get);
                    startActivity(intent);
                    finish();


                }
                else{
                    Toast.makeText(getApplicationContext(), "One or more fields are null", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
                imgUri = data.getData();
                imgView.setImageURI(imgUri);
            }
    }
}
