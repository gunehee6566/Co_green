package ca.bcit.co_green.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import ca.bcit.co_green.MainActivity;
import ca.bcit.co_green.R;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SectionsPageAdapter pagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.view_pager);
        pager.setAdapter(pagerAdapter);

        // Attach the ViewPager to the TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);


        /**
         * Uncomment this to auto login
         */
//        fAuth = FirebaseAuth.getInstance();
//        if (fAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }
    }

    public void setTab(int index) {
        pager.setCurrentItem(index, true);
    }

    public class SectionsPageAdapter extends FragmentPagerAdapter {
        public SectionsPageAdapter(FragmentManager fm) { super(fm); }

        @Override
        public int getCount() { return 2; }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SignupTabFragment();
                case 1:
                    return new LoginTabFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.register);
                case 1:
                    return getResources().getText(R.string.login);
            }
            return null;
        }

    }
}