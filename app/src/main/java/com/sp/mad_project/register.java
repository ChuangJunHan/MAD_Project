package com.sp.mad_project;

//
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

//Firebase Imports
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity {

    private databaseHelper dbHelper;
    EditText signupUsername, signupEmail, signupPhone, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new databaseHelper(this);

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


        signupButton.setOnClickListener(v -> {

            //Firebase Realtime DB Initialising
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String username = signupUsername.getText().toString();
            String email = signupEmail.getText().toString();
            String phoneNumber = signupPhone.getText().toString();
            String password = signupPassword.getText().toString();

            //Checks if any input field is empty
            if(email.isEmpty()){
                signupEmail.setError("Email cannot be empty");
            }
            else if (username.isEmpty()) {
                signupUsername.setError("Username cannot be empty");
            }
            else if (password.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
            }
            else if (phoneNumber.isEmpty()) {
                signupPhone.setError("Phone number cannot be empty");
            }else {

                //For SQL Verification and Storing of User data
                boolean isAdded = dbHelper.addUser(username, password);
                if (isAdded) {

                    HelperClass helperClass = new HelperClass(username, email,phoneNumber, password);
                    reference.child(username).setValue(helperClass);

                    //For the Firebase Authentication Storage of User data
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(register.this, login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Makes the text below direct us from the sign up page to login page
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register.this, login.class);
                startActivity(intent);
            }
        });
    }


}
