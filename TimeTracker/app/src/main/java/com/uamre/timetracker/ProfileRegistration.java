package com.uamre.timetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileRegistration extends AppCompatActivity implements View.OnClickListener{

    EditText editTextDob,editTextAddress1,editTextAddress2,editTextCity,editTextZip,editTextState,editTextPhone;
    Spinner spinnerCountry;
    Button buttonRegister;
    RadioGroup radioGroupGender;
    RadioButton radioButtonGender;
    RequestQueue requestQueue1,requestQueue2;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private ArrayList<String> countryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_registration);

        requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        requestQueue2 = Volley.newRequestQueue(getApplicationContext());
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        editTextDob = (EditText) findViewById(R.id.etDOB);
        editTextAddress1 = (EditText) findViewById(R.id.etAddress1);
        editTextAddress2 = (EditText) findViewById(R.id.etAddress2);
        editTextCity = (EditText) findViewById(R.id.etCity);
        editTextZip = (EditText) findViewById(R.id.etZip);
        editTextState = (EditText) findViewById(R.id.etState);
        spinnerCountry = (Spinner) findViewById(R.id.spnCountry);
        editTextPhone = (EditText) findViewById(R.id.etPhone);
        buttonRegister = (Button) findViewById(R.id.btnRegister);
        radioGroupGender = (RadioGroup) findViewById(R.id.rgGender);
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        radioButtonGender = (RadioButton) findViewById(selectedId);


        countryList = new ArrayList<>();

        buttonRegister.setOnClickListener(this);
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EditText editTextDob = (EditText) findViewById(R.id.etDOB);
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
        if(v == buttonRegister){
            createUser();
        }
    }

    private void createUser() {
        Intent intent = getIntent();
        final String email = intent.getStringExtra("email");
        final String fullName = intent.getStringExtra("fullname");
        final String dob = editTextDob.getText().toString().trim();
        final String address1 = editTextAddress1.getText().toString().trim();
        final String address2 = editTextAddress2.getText().toString().trim();
        final String city = editTextCity.getText().toString().trim();
        final String zip = editTextZip.getText().toString().trim();
        final String state = editTextState.getText().toString().trim();
        final String country = spinnerCountry.getSelectedItem().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String gender = radioButtonGender.getText().toString().trim();
        final String image = "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (firebaseAuth.getCurrentUser() != null){
                                        Intent intent = new Intent(getApplicationContext(),ImageUpload.class);
                                        intent.putExtra("email",email);
                                        intent.putExtra("fullName",fullName);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(ProfileRegistration.this, "Email can not send", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(ProfileRegistration.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileRegistration.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("email",email);
                params.put("name",fullName);
                params.put("dob",dob);
                params.put("gender",gender);
                params.put("address1",address1);
                params.put("address2",address2);
                params.put("city",city);
                params.put("zip",zip);
                params.put("state",state);
                params.put("country",country);
                params.put("phone",phone);
                params.put("image",image);
                return params;
            }
        };
        requestQueue1.add(stringRequest);
    }

    public void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.COUNTRY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("geonames");

                            for(int i =0;i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                Country country = new Country();
                                country.setCountry(object.getString("countryName"));
                                countryList.add(country.getCountry());
                            }
                            spinnerCountry.setAdapter(new ArrayAdapter<String>(ProfileRegistration.this,android.R.layout.simple_spinner_dropdown_item,countryList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue2.add(stringRequest);
    }
}
