package com.example.soilsense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView btnSignup;
    EditText EditTextEmail, EditTextPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //        FirebaseApp.initializeApp();
        mAuth= FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        EditTextEmail = findViewById(R.id.etEmail);
        EditTextPassword = findViewById(R.id.etPassword);


//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user == null){
//            Toast.makeText(LoginActivity.this, "Kindly login", Toast.LENGTH_SHORT).show();
//        }else {
//            mAuth.signOut();
//            Toast.makeText(LoginActivity.this, "Kindly login", Toast.LENGTH_SHORT).show();
//        }

        // Get the saved email and password
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        if (!preferences.getString("email", "").isEmpty()){
            String[] email1 = preferences.getString("email", "").split("@");

            if (mAuth.getCurrentUser() != null){
                mAuth.signOut();
            }
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("email", email1[0]);
            startActivity(i);
        } else{
            EditTextEmail.requestFocus();
        }

        btnLogin.setOnClickListener(v -> loginUser());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }

    private void loginUser(){
        String email = Objects.requireNonNull(EditTextEmail.getText()).toString();
        String pass = Objects.requireNonNull(EditTextPassword.getText()).toString();

        if(TextUtils.isEmpty(email)){
            EditTextEmail.setError("This Field is required!");
            EditTextEmail.requestFocus();
            Toast.makeText(LoginActivity.this, "Email Empty", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pass)){
            EditTextPassword.setError("This Field is required!");
            EditTextPassword.requestFocus();
            Toast.makeText(LoginActivity.this, "Pass Empty", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // SharedPreferences Save id pass locally
                    SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", email);
                    editor.putString("password", pass);
                    editor.apply();

                    String[] email1 = email.split("@");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("email", email1[0]);
                    startActivity(i);

                }else{
                    if(task.getException().getMessage().equals("The email address is badly formatted.")){ Toast.makeText(LoginActivity.this, "Login Error: Invalid Email", Toast.LENGTH_LONG).show(); EditTextEmail.requestFocus();}
                    else if(task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){ Toast.makeText(LoginActivity.this, "Login Error: Invalid Email", Toast.LENGTH_LONG).show(); EditTextEmail.requestFocus();}
                    else if(task.getException().getMessage().equals("The password is invalid or the user does not have a password.")){ Toast.makeText(LoginActivity.this, "Login Error: Invalid Username or Password", Toast.LENGTH_LONG).show();}
                    else if(task.getException().getMessage().equals("We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]")){ Toast.makeText(LoginActivity.this, "Your account blocked due to many failure attempts. Try after some time.", Toast.LENGTH_LONG).show();}
                    else{Toast.makeText(LoginActivity.this, "Login Error: "+ task.getException().getMessage(), Toast.LENGTH_LONG).show(); EditTextEmail.requestFocus(); }
//                    Log.d("Login Error", ""+task.getException().getMessage());
                }
            });
        }

    }

}

