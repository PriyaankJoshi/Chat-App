package com.example.priyaankjoshi.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Users extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        context=getApplicationContext();
        toolbar=findViewById(R.id.users_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView=findViewById(R.id.users_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseRecyclerAdapter<Model,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, UsersViewHolder>(
//                Model.class,
//                R.layout.recyclerview_layout,
//                UsersViewHolder.class,
//                databaseReference) {
//            @Override
//            protected void populateViewHolder(UsersViewHolder viewHolder, Model model, int position) {
//                viewHolder.setData(model.getName(),model.getThumb_image(),model.getStatus());
//
//                final String uid=getRef(position).getKey();
//
//                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent=new Intent(Users.this,Profile.class);
//                        intent.putExtra("user_id",uid);
//                        startActivity(intent);
//                    }
//                });
//            }
//        };

        Query query=FirebaseDatabase.getInstance().getReference().child("Users");

        final FirebaseRecyclerOptions<Model> options=new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(query, Model.class).build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Model, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Model model) {

                holder.setData(model.getName(),model.getThumb_image(),model.getStatus());

                final String uid=getRef(position).getKey();
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(Users.this,Profile.class);
                        intent.putExtra("user_id",uid);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);

                return new UsersViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setData(String name,String imageUrl, String status)
        {
            TextView all_user_name=(TextView) mview.findViewById(R.id.user_name);
            all_user_name.setText(name);
            CircleImageView all_user_image=(CircleImageView) mview.findViewById(R.id.user_pic);
            Picasso.with(getAppContext()).load(imageUrl).into(all_user_image);
            TextView all_user_status=(TextView) mview.findViewById(R.id.user_status);
            all_user_status.setText(status);
        }
        private static Context getAppContext()
        {
            return context;
        }
    }
}
