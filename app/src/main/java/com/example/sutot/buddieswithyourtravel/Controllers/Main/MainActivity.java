package com.example.sutot.buddieswithyourtravel.Controllers.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.CreateTripActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.FavouritesFragment;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.HomeFragment;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.MyProfileFragment;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.disableShiftMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //belepo pont a Firebase Authentifikacio SDK-ba, absztrakt osztaly
    private FirebaseAuth firebaseAuth;
    private TextView m;
    private BottomNavigationView mBottomNavBar;
    private FrameLayout mBotFrameLayout;
    private HomeFragment mHomeFragment;
    private MyProfileFragment mMyProfileFragment;
    private FavouritesFragment mFavouritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.Main_Top_NavBar_AppName);
        mBottomNavBar = (BottomNavigationView) findViewById(R.id.Main_Bot_Navbar);
        mBotFrameLayout = (FrameLayout) findViewById(R.id.Main_Frame_Layout_Main);
        mHomeFragment = new HomeFragment();
        mMyProfileFragment = new MyProfileFragment();
        mFavouritesFragment = new FavouritesFragment();

        disableShiftMode(mBottomNavBar);

        SpannableString text = new SpannableString(getResources().getString(R.string.app_name_bold));
        text.setSpan(new TextAppearanceSpan(this.getApplicationContext(), R.style.MainScreen_AppName_Part1), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(this.getApplicationContext(), R.style.MainScreen_AppName_Part2), 8, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(text, TextView.BufferType.SPANNABLE);

        firebaseAuth = FirebaseAuth.getInstance();
        m = (TextView) findViewById(R.id.abc);

        if (firebaseAuth.getCurrentUser() != null){
            m.setText(firebaseAuth.getCurrentUser().getEmail());
        }

        m.setOnClickListener(this);
        mBottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch ( item.getItemId()) {
                    case R.id.Main_Bot_NavBar_Home:
                        mBotFrameLayout.setBackgroundColor(Color.parseColor("#376589"));
                        setFragment(mHomeFragment);
                        return true;
                    case  R.id.Main_Bot_NavBar_Favourites:
                        mBotFrameLayout.setBackgroundColor(Color.parseColor("#000000"));
                        setFragment(mFavouritesFragment);
                        return true;
                    case R.id.Main_Bot_NavBar_OwnProfile:
                        mBotFrameLayout.setBackgroundColor(Color.parseColor("#ff0000"));
                        setFragment(mMyProfileFragment);
                        return true;
                    case R.id.Main_Bot_NavBar_AddNewTrip:
                        startActivity(new Intent(MainActivity.this, CreateTripActivity.class));
                }
                return false;
            }
        });
        mBottomNavBar.setSelectedItemId(R.id.Main_Bot_NavBar_Home);
    }

    public void onClick(View view)
    {
        if (view == m)
        {
            firebaseAuth.signOut();
            SharedPreferences sharedPref = getSharedPreferences("LogInSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("alreadyLoggedIn", "loggedout" );
            editor.commit();
            startActivity(new Intent(MainActivity.this, LogInActivity.class));
            finish();
        }

    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.Main_Frame_Layout_Main,fragment);
        mFragmentTransaction.commit();
    }
}


