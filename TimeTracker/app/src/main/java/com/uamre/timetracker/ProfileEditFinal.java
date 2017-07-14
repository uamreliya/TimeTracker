package com.uamre.timetracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditFinal extends AppCompatActivity implements View.OnClickListener {

    EditText editTextName,editTextDob,editTextAddress1,editTextAddress2,editTextZip,editTextCity,editTextState,editTextCountry,editTextPhone;
    CircleImageView circleImageView;
    Button buttonUpdate,buttonCancel,buttonChangeImage;
    private final int IMG_REQUEST = 1;
    private Uri filepath;
    private Bitmap bitmap;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_final);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        editTextName = (EditText) findViewById(R.id.etEditFullName);
        editTextDob = (EditText) findViewById(R.id.etEditDOB);
        editTextAddress1 = (EditText) findViewById(R.id.etEditAddress1);
        editTextAddress2 = (EditText) findViewById(R.id.etEditAddress2);
        editTextZip = (EditText) findViewById(R.id.etEditZip);
        editTextCity = (EditText) findViewById(R.id.etEditCity);
        editTextState = (EditText) findViewById(R.id.etEditState);
        editTextCountry = (EditText) findViewById(R.id.etEditCountry);
        editTextPhone = (EditText) findViewById(R.id.etEditPhone);
        circleImageView = (CircleImageView) findViewById(R.id.edit_profile_image);
        buttonUpdate = (Button) findViewById(R.id.btnUpdate);
        buttonCancel = (Button) findViewById(R.id.btnCancel);
        buttonChangeImage = (Button) findViewById(R.id.btnChangeImage);

        Intent intent = getIntent();
        editTextName.setText(intent.getStringExtra("name"));
        editTextDob.setText(intent.getStringExtra("dob"));
        editTextAddress1.setText(intent.getStringExtra("address1"));
        editTextAddress2.setText(intent.getStringExtra("address2"));
        editTextZip.setText(intent.getStringExtra("zip"));
        editTextCity.setText(intent.getStringExtra("city"));
        editTextState.setText(intent.getStringExtra("state"));
        editTextCountry.setText(intent.getStringExtra("country"));
        editTextPhone.setText(intent.getStringExtra("phone"));
        Picasso.with(ProfileEditFinal.this).load(intent.getStringExtra("image")).into(circleImageView);

        buttonUpdate.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        buttonChangeImage.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        EditText editTextDob = (EditText) findViewById(R.id.etEditDOB);
        editTextDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    DateDialog dialog = new DateDialog(v);
                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft,"DatePicker");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == buttonUpdate){
            uploadImage();
        }
        if(v == buttonCancel){
            finish();
            startActivity(new Intent(ProfileEditFinal.this,ProfileFinal.class));
            finish();
        }
        if (v == buttonChangeImage){
            showFileChooser();
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
                circleImageView.setImageBitmap(bitmap);
                circleImageView.setVisibility(View.VISIBLE);
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
        String name= editTextName.getText().toString().trim();
        String email = firebaseUser.getEmail().toString().trim();
        String Dob = editTextDob.getText().toString().toString();
        String address1 = editTextAddress1.getText().toString().trim();
        String address2 = editTextAddress2.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String zip = editTextZip.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String path= getPath(filepath);

        try{
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this,uploadId,Constants.UPDATE_URL).addFileToUpload(path,"image").addParameter("name",name).addParameter("email",email)
                    .addParameter("dob",Dob)
                    .addParameter("address1",address1)
                    .addParameter("address2",address2)
                    .addParameter("city",city)
                    .addParameter("zip",zip)
                    .addParameter("state",state)
                    .addParameter("country",country)
                    .addParameter("phone",phone)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
