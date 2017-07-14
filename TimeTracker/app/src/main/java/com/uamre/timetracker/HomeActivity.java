package com.uamre.timetracker;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    RequestQueue requestQueue;
    final static String NAME_IMAGE_URL = "http://uamreliya.000webhostapp.com/Time Tracker/showImage.php";
    TextView textViewEmail,textViewUserName;
    CircleImageView circleImageViewProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();

        requestQueue = Volley.newRequestQueue(this);
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(HomeActivity.this,Login.class));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        circleImageViewProfile = (CircleImageView) hView.findViewById(R.id.civProfile);
        textViewEmail = (TextView) hView.findViewById(R.id.tvUserEmail);
        textViewUserName = (TextView) hView.findViewById(R.id.tvUserFullName);
        textViewEmail.setText(firebaseUser.getEmail());
        setProfilePicture(firebaseUser.getEmail().toString().trim());
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.nav_Profile);
    }

    private void setProfilePicture(final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, NAME_IMAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("details");

                            JSONObject object = jsonArray.getJSONObject(0);
                            String Image = object.getString("Image");
                            String Name = object.getString("Name");
                            Picasso.with(HomeActivity.this).load(Image).into(circleImageViewProfile);
                            textViewUserName.setText(Name);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }
    private void displaySelectedScreen(int itemId){
        //creating fragment object


        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_Profile:
                Intent intent = new Intent(HomeActivity.this,ProfileFinal.class);
                startActivity(intent);
                break;
            case R.id.nav_profile2:
//                fragment = new Profile2();
                break;
            case R.id.nav_Logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this,Login.class));
                break;
        }

        //replacing the fragment
//        if (fragment != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.content_frame, fragment);
//            ft.commit();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
