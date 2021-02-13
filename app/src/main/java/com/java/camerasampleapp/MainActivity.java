package com.java.camerasampleapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button cameraButton;
    Button SdCardButton;
    ImageView mImageView;

    private int REQUEST_CODE = 42;
    private String FILE_NAME = "photo.jpg";
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
        onClick();
    }

    private void initializeView() {
        cameraButton = findViewById(R.id.cameraButton);
        SdCardButton = findViewById(R.id.SdCardButton);
        mImageView = findViewById(R.id.showImage);
    }

    private void onClick() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    openCamera();
                } else {
                    requestPermissionss();
                }
            }
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFile(FILE_NAME);
//               this does not work for API >= 24
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoFile);

        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.java.camerasampleapp.fileprovider", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE);
        } else {
            Toast.makeText(MainActivity.this, "Unable to open camera", Toast.LENGTH_LONG).show();
        }
    }

    private File getPhotoFile(String file_name) {
        File tempFile = null;
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            tempFile = File.createTempFile(file_name, ".jpg", storageDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && requestCode == Activity.RESULT_OK) {
//            Bitmap takeImage = (Bitmap) data.getExtras().get("data");
            Bitmap takeImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            mImageView.setImageBitmap(takeImage);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermissionss() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // permission denied
                    Toast.makeText(MainActivity.this, "Please grant the permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}