package com.flaviu_mircia.walkit_fitness_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText emailTextLogin;
    private TextView emailTextView;
    private EditText passwordTextLogin;
    private TextView passwordTextView;
    private ImageView backgroundImageView;
    private TextView loginButton;
    private TextView registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_main);

        emailTextLogin =(EditText) findViewById(R.id.EmailReg);
        emailTextView =(TextView)  findViewById(R.id.EmailTextView);//links emailTextView and EmailTextLogin to specific layout

        passwordTextLogin=(EditText) findViewById(R.id.PasswordReg);
        passwordTextView=(TextView) findViewById(R.id.passwordTextView); // links password text and password text view to the layout

        backgroundImageView=(ImageView) findViewById(R.id.imageView);

        registerButton=(TextView) findViewById(R.id.signUpTextView);

        emailTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    emailTextView.setVisibility(View.GONE);
                    }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }); //adds change to email text view on change to emailTextLogin
        passwordTextLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordTextView.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }); // changes password text view when passwordTextLogin is changed
        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailTextLogin.length()==0){
                    emailTextView.setVisibility(View.VISIBLE);
                }
                if(passwordTextLogin.length()==0){
                    passwordTextView.setVisibility(View.VISIBLE);
                }
            }
        }); //if password or email are empty shows "Email adress" and "Password" in container

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,register_activity.class));
            }
        });

    }
}