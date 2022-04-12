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

public class forgotPassword extends AppCompatActivity {
    private EditText emailTextLogin;
    private TextView emailTextView;
    private ImageView backgroundImageView;
    private TextView signUpTextView;
    private TextView reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_forgot_password);

        emailTextLogin =(EditText) findViewById(R.id.EmailReg);
        emailTextView =(TextView)  findViewById(R.id.EmailTextView);//links emailTextView and EmailTextLogin to specific layout

        signUpTextView=(TextView) findViewById(R.id.signUpTextView);

        backgroundImageView=(ImageView) findViewById(R.id.imageView);

        reset=(TextView)findViewById(R.id.restPasswordTextView);

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

        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailTextLogin.length()==0){
                    emailTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(forgotPassword.this,register_activity.class));
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$ regex email adress
                if(emailTextLogin.length()!=0)
                startActivity(new Intent(forgotPassword.this,passwordReseted.class));
            }
        });
    }
}