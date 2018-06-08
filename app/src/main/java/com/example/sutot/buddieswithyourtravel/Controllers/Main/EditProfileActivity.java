package com.example.sutot.buddieswithyourtravel.Controllers.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.Models.User;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.isConnected;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    //kep feltoltesere hasznalt URI
    private Uri pictureUri = null;
    //aktualis user tarolasa
    private User currUser;
    //profile pic kep beallitassa
    private CircleImageView mProfilePicture;
    //gombok
    private ImageButton mSaveChanges,mBackToProfile;
    //kep megcserelesere hasznalt text view
    private TextView mChangePhoto;
    //modosithato szovegreszek
    private EditText mFirstName,mLastName,mUserName,mEmail,mShortDescription;
    //kep beallitasara hasznalt intent id-ja
    private static final int GALLERY_REQUEST = 1;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //aktualis felhasznalo beallitassa
        currUser = setCurrentUser();
        setContentView(R.layout.activity_edit_profile);
        //keyboard eltuntetese
        setupUI(findViewById(R.id.EditProfile_Relative_Main));

        //layout itemek identifikalasa
        mBackToProfile = findViewById(R.id.EditProfile_Back_Button);
        mSaveChanges = findViewById(R.id.EditProfile_Save_Changes);
        mProfilePicture = findViewById(R.id.EditProfile_User_Photo);
        mChangePhoto = findViewById(R.id.EditProfile_Change_Profile_Picture);
        mFirstName = findViewById(R.id.EditProfile_FirstNameEdit);
        mLastName = findViewById(R.id.EditProfile_LastNameEdit);
        mUserName = findViewById(R.id.EditProfile_Username_EditText);
        mEmail = findViewById(R.id.EditProfile_Email_EditText);
        mShortDescription = findViewById(R.id.EditProfile_ShortDescription_Edit);

        //listenerek csatolasa
        mChangePhoto.setOnClickListener(this);
        mBackToProfile.setOnClickListener(this);
        mSaveChanges.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view == mChangePhoto)
        {
            addnewpostimage();
        }
        if (view == mBackToProfile)
        {
            finish();
        }
        if (view == mSaveChanges)
        {
            saveChanges();
            finish();
        }
    }

    private User setCurrentUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        try {
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currUser = new User(dataSnapshot.getValue(User.class));
                    setLook(currUser);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
        return currUser;
    }


    private void setLook(User user)
    {
        if (user.getProfilePicture()==null || !isConnected(this) || user.getProfilePicture().isEmpty())
        {
            mProfilePicture.setImageResource(R.drawable.no_profile_pic);
        }
        else
        {
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfilePicture()))
                    .into(mProfilePicture);
        }
        mFirstName.setText(user.getFirstName());
        mLastName.setText(user.getLastName());
        mEmail.setText(user.getEmail());
        mUserName.setText(user.getUserName());
        if ( user.getBio()!=null && !user.getBio().isEmpty())
        {
            mShortDescription.setText(user.getBio());
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
                mProfilePicture.setImageURI(pictureUri);
            }
        }
    }

    private void saveChanges()
    {
        mProgressDialog = new ProgressDialog(this);
        setupPDialog(mProgressDialog, "Loading...", "Editing profile");
        if (!mFirstName.getText().toString().isEmpty())
        {
            currUser.setFirstName(mFirstName.getText().toString());
        }
        if (!mLastName.getText().toString().isEmpty())
        {
            currUser.setLastName(mLastName.getText().toString());
        }
        if (!mEmail.getText().toString().isEmpty())
        {
            currUser.setEmail(mEmail.getText().toString());
        }
        if (!mUserName.getText().toString().isEmpty())
        {
            currUser.setUserName(mUserName.getText().toString());
        }
        if (!mShortDescription.getText().toString().isEmpty())
        {
            currUser.setBio(mShortDescription.getText().toString());
        }
        if (pictureUri!=null && !pictureUri.toString().isEmpty())
        {
            currUser.setProfilePicture(pictureUri.toString());
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Users/").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images/").child("profilePicture/");
            filepath.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(currUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Edited succesfully!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.hide();
                    Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
            });
        }
        else {
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(currUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Edited succesfully!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setupPDialog(ProgressDialog progressDialog, String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }
}
