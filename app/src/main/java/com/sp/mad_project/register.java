package com.sp.mad_project;

//
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
    EditText signupUsername, signupEmail, signupPhone, signupPassword, confirmPassword;
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
        confirmPassword = findViewById(R.id.confirm_pass);
        signupButton = findViewById(R.id.signup_button);

        //Sign up redirect
        loginRedirectText = findViewById(R.id.loginRedirectText);

        //Firebase Authentication
        auth = FirebaseAuth.getInstance();


        signupButton.setOnClickListener(v -> {

            //Firebase Realtime DB Initialising
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String username = signupUsername.getText().toString().trim();
            String email = signupEmail.getText().toString().trim();
            String phoneNumber = signupPhone.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();
            String confPassword = confirmPassword.getText().toString().trim();


            // Email validation
            if (email.isEmpty()) { //Checks if empty
                signupEmail.setError("Email cannot be empty");
                return;
            }
            //Check if they are in the right format
            else if (!email.contains("@") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                signupEmail.setError("Email must contain '@' and be in valid format");
                return;
            }


            // Username validation
            //Check is username is empty
            if (username.isEmpty()) {
                signupUsername.setError("Username cannot be empty");
                return;
            }

            // Phone validation
            //Checks if phone number is empty or does not have 8 digits
            if (phoneNumber.isEmpty() || phoneNumber.length() != 8) {
                signupPhone.setError("Phone number must be exactly 8 digits");
                return;
            }

            // Password validation
            //Checks if password is empty
            if (password.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
                return;
            }
            //Check if password length greater than 8
            else if (password.length() < 8) {
                signupPassword.setError("Password must be at least 8 characters");
                return;
            }
            //Check if password has at least 1 special character
            else if (!password.matches(".*[!@#$%^&*()_+={}:;'<>,.?/~].*")) {
                signupPassword.setError("Password must contain at least 1 special character");
                return;
            }

            // Confirm Password validation
            //Checks if confirm password input is empty
            if (confPassword.isEmpty()) {
                confirmPassword.setError("Confirm Password cannot be empty");
                return;
            }
            //Check if password entered equals to confirm password entered
            else if (!password.equals(confPassword)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

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
