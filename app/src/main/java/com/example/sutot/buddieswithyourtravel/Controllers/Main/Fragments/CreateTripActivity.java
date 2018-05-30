package com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.RegisterActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;

public class CreateTripActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mAddNewImage, mBacktoMain;
    private EditText mTripTitle, mShortDescription;
    private DatePicker mStartDate, mEndDate;
    private Button mAddNewTrip;
    private Uri pictureUri = null;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog mProgressDialog;
    private RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        //keyboard eltuntetese
        setupUI(findViewById(R.id.CreateTrip_Main_Relative_Layout));

        mAddNewImage = (ImageButton) findViewById(R.id.CreateTrip_Photos);
        mTripTitle = (EditText) findViewById(R.id.CreateTrip_Title_Edit);
        mShortDescription = (EditText) findViewById(R.id.CreateTrip_ShortDescription_Edit);
        mStartDate = (DatePicker) findViewById(R.id.CreateTrip_StartDate);
        mEndDate = (DatePicker) findViewById(R.id.CreateTrip_EndDate);
        mAddNewTrip = (Button) findViewById(R.id.CreateTrip_AddNewPost);
        mMainLayout = (RelativeLayout) findViewById(R.id.CreateTrip_Main_Relative_Layout);
        mBacktoMain = (ImageButton) findViewById(R.id.CreateTrip_Back_Button);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mShortDescription.setMaxLines(5);
        mShortDescription.setScroller(new Scroller(this));
        mShortDescription.setVerticalScrollBarEnabled(true);
        mShortDescription.setMovementMethod(new ScrollingMovementMethod());

        mAddNewImage.setOnClickListener(this);
        mAddNewTrip.setOnClickListener(this);
        mBacktoMain.setOnClickListener(this);

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
            Toast.makeText(this, "asdasdasda", Toast.LENGTH_SHORT).show();
            addnewpostimage();
        }
        if (view == mAddNewTrip) {
            addnewPost();
        }
        if (view == mBacktoMain) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void addnewPost() {

        mProgressDialog = new ProgressDialog(CreateTripActivity.this);
        setupPDialog(mProgressDialog, "Loading...", "Adding new post");
        final String newTripTitle = mTripTitle.getText().toString().trim();
        final String newTripSDescription = mShortDescription.getText().toString().trim();
        final Date newTripStartDate = getDateFromDatePicker(mStartDate);
        final Date newTripEndDate = getDateFromDatePicker(mEndDate);
        final Date createdDate = Calendar.getInstance().getTime();
        if (!newTripTitle.isEmpty() && (!newTripSDescription.isEmpty())) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser currUser = firebaseAuth.getCurrentUser();
            ;
            final String signature = Objects.toString(System.currentTimeMillis(), null);
            StorageReference filepath = storageReference.child("Trips/").child(signature).child("images/").child(currUser.getUid());
            if (pictureUri != null) {
                filepath.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadURL = taskSnapshot.getDownloadUrl();
                        String signatureTemp = signature + currUser.getUid();
                        Trips newPost = new Trips(signatureTemp, currUser.getUid(), newTripTitle, downloadURL.toString(),
                                newTripSDescription, newTripStartDate, newTripEndDate, Calendar.getInstance().getTime(),
                                Calendar.getInstance().getTime());
                        databaseReference.child("Trips").child(signatureTemp).setValue(newPost);
                        mProgressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Uploaded succesfully!", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.hide();
                        Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
            } else {
                String signatureTemp = signature + currUser.getUid();
                Trips newPost = new Trips(signatureTemp, currUser.getUid(), newTripTitle, null,
                        newTripSDescription, newTripStartDate, newTripEndDate, Calendar.getInstance().getTime(),
                        Calendar.getInstance().getTime());
                databaseReference.child("Trips").child(signatureTemp).setValue(newPost);
                mProgressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Uploaded succesfully!", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        } else {
            Toast.makeText(this, "You must complete all of the fields!", Toast.LENGTH_SHORT).show();
            mProgressDialog.hide();
        }
    }

    private void addnewpostimage() {
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                pictureUri = data.getData();
                mAddNewImage.setImageURI(pictureUri);
            }
        }
    }

    public static Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    private void setupPDialog(ProgressDialog progressDialog, String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }
}
