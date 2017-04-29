package com.natsirasrafi.barangku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {
    public static final String TAG = RegisterActivity.class.getSimpleName();

    private TextView mLogin;
    private Button mButtonSignUp;
    private EditText mNamaSignUp;
    private EditText mEmailSignUp;
    private EditText mPasswordSignUp;
    private EditText mConfirmPassSignUp;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();
        createAuthStateListener();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);
        mButtonSignUp = (Button) findViewById(R.id.buttonSignUp);
        mLogin = (TextView) findViewById(R.id.signInText);
        mNamaSignUp = (EditText) findViewById(R.id.accountName);
        mEmailSignUp = (EditText) findViewById(R.id.emailSignUp);
        mPasswordSignUp = (EditText) findViewById(R.id.passwordSignUp);
        mConfirmPassSignUp = (EditText) findViewById(R.id.confirmPassSignUp);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();



            }
        });

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();

            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void startSignUp(){
        final String name = mNamaSignUp.getText().toString().trim();
        final String email = mEmailSignUp.getText().toString().trim();
        final String password = mPasswordSignUp.getText().toString().trim();
         final String confirmPassword = mConfirmPassSignUp.getText().toString().trim();



        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) ){

            mProgress.setMessage("Signing Up...");
            mProgress.show();

            if(!password.equals(confirmPassword)){
                Toast.makeText(RegisterActivity.this, "password not valid" ,Toast.LENGTH_LONG).show();
                mProgress.dismiss();
            }else {
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG,"Authentication Successfull" );
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = mDatabase.child(user_id);
                            current_user_db.child("name").setValue(name);
                            current_user_db.child("image").setValue("default");
                            current_user_db.child("email").setValue(email);
                            current_user_db.child("phone").setValue("");
                            current_user_db.child("address").setValue("");
                            mProgress.dismiss();

                            Intent intentAccount = new Intent(RegisterActivity.this, AccountActivity.class);
                            startActivity(intentAccount);





                        }else {
                            Toast.makeText(RegisterActivity.this, "Authenctication Failed.",Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    }
                });
            }





        }else {
            Toast.makeText(RegisterActivity.this, "form cannot be empty" ,Toast.LENGTH_LONG).show();
        }




    }
    private void createAuthStateListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null ){
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

}
