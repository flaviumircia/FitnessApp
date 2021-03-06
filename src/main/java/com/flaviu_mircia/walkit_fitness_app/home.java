package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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

public class home extends AppCompatActivity implements SensorEventListener {
    private TextView nickname, stepCounter;
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorPresent;
    private int stepCount = 0;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private ImageView userPhoto;
    private Uri imageURI;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null){
            imageURI=data.getData();
            uploadImage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar

        setContentView(R.layout.activity_home);

        nickname = (TextView) findViewById(R.id.nickname);

        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        userPhoto = (ImageView) findViewById(R.id.userPhoto);
        //initializing variables

        userPhoto.setOnClickListener(new View.OnClickListener() {//on click listener for user photo, tap to change
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nickname.setText(value.getString("userName"));
                downloadPhoto();
            }
        });
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) { //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        stepCounter = (TextView) findViewById(R.id.stepsCounter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            Toast.makeText(home.this, "Pedometer sensor is not present!", Toast.LENGTH_SHORT).show();
            isCounterSensorPresent = false;
        }
    }

    private void downloadPhoto() {
        StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("images/"+ userID);

        try{
            final File localFile=File.createTempFile("userPhoto","jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(home.this, "Photo successfully updated!", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    userPhoto.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(home.this, "Photo failed to update!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    private void uploadImage() {
        String filename=userID;
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+filename);

        storageReference.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(home.this,"Successfully uploaded!",Toast.LENGTH_SHORT).show();
                downloadPhoto();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(home.this,"Failed to upload!",Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == mStepCounter) {
            int stepDaily=0;
            stepCount = (int) sensorEvent.values[0];
            int totalSteps=stepCount;


            //not working yet
            SimpleDateFormat format=new SimpleDateFormat("HH_mm_ss",Locale.ENGLISH);
            Date now= new Date();
            String data=format.format(now);
            Map<String,Object> user=new HashMap<String,Object>();
            if(data.equals("00_00_01")){
                stepDaily=0;
                totalSteps=stepCount;
            }else {
                stepDaily=stepCount-totalSteps;
            }
            user.put("step"+data,stepDaily);
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    stepCounter.setText(String.valueOf(value.getString("stepDaily"+data)));

                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }
    }
}