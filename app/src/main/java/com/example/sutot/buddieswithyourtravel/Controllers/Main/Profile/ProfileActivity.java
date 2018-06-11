package com.example.sutot.buddieswithyourtravel.Controllers.Main.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sutot.buddieswithyourtravel.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mBack;

    private TextView mFullName, mLocation, mShortDescription, mEmail;

    private CircleImageView mProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mBack = findViewById(R.id.Profile_Back_Button);
        mFullName = findViewById(R.id.Profile_Full_Name_TextView);
        mLocation = findViewById(R.id.Profile_Location);
        mShortDescription = findViewById(R.id.Profile_Short_Description);
        mEmail = findViewById(R.id.Profile_Email);
        mProfilePic = findViewById(R.id.Profile_Profile_Pic);

        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mBack) {
            finish();
        }
    }
}
