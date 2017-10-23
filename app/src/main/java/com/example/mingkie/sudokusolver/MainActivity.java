package com.example.mingkie.sudokusolver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    //static final int REQUEST_IMAGE_CAPTURE = 1;
    //static final int REQUEST_TAKE_PHOTO = 1;
    private String currentPhotoPath;
    private VisualRecognition vrClient;
    private CameraHelper cameraHelper;
    private Button btnCamera;
    private ImageView loadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        loadedImage = (ImageView) findViewById(R.id.imageView);

        // Initialize Visual Recognition client
        vrClient = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.api_key)
        );

        // Initialize camera cameraHelper
        cameraHelper = new CameraHelper(this);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "Clicked");
                cameraHelper.dispatchTakePictureIntent();
                Log.d("Debug", "Ended");
            }
        });
    }


    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
            Log.d("Debug", "Display photo");
            final Bitmap photo = cameraHelper.getBitmap(resultCode);
            final File photoFile = cameraHelper.getFile(resultCode);
            loadedImage.setImageBitmap(photo);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ClassifyOptions options = new ClassifyOptions.Builder()
                                .imagesFile(photoFile)
                                .build();
                        ClassifiedImages result = vrClient.classify(options).execute();
                        Log.d("Debug", String.valueOf(result));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });



        }

    }


}
