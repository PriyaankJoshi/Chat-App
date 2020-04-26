package com.example.priyaankjoshi.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mtoolbar;
    private ViewPager pager;
    private SectionPagerAdapter adapter;
    private TabLayout tabLayout;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mtoolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chat App");
        pager=findViewById(R.id.main_tabPager);
        adapter=new SectionPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabLayout=findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(pager);
        currentUser=mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
        {
            sendToStart();
        }
        else
        {
            databaseReference.child("value").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser!=null) {
            databaseReference.child("value").setValue(false);
        }
    }

    private void sendToStart()
    {
        startActivity(new Intent(MainActivity.this,Start.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.log_out_menu:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            case R.id.account_setting_menu:
                Intent intent=new Intent(MainActivity.this,Settings.class);
                startActivity(intent);
                break;
            case R.id.all_users_menu:
                Intent intent1=new Intent(MainActivity.this,Users.class);
                startActivity(intent1);
                break;
        }
        return true;
    }
}
