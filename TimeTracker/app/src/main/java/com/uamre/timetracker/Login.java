package com.uamre.timetracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText editTextEmail,editTextPassword;
    TextInputLayout tilEmailWrapper,tilPasswordWrapper;
    Button buttonSignIn;
    TextView textViewSignUp,textViewForgotPassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    private Pattern email_pattern,password_pattern;
    private Matcher email_matcher,password_matcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tilEmailWrapper = (TextInputLayout) findViewById(R.id.emailWrapper);
        tilPasswordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);

        password_pattern = Pattern.compile(PASSWORD_PATTERN);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(Login.this,HomeActivity.class));
            finish();
        }
        progressDialog = new ProgressDialog(this);
        editTextEmail = (EditText) findViewById(R.id.etLoginUserName);
        editTextPassword = (EditText) findViewById(R.id.etLoginPassword);
        buttonSignIn = (Button) findViewById(R.id.btnSignIn);
        textViewSignUp = (TextView) findViewById(R.id.tvSignUp);
        textViewForgotPassword = (TextView) findViewById(R.id.tvForgetpassword);

        buttonSignIn.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            hideKeyboard();

            String email = tilEmailWrapper.getEditText().getText().toString().trim();
            String password = tilPasswordWrapper.getEditText().getText().toString().trim();

            if (email.isEmpty() || !validateEmail(email)) {
                editTextEmail.setError("Not a valid email address!");
            }
            else if(password.length()<5 && !validatePassword(password)){
                tilPasswordWrapper.setPasswordVisibilityToggleEnabled(false);
                editTextPassword.setError("Not a valid password!");
            }
            else{
                tilEmailWrapper.setErrorEnabled(false);
                tilPasswordWrapper.setErrorEnabled(false);
                login(email,password);
            }

        }
        if(v == textViewForgotPassword){
            startActivity(new Intent(Login.this,ForgotPassword.class));
        }
        if(v == textViewSignUp){
            startActivity(new Intent(Login.this,Register.class));
        }
    }

    private void login(String email,String password) {

        tilPasswordWrapper.setPasswordVisibilityToggleEnabled(true);
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Email or Password can not be empty !!", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    startActivity(new Intent(Login.this,HomeActivity.class));
                    finish();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Invalid Email or Password !!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public boolean validatePassword(String password) {
        password_matcher = password_pattern.matcher(password);
        return password_matcher.matches();
    }
}
