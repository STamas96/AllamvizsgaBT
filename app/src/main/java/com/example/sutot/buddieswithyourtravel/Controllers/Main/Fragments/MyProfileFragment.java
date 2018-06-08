package com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.EditProfileActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.R;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private CircleImageView mProfilePic;
    private TextView mUserFullName;
    private static final int GALLERY_REQUEST = 1;
    private MainActivity mMainActivity;
    private Button mEditProfile;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mMainActivity = (MainActivity) getActivity();
        mProfilePic = (CircleImageView) mMainActivity.findViewById(R.id.MyProfile_Profile_Image);
        mUserFullName = (TextView) mMainActivity.findViewById(R.id.MyProfile_User_Full_Name);
        mEditProfile = (Button) mMainActivity.findViewById(R.id.MyPRofile_Edit_Profile_Button);

        mMainActivity.currUser = mMainActivity.setCurrentUser();
        mUserFullName.setText(mMainActivity.currUser.getFirstName() + " " + mMainActivity.currUser.getLastName());
        mEditProfile.setOnClickListener(this);
        mMainActivity.mTopRight.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        if (view == mMainActivity.mTopRight) {
            LogOut();
        }
        if (view == mEditProfile)
        {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        }
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPref = mMainActivity.getSharedPreferences("LogInSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alreadyLoggedIn", "loggedout");
        editor.commit();
        Intent loginscreen = new Intent(getContext(), LogInActivity.class);
        loginscreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginscreen);
        mMainActivity.finish();

    }

}
