package com.uamre.timetracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class Register extends AppCompatActivity implements View.OnClickListener{

    TextInputLayout tilRegisterPasswordWrapper,tilRegisterEmailWrapper;
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonSignIn;
    EditText editTextFirstName,editTextLastName;
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    TextView textViewSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private Pattern password_pattern;
    private Matcher password_matcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        password_pattern = Pattern.compile(PASSWORD_PATTERN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tilRegisterPasswordWrapper = (TextInputLayout) findViewById(R.id.registerPasswordWrapper);
        tilRegisterEmailWrapper = (TextInputLayout) findViewById(R.id.registerEmailWrapper);
        editTextEmail = (EditText) findViewById(R.id.etSignUpUserName);
        editTextPassword = (EditText) findViewById(R.id.etSignUpPassword);
        editTextFirstName = (EditText) findViewById(R.id.etFirstName);
        editTextLastName = (EditText) findViewById(R.id.etLastName);
        buttonSignIn = (Button) findViewById(R.id.btnSignIn);
        textViewSignIn = (TextView) findViewById(R.id.tvSignIn);

        progressDialog =new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {


        if(v == buttonSignIn){

            hideKeyboard();

            String email = tilRegisterEmailWrapper.getEditText().getText().toString().trim();
            String password = tilRegisterPasswordWrapper.getEditText().getText().toString().trim();
            String FirstName = editTextFirstName.getText().toString().trim();
            String LastName = editTextLastName.getText().toString().trim();

            if (email.isEmpty() || !validateEmail(email)) {
                editTextEmail.setError("Not a valid email address!");
            }
            else if(password.length()<5 || !validatePassword(password) || password.isEmpty()){
                tilRegisterPasswordWrapper.setPasswordVisibilityToggleEnabled(false);
                editTextPassword.setError("Not a valid password!");
            }
            else if(email.isEmpty() && password.isEmpty() && FirstName.isEmpty() && LastName.isEmpty()){
                editTextEmail.setError("Email can not be blank");
                editTextPassword.setError("Password can not be blank");
                editTextFirstName.setError("First name can not be blank");
                editTextLastName.setError("Last name can not be blank");
            }
            else if(email.isEmpty() && password.isEmpty()){
                editTextEmail.setError("Email can not be blank");
                editTextPassword.setError("Password can not be blank");
            }
            else if(FirstName.isEmpty() && LastName.isEmpty()){
                editTextFirstName.setError("First name can not be blank");
                editTextLastName.setError("Last name can not be blank");
            }
            else if(TextUtils.isEmpty(FirstName)){
                editTextFirstName.setError("First name can not be empty.");
            }
            else if(TextUtils.isEmpty(LastName)){
                editTextLastName.setError("Last name can not be empty.");
            }
            else{
                createUser(email,password,FirstName,LastName);
            }
        }
        if(v == textViewSignIn){
            startActivity(new Intent(Register.this,Login.class));
        }
    }

    private void createUser(final String email, String password, String Firstname,String LastName) {

        final String fullname = Firstname.concat(" "+LastName);
        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();

                    Toast.makeText(Register.this, "User registered successfully, Please verify your email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this,ProfileRegistration.class);
                    intent.putExtra("email",email);
                    intent.putExtra("fullname",fullname);
                    startActivity(intent);
                    finish();
//                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(firebaseAuth.getCurrentUser() != null){
//
//                            }
//                            else{
//                                Toast.makeText(Register.this, "Can't send mail", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "Could not registered, please try again later.", Toast.LENGTH_LONG).show();
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
