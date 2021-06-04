package com.example.schedular;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.internal.RegisterListenerMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText RegEmail, RegPassword;
    private Button RegBtn;
    private TextView RegQn;
    private FirebaseAuth mAuth;

    private ProgressDialog loader;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registration);

        toolbar=findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration");

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);


        RegEmail=findViewById(R.id.RegistrationEmail);
        RegPassword=findViewById(R.id.RegistrationPassword);
        RegBtn=findViewById(R.id.RegistrationButton);
        RegQn=findViewById(R.id.RegistrationPageQuestion);


        RegQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registration.this,LoginActivity.class);
                startActivity(intent);
                
            }
        });
        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = RegEmail.getText().toString().trim();
                String Password = RegPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    RegEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(Password)){
                    RegPassword.setError("Password is required");
                    return;
                }else {
                    loader.setMessage("Registration in prograss");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mAuth.createUserWithEmailAndPassword(email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Intent intent = new Intent(Registration.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            loader.dismiss();
                        }else {
                            String error = task.getException().toString();
                            Toast.makeText(Registration.this, "Registration failed" + error, Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }
                        }

                    });
                }
            }
        });
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }
}