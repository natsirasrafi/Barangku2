package com.natsirasrafi.barangku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.R.id.message;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button accountButtonOk;
    private DatabaseReference mDatabase;
    private Button mLogOutButton;
    private ImageButton avatarImage;
    private EditText accountNameET;
    private EditText accountEmailET;
    private EditText accountPhoneET;
    private EditText accountAddresET;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    private Uri mImageUri = null;
    private StorageReference mStorageImage;

    private static final int GALLERY_REQUEST= 1;
    public ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        avatarImage = (ImageButton) findViewById(R.id.avatar);
        accountNameET = (EditText) findViewById(R.id.accountName);
        accountEmailET = (EditText) findViewById(R.id.accountEmail);
        accountPhoneET = (EditText) findViewById(R.id.accountPhone);
        accountAddresET = (EditText) findViewById(R.id.accountAddress);

        accountButtonOk = (Button) findViewById(R.id.accountOkButton);
        mLogOutButton = (Button) findViewById(R.id.buttonLogOut);
        mStorageImage = FirebaseStorage.getInstance().getReference();

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                accountNameET.setText( dataSnapshot.child("name").getValue(String.class));
                accountEmailET.setText(dataSnapshot.child("email").getValue(String.class));
                accountAddresET.setText(dataSnapshot.child("address").getValue(String.class));
                accountPhoneET.setText(dataSnapshot.child("phone").getValue(String.class));

                Picasso.with(AccountActivity.this).load(dataSnapshot.child("image").getValue(String.class)).fit().centerCrop().into(avatarImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //getCurrentUserData();


        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                startActivity(new Intent(AccountActivity.this, MainActivity.class));

            }
        });
        accountButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAccountSettup();
                // startActivity(new Intent(AccountActivity.this, CategoryActivity.class));
                //goToCategory();

            }
        });

        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
               // goToGallery();
            }
        });




    }
    public void getCurrentUserData(){
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                accountNameET.setText( dataSnapshot.child("name").getValue(String.class));
                accountEmailET.setText(dataSnapshot.child("email").getValue(String.class));
                accountAddresET.setText(dataSnapshot.child("address").getValue(String.class));
                accountPhoneET.setText(dataSnapshot.child("phone").getValue(String.class));



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void startAccountSettup(){

        final String name = accountNameET.getText().toString().trim();
        final String email = accountEmailET.getText().toString().trim();
        final String phone = accountPhoneET.getText().toString().trim();
        final String address = accountAddresET.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)  ){
            mProgress.setMessage("Setting Up Account....");
            mProgress.show();

            StorageReference filepath = mStorageImage.child("profile image").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();


                    mDatabase.child(user_id).child("name").setValue(name);
                    mDatabase.child(user_id).child("email").setValue(email);
                    mDatabase.child(user_id).child("phone").setValue(phone);
                    mDatabase.child(user_id).child("address").setValue(address);
                    mDatabase.child(user_id).child("image").setValue(downloadUrl.toString());

                    mProgress.dismiss();
                    goToCategory();

                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToCategory();
    }

    public void goToCategory(){
        Intent intent = new Intent(AccountActivity.this, CategoryActivity.class);
        startActivity(intent);
    }
    public void goToGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST) ;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            avatarImage.setImageURI(mImageUri);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == GALLERY_REQUEST && resultCode ==  RESULT_OK){

             Uri imageUri = data.getData();

             CropImage.activity(imageUri)
                     .setGuidelines(CropImageView.Guidelines.ON)
                     .setAspectRatio(1,1)
                     .start(this);
         }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri mImageUri = result.getUri();

                avatarImage.setImageURI(mImageUri );
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }*/


}
