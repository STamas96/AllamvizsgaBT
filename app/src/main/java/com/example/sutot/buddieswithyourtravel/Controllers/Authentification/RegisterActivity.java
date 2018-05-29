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


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //modosithato fieldek
    private EditText mEditFirstName,mEditLastName,mEditEmail,mEditPassword,mEditUserName;
    //ha mar van felhasznalonk
    private TextView mExistingAccount;
    //belepo gombok
    private Button mSignUp;
    //osszekottetes a firebase auth sdk-val
    private FirebaseAuth mFirebaseAuth;
    //betoltesnel hasznalat dialog
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

        //firebase autentifikaciojaraol letrehozunk egy peldat es az adatbazis refereniat definialjuk
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //csatolasok
        mEditFirstName = (EditText) findViewById(R.id.Register_FirstNameEdit);
        mEditLastName = (EditText) findViewById(R.id.Register_LastNameEdit);
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
        //uj felhasznalos gomb esete
        if ( view == mExistingAccount)
        {
            //ha regisztralni akarunk atterunk erre az activityre es kiurtijuk a stacket
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if ( view == mSignUp)
        {
            //kiolvassuk az elemeket
            final String newUserFirstName = mEditFirstName.getText().toString().trim();
            final String newUserLastName = mEditLastName.getText().toString().trim();
            final String newUserEmail = mEditEmail.getText().toString().trim();
            final String newUserPassword = mEditPassword.getText().toString().trim();
            final String newUserName = mEditUserName.getText().toString().trim();

            //validaljuk oket - TEMPORALIS!!!!!!!!
            if (validEntries(newUserFirstName,newUserLastName,newUserEmail,newUserPassword,newUserName))
            {
                //probalunk regisztralni
                mSignUpPDialog = new ProgressDialog(RegisterActivity.this);
                setupPDialog(mSignUpPDialog,"Loading...","Logging in");
                signUpFirebase(mSignUpPDialog,newUserName,newUserFirstName,newUserLastName,newUserEmail,newUserPassword);
            }
            else
            {
                Toast.makeText(RegisterActivity.this,"Registered failed!",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void signUpFirebase(final ProgressDialog mSignUpPDialog,
            final String newUserUsername,final String newUserFirstName, final String newUserLastName,final String newUserEmail,String newUserPassword)
    {
        mFirebaseAuth.createUserWithEmailAndPassword(newUserEmail,newUserPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            signUpOwnDatabase(newUserUsername,newUserFirstName,newUserLastName,newUserEmail);
                            mSignUpPDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            Toast.makeText(RegisterActivity.this,"Registered succesfully!",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            FirebaseException e = (FirebaseException)task.getException();
                            mSignUpPDialog.dismiss();
                        }
                    }
                });
    }

    private void signUpOwnDatabase(String newUsername, String newUserFirstName,String newUserLastName,String newUserEmail)
    {

        try{
            User newUser = new User(newUsername,newUserFirstName,newUserLastName,newUserEmail,getDateFromDatePicker(mUserBirthday));
            mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).setValue(newUser);
        }
        catch(RuntimeException e)
        {
            Toast.makeText(this.getApplicationContext(),"Registration failed with error: " + e,Toast.LENGTH_LONG).show();
        }


    }

    private void setupPDialog(ProgressDialog  progressDialog, String title, String message)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private boolean validEntries(String newUserFirstName,String newUserLastName,String newUserEmail,String newUserPassword,String newUserName)
    {

        if (validateData(newUserFirstName))
        {
            if (validateData(newUserLastName))
            {
                if (isValidEmail(newUserEmail))
                {
                    if (validatePassword(newUserPassword))
                    {
                        if (validateBirthday(mUserBirthday))
                        {
                            if (validateUsername(newUserName))
                            {
                                return true;
                            }
                            else
                            {
                                Toast.makeText(this.getApplicationContext(),"You are username can't contain special characters !",Toast.LENGTH_LONG).show();
                                return false;
                            }
                        }
                        else
                        {
                            Toast.makeText(this.getApplicationContext(),"You are too old !",Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                    else
                    {
                        Toast.makeText(this.getApplicationContext(),"Your password is too simple! It must contain at least 6 letters, big,small,number and alphanumeric character!",Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                else
                {
                    Toast.makeText(this.getApplicationContext(),"Your email is invalid",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            else
            {
                Toast.makeText(this.getApplicationContext(),"Your last name can't contain special characters!",Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else {
            Toast.makeText(this.getApplicationContext(),"Your first name can't contain special characters!",Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
