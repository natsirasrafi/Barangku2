package com.natsirasrafi.barangku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button mLoginButton;
    private EditText mEmailField;
    private EditText mPasswordField;
    public TextView mSignUpText;
    ProgressDialog mLoginProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mLoginProgress = new ProgressDialog(this);

        mEmailField = (EditText) findViewById(R.id.fieldEmail);
        mPasswordField = (EditText) findViewById(R.id.fieldPassword);
        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mSignUpText = (TextView) findViewById(R.id.signUpText);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){

                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));

                }
            }
        };

        mLoginButton.setOnClickListener(new View.OnClickListener(){
                @Override
            public void onClick(View view){
                    startSignIn();
                }
        });

        mSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();



    }
    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){
        mLoginProgress.setMessage("Loging In..");
        mLoginProgress.show();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

            Toast.makeText(MainActivity.this, "Fields are Empty", Toast.LENGTH_LONG).show();
            mLoginProgress.dismiss();
        }else{

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Sign In Problem", Toast.LENGTH_LONG).show();
                        mLoginProgress.dismiss();
                    }
                }
            });

        }



    }


}
