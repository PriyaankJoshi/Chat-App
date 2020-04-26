package com.example.priyaankjoshi.chatapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    private String current_uid;
    private DatabaseReference databaseReference;

    public Chat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView=view.findViewById(R.id.chat_recycler_view);
        auth=FirebaseAuth.getInstance();
        current_uid="XQDJPw8ZcBPpnrsEuuVIHxn3Im03";//auth.getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query=databaseReference;

        final FirebaseRecyclerOptions<Model> options=new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(query,Model.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model,ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Model model) {

                final String uid=getRef(position).getKey();
                holder.setData(model.getName(), model.getThumb_image(), getContext(), model.getStatus());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(getActivity(), ChatPage.class);
                        intent.putExtra("user_id", uid);
                        startActivity(intent);
                    }
                });
            }



            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
                return new ChatViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        public ChatViewHolder(View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setData(String name, String imageUrl, Context context, String status)
        {
            TextView all_user_name= view.findViewById(R.id.user_name);
            all_user_name.setText(name);
            CircleImageView all_user_image= view.findViewById(R.id.user_pic);
            Picasso.with(context).load(imageUrl).into(all_user_image);
            TextView all_user_status= view.findViewById(R.id.user_status);
            all_user_status.setText(status);
        }
    }
}
