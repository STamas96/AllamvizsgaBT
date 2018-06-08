package com.example.sutot.buddieswithyourtravel.Controllers.Authentification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.buildDialogNeedToHaveMDorWiFi;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isConnected;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;

public class LogInActivity extends AppCompatActivity  implements View.OnClickListener{

    //modosithato fieldek
    private EditText mEditUsername,mEditPassword;
    //uj felhasznalo letrehozasa
    private TextView mCreateNewAccount;
    //belepo gombok
    private Button mSignInFirebase;
    //osszekottetes a firebase auth sdk-val
    private FirebaseAuth mFirebaseAuth;
    //betoltesnel hasznalat dialog
    private ProgressDialog mSignInPDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layout megjelenitese
        setContentView(R.layout.activity_log_in);
        //keyboard eltuntetese
        setupUI(findViewById(R.id.LogIn_ParentScrollView));

        //osszekotjuk a programreszletet a kinezettel
        mEditUsername = (EditText) findViewById(R.id.LogIn_EmailEditText);
        mEditPassword = (EditText) findViewById(R.id.LogIn_PasswordEditText);
        mSignInFirebase = (Button) findViewById(R.id.LogIn_SignInButton);
        mCreateNewAccount = (TextView) findViewById(R.id.LogIn_CreateANewAccount);

        //listenerek csatolasa
        mSignInFirebase.setOnClickListener(this);
        mCreateNewAccount.setOnClickListener(this);
    }

    public void onClick(View view) {
        /*ha a felhasznalo beszeretne lepni, akkor megnezzuk ha van internet kapcsolatot, ha van akkor megprobalunk belepni, hanem
        akkor szolunk neki hogy szukseges lesz internetre*/
        if ( view == mSignInFirebase)
        {
            if(!isConnected(LogInActivity.this))
            {
                buildDialogNeedToHaveMDorWiFi(LogInActivity.this);
            }
            else{
                signIn();
            }
        }
        //ha a felhasznalo letreszeretne hozni egy uj fiokot akkor segitunk neki ezt megoldani, atiranyitva az adott ablakra
        if ( view == mCreateNewAccount)
        {
            startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
        }
    }


    //belepes
    private void signIn() {

        //olvassuk a szoveget a bemenetekbol
        String UserEmail = mEditUsername.getText().toString().trim();
        String UserPassword = mEditPassword.getText().toString().trim();

        //ha valamelyik mezot uresen hag yta a felhasznalo akkor szolunk neki
        if (TextUtils.isEmpty(UserEmail)) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(UserPassword)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        //elinditunk egy ablakot amely egy kis turelmet ker a felhasznalotol
        mSignInPDialog = new ProgressDialog(LogInActivity.this);
        setupPDialog(mSignInPDialog,"Loading...","Logging in");

        //megprobalunk bejelentkezni a firebasebe emailel es jelszoval
        mFirebaseAuth.signInWithEmailAndPassword(UserEmail, UserPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*ha sikeresen bejelentkeztunk, elmentjuk ezt offline modeba is majd atlepunk a fo ablakba
                        ugymond elindul maga az applikacio*/
                        if (task.isSuccessful()) {
                            setLoggedIn("True");
                            mSignInPDialog.dismiss();
                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                            Toast.makeText(LogInActivity.this, "Logged in succesfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        //ha nem sikerult belepni akkor kozoljuk a hiba uzenetet a felhasznaloval is, elmondjuk mi tortent
                        else {
                            FirebaseException e = (FirebaseException) task.getException();
                            mSignInPDialog.dismiss();
                            Toast.makeText(LogInActivity.this, "LogIn failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    //egy fuggveny amely altal inicializalni tudunk egy progress dialogot
    private void setupPDialog(ProgressDialog  progressDialog, String title, String message)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    //ez a fuggveny segitsegevel eltaruljuk a bejelentkezes allapotat a cachebe
    private void setLoggedIn(String pm)
    {
        SharedPreferences sharedPref = getSharedPreferences("LogInSettings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alreadyLoggedIn", pm );
        editor.commit();
    }

}
