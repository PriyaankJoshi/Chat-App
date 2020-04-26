package com.example.priyaankjoshi.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private TextInputLayout status;
    private Button statusbtn;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        toolbar=findViewById(R.id.status_appbar);
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=currentUser.getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        status=findViewById(R.id.status_input);
        statusbtn=findViewById(R.id.status_button);
        String status_value=getIntent().getStringExtra("status_value");
        status.getEditText().setText(status_value);
        statusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog=new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("Please wait while we save changes");
                progressDialog.show();
                String st=status.getEditText().getText().toString();
                databaseReference.child("status").setValue(st).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            progressDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(StatusActivity.this,"Status not changed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
