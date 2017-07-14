package com.uamre.timetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginIntent extends AppCompatActivity implements View.OnClickListener {

    TextView textViewLoginIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_intent);

        textViewLoginIntent = (TextView) findViewById(R.id.tvLoginIntent);

        textViewLoginIntent.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == textViewLoginIntent){
            startActivity(new Intent(LoginIntent.this,Login.class));
        }
    }
}
