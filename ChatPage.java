package com.example.priyaankjoshi.chatapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatPage extends AppCompatActivity {

    private String current_uid;
    private DatabaseReference userdatabase;
    private TextView mname;
    private CircleImageView mimage;
    private EditText msg;
    private Button send;
    private RecyclerView messageList;
    private final List<Message> list = new ArrayList<>();
    private LinearLayoutManager linearLayout;
    private MessageAdapter adapter;
    private String chat_id;
    private DatabaseReference messageDatabase;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        mimage=findViewById(R.id.chat_image);
        mname=findViewById(R.id.name);
        msg=findViewById(R.id.message);
        send=findViewById(R.id.send_button);

        adapter=new MessageAdapter(list);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Message");

        messageList=findViewById(R.id.chat_recycler_view);
        linearLayout=new LinearLayoutManager(this);

        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(linearLayout);
        messageList.setAdapter(adapter);

//        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChild("Message"))
//                {
//                    loadMessage();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        chat_id=getIntent().getStringExtra("user_id");

        messageDatabase=FirebaseDatabase.getInstance().getReference().child("Message");

        current_uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        userdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(chat_id);

        userdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();

                mname.setText(name);
                Picasso.with(ChatPage.this).load(thumb_image).placeholder(R.drawable.user).into(mimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(msg.getText()==null || msg.getText().equals(""))
                {
                    Toast.makeText(ChatPage.this,"Type something to send the message", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String message=msg.getText().toString();
                    postMessage(chat_id, message);
                    msg.setText("");
                }
            }
        });
    }

    private void loadMessage()
    {
//        messageDatabase.child(current_uid).child(chat_id).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                Message message=dataSnapshot.getValue(Message.class);
//                list.add(message);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
    }

    private void postMessage(String chat_id, String message)
    {
        String push_id=messageDatabase.child(current_uid).child(chat_id).push().getKey();
        messageDatabase.child(current_uid).child(chat_id).child(push_id).child("message").setValue(message);
        messageDatabase.child(current_uid).child(chat_id).child(push_id).child("type").setValue(current_uid);
        messageDatabase.child(chat_id).child(current_uid).child(push_id).child("message").setValue(message);
        messageDatabase.child(chat_id).child(current_uid).child(push_id).child("type").setValue(current_uid);
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.child(current_uid).child(chat_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Message message=dataSnapshot.getValue(Message.class);
                list.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
