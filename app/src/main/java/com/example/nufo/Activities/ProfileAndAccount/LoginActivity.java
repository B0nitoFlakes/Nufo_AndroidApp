package com.example.nufo.Activities.ProfileAndAccount;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nufo.Activities.FoodDiary.FoodDiaryDetailsActivity;
import com.example.nufo.Activities.MainActivity;
import com.example.nufo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText loginEmail, loginPassword, editText_email;
    TextView redirectSignUpText, textView_login_forgotPassword;
    Button loginButton, resetButton, cancelButton;
    Dialog forgetPassDialog;

    FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.LoginEmail);
        loginPassword = findViewById(R.id.LoginPassword);
        redirectSignUpText = findViewById(R.id.SignUpRedirectText);
        loginButton = findViewById(R.id.buttonLogin);
        textView_login_forgotPassword = findViewById(R.id.textView_login_forgotPassword);

        forgetPassDialog = new Dialog(LoginActivity.this);
        forgetPassDialog.setContentView(R.layout.reset_password);
        forgetPassDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        forgetPassDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        forgetPassDialog.setCancelable(true);

        resetButton = forgetPassDialog.findViewById(R.id.button_reset);
        cancelButton = forgetPassDialog.findViewById(R.id.button_cancel);
        editText_email = forgetPassDialog.findViewById(R.id.editText_email);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail() | !validatePassword()) {

                }
                else
                {
                    checkUser();
                }
            }
        });

        redirectSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        textView_login_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassDialog.show();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmailReset = editText_email.getText().toString();

                if(TextUtils.isEmpty(userEmailReset) && Patterns.EMAIL_ADDRESS.matcher(userEmailReset).matches())
                {
                    Toast.makeText(LoginActivity.this, "Enter your registed user email", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.sendPasswordResetEmail(userEmailReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                            forgetPassDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Unable to send, Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassDialog.dismiss();
            }
        });

    }

    public Boolean validateEmail()
    {
        String val = loginEmail.getText().toString();
        if(val.isEmpty())
        {
            loginEmail.setError("Email cannot be empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(val).matches())
        {
            loginEmail.setError("Incorrect email format");
            return false;
        }
        else
        {
            loginEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword()
    {
        String val = loginPassword.getText().toString();
        if(val.isEmpty())
        {
            loginPassword.setError("Password cannot be empty");
            return false;
        }
        else
        {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser()
    {
        String userEmail = loginEmail.getText().toString().trim();
        String userPass = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot userSnapshot : snapshot.getChildren())
                    {
                        String email = userSnapshot.child("email").getValue(String.class);

                            auth.signInWithEmailAndPassword(email, userPass).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "Login successful for email: " + email);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    loginPassword.setError("Incorrect Password");
                                    loginPassword.requestFocus();
                                    Log.d("TAG", "Login failed: " + task.getException().getMessage());
                                    Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }
                else {
                    loginEmail.setError("User did not exist");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}