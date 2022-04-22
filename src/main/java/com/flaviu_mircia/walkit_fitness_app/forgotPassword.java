package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {
    private EditText emailText;
    private TextView signUpTextView;
    private TextView resetButton;
    private TextView infotext;
    private FirebaseAuth fAuth;
    private TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_forgot_password);
        fAuth=FirebaseAuth.getInstance();
        emailText =(EditText) findViewById(R.id.Email);
        signUpTextView=(TextView) findViewById(R.id.signUpTextView);
        resetButton=(TextView) findViewById(R.id.resetPasswordTextView);
        infotext=(TextView) findViewById(R.id.infoTextPass);
        login=(TextView) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(forgotPassword.this,MainActivity.class));
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailText.getText().toString().trim();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        infotext.setGravity(Gravity.CENTER);
                        String text="We sent you an e-mail for resetting your password.";
                        infotext.setText(text);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(forgotPassword.this,"ERROR! Reset link is not sent!",Toast.LENGTH_SHORT).show();
                    }
                });

                emailText.setVisibility(View.GONE);
                resetButton.setVisibility(View.GONE);
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(forgotPassword.this,register_activity.class));
            }
        });
    }

}