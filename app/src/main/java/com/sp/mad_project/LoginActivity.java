package com.sp.mad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword, loginUsername;
    private FirebaseAuth auth;

    Button loginButton;
    TextView signupRedirectText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Gets the EditText/Button inputs
        loginEmail = findViewById(R.id.login_email);
        loginUsername = findViewById(R.id.login_userName);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        //Redirect texts as an transition
        signupRedirectText = findViewById(R.id.signupRedirectText);

        //Firebase Authentication
        auth = FirebaseAuth.getInstance();

        //Event Listener for login button to handle all user logic and data
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateEmail() | !validatePassword() | !validateUsername()) {

                } else {
                    checkUser();
                }
            }
        });
                /*Gets the password and email from the login inputs
                String pass = loginPassword.getText().toString().trim();
                String email = loginEmail.getText().toString().trim();



                //Checks if email input is empty and matches the correct email input
                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    //Checks if password input is empty
                 if(!pass.isEmpty()) {

                     checkUser();
                     auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        //When login is successful, the toast displays a message and brings us to the main activity.
                         @Override
                        public void onSuccess(AuthResult authResult) {

                            Toast.makeText(LoginActivity.this, "Login Successfully!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() { //To handle failure of login
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login Failed :( ", Toast.LENGTH_SHORT).show();
                        }
                    });

                    } else {
                     loginPassword.setError("Password cannot be empty");
                        }
                } else if(email.isEmpty()) {
                    loginEmail.setError("Email cannot be empty");
                } else {
                    loginEmail.setError("Please enter valid email");
                }

            }

        });*/

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

     //Checks if the Username input field is empty
    public Boolean validateUsername() {
        String username = loginUsername.getText().toString();
        if (username.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    //Checks if the email input field is empty
    public Boolean validateEmail() {
        String email = loginEmail.getText().toString();
        if (email.isEmpty()) {
            loginEmail.setError("Email cannot be empty");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    //Checks if the password input field is empty
    public Boolean validatePassword(){
        String password = loginPassword.getText().toString();
        if (password.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    // Function to check if user data is in DB
    public void checkUser(){
        String userUsername = loginUsername.getText().toString().trim();
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    loginUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userPassword)) {
                        loginPassword.setError(null);

                        String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String phoneFromDB = snapshot.child(userUsername).child("phoneNumber").getValue(String.class);

                        Intent intent = new Intent(LoginActivity.this, chatGroups.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phone", phoneFromDB);
                        intent.putExtra("password", passwordFromDB);

                        startActivity(intent);


                    } else {
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("User does not exist");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}