package com.example.licentademo4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class ClassifyActivity extends AppCompatActivity {

    private int inputSize=299;
    private String modelPath="model100.tflite";
    private String labelPath="labels.txt";
    private Classifier classifier;
    private TextView tvRezultat;
    private Bitmap bitmap;
    private ImageView imageView;
    private Button btnPredict;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

        try {
            initClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }


        initViews();

    }
    private void initClassifier() throws IOException{
        classifier=new Classifier(getAssets(),modelPath,labelPath,inputSize);
    }

    private void initViews(){
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

                float result=classifier.recognizeImage(bitmap);


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                String rez=" Benign:"+String.format("%.2f", result*100)+"% Malign:"+String.format("%.2f", (1-result)*100)+"%";


                tvRezultat.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(),rez,Toast.LENGTH_LONG).show();
                tvRezultat.setText(rez);

               // PyObject obj =pythonObj.callAttr("main");
                //tvRezultat.setText(obj.toString());
            }
        });


    }

}