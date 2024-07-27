package com.example.nufo.Activities.ProfileAndAccount;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nufo.Activities.MainActivity;
import com.example.nufo.Helpers.AccountHelperClass;
import com.example.nufo.Helpers.DiaryHelperClass;
import com.example.nufo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

public class AccountActivity extends AppCompatActivity {
    private String uid;
    private TextView textView_account_email, textView_account_username, textView_dialog_oldEmail, textView_dialog_oldEmail1;
    private EditText editText_dialog_email, editText_dialog_password, editText_dialog_password1, editText_dialog_username, editText_dialog_newPassword;
    private Button button_account_changePassword, button_account_changeUsername, button_account_changeEmail, button_dialog_updateEmail, button_dialog_authenticate, button_dialog_updateUsername, button_dialog_updatePassword, button_dialog_authenticate1, buttonHome_account;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference reference, usersRef;
    private Dialog updateEmailDialog, updateUserNameDialog, updatePasswordDialog;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        findViewsAndDialogs();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        reference = database.getReference("users").child(uid);
        user = auth.getCurrentUser();

        userAccountCredentials();

        changeCredentialsButtons();

        emptyDialogText();

    }
    private  void findViewsAndDialogs()
    {
        textView_account_email = findViewById(R.id.textView_account_email);
        textView_account_username = findViewById(R.id.textView_account_username);

        button_account_changePassword = findViewById(R.id.button_account_changePassword);
        button_account_changeUsername = findViewById(R.id.button_account_changeUsername);
        button_account_changeEmail = findViewById(R.id.button_account_changeEmail);
        buttonHome_account = findViewById(R.id.buttonHome_account);

        updateEmailDialog = new Dialog(AccountActivity.this);
        updateEmailDialog.setContentView(R.layout.update_email_dialog);
        updateEmailDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        updateEmailDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        updateEmailDialog.setCancelable(true);

        button_dialog_updateEmail = updateEmailDialog.findViewById(R.id.button_dialog_updateEmail);
        button_dialog_authenticate = updateEmailDialog.findViewById(R.id.button_dialog_authenticate);
        textView_dialog_oldEmail = updateEmailDialog.findViewById(R.id.textView_dialog_oldEmail);
        editText_dialog_email = updateEmailDialog.findViewById(R.id.editText_dialog_email);
        editText_dialog_password = updateEmailDialog.findViewById(R.id.editText_dialog_password);

        updateUserNameDialog = new Dialog(AccountActivity.this);
        updateUserNameDialog.setContentView(R.layout.update_username_dialog);
        updateUserNameDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        updateUserNameDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        updateUserNameDialog.setCancelable(true);

        button_dialog_updateUsername = updateUserNameDialog.findViewById(R.id.button_dialog_updateUsername);
        editText_dialog_username = updateUserNameDialog.findViewById(R.id.editText_dialog_username);

        updatePasswordDialog = new Dialog(AccountActivity.this);
        updatePasswordDialog.setContentView(R.layout.update_password_dialog);
        updatePasswordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        updatePasswordDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.log_dialog_bg));
        updatePasswordDialog.setCancelable(true);

        button_dialog_updatePassword = updatePasswordDialog.findViewById(R.id.button_dialog_updatePassword);
        editText_dialog_newPassword = updatePasswordDialog.findViewById(R.id.editText_dialog_newPassword);
        editText_dialog_password1 = updatePasswordDialog.findViewById(R.id.editText_dialog_password1);
        textView_dialog_oldEmail1 = updatePasswordDialog.findViewById(R.id.textView_dialog_oldEmail1);
        button_dialog_authenticate1 = updatePasswordDialog.findViewById(R.id.button_dialog_authenticate1);

    }
    private void userAccountCredentials()
    {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AccountHelperClass accountHelperClass = dataSnapshot.getValue(AccountHelperClass.class);
                String email = accountHelperClass.getEmail();
                String username = accountHelperClass.getUsername();

                textView_account_email.setText(email);
                textView_account_username.setText(username);
                textView_dialog_oldEmail.setText(email);
                textView_dialog_oldEmail1.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void updateEmailInDatabase(String newEmail)
    {
        reference.child("email").setValue(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AccountActivity.this, "Email updated in database", Toast.LENGTH_SHORT).show();
                textView_account_email.setText(newEmail);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this, "Failed to update email in database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void emptyDialogText()
    {
        updateEmailDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                editText_dialog_email.setText("");
                editText_dialog_password.setText("");
            }
        });

        updateUserNameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                editText_dialog_username.setText("");
            }
        });

        updatePasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                editText_dialog_password1.setText("");
                editText_dialog_newPassword.setText("");
            }
        });
    }
    private void changeCredentialsButtons()
    {
        button_account_changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEmailDialog.show();
            }
        });

        button_dialog_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldEmail = textView_dialog_oldEmail.getText().toString();
                String password = editText_dialog_password.getText().toString();

                if (oldEmail.isEmpty()) {
                    textView_dialog_oldEmail.setError("Email is empty");
                    textView_dialog_oldEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editText_dialog_password.setError("Password is empty");
                    editText_dialog_password.requestFocus();
                    return;
                }

                AuthCredential authCredential = EmailAuthProvider.getCredential(oldEmail, password);

                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AccountActivity.this, "User Authenticated, can proceed to update email", Toast.LENGTH_SHORT).show();

                        button_dialog_updateEmail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String updateEmail = editText_dialog_email.getText().toString();
                                if(updateEmail.isEmpty())
                                {
                                    editText_dialog_email.setError("It is empty");
                                    editText_dialog_email.requestFocus();

                                } else if (!Patterns.EMAIL_ADDRESS.matcher(updateEmail).matches()) {
                                    editText_dialog_email.setError("Incorrect email format");
                                    editText_dialog_email.requestFocus();
                                }
                                else
                                {
                                    user.verifyBeforeUpdateEmail(updateEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()) {
                                                Toast.makeText(AccountActivity.this, "Check Email for Verification", Toast.LENGTH_SHORT).show();
                                                updateEmailInDatabase(updateEmail);
                                                updateEmailDialog.dismiss();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("error message", e.getMessage());
                                            updateEmailDialog.dismiss();
                                        }
                                    });
                                }

                            }
                        });
                    }
                });
            }
        });

        button_account_changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserNameDialog.show();
            }
        });

        button_dialog_updateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText_dialog_username.getText().toString();
                if (username.isEmpty()) {
                    editText_dialog_username.setError("Please Enter a Username");
                    editText_dialog_username.requestFocus();
                    return;
                }
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    boolean usernameExists = false;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                        {
                            String existingUsername = userSnapshot.child("username").getValue(String.class);
                            if(existingUsername != null && existingUsername.equals(username))
                            {
                                usernameExists = true;
                                break;
                            }
                        }
                        if(usernameExists)
                        {
                            editText_dialog_username.setError("This username is already taken");
                            editText_dialog_username.requestFocus();
                        }
                        else
                        {
                            reference.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isComplete())
                                    {
                                        Toast.makeText(AccountActivity.this, "Username Updated", Toast.LENGTH_SHORT).show();
                                        updateUserNameDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    textView_account_username.setText(username);
                                    updateUserNameDialog.dismiss();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        button_account_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePasswordDialog.show();
            }
        });

        button_dialog_authenticate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldEmail = textView_dialog_oldEmail1.getText().toString();
                String password = editText_dialog_password1.getText().toString();

                if (oldEmail.isEmpty()) {
                    textView_dialog_oldEmail1.setError("Email is empty");
                    editText_dialog_password1.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    textView_dialog_oldEmail1.setError("Password is empty");
                    editText_dialog_password1.requestFocus();
                    return;
                }

                AuthCredential authCredential = EmailAuthProvider.getCredential(oldEmail, password);

                user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AccountActivity.this, "User Authenticated, can proceed to update password", Toast.LENGTH_SHORT).show();

                        button_dialog_updatePassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String updatePass = editText_dialog_newPassword.getText().toString();
                                if(updatePass.isEmpty())
                                {
                                    editText_dialog_email.setError("It is empty");
                                    editText_dialog_email.requestFocus();

                                }
                                else
                                {
                                    user.updatePassword(updatePass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AccountActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
                                            updatePasswordDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.d("error message", e.getMessage());
                                            updatePasswordDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });

        buttonHome_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}