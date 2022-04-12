package com.flaviu_mircia.walkit_fitness_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
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
    private TextView passwordTextView;
    private TextView passwordConfirmTextView;
    private TextView userNameTextView;
    private TextView emailAdressTextView;
    private EditText emailEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_register);
        backgroundImage=(ImageView) findViewById(R.id.imageView);
        passwordEditText=(EditText) findViewById(R.id.PasswordReg);
        passwordTextView=(TextView) findViewById(R.id.passwordTextView);

        passwordEditText.addTextChangedListener(new TextWatcher() {
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
        });

        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordEditText.length()==0){
                    passwordTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}