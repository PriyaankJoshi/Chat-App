package com.example.priyaankjoshi.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterAccount extends AppCompatActivity {

    private EditText name,email,password;
    private Button createacc;
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        createacc=findViewById(R.id.button);
        mToolbar=findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sname=name.getText().toString();
                String semail=email.getText().toString();
                String pass=password.getText().toString();
                if(!TextUtils.isEmpty(sname)|| !TextUtils.isEmpty(semail)|| !TextUtils.isEmpty(pass))
                {
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please wait while we create account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register_user(sname,semail,pass);
                }
            }
        });
    }
    private void register_user(final String name, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                    String uid=current_user.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, String> userMap=new HashMap<>();
                    userMap.put("name",name);
                    userMap.put("status","Hello there, I am using Chat App");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");
                    userMap.put("device_token",deviceToken);
                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Intent intent=new Intent(RegisterAccount.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else
                {
                    progressDialog.hide();
                    Toast.makeText(RegisterAccount.this,"REGISTRATION ERROR",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
