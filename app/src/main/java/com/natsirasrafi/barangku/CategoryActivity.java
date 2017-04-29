package com.natsirasrafi.barangku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity {


    private RecyclerView mCategoryList;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        mCategoryList = (RecyclerView) findViewById(R.id.categoryRecylerView);
        mCategoryList.setHasFixedSize(true);
        mCategoryList.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance().getReference().child("barangku");
        mStorage = FirebaseStorage.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(CategoryActivity.this, MainActivity.class);

                }
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ItemCategory, ItemCategoryViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ItemCategory, ItemCategoryViewHolder>(
                ItemCategory.class,
                R.layout.category_list,
                ItemCategoryViewHolder.class,
                mDatabase

        ) {


            @Override
            protected void populateViewHolder(ItemCategoryViewHolder viewHolder, ItemCategory model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

            }
        };

        mCategoryList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class ItemCategoryViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ItemCategoryViewHolder(View itemView){
            super(itemView);
                    mView = itemView;

        }
        public void setName (String name){
            TextView postCategoryName = (TextView) mView.findViewById(R.id.postCategoryName);
            postCategoryName.setText(name);
        }
        public void setDescription (String description){
            TextView postDescCategory = (TextView) mView.findViewById(R.id.postCategoryDesc);
            postDescCategory.setText(description);
        }
        public void setImage(Context ctx, String image){
            ImageView postImage = (ImageView) mView.findViewById(R.id.postCategoryImage);
            Picasso.with(ctx).load(image).into(postImage);
        }

    }

    public void newCategory(View view) {
        Intent intent = new Intent(CategoryActivity.this, AddCategory.class);
    startActivity(intent);
    }
}
