package com.example.medell.medbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login, register;
    String str1, str2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_login_btn);
        register = (Button) findViewById(R.id.login_register_btn);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    public void Register(View view) {
        Intent register_intent = new Intent(Login.this, register.class);
        startActivity(register_intent);
    }

    public void Login(View view) {
        str1 = email.getText().toString();
        str2 = password.getText().toString();
        if ((!str1.isEmpty()) && ((!str2.isEmpty()))) {
            signIn(str1,str2);
        } else {
            Toast.makeText(getApplicationContext(), "PLEASE ENTER THE COMPLETE CREDENTIALS", Toast.LENGTH_LONG).show();
        }
    }


     void signIn(final String email1, String password1) {
        mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "AUTHENTICATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    email.setText("");
                    password.setText("");
                    Intent intent=new Intent(Login.this,BlueConnnect.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "AUTHENTICATION FAILED", Toast.LENGTH_SHORT).show();
                }
                if (!task.isSuccessful()) {
                }
            }
        });
    }

}
