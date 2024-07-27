package com.example.nufo.Activities.ProfileAndAccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nufo.Helpers.AccountHelperClass;
import com.example.nufo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private EditText signUpEmail, signUpPassword, reEnterSignUpPassword, signUpUsername;
    private TextView loginRedirectText;
    private Button signUpButton;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpUsername = findViewById(R.id.signUpUsername);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        reEnterSignUpPassword =findViewById(R.id.reEnterSignUpPassword);
        signUpButton = findViewById(R.id.buttonSignUp);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String username = signUpUsername.getText().toString();
                String email = signUpEmail.getText().toString();
                String pass = signUpPassword.getText().toString();
                String rePass = reEnterSignUpPassword.getText().toString();

                inputValidation(username, email, pass, rePass);
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkCredentialsExist(String username, String email, String pass) {
        reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    signUpUsername.setError("Username already exists");
                    signUpUsername.requestFocus();
                } else {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String uid = auth.getCurrentUser().getUid();
                                AccountHelperClass accountHelperClass = new AccountHelperClass(username, email);
                                reference.child(uid).setValue(accountHelperClass);

                                Toast.makeText(SignUpActivity.this, "You have sign up successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, YourInformationActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                signUpEmail.setError("Email Has been used by another account");
                                signUpEmail.requestFocus();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignUpActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inputValidation(String username, String email, String pass, String rePass)
    {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(pass);

        if(email.isEmpty())
        {
            signUpEmail.setError("It is empty");
            signUpEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signUpEmail.setError("Incorrect email format");
            signUpEmail.requestFocus();
        } else if(pass.isEmpty())
        {
            signUpPassword.setError("Password is empty");
            signUpPassword.requestFocus();
        }
        else if(rePass.isEmpty())
        {
            reEnterSignUpPassword.setError("Reenter Password is empty");
            reEnterSignUpPassword.requestFocus();
        }
        else if(!matcher.matches())
        {
            signUpPassword.setError("Password must be at least 6 characters long, contain at least one uppercase letter, one lowercase letter, one special character, and one number");
            signUpPassword.requestFocus();
            reEnterSignUpPassword.setError("Password must be at least 6 characters long, contain at least one uppercase letter, one lowercase letter, one special character, and one number");
            reEnterSignUpPassword.requestFocus();
        }
        else if (!pass.equals(rePass))
        {
            signUpPassword.setError("Need to match");
            signUpPassword.requestFocus();
            reEnterSignUpPassword.setError("Need to Match");
            reEnterSignUpPassword.requestFocus();
        }
        else
        {
            checkCredentialsExist(username,email, pass);
        }
    }

}