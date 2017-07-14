package com.uamre.timetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{

    EditText editTextForgotPassword;
    Button buttonSend;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        editTextForgotPassword = (EditText) findViewById(R.id.etForgotPassword);
        buttonSend = (Button) findViewById(R.id.btnForgotSubmit);

        buttonSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
           if(v==buttonSend){
               String email = editTextForgotPassword.getText().toString().trim();

               if(email.isEmpty() || !validateEmail(email)){
                    editTextForgotPassword.setError("Not a valid email address!");
               }
               else{
                   sendMail(email);
               }

           }
    }

    private void sendMail(String email) {


        progressDialog.setMessage("Mail sending to your email id");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "Password reset link sent to your email id.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPassword.this, "Message sending failed !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
