package com.sp.mad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class SignupActivity extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPhone, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Edit Text / Button Inputs
        signupUsername = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);

        //Sign up redirect
        loginRedirectText = findViewById(R.id.loginRedirectText);

        //Firebase Authentication
        auth = FirebaseAuth.getInstance();


        //Event Listener for the Sign Up button to handle the sign up inputs.
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                //Firebase Realtime DB Initialising
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                //Get all the inputs and store them as a string
                String Username = signupUsername.getText().toString();
                String email = signupEmail.getText().toString();
                String phoneNumber = signupPhone.getText().toString();
                String password = signupPassword.getText().toString();

                //Checks if input field is empty
                if(email.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                }
                else if (Username.isEmpty()) {
                    signupUsername.setError("Username cannot be empty");
                }
                else if (password.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                }
                else if (phoneNumber.isEmpty()) {
                    signupPhone.setError("Phone number cannot be empty");
                } else {

                    //For the Firebase Realtime DB Authentication storing
                    //Stores the
                    HelperClass helperClass = new HelperClass(Username, email,phoneNumber, password);
                    reference.child(Username).setValue(helperClass);

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        //Displays a toast message to tell us if sign up was successful, and directs us to the Login page if so.
                        //Otherwise, remains on the Sign up page.
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignupActivity.this, "Sign up failed!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        });

        //Makes the text below direct us from the sign up page to login page
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}