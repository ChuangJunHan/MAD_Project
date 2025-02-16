package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.widget.Toast;

//Firebase Imports
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

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private databaseHelper dbHelper;
    EditText loginEmail, loginPassword, loginUsername;
    private FirebaseAuth auth;

    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new databaseHelper(this);

        loginEmail = findViewById(R.id.login_email);
        loginUsername = findViewById(R.id.login_userName);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        signupRedirectText = findViewById(R.id.signupRedirectText);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {

            String username = loginUsername.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            String userUsername = loginUsername.getText().toString().trim();
            String userEmail = loginEmail.getText().toString().trim();
            String userPassword = loginPassword.getText().toString().trim();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

            if (!validateEmail() | !validatePassword() | !validateUsername()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                boolean isAuthenticated = dbHelper.authenticateUser(username, password);
                if (isAuthenticated) {

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

                                    Intent intent = new Intent(login.this, homePage.class);
                                    intent.putExtra("name", nameFromDB);
                                    intent.putExtra("email", emailFromDB);
                                    intent.putExtra("phone", phoneFromDB);
                                    intent.putExtra("password", passwordFromDB);
                                    intent.putExtra("loggedInUser", username);

                                    startActivity(intent);
                                    finish();

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

                    Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                }

            }

        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        });
    }

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
}
