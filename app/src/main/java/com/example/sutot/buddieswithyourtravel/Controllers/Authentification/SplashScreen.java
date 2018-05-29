package com.example.sutot.buddieswithyourtravel.Controllers.Authentification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.buildDialogNeedToHaveMDorWiFi;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isConnected;

public class SplashScreen extends AppCompatActivity {

    //Beallitjuk a kesleltetes a SplashActivity es a kovetkezo kozott
    private final int mSecondsDelayed = 1;
    //Belepesi pont a Firebase Authentifikacio SDK-ba
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Beallitja a kepernyot full screenbe
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_splash_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Ha mar bevagyunk jelentkezve akkor a fo ablakba ugorjon at, hanem akkor a bejelentkezesi ablakba
        ChooseWhereToJump();
    }

    //Ellenorizzuk milyen allapotba a user, bevan-e vagy nincs jelentkezve
    private boolean AlreadyLoggedIn()
    {
        SharedPreferences sharedPref = getSharedPreferences("LogInSettings", Context.MODE_PRIVATE);
        String alreadyloggedin = sharedPref.getString("alreadyLoggedIn", "Unknown");
        if(!isConnected(SplashScreen.this))
        {
            //szolunk a felhasznalonak hogy nincs internet
            buildDialogNeedToHaveMDorWiFi(SplashScreen.this);
            return false;
        }
        else{
            if ((mFirebaseAuth.getCurrentUser() != null) || (alreadyloggedin.equals("True")))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    private void ChooseWhereToJump()
    {
        if (AlreadyLoggedIn() == true){
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }, mSecondsDelayed * 1000);
        }
        else
        {
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                    finish();
                }
            }, mSecondsDelayed * 1000);
        }
    }
}
