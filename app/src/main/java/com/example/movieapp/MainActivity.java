package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Util.NetworkAccess;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText emailEdit;
    private EditText passwordEdit;
    private FirebaseAuth mAuth;


    private FirebaseAuth.AuthStateListener mAuthListener;

    private NetworkAccess connection;

    private AlertDialog.Builder builder;

    private Context context= this;

    private TextView clickButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        NetworkAccess conn = new NetworkAccess();
            if(!conn.Connected(this)){

                builder = new AlertDialog.Builder(this);

                builder.setTitle("No Internet Connection");
                builder.setMessage("You're not connected to the internet, an internet connection is essential to this app. Click 'OK' to exit");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.create().show();


        }




        emailEdit = (EditText) findViewById(R.id.nameField);
        passwordEdit = (EditText) findViewById(R.id.passwordField);
        clickButton = (TextView) findViewById(R.id.clickButton);


        button = (Button) findViewById(R.id.signinButton);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(user != null){
                        Toast.makeText(MainActivity.this, "You are already logged in", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(MainActivity.this, theactivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please log in", Toast.LENGTH_SHORT).show();
                    }
            }
        };


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if(email.isEmpty()){
                    emailEdit.setError("Enter email address");
                    emailEdit.requestFocus();
                }
                else if(password.isEmpty()){
                    passwordEdit.setError("Enter password");
                    passwordEdit.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_LONG).show();
                }
                else if(!(email.isEmpty() && password.isEmpty())){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Sign in error please try again", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Signed in succesfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, theactivity.class));
                                    finish();
                                }
                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Sign_Up.class));
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();

        connection = new NetworkAccess();

        if(!connection.Connected(this)){

        }
        else{

            mAuth.addAuthStateListener(mAuthListener);
        }



    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
