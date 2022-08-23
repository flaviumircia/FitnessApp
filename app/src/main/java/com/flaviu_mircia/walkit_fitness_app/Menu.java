package com.flaviu_mircia.walkit_fitness_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Menu extends AppCompatActivity {
    private ImageView userPhoto;
    private FirebaseStorage fStorage;
    private FirebaseAuth fAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        userPhoto=(ImageView) findViewById(R.id.userPhotoMenu);
        fStorage=FirebaseStorage.getInstance();
        fAuth=FirebaseAuth.getInstance();
        userID=fAuth.getCurrentUser().getUid();
        downloadPhoto();
    }

    private void downloadPhoto() {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("images/"+ userID);

        try{
            final File localFile=File.createTempFile("userPhoto","jpg");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Menu.this, "Photo successfully updated!", Toast.LENGTH_SHORT).show();
                    Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    bitmap=getClip(bitmap);
                    userPhoto.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Menu.this, "Photo failed to update!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
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
}