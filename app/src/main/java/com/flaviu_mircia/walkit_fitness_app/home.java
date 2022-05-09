package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class home extends AppCompatActivity implements SensorEventListener {
    public static final int RECORD_AUDIO = 0;
    private TextView nickname, stepCounter;
    private SensorManager sensorManager;
    private Sensor mStepCounter,mLightSensor;
    private boolean isCounterSensorPresent;
    private int stepCount = 0;
    private int count=0;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private ImageView userPhoto,settingsIconHome;
    private Uri imageURI;
    private CircularProgressBar circularProgressBar;
    private UserDay userDay;
    private ArrayList<Stats> userStats=new ArrayList<>();
    private MediaRecorder mRecorder;
    private String second;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null){
            imageURI=data.getData();
            uploadImage();
            downloadPhoto();
        }
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImageURI(Uri imageURI) {
        this.imageURI = imageURI;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notifications bar
        setContentView(R.layout.activity_home);
        userDay=new UserDay();
        nickname = (TextView) findViewById(R.id.nickname);
        circularProgressBar=(CircularProgressBar) findViewById(R.id.circularProgressBar);

        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        userPhoto = (ImageView) findViewById(R.id.userPhoto);
        //initializing variables

        downloadPhoto();
        settingsIconHome=(ImageView) findViewById(R.id.settings);
        settingsIconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,menu.class));
            }
        });

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
            }
        });


        Runnable helloRunnable=new Runnable() {
            @Override
            public void run() {
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd",Locale.ENGLISH);
                    Date data=new Date();
                    String todayData= sdf.format(data);
                    String [] part=checkTime().split("_");
                    second=part[2];
                    DocumentReference documentReference1 = fStore.collection("users").document(userID).collection("data").document(todayData);
                    if (checkTime().equals("23_59_59")) {
                        HashMap<String, Object> map = new HashMap<>();
                        userDay.setSleep("00h00m");
                        userDay.setWeight(0);
                        map.put("steps", userDay.getSteps());
                        map.put("weight",userDay.getWeight());
                        map.put("sleep",userDay.getSleep());
                        documentReference1.set(map);
                    } else if (checkTime().equals("00_00_01")) {
                        ok = 1;
                    }else if(checkTime().equals("23_40_50")){
                        String output="";
                        for(int i=0;i<userStats.size();i++){
                            output=output+String.valueOf(i)+","
                                    +String.valueOf(userStats.get(i).getIsbetweenRestingHours())+","
                                    +String.valueOf(userStats.get(i).getIsScreenOn())+","
                                    +String.valueOf(userStats.get(i).getLuxQuantity())+","
                                    +String.valueOf(userStats.get(i).getDb())+","
                                    +"S"+"\n";}
//                        DocumentReference mlFile=fStore.collection("users").document(userID).collection("sleepInfo").document("train");
//                        mlFile.set(output);
                        writeToFile(output,getApplicationContext());
                    }
                    Log.d("TAG", "onSensorChanged: Time is: " + checkTime());
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 1, TimeUnit.SECONDS);
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

        mLightSensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);

        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile("/dev/null");
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();


    }

    //gets sound amplitude
    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    private int ok=1;
    private int difference=0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor == mStepCounter) {
            stepCount=(int)sensorEvent.values[0];
            if(ok==1){
                difference=stepCount;
                ok=0;
            }
            if(difference==0 )
                {stepCounter.setText(String.valueOf(0));}
            else
                stepCounter.setText(String.valueOf(stepCount-difference));
            userDay.setSteps(Integer.parseInt(stepCounter.getText().toString()));
            circularProgressBar.setProgressWithAnimation(Float.parseFloat(stepCounter.getText().toString())/100, 1000L);
        }

        if(sensorEvent.sensor==mLightSensor || Integer.parseInt(second)%10==0){
            int isOn=0,isBetweenRestingHours=0;
            String time=checkTime();
            String[] parts=time.split("_");
            String seconds=parts[2];
            if(Integer.parseInt(parts[0])>=22 || Integer.parseInt(parts[0])<=9){
                isBetweenRestingHours=1;
            }
           double powerDb = 20 * Math.log10(getAmplitude() / 1);
            if(Integer.parseInt(seconds)%10==0){
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                if (powerManager.isScreenOn()){ isOn=1; }
                  Stats userObject=new Stats(isOn,isBetweenRestingHours,sensorEvent.values[0],powerDb);
                DocumentReference mlFile=fStore.collection("users").document(userID).collection("sleepInfo").document(String.valueOf(count));
                HashMap <String,Object> sleepInfos=new HashMap<>();
                sleepInfos.put("luxes",userObject.getLuxQuantity());
                sleepInfos.put("isScreenOn",userObject.getIsScreenOn());
                sleepInfos.put("isBetweenRestingHours",userObject.getIsbetweenRestingHours());
                sleepInfos.put("dBm",userObject.getDb());
                mlFile.set(sleepInfos);
                count++;
                Log.d("TAG", "onSensorChanged: lux="+ userObject.getLuxQuantity() +" isScreenOn="+userObject.getIsScreenOn()+ " isBetweenRestingHours="+userObject.getIsbetweenRestingHours()+" dBm="+userObject.getDb());
                userStats.add(userObject);
            }
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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            sensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, mStepCounter);
        }

    }
    public static Bitmap getClip(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f,
                bitmap.getWidth() / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    //gets image from server
    private void downloadPhoto() {
        StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("images/"+ userID);

        try{
            final File localFile=File.createTempFile("userPhoto","jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(home.this, "Photo successfully updated!", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    bitmap=getClip(bitmap);
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

    //puts image to server
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

    //checking system time
    private String checkTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH_mm_ss",Locale.getDefault());
        Date now= new Date();
        String time=format.format(now);
        return time;
    }
    //export txt file for training the AI algorithm
    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}