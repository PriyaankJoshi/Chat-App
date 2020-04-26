package com.example.priyaankjoshi.chatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private FirebaseAuth auth;
    //private DatabaseReference databaseReference;
    private List<Message> list;
    private Context context;

    public MessageAdapter(List<Message> list)
    {
        this.list=list;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        auth=FirebaseAuth.getInstance();

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_layout, parent, false);

        context=view.getContext();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        Message msg=list.get(position);
        //holder.message.setText(msg.getMessage());

        String messageSenderId = auth.getCurrentUser().getUid();
        //String fromUserId = msg.getType();
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.hasChild("image"))
//                {
//                    String receiveImage = dataSnapshot.child("thumb_image").getValue().toString();
//                    Picasso.with(context).load(receiveImage).placeholder(R.drawable.user).into(holder.image);
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        if(fromUserId.equals(messageSenderId))
//        {
//            holder.image.setVisibility(View.INVISIBLE);
//            holder.message.setText(msg.getMessage());
//        }
//        else
//        {
//            holder.image.setVisibility(View.VISIBLE);
//            holder.message.setText(msg.getMessage());
//        }

        holder.message.setText(msg.getMessage());
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView message;
        public CircleImageView image;
        public MessageViewHolder(View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.message_text_layout);
            image=itemView.findViewById(R.id.message_profile_layout);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
