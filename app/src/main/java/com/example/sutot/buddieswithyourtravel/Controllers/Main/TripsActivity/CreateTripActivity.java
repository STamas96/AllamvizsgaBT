package com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;

public class CreateTripActivity extends AppCompatActivity implements View.OnClickListener {

    //felso lec gombok
    private ImageButton mAddNewImage, mBacktoMain;
    //modosithato szovegreszek
    private EditText mTripTitle, mShortDescription,mDetailedDescription;
    //datumok
    private DatePicker mStartDate, mEndDate;
    //gomb mely altal letrehozzuk az uj objektumot
    private Button mAddNewTrip;
    //a kep feltoltesere hasznalt uri
    private Uri pictureUri = null;
    //action id-ja
    private static final int GALLERY_REQUEST = 1;
    //adatbazis referenciak
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    //betolto dialogus
    private ProgressDialog mProgressDialog;
    //fo relative layout
    private RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        //keyboard eltuntetese
        setupUI(findViewById(R.id.CreateTrip_Main_Relative_Layout));

        //layout itemjeinek a csatolasai
        mAddNewImage = (ImageButton) findViewById(R.id.CreateTrip_Photos);
        mTripTitle = (EditText) findViewById(R.id.CreateTrip_Title_Edit);
        mShortDescription = (EditText) findViewById(R.id.CreateTrip_ShortDescription_Edit);
        mStartDate = (DatePicker) findViewById(R.id.CreateTrip_StartDate);
        mEndDate = (DatePicker) findViewById(R.id.CreateTrip_EndDate);
        mAddNewTrip = (Button) findViewById(R.id.CreateTrip_AddNewPost);
        mMainLayout = (RelativeLayout) findViewById(R.id.CreateTrip_Main_Relative_Layout);
        mBacktoMain = (ImageButton) findViewById(R.id.CreateTrip_Back_Button);
        mDetailedDescription = (EditText) findViewById(R.id.CreateTrip_DetailedDescription_Edit);

        //referenciak letrehozasa
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //leiras beallitassa, sorok szama, scrollozhato legyen
        mShortDescription.setMaxLines(5);
        mShortDescription.setScroller(new Scroller(this));
        mShortDescription.setVerticalScrollBarEnabled(true);
        mShortDescription.setMovementMethod(new ScrollingMovementMethod());

        mDetailedDescription.setMaxLines(50);
        mDetailedDescription.setScroller(new Scroller(this));
        mDetailedDescription.setVerticalScrollBarEnabled(true);
        mDetailedDescription.setMovementMethod(new ScrollingMovementMethod());

        //listenerek csatolasa, erzekeljek a clicket
        mAddNewImage.setOnClickListener(this);
        mAddNewTrip.setOnClickListener(this);
        mBacktoMain.setOnClickListener(this);

        //start es end date beallitasa
        mStartDate.setMinDate(System.currentTimeMillis() - 1000);
        mEndDate.setMinDate(System.currentTimeMillis() - 1000);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 10);
        mStartDate.setMaxDate(c.getTimeInMillis());
        mEndDate.setMaxDate(c.getTimeInMillis());

    }

    @Override
    public void onClick(View view) {
        if (view == mAddNewImage) {
            addnewpostimage();
        }
        if (view == mAddNewTrip) {
            addnewPost();
        }
        //ha visszakarunk lepni a fo activitybe, uritjuk a vermet is
        if (view == mBacktoMain) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //uj kirandulas letrehozasa
    private void addnewPost() {

        //adatokat kiolvasasa es dialogus inicializalasa
        mProgressDialog = new ProgressDialog(CreateTripActivity.this);
        setupPDialog(mProgressDialog, "Loading...", "Adding new post");
        final String newTripTitle = mTripTitle.getText().toString().trim();
        final String newTripSDescription = mShortDescription.getText().toString().trim();
        final String newTripDDescription = mDetailedDescription.getText().toString().trim();
        final Date newTripStartDate = getDateFromDatePicker(mStartDate);
        final Date newTripEndDate = getDateFromDatePicker(mEndDate);
        final Date createdDate = Calendar.getInstance().getTime();

        //ha a cim es a leiras sem ures
        if (!newTripTitle.isEmpty() && (!newTripSDescription.isEmpty()) && (!newTripDDescription.isEmpty())) {

            //kiolvassuk a jelenlegi felhasznalot a firebase adatbazisabol
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser currUser = firebaseAuth.getCurrentUser();

            //keszitunk egy signaturat mely altal fogjuk megkulonboztetni a kirandulasokat
            final String signature = Objects.toString(System.currentTimeMillis(), null);

            //beallitjuk hova mentsuk el a kepet
            StorageReference filepath = storageReference.child("Trips/").child(signature).child("images/").child(currUser.getUid());

            //ha a kep nem ures, azaz raktunk valami kepet
            if (pictureUri != null) {
                //megprobaljuk felrakni a kepet
                filepath.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //ha sikerult felrakni a gepet, akkor kerunk egy urit amely altal elerjuk
                        Uri downloadURL = taskSnapshot.getDownloadUrl();

                        //letrehozzuk a signaturat amely az idot tarolja nanosec-ba es a szerzo userID-jat
                        String signatureTemp = signature + currUser.getUid();
                        //letrehozzuk a kirandulas objektumot a megfelelo parameterekkel, majd megprobaljuk feltolteni
                        //utvonal -> Kirandulas reszleg / szignatura
                        Trips newPost = new Trips(signatureTemp, currUser.getUid(), newTripTitle, downloadURL.toString(),
                                newTripSDescription, newTripStartDate, newTripEndDate, Calendar.getInstance().getTime(),
                                Calendar.getInstance().getTime(),newTripDDescription);
                        databaseReference.child("Trips").child(signatureTemp).setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Uploaded succesfully!", Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        });//ha nem sikerult feltolteni a kepet akkor szolunk hogy mi volt a baj
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.hide();
                        Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });/*ha a felhasznalo nem szeretne felrakni kepet a kirandulasrol, ugyanugy jarunk el mint fentebb csak kep resze
                ures lesz*/
            } else {
                String signatureTemp = signature + currUser.getUid();
                Trips newPost = new Trips(signatureTemp, currUser.getUid(), newTripTitle, null,
                        newTripSDescription, newTripStartDate, newTripEndDate, Calendar.getInstance().getTime(),
                        Calendar.getInstance().getTime(),newTripDDescription);
                databaseReference.child("Trips").child(signatureTemp).setValue(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Uploaded succesfully!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
            }
        } else//ha valamelyik resze ures a cim es a leiras kozul akkor szolunk a usernek
            {
            Toast.makeText(this, "You must complete all of the fields!", Toast.LENGTH_SHORT).show();
            mProgressDialog.hide();
        }
    }

    //uj kep feltoltese
    private void addnewpostimage() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, GALLERY_REQUEST);
    }

    //a visszakapott adatot lementjuk es beallitjuk hogy jelenjen is meg a UI-n
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                pictureUri = data.getData();
                mAddNewImage.setImageURI(pictureUri);
            }
        }
    }

    //datum kiolvasasa a datepickerbol
    public static Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    //fuggveny mely altal inicializaljuk a dialogust
    private void setupPDialog(ProgressDialog progressDialog, String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }
}
