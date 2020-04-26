package com.example.priyaankjoshi.chatapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Friends extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private DatabaseReference userdatabase;
    private FirebaseAuth auth;
    private String current_uid;
    private View view;

    public Friends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView=view.findViewById(R.id.friends_list);
        auth=FirebaseAuth.getInstance();
        current_uid=auth.getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Friends").child(current_uid);
        databaseReference.keepSynced(true);
        userdatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        userdatabase.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

//        FirebaseRecyclerAdapter<FriendsModel,FriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FriendsModel, FriendsViewHolder>(
//                FriendsModel.class,
//                R.layout.recyclerview_layout,
//                FriendsViewHolder.class,
//                databaseReference.child(current_uid)
//        ) {
//            @Override
//            protected void populateViewHolder(final FriendsViewHolder viewHolder, FriendsModel model, int position) {
//
//                viewHolder.setDate(model.getDate());
//
//                String list_user_id=getRef(position).getKey();
//
//                userdatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        String user_name=dataSnapshot.child("name").getValue().toString();
//                        String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
//                        if(dataSnapshot.hasChild("online"))
//                        {
//                            String user_online=dataSnapshot.child("online").getValue().toString();
//                            viewHolder.setUserOnline(user_online);
//                        }
//
//                        viewHolder.setName(user_name);
//                        viewHolder.setImage(thumb_image, getContext());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        };
//        firebaseRecyclerAdapter.startListening();
//        recyclerView.setAdapter((firebaseRecyclerAdapter));

        startListening1();
    }

    public void startListening1()
    {

        Query query=FirebaseDatabase.getInstance().getReference().child("Users");
        //Query query=databaseReference.child(current_uid);
        System.out.println(query.toString());

        final FirebaseRecyclerOptions<FriendsModel> options=new FirebaseRecyclerOptions.Builder<FriendsModel>()
                .setQuery(query, FriendsModel.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FriendsModel, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull FriendsModel model) {

                holder.setDate(model.getDate());
                //System.out.println(model.getDate());
                //System.out.println("Text");

                final String list_user_id=getRef(position).getKey();
                userdatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName=dataSnapshot.child("name").getValue().toString();
                        //System.out.println(userName);
                        String userThumb=dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online"))
                        {
                            String userOnline=dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }
                        holder.setName(userName);
                        holder.setImage(userThumb, getContext());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
                return new FriendsViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View view;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            view=itemView;
        }

        public void setDate(String date)
        {
            TextView userStatusView=view.findViewById(R.id.user_status);
            userStatusView.setText(date);
        }

        public void setName(String name)
        {
            TextView userNameView=view.findViewById(R.id.user_name);
            userNameView.setText(name);
        }

        public void setImage(String thumb_image, Context context)
        {
            CircleImageView all_user_image=(CircleImageView) view.findViewById(R.id.user_pic);
            Picasso.with(context).load(thumb_image).into(all_user_image);
        }

        public void setUserOnline(String online_status)
        {
            ImageView icon=view.findViewById(R.id.user_online_status);
            if(online_status.equals("true"))
            {
                icon.setVisibility(View.VISIBLE);
            }
            else
            {
                icon.setVisibility(View.INVISIBLE);
            }
        }
    }
}
