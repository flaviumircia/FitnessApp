package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class profile_reg extends AppCompatActivity {
    //defining the variables
    private TextView date,nickname,continueButton;
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    private ImageView userPhoto;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private Uri imageURI;
    private String userID;
    @Override
    //REQUEST for server
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null){
            imageURI=data.getData();
            uploadImage();
            downloadPhoto();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_profile_reg);

        userPhoto = (ImageView) findViewById(R.id.profilePic);
        nickname=(TextView)findViewById(R.id.nickName);
        fAuth = FirebaseAuth.getInstance();
        date=(TextView) findViewById(R.id.birthdayDate);
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        continueButton=(TextView) findViewById(R.id.continueButton);

        //calling getNickname function
        getNickname();
        //on click listener for user photo, tap to change
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });
        //date on clickListener
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDate();
            }
        });
        //showing the chosen date
        datePickerDialog=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                String data=day+"/"+month+"/"+year;
                Log.d("TAG", "onDateSet: date: "+year +"/"+month+"/"+day);
                date.setText(data);
            }
        };
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile_reg.this,home.class));
            }
        });
    }
    //getting age from today_date-birthday_date
    //doesn't work yet
    private void gettingAgeFromDate() {
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
        Date now=new Date();
        String d1=sdf.format(now);
        Log.d("TAG", "gettingAgeFromDate: entered");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate todayDate=LocalDate.parse(d1, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate secondDate=LocalDate.parse(date.getText().toString(),DateTimeFormatter.ISO_LOCAL_DATE);
            Period period=Period.between(secondDate,todayDate);
            int years = Math.abs(period.getYears());
            Log.d("TAG", "gettingAgeFromDate: years "+ years);
            HashMap <String,Object> map=new HashMap<>();
            map.put("age",years);
            DocumentReference documentReference=fStore.collection("users").document(userID);
            documentReference.update(map);
        }


    }
    //getting nickname from the server
    private void getNickname() {
        DocumentReference documentReference=fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nickname.setText(value.getString("userName"));
            }
        });
    }
    //geting data from user input dialog
    private void inputDate() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog=new DatePickerDialog(profile_reg.this, com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,datePickerDialog,year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        dialog.show();
    }
    //upload image to the server
    private void uploadImage() {
        String filename=userID;
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+filename);

        storageReference.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(profile_reg.this,"Successfully uploaded!",Toast.LENGTH_SHORT).show();
                downloadPhoto();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profile_reg.this,"Failed to upload!",Toast.LENGTH_SHORT).show();

            }
        });
    }
    //download image from the server
    private void downloadPhoto() {
        StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("images/"+ userID);

        try{
            final File localFile=File.createTempFile("userPhoto","jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(profile_reg.this, "Photo successfully updated!", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    userPhoto.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(profile_reg.this, "Photo failed to update!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}