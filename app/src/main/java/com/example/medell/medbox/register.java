package com.example.medell.medbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class register extends AppCompatActivity {

    EditText email, password, cPassword;
    Button register;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        cPassword = (EditText) findViewById(R.id.register_confirm_password);
        register = (Button) findViewById(R.id.register_register_btn);
        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(final String email1, String password1) {
        mAuth.createUserWithEmailAndPassword(email1, password1).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    email.setText("");
                    password.setText("");
                    cPassword.setText("");
                    Toast.makeText(register.this, "REGISTRATION SUCCESSFUL.",Toast.LENGTH_SHORT).show();
                    Intent login_intent = new Intent(register.this, Login.class);
                    startActivity(login_intent);
                } else {
                    Toast.makeText(register.this, "USER ALREADY EXISTS",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Register(View view) {
        String str1 = email.getText().toString();
        String str2 = password.getText().toString();
        String str3 = cPassword.getText().toString();
        if ((!str1.isEmpty()) && ((!str2.isEmpty())) && ((!str3.isEmpty())) && (str2.equals(str3)) && (str2.length()>5) && (str1.contains(".com"))) {
            createAccount(str1,str2);
        }else if ((!str1.isEmpty()) && ((!str2.isEmpty())) && ((!str3.isEmpty())) && (str2.equals(str3)) && (str2.length()>5) && (!str1.contains(".com"))) {
            Toast.makeText(getApplicationContext(), "IMPROPER EMAIL ID", Toast.LENGTH_SHORT).show();
        }else if ((!str1.isEmpty()) && ((!str2.isEmpty())) && ((!str3.isEmpty())) && (str2.equals(str3)) && (str2.length()<5)) {
            Toast.makeText(getApplicationContext(), "PASSWORD LENGTH TO SHORT", Toast.LENGTH_SHORT).show();
        } else if ((!str1.isEmpty()) && ((!str2.isEmpty())) && ((!str3.isEmpty())) && (!str2.equals(str3))) {
            Toast.makeText(getApplicationContext(), "PASSWORD DO NOT MATCH", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "PLEASE ENTER THE COMPLETE CREDENTIALS", Toast.LENGTH_SHORT).show();
        }

    }
}
