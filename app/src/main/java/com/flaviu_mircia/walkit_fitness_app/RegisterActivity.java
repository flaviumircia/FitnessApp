package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviu_mircia.walkit_fitness_app.models.UserDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class RegisterActivity extends AppCompatActivity {
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
        //check if the password has one capital leter, one special symbol, one number and at least 8 char long
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
                    Toast.makeText(RegisterActivity.this,"Please read and accept our terms and conditions!",Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_LONG).show();
                            userID=fAuth.getCurrentUser().getUid();
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
                            Date data=new Date();
                            String todayData= sdf.format(data);
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            DocumentReference documentReference1=fStore.collection("users").document(userID).collection("data").document(todayData);
                            Map<String,Object> user=new HashMap<String,Object>();
                            UserDay userDay=new UserDay(0,0,"00h00m");
                            user.put("userName",userName);
                            Map<String,Object> userClass=new HashMap<>();
                            userClass.put("steps",userDay.getSteps());
                            userClass.put("weight",userDay.getWeight());
                            userClass.put("sleep",userDay.getSleep());

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "onSuccess: user Profile is created for "+ userID);
                                }
                            });
                            documentReference1.set(userClass);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }
}