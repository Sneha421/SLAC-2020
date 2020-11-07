package com.example.gupshup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gupshup.Chat.ChatFragment;
import com.example.gupshup.Common.NodesList;
import com.example.gupshup.FindFriends.FindFriendsFragment;
import com.example.gupshup.Profile.ProfileActivity;
import com.example.gupshup.Requests.RequestsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.NodeList;

public class MainActivity extends AppCompatActivity
{
    TabLayout tabMain;
    ViewPager viewPagerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabMain = findViewById(R.id.tabMain);
        viewPagerMain = (ViewPager) findViewById(R.id.viewPagerMain);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        DatabaseReference databaseReferenceUsers = FirebaseDatabase.getInstance().getReference()
                .child(NodesList.USERS).child(firebaseAuth.getCurrentUser().getUid());

        databaseReferenceUsers.child(NodesList.STATUS).setValue(true);
        databaseReferenceUsers.child(NodesList.STATUS).onDisconnect().setValue(false);


        setViewPagerMain();
    }

    class Adapter extends FragmentPagerAdapter
    {

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    ChatFragment chatFragment = new ChatFragment();
                    return chatFragment;
                case 1:
                    RequestsFragment requestsFragment = new RequestsFragment();
                    return requestsFragment;
                case 2:
                    FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
                    return findFriendsFragment;
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return tabMain.getTabCount();
        }
    }


    private void setViewPagerMain()
    {
        tabMain.addTab(tabMain.newTab().setCustomView(R.layout.tab_chat));
        tabMain.addTab(tabMain.newTab().setCustomView(R.layout.tab_requests));
        tabMain.addTab(tabMain.newTab().setCustomView(R.layout.tab_find_friends));

        tabMain.setTabGravity(TabLayout.GRAVITY_FILL);

        Adapter adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPagerMain.setAdapter(adapter);

        tabMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerMain.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPagerMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabMain));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menuProfile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean doubleBackPress = false;

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();

        if(tabMain.getSelectedTabPosition()>0)
        {
            tabMain.selectTab(tabMain.getTabAt(0));
        }

        else
        {
            if(doubleBackPress)
            {
                finishAffinity();
            }
            else
            {
                doubleBackPress = true;
                Toast.makeText(this,R.string.back_again_to_exit,Toast.LENGTH_SHORT).show();

                //delay

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPress = false;
                    }
                }, 2000);
            }
        }
    }
}