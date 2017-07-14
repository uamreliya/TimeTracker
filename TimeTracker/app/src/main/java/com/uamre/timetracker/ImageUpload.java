package com.uamre.timetracker;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageUpload extends AppCompatActivity implements View.OnClickListener{

    private static final int STORAGE_PERMISSION_CODE = 2;
    private final int IMG_REQUEST = 1;
    CircleImageView circleImageViewUploadPhoto;
    Button buttonChooseImage,buttonUpload;
    TextView textViewSkip;
    private String uploadUrl = "https://uamreliya.000webhostapp.com/Time Tracker/upload.php";
    private Uri filepath;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        requestStoragePermission();

        circleImageViewUploadPhoto = (CircleImageView) findViewById(R.id.civUpload);
        buttonChooseImage = (Button) findViewById(R.id.btnChangeImage);
        buttonUpload = (Button) findViewById(R.id.btnUpload);
        textViewSkip = (TextView) findViewById(R.id.tvSkip);

        buttonUpload.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);
        textViewSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChooseImage){
            showFileChooser();
        }
        if(v == buttonUpload){
            uploadImage();
            startActivity(new Intent(ImageUpload.this,LoginIntent.class));
            finish();
        }
        if(v == textViewSkip){
            startActivity(new Intent(ImageUpload.this,LoginIntent.class));
            finish();
        }
    }
    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return;
        }
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,"Permission not granted",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),IMG_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();
//            Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                circleImageViewUploadPhoto.setImageBitmap(bitmap);
                circleImageViewUploadPhoto.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);

        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,MediaStore.Images.Media._ID + " = ?", new String[]{document_id},null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void uploadImage(){
        Intent intent = getIntent();
        String name= intent.getStringExtra("fullName");
        String email = intent.getStringExtra("email");
        String path= getPath(filepath);

        try{
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this,uploadId,uploadUrl).addFileToUpload(path,"image").addParameter("name",name).addParameter("email",email)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
