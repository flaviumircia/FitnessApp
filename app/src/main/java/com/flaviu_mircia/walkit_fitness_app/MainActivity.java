package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText emailTextLogin;
    private EditText passwordTextLogin;
    private ImageView backgroundImageView;
    private TextView loginButton;
    private TextView registerButton;
    private TextView forgotPass;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_main);
        emailTextLogin =(EditText) findViewById(R.id.Email);

        passwordTextLogin=(EditText) findViewById(R.id.Password);


        registerButton=(TextView) findViewById(R.id.signUpTextView);

        forgotPass=(TextView) findViewById(R.id.ForgotPassword);

        loginButton=(TextView) findViewById(R.id.loginButton);
        fAuth=FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailTextLogin.getText().toString().trim();
                String password=passwordTextLogin.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    emailTextLogin.setError("Email is empty");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordTextLogin.setError("Password can NOT be empty");
                    return;
                }
                if(password.length()<8){
                    passwordTextLogin.setError("Password is too short");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "User connected", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else {
                            Toast.makeText(MainActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

    }
}