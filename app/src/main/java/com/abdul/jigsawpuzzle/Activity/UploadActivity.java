package com.abdul.jigsawpuzzle.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.Interface.IComputerVision;
import com.abdul.jigsawpuzzle.Model.CategoryItem;
import com.abdul.jigsawpuzzle.Model.ComputerVision;
import com.abdul.jigsawpuzzle.Model.PuzzleItem;
import com.abdul.jigsawpuzzle.Model.URLUpload;
import com.abdul.jigsawpuzzle.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    ImageView image_preview;
    Button btn_upload, btn_browser, btn_submit;
    MaterialSpinner spinner;

    //Material Spinner Data
    Map<String,String> spinnerData = new HashMap<>();

    private Uri filePath;

    String categoryIdSelect="", directUrl="", nameOfFile="";

    //FireStorage
    StorageReference storage;
    DatabaseReference storageReference;

    IComputerVision iComputerVision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        iComputerVision = Common.getComputerVisionAPI();

        //Firebase Storage Init
        storage = FirebaseStorage.getInstance().getReference();
        storageReference = FirebaseDatabase.getInstance().getReference();

        //View
        image_preview = (ImageView)findViewById(R.id.image_preview);
        btn_browser = (Button)findViewById(R.id.btn_browser);
        btn_upload = (Button)findViewById(R.id.btn_upload);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        spinner = (MaterialSpinner)findViewById(R.id.spinner);

        //Load Spinner data
        loadCategoryToSpinner();

        btn_browser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedIndex() == 0) //Hint , not choose anymore
                    Toast.makeText(UploadActivity.this, "Please choose category", Toast.LENGTH_SHORT).show();
                else
                    uploadImage();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectAdultContent(directUrl);
            }
        });
    }

    private void detectAdultContent(final String directUrl) {
        if (directUrl.isEmpty())
            Toast.makeText(this, "Picture not Uploaded", Toast.LENGTH_SHORT).show();
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Analyzing...");
            progressDialog.show();

            iComputerVision.analyzeImage(Common.getAPIAdultEndPoint(), new URLUpload(directUrl))
                    .enqueue(new Callback<ComputerVision>() {
                        @Override
                        public void onResponse(Call<ComputerVision> call, Response<ComputerVision> response) {
                            if (response.isSuccessful()){
                                if (!response.body().getAdult().isAdultContent()){
                                    progressDialog.dismiss();
                                    saveUrlToCategory(categoryIdSelect, directUrl);
                                    Toast.makeText(UploadActivity.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    deleteFileFromStorage(nameOfFile);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ComputerVision> call, Throwable t) {

                        }
                    });
        }
    }

    //Delete from Cloud Storage
    private void deleteFileFromStorage(String nameOfFile) {
        StorageReference storageReference = storage.child("images/" +nameOfFile);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UploadActivity.this, "Adult Content detect", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    //Upload to Cloud Storage
    private void uploadImage() {
        if (filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            nameOfFile = UUID.randomUUID().toString();
            final StorageReference reference = storage.child(new StringBuilder("images/" +nameOfFile)
            .toString());

            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    directUrl =  uri.toString();
                                    btn_submit.setEnabled(true);
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded :" +(int)progress+"%");
                        }
                    });
        }
    }

    //Upload to Firebase Real-time Database Category
    private void saveUrlToCategory(String categoryIdSelect, String imageUrl){
        FirebaseDatabase.getInstance()
                .getReference(Common.PUZZLES)
                .push() //Gen key
                .setValue(new PuzzleItem(imageUrl, categoryIdSelect))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    //Select Image from device
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image_preview.setImageBitmap(bitmap);
                btn_upload.setEnabled(true);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    private void loadCategoryToSpinner() {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                            CategoryItem item = postSnapShot.getValue(CategoryItem.class);
                            String key = postSnapShot.getKey();

                            spinnerData.put(key, item.getName());
                        }

                        Object[] valueArray = spinnerData.values().toArray();
                        List<Object> valueList = new ArrayList<>();
                        valueList.add(0, "Category"); //We will as  first item is Hint
                        valueList.addAll(Arrays.asList(valueArray)); //And add all remaining category name
                        spinner.setItems(valueList);//set source data for spinner
                        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                //When user choose category, we will get category (key)
                                Object[] keyArray = spinnerData.keySet().toArray();
                                List<Object> keyList = new ArrayList<>();
                                keyList.add(0, "Category_Key");
                                keyList.addAll(Arrays.asList(keyArray));
                                categoryIdSelect = keyList.get(position).toString(); //Assign key when user choose category
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        deleteFileFromStorage(nameOfFile);
        super.onBackPressed();
    }
}
