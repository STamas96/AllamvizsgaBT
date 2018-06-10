package com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.buildDialogNeedToHaveMDorWiFi;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isConnected;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;

public class EditTripsActivity extends AppCompatActivity implements View.OnClickListener {

    //visszalepes es kep feltoltes
    private ImageButton mBacktoMain, mSaveChanges;
    private ImageView mCoverImage;
    //modosithato szovegreszek
    private EditText mTripTitle, mShortDescription, mDetailedDescription;
    //datumok
    private DatePicker mStartDate, mEndDate;
    //gomb mely altal lementjuk a modositasokat es a torles gomb
    private Button mDeletePost;
    //a kep feltoltesere hasznalt uri
    private Uri pictureUri;
    //action id-ja
    private static final int GALLERY_REQUEST = 1;
    //adatbazis referenciak
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    //betolto dialogus
    private ProgressDialog mProgressDialog;
    //fo relative layout
    private RelativeLayout mMainLayout;
    private String tripKey;
    private TextView mChangePhotoTV;
    private Trips temp;
    private boolean mChangePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trips);

        tripKey = getIntent().getExtras().get("TripKey").toString();

        //keyboard eltuntetese
        setupUI(findViewById(R.id.EditTrip_Main_Relative_Layout));

        //layout itemjeinek a csatolasai
        mCoverImage = (ImageView) findViewById(R.id.EditTrip_Photos);
        mTripTitle = (EditText) findViewById(R.id.EditTrip_Title_Edit);
        mShortDescription = (EditText) findViewById(R.id.EditTrip_ShortDescription_Edit);

        mStartDate = (DatePicker) findViewById(R.id.EditTrip_StartDate);
        mEndDate = (DatePicker) findViewById(R.id.EditTrip_EndDate);
        mMainLayout = (RelativeLayout) findViewById(R.id.EditTrip_Main_Relative_Layout);
        mBacktoMain = (ImageButton) findViewById(R.id.EditTrip_Back_Button);
        mDetailedDescription = (EditText) findViewById(R.id.EditTrip_DetailedDescription_Edit);
        mChangePhotoTV = (TextView) findViewById(R.id.EditTrip_Change_Photo_TV);
        mDeletePost = (Button) findViewById(R.id.EditTrip_Delete_Post);
        mSaveChanges = (ImageButton) findViewById(R.id.EditTrip_Save_Changes);

        //referenciak letrehozasa
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips").child(tripKey);

        //leiras beallitassa, sorok szama, scrollozhato legyen
        mShortDescription.setMaxLines(5);
        mShortDescription.setScroller(new Scroller(this));
        mShortDescription.setVerticalScrollBarEnabled(true);
        mShortDescription.setMovementMethod(new ScrollingMovementMethod());

        mShortDescription.setMaxLines(50);
        mShortDescription.setScroller(new Scroller(this));
        mShortDescription.setVerticalScrollBarEnabled(true);
        mShortDescription.setMovementMethod(new ScrollingMovementMethod());

        //listenerek csatolasa, erzekeljek a clicket
        mBacktoMain.setOnClickListener(this);
        mSaveChanges.setOnClickListener(this);
        mChangePhotoTV.setOnClickListener(this);
        mDeletePost.setOnClickListener(this);

        //start es end date beallitasa
        mStartDate.setMinDate(System.currentTimeMillis() - 1000);
        mEndDate.setMinDate(System.currentTimeMillis() - 1000);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 10);
        mStartDate.setMaxDate(c.getTimeInMillis());
        mEndDate.setMaxDate(c.getTimeInMillis());

        setLook();


    }

    @Override
    public void onClick(View view) {
        if (view == mBacktoMain) {
            finish();
        }
        if (view == mDeletePost) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            DeleteTrip();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setCancelable(false).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }
        if (view == mSaveChanges) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialog.dismiss();
                            EditTrip();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setCancelable(false).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        if (view == mChangePhotoTV) {
            Intent galleryintent = new Intent(Intent.ACTION_PICK);
            galleryintent.setType("image/*");
            startActivityForResult(galleryintent, GALLERY_REQUEST);
        }
    }

    //elmenti a valaszt
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                pictureUri = data.getData();
                mCoverImage.setImageURI(pictureUri);
                mChangePicture = true;
            }
        }
    }

    private void EditTrip() {
        mProgressDialog = new ProgressDialog(EditTripsActivity.this);
        setupPDialog(mProgressDialog, "Editing post", "Loading...");
        if (isConnected(this)) {
            if (!mTripTitle.getText().toString().isEmpty()) {
                temp.setTitle(mTripTitle.getText().toString());
            }
            if (!mShortDescription.getText().toString().isEmpty()) {
                temp.setShortDescription(mShortDescription.getText().toString());
            }
            if (!mDetailedDescription.getText().toString().isEmpty()) {
                temp.setDetailedDescription(mDetailedDescription.getText().toString());
            }
            if (getDateFromDatePicker(mStartDate) != temp.getStartDate()) {
                temp.setStartDate(getDateFromDatePicker(mStartDate));
            }
            if (getDateFromDatePicker(mEndDate) != temp.getEndDate()) {
                temp.setEndDate(getDateFromDatePicker(mEndDate));
            }
            temp.setLastTimeModified(Calendar.getInstance().getTime());
            if (pictureUri != null && !pictureUri.toString().isEmpty()) {
                StorageReference filepath = storageReference.child("Trips/").child(tripKey.substring(0, 12)).child("images/").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                filepath.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //ha sikerult felrakni a gepet, akkor kerunk egy urit amely altal elerjuk
                        Uri downloadURL = taskSnapshot.getDownloadUrl();
                        temp.setFilePath(downloadURL.toString());
                        databaseReference.setValue(temp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Edited succesfully!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditTripsActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
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
                    //ha nem sikerult feltolteni a kepet akkor szolunk hogy mi volt a baj
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        databaseReference.setValue(temp).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getBaseContext(), "Edited succesfully!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditTripsActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
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
                });
            }
        }

    }

    private void setLook() {
        //lekerjuk a referenciat a felhasznalo tablahoz
        try {
            /*megkeressuk a mi felhasznalonkat, ha megkapjuk atalittjuk az adatait, ha pedig nem akkor valami hiba tortent es biztonsagi
            okokbol visszaugrunk a belepesi fazisba*/
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        temp = new Trips(dataSnapshot.getValue(Trips.class));
                        mTripTitle.setText(temp.getTitle());
                        mShortDescription.setText(temp.getShortDescription());
                        mDetailedDescription.setText(temp.getDetailedDescription());
                        mStartDate.init(temp.getStartDate().getYear(), temp.getStartDate().getMonth(), temp.getStartDate().getDay(), null);
                        mEndDate.init(temp.getEndDate().getYear(), temp.getEndDate().getMonth(), temp.getEndDate().getDay(), null);
                        if (temp.getFilePath() == null) {
                            mCoverImage.setImageResource(R.drawable.no_profile_pic);
                        } else {
                            if (!temp.getFilePath().isEmpty()) {
                                StorageReference pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(temp.getFilePath().toString());
                                Glide.with(getApplicationContext())
                                        .using(new FirebaseImageLoader())
                                        .load(pictureref)
                                        .into(mCoverImage);
                            }
                        }
                    }
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
        }
    }

    private void DeleteTrip() {

        mProgressDialog = new ProgressDialog(EditTripsActivity.this);
        setupPDialog(mProgressDialog, "Deleteing post", "Loading...");
        if (isConnected(this)) {
            databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Deleted succesfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditTripsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditTripsActivity.this, LogInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        } else {
            mProgressDialog.dismiss();
            buildDialogNeedToHaveMDorWiFi(this);
        }
    }

    //egy fuggveny amely altal inicializalni tudunk egy progress dialogot
    private void setupPDialog(ProgressDialog progressDialog, String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }
}
