package com.example.sutot.buddieswithyourtravel.Controllers.Main.Profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mBack;

    private TextView mFullName, mCreatedTrips, mInterestedTrips, mShortDescription, mEmail, mUsername;

    private CircleImageView mProfilePic;

    private String personKey;

    //adatbazis referenciak
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //kiolvassuk az aktualis kirandulas kulcsat
        personKey = getIntent().getExtras().get("PersonKey").toString();

        mBack = findViewById(R.id.Profile_Back_Button);
        mFullName = findViewById(R.id.Profile_Full_Name_TextView);
        mShortDescription = findViewById(R.id.Profile_Short_Description);
        mEmail = findViewById(R.id.Profile_Email);
        mCreatedTrips = findViewById(R.id.Profile_CreatedTrips);
        mInterestedTrips = findViewById(R.id.Profile_InterestedTrips);
        mProfilePic = findViewById(R.id.Profile_Profile_Pic);
        mUsername = findViewById(R.id.Profile_Username);

        //beallitasok, a jelenlegi kirandulassal
        try {
            //referenciak letrehozasa
            storageReference = FirebaseStorage.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personKey);
        }
        catch (Exception e)
        {

        }
        setLook();

        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBack) {
            finish();
        }
    }

    private void setLook() {
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User temp = new User(dataSnapshot.getValue(User.class));
                    mFullName.setText(temp.getName());
                    mShortDescription.setText(temp.getBio());
                    mCreatedTrips.setText(String.valueOf(temp.getCreatedTripsNr()) + "\ncreated trips");
                    mEmail.setText(temp.getEmail());
                    mInterestedTrips.setText("0");
                    mUsername.setText(temp.getUserName());
                    if (temp.getProfilePicture() == null) {
                        mProfilePic.setImageResource(R.drawable.no_profile_pic);
                    } else {
                        if (!temp.getProfilePicture().isEmpty()) {
                            StorageReference pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(temp.getProfilePicture().toString());
                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(pictureref)
                                    .into(mProfilePic);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
