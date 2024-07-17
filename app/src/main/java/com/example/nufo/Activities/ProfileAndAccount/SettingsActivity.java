package com.example.nufo.Activities.ProfileAndAccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.nufo.Activities.MainActivity;
import com.example.nufo.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    CardView cardView_profile, cardView_account;
    Button button_logout, buttonHome_settingsNav;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        cardView_profile = findViewById(R.id.cardView_profile);
        cardView_account = findViewById(R.id.cardView_account);
        button_logout = findViewById(R.id.button_logout);
        buttonHome_settingsNav = findViewById(R.id.buttonHome_settingsNav);

        auth = FirebaseAuth.getInstance();



        cardView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        cardView_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent= new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(SettingsActivity.this, "Successful logout", Toast.LENGTH_SHORT).show();
            }
        });

        buttonHome_settingsNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}