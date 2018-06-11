package com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.sutot.buddieswithyourtravel.Controllers.Authentification.LogInActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Profile.EditProfileActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.MainActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.SearchFriendsActivity;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

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
    private final int CHANGE_PROFILE = 1;

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

        //megszerezzuk a fo activityt
        mMainActivity = (MainActivity) getActivity();
        mProfilePic = (CircleImageView) mMainActivity.findViewById(R.id.MyProfile_Profile_Image);
        mUserFullName = (TextView) mMainActivity.findViewById(R.id.MyProfile_User_Full_Name);
        mEditProfile = (Button) mMainActivity.findViewById(R.id.MyPRofile_Edit_Profile_Button);

        //megnyitjuk a megosztott fajlokat,hogy majd tudjunk innen kiolvasni adatokat illetve tarolni
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        //atirjuk a fo cimet, a felhasznalonevre
        SpannableString text = new SpannableString(mMainActivity.currUser.getUserName());
        text.setSpan(new TextAppearanceSpan(getContext(), R.style.MainScreen_AppName_Part3), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMainActivity.mTopBarCenterTV.setText(text, TextView.BufferType.SPANNABLE);

        //beallitjuk a felhasznalo lassa a sajat nevet illetve profil kepet
        mUserFullName.setText(mMainActivity.currUser.getFirstName() + " " + mMainActivity.currUser.getLastName());
        if (mMainActivity.currUser.getProfilePicture() == null || mMainActivity.currUser.getProfilePicture().isEmpty()) {
            mProfilePic.setImageResource(R.drawable.no_profile_pic);
        } else {
            String sign = sharedPref.getString("glidepic", new StringSignature(String.valueOf(System.currentTimeMillis())).toString());
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(FirebaseStorage.getInstance().getReferenceFromUrl(mMainActivity.currUser.getProfilePicture()))
                    .signature(new StringSignature(sign))
                    .into(mProfilePic);
        }

        //gombok erzekeljek mikor rajuk klikkeltunk
        mEditProfile.setOnClickListener(this);
        mMainActivity.mTopLeft.setOnClickListener(this);
        mMainActivity.mTopRight.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //ha a job felsot hasznaljuk akkor kiszeretnenk lepni
        if (view == mMainActivity.mTopRight) {
            LogOut();
        }

        if (view == mMainActivity.mTopLeft) {
             startActivity(new Intent(getContext(), SearchFriendsActivity.class));
        }

        //profil modositasa, valasszal
        if (view == mEditProfile) {
            Intent i = new Intent(getContext(), EditProfileActivity.class);
            startActivityForResult(i, CHANGE_PROFILE);
        }
    }

    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        //ha profil modositasbol erkeztunk ide
        if (requestCode == CHANGE_PROFILE) {
            //frisstijuk a felhasznalo adatait
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            try {
                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //ha sikerult elerni az uj adatokat, akkor ezeket taroljuk, illetve frissitjuk a UI-t
                        User tcurrUser = new User(dataSnapshot.getValue(User.class));
                        //kiolvassuk a  legutolso kepet, ha nincs ilyen akkor frissitjuk ** itt mentjuk el a cachebe hogy melyik volt az
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        StringSignature temp = new StringSignature(String.valueOf(System.currentTimeMillis()));
                        editor.putString("glidepic", temp.toString());
                        editor.commit();
                        mUserFullName.setText(mMainActivity.currUser.getFirstName() + " " + mMainActivity.currUser.getLastName());
                        //ha a kep is modosult
                        if (resultCode == Activity.RESULT_OK) {
                            Glide.with(getContext())
                                    .using(new FirebaseImageLoader())
                                    .load(FirebaseStorage.getInstance().getReferenceFromUrl(mMainActivity.currUser.getProfilePicture()))
                                    .signature(temp)
                                    .into(mProfilePic);

                            SpannableString text = new SpannableString(tcurrUser.getUserName());
                            text.setSpan(new TextAppearanceSpan(getContext(), R.style.MainScreen_AppName_Part3), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mMainActivity.mTopBarCenterTV.setText(text, TextView.BufferType.SPANNABLE);
                            mUserFullName.setText(tcurrUser.getFirstName() + " " + tcurrUser.getLastName());
                        }
                        //ha a kep nem modosult
                        if (resultCode == Activity.RESULT_CANCELED) {
                            SpannableString text = new SpannableString(tcurrUser.getUserName());
                            text.setSpan(new TextAppearanceSpan(getContext(), R.style.MainScreen_AppName_Part3), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mMainActivity.mTopBarCenterTV.setText(text, TextView.BufferType.SPANNABLE);
                            mUserFullName.setText(tcurrUser.getFirstName() + " " + tcurrUser.getLastName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                //ha hiba tortent kilepunk az applikaciobol
            } catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    }

    //fuggveny mely segitsegevel kilepunk
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
