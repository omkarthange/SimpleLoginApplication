package com.example.noitavonnetask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText nameEditText;
    EditText phoneNoEditText;
    EditText emailEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    Button cancelButton;
    Button registerButton;

    String name;
    String phoneNo;
    String email;
    String password;
    String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.sign_up_name);
        phoneNoEditText = findViewById(R.id.sign_up_phone_no);
        emailEditText = findViewById(R.id.sign_up_email);
        passwordEditText = findViewById(R.id.sign_up_password);
        confirmPasswordEditText = findViewById(R.id.sign_up_confirm_password);
        cancelButton = findViewById(R.id.sign_up_cancel_button);
        registerButton = findViewById(R.id.sign_up_register_button);

        setRequired(nameEditText);
        setRequired(phoneNoEditText);
        setRequired(emailEditText);
        setRequired(passwordEditText);
        setRequired(confirmPasswordEditText);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    name = nameEditText.getText().toString();
                    phoneNo = phoneNoEditText.getText().toString();
                    email = emailEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    confirmPassword = confirmPasswordEditText.getText().toString();
                    Map<String, String> inputMap = new HashMap<>();
                    inputMap.put("Email", email);
                    inputMap.put("Name", name);
                    inputMap.put("Phone No", phoneNo);
                    SharedPreference.saveMap(Register.this, inputMap);
                    SharedPreference.register(Register.this, email, password);
                    Toast.makeText(Register.this, "Registered Successfully, Please log in to continue", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, Login.class);
                    intent.putExtra("isLoginSuccess", true);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED));
                }
            }
        });
    }

    private void setRequired(EditText editText){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(editText.getHint());
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append("*");
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), spannableStringBuilder.length()-1, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(spannableStringBuilder);
    }

    private boolean validateData(){
        if(nameEditText.length() == 0){
            nameEditText.setError("Name is required");
            return false;
        }

        if(phoneNoEditText.length() == 0){
            phoneNoEditText.setError("Phone no is required");
            return false;
        }else if(phoneNoEditText.length() != 10){
            phoneNoEditText.setError("Phone no should be of 10 characters");
            return false;
        }else if(!PhoneNumberUtils.isGlobalPhoneNumber(phoneNoEditText.getText().toString())){
            phoneNoEditText.setError("The data you have entered is incorrect");
            return false;
        }

        if(emailEditText.length() == 0){
            emailEditText.setError("Email is required");
            return false;
        } else if(!emailEditText.getText().toString().contains("@")){
            emailEditText.setError("The data you have entered is incorrect");
            return false;
        }

        if(passwordEditText.length() == 0){
            passwordEditText.setError("Password is required");
            return false;
        }else if((passwordEditText.length() < 8) || (!isValidPassword(passwordEditText.getText().toString()))){
            passwordEditText.setError("Password must contain minimum 8 characters at least 1 lowercase, 1 Alphabet and 1 Number");
            return false;
        }
        else if(confirmPasswordEditText.length()!=passwordEditText.length()){
            confirmPasswordEditText.setError("Password is not matching");
            return false;
        }else if(!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
            confirmPasswordEditText.setError("Password is not matching");
            return false;
        }

        return true;
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}