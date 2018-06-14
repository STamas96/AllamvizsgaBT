package com.example.sutot.buddieswithyourtravel.Controllers.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Fragments.HomeFragment;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.Profile.ProfileActivity;
import com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity.DetailedTripActivity;
import com.example.sutot.buddieswithyourtravel.Models.Trips;
import com.example.sutot.buddieswithyourtravel.Models.User;
import com.example.sutot.buddieswithyourtravel.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sutot.buddieswithyourtravel.Controllers.Main.TripsActivity.CreateTripActivity.getDateFromDatePicker;
import static com.example.sutot.buddieswithyourtravel.Utilities.Classes.Utility.setupUI;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mTopNavBar;
    private ImageButton mBack, mSearch;
    private EditText mInput;
    private RecyclerView mRecyclerView;
    private DatabaseReference databaseReference;
    private String searchType, mAfter;
    private TextView mTopTitle, mDescription, mDateRange;
    private DatePicker mStart, mEnd;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        searchType = getIntent().getExtras().get("type").toString();

        //keyboard eltuntetese
        setupUI(findViewById(R.id.Search_Friend_ParentRL));

        mTopNavBar = (LinearLayout) findViewById(R.id.Search_Friend_Top_NavBar_LLayout);
        mBack = (ImageButton) findViewById(R.id.Search_Friend_Back_Button);
        mInput = (EditText) findViewById(R.id.Search_Friend_Input);
        mSearch = (ImageButton) findViewById(R.id.Search_Friend_Search);
        mTopTitle = (TextView) findViewById(R.id.Search_Friend_Title_TextView);
        mDescription = (TextView) findViewById(R.id.Search_Friend_Suggestion);
        mSpinner = (Spinner) findViewById(R.id.Search_Friend_Search_Option);

        try {
            if (searchType.equals("user")) {
                mAfter = "name";
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                mTopTitle.setText("Search friend");
                mDescription.setText("Search after your friends or find a new one:");
                mInput.setHint("Enter his/her name");
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.user_option, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                           switch (position) {
                                                               case 0:
                                                                   mAfter = "name";
                                                                   break;
                                                               case 1:
                                                                   mAfter = "userName";
                                                                   break;
                                                               case 2:
                                                                   mAfter = "email";
                                                                   break;
                                                           }
                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {

                                                       }
                                                   }
                );
            } else {
                mAfter = "title";
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
                mTopTitle.setText("Search trip");
                mDescription.setText("Search after your ideal trip: ");
                mInput.setHint("Enter trip name");
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.trips_option, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                mAfter = "title";
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.Search_Friend_Result);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mBack.setOnClickListener(this);
        mSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mSearch) {
            String searchInput = mInput.getText().toString();
            if (searchType.equals("user")) {
                SearchForPeople(searchInput);
            } else {
                SearchForTrips(searchInput);
            }
        }
    }

    private void SearchForTrips(String searchinput) {
        Toast.makeText(this, "Searching", Toast.LENGTH_LONG).show();

        Query searchTripsQuery = databaseReference.orderByChild(mAfter).startAt(searchinput).endAt(searchinput + "\uf8ff");

        FirebaseRecyclerAdapter<Trips, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trips, UserViewHolder>(
                Trips.class,
                R.layout.search_result_display,
                UserViewHolder.class,
                searchTripsQuery
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, final Trips model, final int position) {
                viewHolder.setFullName(model.getTitle());
                viewHolder.setShortDescription(model.getShortDescription());
                viewHolder.setProfilePicture(getApplicationContext(), model.getFilePath());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailedPost = new Intent(SearchActivity.this, DetailedTripActivity.class);
                        detailedPost.putExtra("TripKey", getRef(position).getKey());
                        startActivity(detailedPost);
                    }
                });
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void SearchForPeople(String searchInput) {

        Toast.makeText(this, "Searching", Toast.LENGTH_LONG).show();

        Query searchFriendsQuery = databaseReference.orderByChild(mAfter).startAt(searchInput).endAt(searchInput + "\uf8ff");

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.search_result_display,
                UserViewHolder.class,
                searchFriendsQuery

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, final int position) {
                String tempFullName = model.getName();
                viewHolder.setFullName(tempFullName);
                viewHolder.setShortDescription(model.getBio());
                viewHolder.setProfilePicture(getApplicationContext(), model.getProfilePicture());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Intent detailedProfile = new Intent(SearchActivity.this, ProfileActivity.class);
                            detailedProfile.putExtra("PersonKey", getRef(position).getKey());
                            startActivity(detailedProfile);
                        }
                        else
                        {
                            finish();
                        }
                    }
                });

            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProfilePicture(Context context, String filepath) {
            CircleImageView mProfilePic = (CircleImageView) mView.findViewById(R.id.Search_Result_Profile_Image);
            StorageReference pictureref;
            if (filepath == null) {
                mProfilePic.setImageResource(R.drawable.no_profile_pic);
            } else {
                pictureref = FirebaseStorage.getInstance().getReferenceFromUrl(filepath);
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(pictureref)
                        .into(mProfilePic);
            }
        }

        public void setFullName(String fullName) {
            TextView postOwner = mView.findViewById(R.id.Search_Result_Full_Name);
            postOwner.setTypeface(null, Typeface.BOLD);
            postOwner.setText(fullName);
        }

        public void setShortDescription(String sDesc) {
            TextView postOwner = mView.findViewById(R.id.Search_Result_ShortDescription);
            if (sDesc != null) {
                if (!sDesc.isEmpty()) {
                    postOwner.setText(sDesc);
                } else {
                    postOwner.setText(" ");
                }
            } else {
                postOwner.setText(" ");
            }
        }
    }
}
