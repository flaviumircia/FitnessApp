package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class register_activity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private TextView signupbutton;
    private FirebaseAuth fAuth;
    private CheckBox check;
    private FirebaseFirestore fStore;
    private String userID;
    public static boolean passwordCheck(EditText password){
        String password_string=password.getText().toString().trim();
        Pattern pattern=Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
        Matcher matcher=pattern.matcher(password_string);
        return matcher.find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_register);
        passwordEditText=(EditText) findViewById(R.id.PasswordReg);
        passwordConfirmEditText=(EditText) findViewById(R.id.passwordConfirm);

        emailEditText=(EditText) findViewById(R.id.EmailReg);

        check=(CheckBox) findViewById(R.id.agreeCheckBox);

        signupbutton=(TextView) findViewById(R.id.restPasswordTextView);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailEditText.getText().toString().trim();
                String password=passwordEditText.getText().toString().trim();
                String passwordConf=passwordConfirmEditText.getText().toString().trim();
                String[] parts=email.split("@");
                String userName=parts[0];

                if(TextUtils.isEmpty(email)){
                    emailEditText.setError("Email is empty");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordEditText.setError("Password can NOT be empty");
                    return;
                }
                if(password.equals(passwordConf)){
                }else{
                    passwordConfirmEditText.setError("Password should match");
                    return;
                }

                if(!passwordCheck(passwordEditText)){
                    passwordEditText.setError("You need a stronger password(at least one upper case, one special character, one digit)");
                    return;
                }
                if(!check.isChecked()){
                    Toast.makeText(register_activity.this,"Please read and accept our terms and conditions!",Toast.LENGTH_SHORT).show();
                    return;
                }
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(register_activity.this, "User created", Toast.LENGTH_LONG).show();
                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fStore.collection("users").document(userID);
                            Map<String,Object> user=new HashMap<String,Object>();
                            user.put("userName",userName);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "onSuccess: user Profile is created for "+ userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(register_activity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }
}