package com.example.sutot.buddieswithyourtravel.Controllers.Authentification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.buildDialogNeedToHaveMDorWiFi;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isConnected;

public class SplashScreen extends AppCompatActivity {

    //Beallitjuk a kesleltetes a SplashActivity es a kovetkezo kozott
    private final int mSecondsDelayed = 1;
    private boolean mRespond = false;
    //Belepesi pont a Firebase Authentifikacio SDK-ba
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Beallitja a kepernyot full screenbe
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Ha mar bevagyunk jelentkezve akkor a fo ablakba ugorjon at, hanem akkor a bejelentkezesi ablakba
        AlreadyLoggedIn();
    }

    //Ellenorizzuk milyen allapotba a user, bevan-e vagy nincs jelentkezve
    private void AlreadyLoggedIn() {
        SharedPreferences sharedPref = getSharedPreferences("LogInSettings", Context.MODE_PRIVATE);
        String alreadyloggedin = sharedPref.getString("alreadyLoggedIn", "Unknown");
        if (!isConnected(SplashScreen.this)) {
            //szolunk a felhasznalonak hogy nincs internet
            buildDialogNeedToHaveMDorWiFi(SplashScreen.this);
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                    finish();
                }
            }, mSecondsDelayed * 1000);
        } else {
            if ((mFirebaseAuth.getCurrentUser() != null) || (alreadyloggedin.equals("True"))) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                try {
                    reference.child(mFirebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User currUser = dataSnapshot.getValue(User.class);
                            if (currUser != null) {
                                new Handler().postDelayed(new Runnable() {

                                    public void run() {
                                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                        finish();
                                    }
                                }, mSecondsDelayed * 1000);
                            } else {
                                new Handler().postDelayed(new Runnable() {

                                    public void run() {
                                        startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                                        finish();
                                    }
                                }, mSecondsDelayed * 1000);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                                    finish();
                                }
                            }, mSecondsDelayed * 1000);
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                            finish();
                        }
                    }, mSecondsDelayed * 1000);
                }
            } else {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                        finish();
                    }
                }, mSecondsDelayed * 1000);
            }
        }
    }
}