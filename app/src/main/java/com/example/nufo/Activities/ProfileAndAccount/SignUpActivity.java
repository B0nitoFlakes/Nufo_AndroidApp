package com.example.nufo.Activities.ProfileAndAccount;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    EditText signUpEmail, signUpPassword, reEnterSignUpPassword, signUpUsername;
    TextView loginRedirectText;
    Button signUpButton;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

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

                if(email.isEmpty())
                {
                    signUpEmail.setError("It is empty");
                }
                else if(pass.isEmpty())
                {
                    signUpPassword.setError("Empty");
                }
                else if(rePass.isEmpty())
                {
                    reEnterSignUpPassword.setError("Oi empty");
                }
                else if (!pass.equals(rePass))
                {
                    signUpPassword.setError("Need to match");
                    reEnterSignUpPassword.setError("Need to Match");
                }
                else
                {
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
                                Toast.makeText(SignUpActivity.this, "Sign up failed" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
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
}