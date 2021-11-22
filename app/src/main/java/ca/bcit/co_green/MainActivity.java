package ca.bcit.co_green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import ca.bcit.co_green.home.HomeFragment;
import ca.bcit.co_green.ranking.RankingFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                switch(item.getItemId()) {
                    case R.id.page_home:
                        manager.beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                        return true;
                    case R.id.page_add:
                        manager.beginTransaction().replace(R.id.frame, new InputFragment()).commit();
                        return true;
                    case R.id.page_rank:
                        manager.beginTransaction().replace(R.id.frame, new RankingFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }

    public void goToInput(View view) {
        Intent k = new Intent(this, InputActivity.class);
        startActivity(k);
    }


}