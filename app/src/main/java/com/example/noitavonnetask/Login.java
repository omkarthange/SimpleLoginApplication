package com.example.noitavonnetask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    Button cancelButton;
    Button loginButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.sign_in_email);
        passwordEditText = findViewById(R.id.sign_in_password);
        cancelButton = findViewById(R.id.sign_in_cancel_button);
        loginButton = findViewById(R.id.sign_in_login_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    boolean isLoginSuccess = SharedPreference.login(Login.this, email, password);
                    if(isLoginSuccess){
                        Toast.makeText(Login.this, "Logged in Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("isLoginSuccess", true);
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                    }else{
                        Toast.makeText(Login.this, "Logged in Failed, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateData(){
        if(emailEditText.length() == 0){
            emailEditText.setError("Email is required");
            return false;
        } else if(!emailEditText.getText().toString().contains("@")){
            emailEditText.setError("The data you have entered is incorrect");
            return false;
        }

        if(passwordEditText.length() == 0) {
            passwordEditText.setError("Password is required");
            return false;
        }

        return true;
    }
}