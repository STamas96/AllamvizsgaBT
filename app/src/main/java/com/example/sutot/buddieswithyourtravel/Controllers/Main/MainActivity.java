package com.example.sutot.buddieswithyourtravel.Controllers.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.FavouritesFragment;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.HomeFragment;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.MyProfileFragment;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.disableShiftMode;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isConnected;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //felso lec, kozep szovege deklaralasa
    private TextView mTopBarCenterTV;
    //also navigacios lec deklaralasa
    private BottomNavigationView mBottomNavBar;
    //fragmentek illetve a keret deklaralasa
    private FrameLayout mBotFrameLayout;
    private HomeFragment mHomeFragment;
    private MyProfileFragment mMyProfileFragment;
    private FavouritesFragment mFavouritesFragment;
    //felso lec ket szelso icon deklaralasa
    public ImageView mTopLeft, mTopRight;
    //jelenlegi felhasznalo tarolasara keszitett valtozo
    public User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //csatolasok a layout itemek es programkod kozott
        mTopBarCenterTV = (TextView) findViewById(R.id.Main_Top_NavBar_Center);
        mBottomNavBar = (BottomNavigationView) findViewById(R.id.Main_Bot_Navbar);
        mBotFrameLayout = (FrameLayout) findViewById(R.id.Main_Frame_Layout_Main);
        mTopLeft = (ImageView) findViewById(R.id.Main_Top_NavBar_Left);
        mTopRight = (ImageView) findViewById(R.id.Main_Top_NavBar_Right);

        //fragmensek letrehozasa
        mHomeFragment = new HomeFragment();
        mMyProfileFragment = new MyProfileFragment();
        mFavouritesFragment = new FavouritesFragment();

        //also navbar modositasara hasznalt fuggveny
        disableShiftMode(mBottomNavBar);

        //a kivalasztott item fuggvenyeben letrehozando dolgok
        mBottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Main_Bot_NavBar_Home:
                        initHome();
                        return true;
                    case R.id.Main_Bot_NavBar_Favourites:
                        mBotFrameLayout.setBackgroundColor(Color.parseColor("#000000"));
                        setFragment(mFavouritesFragment);
                        return true;
                    case R.id.Main_Bot_NavBar_OwnProfile:
                        initOwnProfile();
                        return true;
                    case R.id.Main_Bot_NavBar_AddNewTrip:
                        startActivity(new Intent(MainActivity.this, CreateTripActivity.class));
                }
                return false;
            }
        });
        //alapertelmezett fragmens kezdeskor
        mBottomNavBar.setSelectedItemId(R.id.Main_Bot_NavBar_Home);
        //listenerek csatolasa
        mTopLeft.setOnClickListener(this);
        mTopRight.setOnClickListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        currUser = setCurrentUser();

    }

    @Override
    public void onClick(View view) {

    }

    //fragmensek beallitasa
    private void setFragment(Fragment fragment) {
        FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.Main_Frame_Layout_Main, fragment);
        mFragmentTransaction.commit();
    }

    //home fragmens elinditasakor, szukseges muveletek pl. iconok kicserelese, cim beallitasa
    private void initHome() {
        mTopLeft.setImageResource(R.drawable.ic_search_blue);
        mTopRight.setImageResource(R.drawable.ic_chat_blue);
        SpannableString text = new SpannableString(getResources().getString(R.string.app_name_bold));
        text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.MainScreen_AppName_Part1), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.MainScreen_AppName_Part2), 8, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTopBarCenterTV.setText(text, TextView.BufferType.SPANNABLE);
        setFragment(mHomeFragment);
    }

    //ugyanaz csak a profil megjelenitesere
    private void initOwnProfile() {
        mTopLeft.setImageResource(R.drawable.ic_friend);
        mTopRight.setImageResource(R.drawable.ic_log_out);
        mBotFrameLayout.setBackgroundColor(Color.parseColor("#ff0000"));
        SpannableString text = new SpannableString(currUser.getUserName());
        text.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.MainScreen_AppName_Part3), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTopBarCenterTV.setText(text, TextView.BufferType.SPANNABLE);
        setFragment(mMyProfileFragment);
    }

    //a jelenlegi felhasznalo adatai
    public User setCurrentUser() {
        //lekerjuk a referenciat a felhasznalo tablahoz
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        try {
            /*megkeressuk a mi felhasznalonkat, ha megkapjuk atalittjuk az adatait, ha pedig nem akkor valami hiba tortent es biztonsagi
            okokbol visszaugrunk a belepesi fazisba*/
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currUser = new User(dataSnapshot.getValue(User.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return null;
        }
        return currUser;
    }
}


