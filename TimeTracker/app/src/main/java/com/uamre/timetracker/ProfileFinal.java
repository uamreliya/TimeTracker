package com.uamre.timetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFinal extends AppCompatActivity {

    TextView textViewName,textViewDob,textViewAddress1,textViewAddress2,textViewZip,textViewCity,textViewState,textViewCountry,textViewPhone;
    ImageView imageViewProfile;
    RequestQueue requestQueue;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String image_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_final);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();
        requestQueue = Volley.newRequestQueue(this);
        textViewName = (TextView) findViewById(R.id.tvShowFullName);
        textViewDob = (TextView) findViewById(R.id.tvShowBirthday);
        textViewAddress1 = (TextView) findViewById(R.id.tvShowAddress1);
        textViewAddress2 = (TextView) findViewById(R.id.tvShowAddress2);
        textViewZip = (TextView) findViewById(R.id.tvShowzip);
        textViewCity = (TextView) findViewById(R.id.tvShowCity);
        textViewState = (TextView) findViewById(R.id.tvShowState);
        textViewCountry = (TextView) findViewById(R.id.tvShowCountry);
        textViewPhone = (TextView) findViewById(R.id.tvShowPhone);
        imageViewProfile = (ImageView) findViewById(R.id.ivProfile);

        profileInformation(firebaseUser.getEmail().toString().trim());

    }

    private void profileInformation(final String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("details");

                            JSONObject object = jsonArray.getJSONObject(0);
                            textViewName.setText(object.getString("Name"));
                            textViewDob.setText(object.getString("DOB"));
                            textViewAddress1.setText(object.getString("Address1"));
                            textViewAddress2.setText(object.getString("Address2"));
                            textViewZip.setText(object.getString("ZIP"));
                            textViewCity.setText(object.getString("City"));
                            textViewState.setText(object.getString("State"));
                            textViewCountry.setText(object.getString("Country"));
                            textViewPhone.setText(object.getString("Phone"));
                            image_link = object.getString("Image");
                            Picasso.with(ProfileFinal.this).load(object.getString("Image")).into(imageViewProfile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editporfile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String name = textViewName.getText().toString().trim();
        String dob = textViewDob.getText().toString().trim();
        String address1 = textViewAddress1.getText().toString().trim();
        String address2 = textViewAddress2.getText().toString().trim();
        String zip = textViewZip.getText().toString().trim();
        String city = textViewCity.getText().toString().trim();
        String state = textViewState.getText().toString().trim();
        String country = textViewCountry.getText().toString().trim();
        String phone = textViewPhone.getText().toString().trim();
        int id = item.getItemId();
        if(id == R.id.action_edit){
            Intent intent = new Intent(ProfileFinal.this,ProfileEditFinal.class);
            intent.putExtra("image",image_link);
            intent.putExtra("name",name);
            intent.putExtra("dob",dob);
            intent.putExtra("address1",address1);
            intent.putExtra("address2",address2);
            intent.putExtra("zip",zip);
            intent.putExtra("city",city);
            intent.putExtra("state",state);
            intent.putExtra("country",country);
            intent.putExtra("phone",phone);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
