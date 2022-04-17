package com.flaviu_mircia.walkit_fitness_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText emailTextLogin;
    private EditText passwordTextLogin;
    private ImageView backgroundImageView;
    private TextView loginButton;
    private TextView registerButton;
    private TextView forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_main);
        emailTextLogin =(EditText) findViewById(R.id.EmailReg);

        passwordTextLogin=(EditText) findViewById(R.id.PasswordReg);

        backgroundImageView=(ImageView) findViewById(R.id.imageView);

        registerButton=(TextView) findViewById(R.id.signUpTextView);

        forgotPass=(TextView) findViewById(R.id.ForgotPassword);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,register_activity.class));
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,forgotPassword.class));
            }
        });

    }
}