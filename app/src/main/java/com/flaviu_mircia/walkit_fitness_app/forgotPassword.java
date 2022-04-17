package com.flaviu_mircia.walkit_fitness_app;

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

public class forgotPassword extends AppCompatActivity {
    private EditText emailText;
    private TextView signUpTextView;
    private TextView resetButton;
    private TextView infotext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_forgot_password);

        emailText =(EditText) findViewById(R.id.Email);
        signUpTextView=(TextView) findViewById(R.id.signUpTextView);
        resetButton=(TextView) findViewById(R.id.resetPasswordTextView);
        infotext=(TextView) findViewById(R.id.infoTextPass);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infotext.setGravity(Gravity.CENTER);
                infotext.setText("We sent you an e-mail for resetting your password.");
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