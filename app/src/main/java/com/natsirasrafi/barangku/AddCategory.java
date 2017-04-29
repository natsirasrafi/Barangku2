package com.natsirasrafi.barangku;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        mPostDescCategory = (EditText)findViewById(R.id.etAddDescCategory);
        mPostNameCategory = (EditText)findViewById(R.id.etAddNameCategory);
        mSelectImage = (ImageButton) findViewById(R.id.imageButtonSelect);
        mBtnSubmitCategory = (Button) findViewById(R.id.btnSubmtiAddCategory);
        mStorage = FirebaseStorage.getInstance().getReference( );
        mDatabase = FirebaseDatabase.getInstance().getReference().child("barangku");
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
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDatabase.push();

                    newPost.child("name").setValue(strNameAddCategory);
                    newPost.child("description").setValue(strDescAddCategory);
                    newPost.child("image").setValue(downloadUrl.toString());

                    mProgress.dismiss();

                    Intent backCategoryIntent = new Intent(AddCategory.this, CategoryActivity.class);
                    startActivity(backCategoryIntent);
                }
            });
        }

    }


}
