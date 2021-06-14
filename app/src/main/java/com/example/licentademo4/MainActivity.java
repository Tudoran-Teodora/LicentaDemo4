package com.example.licentademo4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.airbnb.lottie.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {


    private static final int UPLOAD_CODE = 100;
    private static final int CAMERA_CODE = 200;
    Button btnGalerie;
    ImageView imageView;
    Button btnCamera;
    Uri picUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.imageView);
        btnGalerie = findViewById(R.id.buttonGalerie);
        btnGalerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");

                startActivityForResult(i, UPLOAD_CODE);
            }});

        btnCamera=findViewById(R.id.buttonCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                i.setType("image/*");
//
//                startActivityForResult(i, CAMERA_CODE);

                String fileName = System.currentTimeMillis()+".jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                picUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(intent, CAMERA_CODE);

            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_CODE && resultCode == RESULT_OK && null != data) {
            {

                Bitmap bitmap = null;

                Uri uri = data.getData();


                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                try {
                    InputStream imageStream = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Intent intent1 = new Intent(getApplicationContext(), ClassifyActivity.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] bytes = stream.toByteArray();


                intent1.putExtra("Bitmap", bytes);
                startActivity(intent1);


            }
        }

        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK ) {
            Toast.makeText(getApplicationContext(),"ceva",Toast.LENGTH_LONG).show();

            Bitmap bitmap = null;

            //Uri uri = data.getData();


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream imageStream = getContentResolver().openInputStream(picUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            Intent intent1 = new Intent(getApplicationContext(), ClassifyActivity.class);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytes = stream.toByteArray();


            intent1.putExtra("Bitmap", bytes);
            startActivity(intent1);

        }


    }

}