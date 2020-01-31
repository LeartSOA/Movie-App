package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class Sign_Up extends AppCompatActivity {
    private EditText nameE, emailE, passE;
    private Button signUpButton;
    private ImageView img;
    private FirebaseAuth mAuth;
    private StorageReference mFirebaseStorage;
    String name,email,password;
    private static final int GALLERY_CODE = 1;
    Uri resultUri = null;
    Uri downloadLink = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);
        setUpUI();

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("Users_profile_pictures");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                name = nameE.getText().toString();
                email = emailE.getText().toString();
                password = passE.getText().toString();
                progressDialog.setMessage("Creating Account");
                progressDialog.show();



                if(!(name.isEmpty() || email.isEmpty() || password.isEmpty())){



                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Sign_Up.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Some error during sign up",Toast.LENGTH_LONG).show();
                                }
                                else {

                                     final StorageReference imagePath = mFirebaseStorage.child("Users_profile_pictures")
                                            .child(resultUri.getLastPathSegment());

                                    imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                            imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String user_id = mAuth.getCurrentUser().getUid();
                                                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                                    Map newPost = new HashMap();
                                                    newPost.put("name", name);
                                                    newPost.put("email", email);
                                                    newPost.put("imageLink", uri.toString());


                                                    Log.d("name in new Post", newPost.get("name").toString());

                                                    dbref.setValue(newPost);
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Successfully created",Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(getApplicationContext(), theactivity.class));
                                                    finish();
                                                }
                                            });

                                            progressDialog.dismiss();


                                        }
                                    });



                                }
                            }
                        });



                }
                else {

                    Toast.makeText(getApplicationContext(), "One or more fields are not filled",Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK);
        {
            if (data.getData() != null) {
                Uri mImageUri = data.getData();

                CropImage.activity(mImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
            else {
                //Toast.makeText(getApplicationContext(), "Error data is null", Toast.LENGTH_LONG).show();
                Log.d("Value is null", "Yes it is");
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                img.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void setUpUI(){
        nameE = (EditText) findViewById(R.id.nameSU);
        emailE = (EditText) findViewById(R.id.emailSU);
        passE = (EditText) findViewById(R.id.passwordSU);
        signUpButton = (Button) findViewById(R.id.signupSU);
        img = (ImageView) findViewById(R.id.profileSU);
    }
}

