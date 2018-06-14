package com.example.sutot.buddieswithyourtravel.Controllers.Authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.getDateFromDatePicker;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isValidEmail;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.validateBirthday;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.validateData;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.validatePassword;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.validateUsername;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //az EditTextek deklaralasa
    private EditText mEditName, mEditEmail, mEditPassword, mEditUserName;
    //a letezo felhasznalo opciora letrehozott szoveg deklaralasa
    private TextView mExistingAccount;
    //belepo gomb deklaralasa
    private Button mSignUp;
    //ebbe a valtozoba fogjuk tarolni az autentifikalashoz szukseges firebase objektumot
    private FirebaseAuth mFirebaseAuth;
    //betoltesnel hasznalat dialogus
    private ProgressDialog mSignUpPDialog;
    //felhasznalo szuletesnapjanak a bevitele
    private DatePicker mUserBirthday;
    //a Firebase adatbazisahoz szukseges referenciat fogjuk benne tarolni
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //keyboard eltuntetese
        setupUI(findViewById(R.id.Register_ParentScrollView));

        //firebase autentifikaciojarol es adatbazisunkrol letrehozunk egy-egy peldat, illetve referenciat
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //csatoljuk a program kod reszunket a layoutban definialt itemekkel
        mEditName = (EditText) findViewById(R.id.Register_NameEdit);
        mEditEmail = (EditText) findViewById(R.id.Register_EmailEditText);
        mEditPassword = (EditText) findViewById(R.id.Register_PasswordEditText);
        mExistingAccount = (TextView) findViewById(R.id.Register_AlreadyHaveAccount);
        mUserBirthday = (DatePicker) findViewById(R.id.Register_Birthday);
        mSignUp = (Button) findViewById(R.id.Register_SignUpButton);
        mEditUserName = (EditText) findViewById(R.id.Register_UsernameEditText);

        //listenerek csatolasa
        mSignUp.setOnClickListener(this);
        mExistingAccount.setOnClickListener(this);
    }

    public void onClick(View view) {
        /*ha mar van felhasznalonk es beszeretnenk jelentkezni akkor visszalepunk a bejelentkezo ablakba, illetve toroljuk az
        acitivity stacket, hogy ne tudjunk visszalepni a back gombbal*/
        if (view == mExistingAccount) {
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        //ha regisztralni szeretnenk, kiolvassuk a mezokbol az adatokat, validaljuk ezeket majd ha minden jo megprobalunk belepni
        if (view == mSignUp) {
            //kiolvassuk az elemeket
            final String newUserFullName = mEditName.getText().toString().trim();
            final String newUserEmail = mEditEmail.getText().toString().trim();
            final String newUserPassword = mEditPassword.getText().toString().trim();
            final String newUserName = mEditUserName.getText().toString().trim();

            //validaljuk oket - 2DO
            if (validEntries(newUserFullName, newUserEmail, newUserPassword, newUserName)) {
                //betolto ablak deklaralasa es inicializalasa, majd regisztralas
                mSignUpPDialog = new ProgressDialog(RegisterActivity.this);
                setupPDialog(mSignUpPDialog, "Loading...", "Logging in");
                signUpFirebase(mSignUpPDialog, newUserName, newUserFullName, newUserEmail, newUserPassword);
            } else {
                //ha valamelyik adat nem felel meg a kriteriumoknak akkor szolunk a felhasznalonak
                Toast.makeText(RegisterActivity.this, "Registered failed!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    //belepes
    private void signUpFirebase(final ProgressDialog mSignUpPDialog,
                                final String newUserUsername, final String newUserFullName, final String newUserEmail, String newUserPassword) {
        mFirebaseAuth.createUserWithEmailAndPassword(newUserEmail, newUserPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    //ha sikeresen beleptunk akkor, megprobalunk regisztralni a sajat adatbazisunkba is, majd belepni az applikacioba
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signUpOwnDatabase(newUserUsername, newUserFullName, newUserEmail);
                            mSignUpPDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            Toast.makeText(RegisterActivity.this, "Registered succesfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            //ha nem sikerult belepni akkor megnezzuk az okat
                            FirebaseException e = (FirebaseException) task.getException();
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mSignUpPDialog.dismiss();
                        }
                    }
                });
    }

    //regisztralas sajat adatbazisba
    private void signUpOwnDatabase(String newUsername, String newUserFullName, String newUserEmail) {

        try {
            //letrehozzuk az objektumot, majd beszurjuk a tablaba
            User newUser = new User(newUsername, newUserFullName, newUserEmail, getDateFromDatePicker(mUserBirthday), null, null);
            mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).setValue(newUser).addOnFailureListener(new OnFailureListener() {
                @Override
                //ha nem sikerult beszurni az adatbazisba az uj embert
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Registration failed with error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        //ha futas beli hibat kaptunk
        catch (RuntimeException e) {
            Toast.makeText(this.getApplicationContext(), "Registration failed with error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void setupPDialog(ProgressDialog progressDialog, String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private boolean validEntries(String newUserFullName, String newUserEmail, String newUserPassword, String newUserName) {

        if (validateData(newUserFullName)) {
            if (isValidEmail(newUserEmail)) {
                if (validatePassword(newUserPassword)) {
                    if (validateBirthday(mUserBirthday)) {
                        if (validateUsername(newUserName)) {
                            return true;
                        } else {
                            Toast.makeText(this.getApplicationContext(), "You are username can't contain special characters !", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } else {
                        Toast.makeText(this.getApplicationContext(), "You are too young !", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Toast.makeText(this.getApplicationContext(), "Your password is too simple! It must contain at least 6 letters, big,small,number and alphanumeric character!", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Your email is invalid", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Your name can't contain special characters!", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
