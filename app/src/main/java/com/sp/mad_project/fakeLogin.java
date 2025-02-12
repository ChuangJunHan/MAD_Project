package com.sp.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class fakeLogin extends AppCompatActivity {

    private ListView userListView;
    private List<String> fakeUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_login);

        userListView = findViewById(R.id.userListView);

        // Predefined fake users
        fakeUsers = new ArrayList<>();
        fakeUsers.add("User1");
        fakeUsers.add("User2");
        fakeUsers.add("User3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fakeUsers);
        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = fakeUsers.get(position);

            // Pass the selected user to the chatGroups activity
            Intent intent = new Intent(fakeLogin.this, chatGroups.class);
            intent.putExtra("loggedInUser", selectedUser);
            Toast.makeText(this, "Logged in as " + selectedUser, Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish(); // Finish the login page
        });
    }
}
