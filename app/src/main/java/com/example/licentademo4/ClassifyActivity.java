package com.example.licentademo4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ClassifyActivity extends AppCompatActivity {

    private int inputSize=299;
    private String modelPath="modelClasificare.tflite";
    private String labelPath="labels.txt";
    private Classifier classifier;
    private TextView tvRezultat;
    private Bitmap bitmap;
    private ImageView imageView;
    private Button btnPredict;

    private List<Upload> uploads;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        try {
            initClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }


        initViews();

      //  textView=(TextView)findViewById(R.id.textView);

        if(!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        Python py=Python.getInstance();

        PyObject pyobj=py.getModule("script");
      //  PyObject obj=pyobj.callAttr("main");
        //Toast.makeText(getApplicationContext(),obj.toString(),Toast.LENGTH_LONG).show();
       // textView.setText(obj.toString());

    }
    private void initClassifier() throws IOException{
        classifier=new Classifier(getAssets(),modelPath,labelPath,inputSize);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void initViews(){

        storageReference= FirebaseStorage.getInstance().getReference("uploads");
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");

        uploads=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Upload upload=dataSnapshot.getValue(Upload.class);
                    uploads.add(upload);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });




        tvRezultat=findViewById(R.id.tvRezultat);

        byte[] bytes=getIntent().getByteArrayExtra("Bitmap");
        bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        imageView=findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        btnPredict=findViewById(R.id.buttonPredict);


//        if(!Python.isStarted())
//            Python.start(new AndroidPlatform(this));
//
//        final Python[] python = {Python.getInstance()};
//        final PyObject pythonObj= python[0].getModule("script");
//


        btnPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bitmap bitmap=((BitmapDrawable)((ImageView)imageView).getDrawable()).getBitmap();

                float[] result=classifier.recognizeImage(bitmap);


                if(result[2]>result[0]&&result[2]>result[1] ){
                    Toast.makeText(getApplicationContext(),"Nu se poate identifica un melanom",Toast.LENGTH_LONG).show();
                }else {
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    String rez = " Benign:" + String.format("%.2f", result[0] * 100) + "% Malign:" + String.format("%.2f", result[1] * 100) + "%";


                    tvRezultat.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),rez,Toast.LENGTH_LONG).show();
                    tvRezultat.setText(rez);

                    uploadFile();

                    // PyObject obj =pythonObj.callAttr("main");
                    //tvRezultat.setText(obj.toString());

                }
            }
        });


    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile(){

        Uri uri=getImageUri(getApplicationContext(),bitmap);


        StorageReference fileReference=storageReference.child(System.currentTimeMillis()
                +"."+getFileExtension(uri));

        fileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getApplicationContext(),"S-a incarcat",Toast.LENGTH_LONG).show();

                        Upload upload=new Upload("a",taskSnapshot.getUploadSessionUri().toString());
                        String uploadId=databaseReference.push().getKey();
                        databaseReference.child(uploadId).setValue(upload);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

}