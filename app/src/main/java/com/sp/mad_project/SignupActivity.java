package com.sp.mad_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class SignupActivity extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPhone, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsername = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){


                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String Username = signupUsername.getText().toString();
                String email = signupEmail.getText().toString();
                String phoneNumber = signupPhone.getText().toString();
                String password = signupPassword.getText().toString();

                HelperClass helperClass = new HelperClass(Username, email,phoneNumber, password);
                reference.child(Username).setValue(helperClass);

                Toast.makeText(SignupActivity.this, "You have successfully signed up!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);



            }

        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}