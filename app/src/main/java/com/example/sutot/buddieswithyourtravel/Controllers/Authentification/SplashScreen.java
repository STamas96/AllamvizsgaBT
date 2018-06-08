package com.example.sutot.buddieswithyourtravel.Controllers.Authentification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
        //Megjeleniti a neki megfelelo layoutot
        setContentView(R.layout.activity_splash_screen);
        //A firebase autentifikalis objektumot peldanyositjuk
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Ellenorizzuk az aktualis allapotot, melyik legyen a kovetkezo ablak
        AlreadyLoggedIn();
    }

    //Ellenorizzuk az aktualis allapotot, melyik legyen a kovetkezo ablak
    private void AlreadyLoggedIn() {
        //megnezzuk a cache filekban hogy bevan lepve e mar a felhasznalo vagy nincs ( offline mode )
        SharedPreferences sharedPref = getSharedPreferences("LogInSettings", Context.MODE_PRIVATE);
        String alreadyloggedin = sharedPref.getString("alreadyLoggedIn", "Unknown");

        /*ellenorizzuk hogy ha van internet kapcsolatunk, maskepp kirrjuk hogy szukseg lesz erre majd a bejelentkezo kepernyohoz
        lepunk ha a felhasznalo elolvasta*/
        if (!isConnected(SplashScreen.this)) {
            AlertDialog.Builder Builder = new AlertDialog.Builder(this);
            Builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit")
                    .setCancelable(false)
                    .setTitle("No Internet Connection")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Handler().postDelayed(new Runnable() {

                                public void run() {
                                    startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                                    finish();
                                }
                            }, mSecondsDelayed * 1000);
                        }
                    })
                    .show();

        } // ha van internetunk is akkor megnezzuk hogy ha mar bevan jelentkezve a felhasznalo
        else {
            if ((mFirebaseAuth.getCurrentUser() != null) || (alreadyloggedin.equals("True"))) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                try {
                    //ha sikerul hozzaferni az aktualis felhasznalo adataihoz akkor a fo ablakba ugrunk be
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
                            } //ha nem sikerul akkor ugyancsak megkerjuka  felhasznalot jelentkezzen be
                            else {
                                new Handler().postDelayed(new Runnable() {

                                    public void run() {
                                        startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                                        finish();
                                    }
                                }, mSecondsDelayed * 1000);
                            }
                        }
                        //ha a felhasznalo megszakitja a folyamatot akkor is a bejelentkezo ablakba ugrunk be
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
                } //ha egyeb mas hiba lep kozbe akkor errol tanustijuk a felhasznalot, illetve a bejelentkezo ablakba lepunk
                catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            startActivity(new Intent(SplashScreen.this, LogInActivity.class));
                            finish();
                        }
                    }, mSecondsDelayed * 1000);
                }
            }
            //ha nincs bejelentkezve a felhasznalo akkor csak a bejelentkezeshez lepunk be
            else {
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