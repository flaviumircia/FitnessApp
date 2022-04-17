package com.flaviu_mircia.walkit_fitness_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class register_activity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private TextView signupbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_register);
        passwordEditText=(EditText) findViewById(R.id.PasswordReg);
        passwordConfirmEditText=(EditText) findViewById(R.id.passwordConfirm);

        signupbutton=(TextView) findViewById(R.id.restPasswordTextView);

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(passwordEditText.toString().equals(passwordConfirmEditText) && passwordEditText.toString().length()>=3){
//                }
            }
        });

    }
}