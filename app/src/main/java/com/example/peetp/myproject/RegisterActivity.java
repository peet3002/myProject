package com.example.peetp.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaCodec;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AndroidException;
import android.util.Patterns;
import android.view.View;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword, userConfirmPassword;
    private TextView textView, textView2, linkLogin;
    private Button createAccountButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        userConfirmPassword = (EditText) findViewById(R.id.register_confirm_password_password);
        createAccountButton = (Button) findViewById(R.id.register_create_account);
        textView = (TextView) findViewById(R.id.register_text1);
        textView2 = (TextView) findViewById(R.id.register_text2);
        linkLogin = (TextView) findViewById(R.id.link_login);
        loadingBar = new ProgressDialog(this);

        Typeface extraLight = Typeface.createFromAsset(getAssets(), "fonts/Prompt-ExtraLight.ttf");
        userEmail.setTypeface(extraLight);
        userPassword.setTypeface(extraLight);
        userConfirmPassword.setTypeface(extraLight);
        createAccountButton.setTypeface(extraLight);
        textView.setTypeface(extraLight);
        textView2.setTypeface(extraLight);

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(mainIntent);
            }
        });


        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    if(userPassword.getText().toString().length() < 6){
                        userPassword.setError("รหัสผ่านต้องมีความยาวมากกว่า 6 ตัว");
                    }else{

                    }
                }
            }
        });

        userConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!userPassword.getText().toString().equals(userConfirmPassword.getText().toString())){
                        userConfirmPassword.setError("รหัสผ่านไม่ตรงกัน");
                    }
                }
            }
        });

        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if (userEmail.getText().toString().matches(emailPattern)) {

                    }
                    else{
                        userEmail.setError("อีเมล์ไม่ถูกต้อง");
                    }
                }
            }
        });


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void CreateNewAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "กรุณาใส่อีเมล์...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "กรุณาใส่รหัสผ่าน...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "กรุณาใส่ยืนยันรหัสผ่าน...", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPassword) ) {
            Toast.makeText(this, "กรุณาใส่รหัสผ่านกับยืนยันรหัสผ่านให้ตรงกัน", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("กำลังลงทะเบียน");
            loadingBar.setMessage("กรุณารอสักครู่กำลังสร้างรหัสของคุณ...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                SendUserToSetupActivity();
                                Toast.makeText(RegisterActivity.this, "การละทะเบียนสำเร็จ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else{
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "เกิดเหตุขัดข้อง: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetUpActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



}
