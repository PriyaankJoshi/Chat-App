package com.example.priyaankjoshi.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private ImageView profile_image;
    private TextView name,status,total_friends;
    private Button request, decline_request;
    private DatabaseReference databaseReference;
    private DatabaseReference friends_database;
    private DatabaseReference friends_request_database;
    private DatabaseReference notification_database;
    private FirebaseUser current_user;
    private ProgressDialog progressDialog;
    private String current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id=getIntent().getStringExtra("user_id");
        current_user=FirebaseAuth.getInstance().getCurrentUser();
        friends_database=FirebaseDatabase.getInstance().getReference().child("Friends");
        friends_request_database=FirebaseDatabase.getInstance().getReference("Friend_request");
        //notification_database=FirebaseDatabase.getInstance().getReference().child("notification");
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        profile_image=findViewById(R.id.user_photo);
        name=findViewById(R.id.profile_display_name);
        status=findViewById(R.id.user_status);
        total_friends=findViewById(R.id.total_friends);
        request=findViewById(R.id.request);
        decline_request=findViewById(R.id.decline_request);

        current_state="not_friends";

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading user data");
        progressDialog.setMessage("Please wait while we load user data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pname=dataSnapshot.child("name").getValue().toString();
                String pstatus=dataSnapshot.child("status").getValue().toString();
                String pimage=dataSnapshot.child("image").getValue().toString();

                name.setText(pname);
                status.setText(pstatus);
                Picasso.with(Profile.this).load(pimage).placeholder(R.drawable.user).into(profile_image);


                friends_request_database.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id))
                        {
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received"))
                            {
                                current_state="request_received";
                                request.setText("ACCEPT FRIEND REQUEST");

                                decline_request.setVisibility(View.VISIBLE);
                                decline_request.setEnabled(true);
                            }
                            else if(req_type.equals("sent"))
                            {
                                current_state="request_sent";
                                request.setText(R.string.cancel_request);

                                decline_request.setVisibility(View.INVISIBLE);
                                decline_request.setEnabled(false);
                            }
                            progressDialog.dismiss();
                        }
                        else
                        {
                            friends_database.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        current_state="friends";
                                        request.setText("UNFRIEND THIS PERSON");

                                        decline_request.setVisibility(View.INVISIBLE);
                                        decline_request.setEnabled(false);
                                    }
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setEnabled(false);
                if(current_state.equals("not_friends"))
                {
                    friends_request_database.child(current_user.getUid()).child(user_id).child("request_type")
                    .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                friends_request_database.child(user_id).child(current_user.getUid()).child("request_type")
                                        .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
//                                            HashMap<String, String>  notification=new HashMap<>();
//                                            notification.put("from",current_user.getUid());
//                                            notification.put("type","request");

//                                            notification_database.child(user_id).push().setValue(notification)
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if(task.isSuccessful())
//                                                            {
//                                                                current_state="request_sent";
//                                                                request.setText(R.string.cancel_request);
//
//                                                                decline_request.setVisibility(View.INVISIBLE);
//                                                                decline_request.setEnabled(false);
//
//                                                                Toast.makeText(Profile.this,"Request sent",Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    });

                                            current_state="request_sent";
                                            request.setText(R.string.cancel_request);

                                            decline_request.setVisibility(View.INVISIBLE);
                                            decline_request.setEnabled(false);

                                            Toast.makeText(Profile.this,"Request sent",Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(Profile.this,"Request not sent",Toast.LENGTH_SHORT).show();
                                        }

                                        request.setEnabled(true);
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(Profile.this,"Request not sent",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if(current_state.equals("request_sent"))
                {
                    friends_request_database.child(current_user.getUid()).child(user_id).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                friends_request_database.child(user_id).child(current_user.getUid()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            current_state="not_friends";
                                            request.setText(R.string.send_request);

                                            decline_request.setVisibility(View.INVISIBLE);
                                            decline_request.setEnabled(false);
                                        }
                                        else
                                        {
                                            Toast.makeText(Profile.this,"Request not cancelled",Toast.LENGTH_SHORT).show();
                                        }

                                        request.setEnabled(true);
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(Profile.this,"Request not canceled",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                //UNFRIEND THE PERSON
                if(current_state.equals("friends"))
                {
                    friends_database.child(current_user.getUid()).child(user_id).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            friends_database.child(user_id).child(current_user.getUid()).removeValue();
                        }
                    });
                }

                // REQUEST RECEIVED
                if(current_state.equals("request_received"))
                {
                    final String date=DateFormat.getDateInstance().format(new Date());
                    friends_database.child(current_user.getUid()).child(user_id).setValue(date)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            friends_database.child(user_id).child(current_user.getUid()).setValue(date)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    friends_request_database.child(current_user.getUid()).child(user_id).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        friends_request_database.child(user_id).child(current_user.getUid()).removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful())
                                                                        {
                                                                            current_state="friends";
                                                                            request.setText("UNFRIEND THIS PERSON");

                                                                            decline_request.setVisibility(View.INVISIBLE);
                                                                            decline_request.setEnabled(false);
                                                                        }
                                                                        else
                                                                        {
                                                                            Toast.makeText(Profile.this,"Request not cancelled",Toast.LENGTH_SHORT).show();
                                                                        }

                                                                        request.setEnabled(true);
                                                                    }
                                                                });
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(Profile.this,"Request not canceled",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });
                }

            }
        });
    }
}
