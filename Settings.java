package com.example.priyaankjoshi.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Settings extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private CircleImageView mimage;
    private TextView mname, mstatus;
    private Button status_btn, imagebtn;
    private static final int GALLERY_PIC = 1;
    private StorageReference imageStorage;
    private ProgressDialog progressDialog;
    private String setimage,setthumbimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        imageStorage=FirebaseStorage.getInstance().getReference();
        mimage = findViewById(R.id.user_image);
        mname = findViewById(R.id.user_name);
        mstatus = findViewById(R.id.user_status);
        status_btn = findViewById(R.id.status_button);
        try {
            status_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String status_value = mstatus.getText().toString();
                    Intent intent = new Intent(Settings.this, StatusActivity.class);
                    intent.putExtra("status_value", status_value);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            Toast.makeText(Settings.this, "Error", Toast.LENGTH_SHORT).show();
        }
        imagebtn = findViewById(R.id.name_button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumbnail = dataSnapshot.child("thumb_image").getValue().toString();
                mname.setText(name);
                mstatus.setText(status);
                if(!image.equals("default"))
                {
                    Picasso.with(Settings.this).load(thumbnail).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user).into(mimage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(Settings.this).load(thumbnail).placeholder(R.drawable.user).into(mimage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_PIC);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PIC && resultCode == RESULT_OK) {
            Uri image_uri = data.getData();
            CropImage.activity(image_uri).setAspectRatio(1, 1).start(Settings.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please wait while we upload image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                try {
                    final String current_user_id = currentUser.getUid();
                    Uri resultUri = result.getUri();
                    File thumb_filepath = new File(resultUri.getPath());
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    final StorageReference filepath = imageStorage.child("profile_images").child(current_user_id + ".jpg");
                    final StorageReference thumb_path_reference = imageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                //final String download_url = task.getResult().getUploadSessionUri().toString();
                                //final String download_url=task.getResult().getStorage().getDownloadUrl().toString();
                                //final String download_url=filepath.getDownloadUrl().toString();
                                //final String download_url=task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                                //final String download_url=filepath.getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_path_reference.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        if(thumb_task.isSuccessful()) {

                                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    setimage = task.getResult().toString();
                                                    databaseReference.child("image").setValue(setimage);
                                                }
                                            });

                                            //final String thumb_downloadUrl=thumb_task.getResult().getUploadSessionUri().toString();
                                            thumb_path_reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    //Toast.makeText(Settings.this,task.getResult().toString(),Toast.LENGTH_LONG).show();
                                                    setthumbimage = task.getResult().toString();
                                                    databaseReference.child("thumb_image").setValue(setthumbimage);
                                                }
                                            });
                                            progressDialog.dismiss();
                                        }
                                        //Task<Uri> uri=thumb_task.getResult().getStorage().getDownloadUrl();
                                        //while (!uri.isComplete());
                                        //final Uri thumb_downloadUrl=uri.getResult();
                                        //final String thumb_downloadUrl = thumb_task.getResult().getUploadSessionUri().toString();

//                                        if (thumb_task.isSuccessful()) {
//
//                                            Map update_hashMap = new HashMap();
//                                            update_hashMap.put("image", setimage);
//                                            update_hashMap.put("thumb_image", setthumbimage);
//
//                                            databaseReference.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        progressDialog.dismiss();
//                                                        //Toast.makeText(Settings.this, thumb_downloadUrl, Toast.LENGTH_SHORT).show();
//                                                    } else {
//                                                        Toast.makeText(Settings.this, "Error, Image cannot be uploaded", Toast.LENGTH_SHORT).show();
//                                                        progressDialog.dismiss();
//                                                    }
//                                                }
//                                            });


//                                        } else {
//                                            Toast.makeText(Settings.this, "Error while uploading...", Toast.LENGTH_SHORT).show();
//                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(Settings.this, "Not Working", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(Settings.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
