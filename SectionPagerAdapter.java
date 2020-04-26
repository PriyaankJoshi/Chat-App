package com.example.priyaankjoshi.chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                Request request=new Request();
                return request;
            case 1:
                Chat chat=new Chat();
                return chat;
            case 2:
                Friends friends=new Friends();
                return friends;
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "REQUESTS";
            case 1:
                return "CHAT";
            case 2:
                return "FRIENDS";
                default:
                    return null;
        }
    }
}
