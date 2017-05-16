package com.natsirasrafi.barangku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddCategory extends Activity {

    ImageButton mSelectImage;
    private static final int GALLERY_REQUEST = 1;
    private EditText mPostNameCategory ;
    private EditText mPostDescCategory;
    private Button mBtnSubmitCategory;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser= mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference( );
        mDatabase = FirebaseDatabase.getInstance().getReference().child("barangku");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User").child(mCurrentUser.getUid());
        mPostDescCategory = (EditText)findViewById(R.id.etAddDescCategory);
        mPostNameCategory = (EditText)findViewById(R.id.etAddNameCategory);
        mSelectImage = (ImageButton) findViewById(R.id.imageButtonSelect);
        mBtnSubmitCategory = (Button) findViewById(R.id.btnSubmtiAddCategory);

       // StorageReference filePath = mStorage.getReferenceFromUrl("gs://barangku-5301d.appspot.com");

        mProgress = new ProgressDialog(this);



        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        mBtnSubmitCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
        mImageUri = data.getData();
        mSelectImage.setImageURI(mImageUri);
    }
    }

    private void startPosting(){
        mProgress.setMessage("Uploading..");
        mProgress.show();

        final String strNameAddCategory = mPostNameCategory.getText().toString().trim();
        final String strDescAddCategory = mPostDescCategory.getText().toString().trim();

        if(!TextUtils.isEmpty(strNameAddCategory) && !TextUtils.isEmpty(strDescAddCategory) && mImageUri != null){

            StorageReference filePath = mStorage.child("Category_Images").child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newPost = mDatabase.push();

                    /*newPost.child("name").setValue(strNameAddCategory);
                    newPost.child("description").setValue(strDescAddCategory);
                    newPost.child("image").setValue(downloadUrl.toString());
*/

                   mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("name").setValue(strNameAddCategory);
                            newPost.child("description").setValue(strDescAddCategory);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue(String.class)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Intent backCategoryIntent = new Intent(AddCategory.this, CategoryActivity.class);
                                        startActivity(backCategoryIntent);
                                    }else {

                                    }
                                    //startActivity(new Intent(AddCategory.this, CategoryActivity.class));

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });




                    mProgress.dismiss();
                    //Intent backCategoryIntent = new Intent(AddCategory.this, CategoryActivity.class);
                    //startActivity(backCategoryIntent);

                }
            });
        }

    }


}
