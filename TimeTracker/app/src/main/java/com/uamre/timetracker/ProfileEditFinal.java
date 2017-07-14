package com.uamre.timetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditFinal extends AppCompatActivity implements View.OnClickListener {

    EditText editTextName,editTextDob,editTextAddress1,editTextAddress2,editTextZip,editTextCity,editTextState,editTextCountry,editTextPhone;
    CircleImageView circleImageView;
    Button buttonUpdate,buttonCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_final);

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

        }
        if(v == buttonCancel){
            finish();
            startActivity(new Intent(ProfileEditFinal.this,ProfileFinal.class));
            finish();
        }
    }
}
